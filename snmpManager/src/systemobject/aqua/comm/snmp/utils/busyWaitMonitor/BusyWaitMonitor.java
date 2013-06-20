package systemobject.aqua.comm.snmp.utils.busyWaitMonitor;

import java.util.HashMap;
import java.util.LinkedList;

import jsystem.framework.monitor.Monitor;

import org.snmp4j.PDU;
import org.snmp4j.smi.Variable;

import systemobject.aqua.comm.snmp.Snmp;
import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class BusyWaitMonitor extends Monitor {

	private Snmp snmp = null;

	private String[] oids = null;

	private long timeout = 0L;

	private boolean result = false;

	private HashMap<String, PDU> valMap = null;

	private LinkedList<SnmpMonitorListener> listeners = new LinkedList<SnmpMonitorListener>();

	public BusyWaitMonitor(Snmp snmp, long timeout, String... oids)
			throws SnmpException {

		super("Snmp Busy Wait Monitor");

		this.snmp = new Snmp(snmp, true);
		this.oids = oids;
		this.timeout = timeout;
	}

	public void addListener(SnmpMonitorListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
			valMap = new HashMap<String, PDU>();
			boolean temp = true;
			int i = 0;
			for (; i < oids.length
					&& (System.currentTimeMillis() - start) < timeout; i++) {
				if (!temp) {
					i--;
				}
				temp = snmp.busyWaitForRecover(oids[i],
						timeout - (System.currentTimeMillis() - start), 1);
				if (temp) {
					valMap.put(oids[i], snmp.getLastPduReceived());
				}
			}
			if (i < oids.length) {
				for (; i < oids.length; i++) {
					boolean currRes = false;
					if (currRes = snmp.isExist(oids[i], 0)) {
						valMap.put(oids[i], snmp.getLastPduReceived());
					}
					temp &= currRes;
				}
			}
			result = temp;

			if (result) {
				Variable var = snmp.getLastPduReceived().get(0).getVariable();
				report.report(var.toString());
				try {
					for (SnmpMonitorListener listener : listeners) {
						listener.notify(valMap);
					}
				} catch (SnmpException e) {
					report.report(
							"Unexpected Error During Notify To All Listeners",
							false);
				}
			}

		} catch (SnmpException e) {
			report.report("Unexpected Error During Snmp Busy Wait Process",
					false);
		}
	}

	public HashMap<String, PDU> getValMap() {
		return valMap;
	}

	public boolean isResult() {
		return result;
	}

}
