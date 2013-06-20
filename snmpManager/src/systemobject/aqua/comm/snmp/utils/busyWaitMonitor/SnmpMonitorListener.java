package systemobject.aqua.comm.snmp.utils.busyWaitMonitor;

import java.util.HashMap;

import org.snmp4j.PDU;

import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public interface SnmpMonitorListener {

	public void notify(HashMap<String, PDU> valMap) throws SnmpException;

	public HashMap<String, Object> getMibMap();

}
