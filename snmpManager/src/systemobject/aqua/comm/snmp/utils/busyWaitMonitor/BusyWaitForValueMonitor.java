package systemobject.aqua.comm.snmp.utils.busyWaitMonitor;

import java.util.HashMap;
import java.util.LinkedList;

import jsystem.framework.monitor.Monitor;

import org.snmp4j.PDU;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Variable;

import systemobject.aqua.comm.snmp.Snmp;
import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class BusyWaitForValueMonitor extends Monitor {

	private Snmp snmp = null;

	private String oid = null;

	private int value;

	private long timeout = 0L;

	private boolean result = false;

	private HashMap<String, PDU> valMap = null;

	private LinkedList<SnmpMonitorListener> listeners = new LinkedList<SnmpMonitorListener>();

	public BusyWaitForValueMonitor(Snmp snmp, long timeout, String oid,
			int value) throws SnmpException {

		super("Snmp Busy Wait Monitor");

		this.snmp = new Snmp(snmp, true);
		this.oid = oid;
		this.value = value;
		this.timeout = timeout;
	}

	public void addListener(SnmpMonitorListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(SnmpMonitorListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
			valMap = new HashMap<String, PDU>();
			boolean temp = true;
			Variable v = new Integer32(value);

			temp = snmp.busyWaitForValue(oid,
					timeout - (System.currentTimeMillis() - start), 1, v);
			if (temp) {
				valMap.put(oid, snmp.getLastPduReceived());
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
