package systemobject.aqua.comm.snmp;

import junit.framework.SystemTestCase;
import systemobject.aqua.comm.snmp.exception.SnmpException;
import systemobject.aqua.comm.snmp.manager.SnmpManager;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class TestMibCompailer extends SystemTestCase {

	public void testCompileMib() throws SnmpException {
		SnmpManager manager = new SnmpManager(false);
		manager.setMibsDir("C:\\MIBS\\REL_4_10");
		manager.loadMibsToMap(false);
		System.out.println(manager.getOid("sysUpTime"));
	}
}
