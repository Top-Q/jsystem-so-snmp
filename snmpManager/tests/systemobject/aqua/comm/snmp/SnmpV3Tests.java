package systemobject.aqua.comm.snmp;

import java.text.SimpleDateFormat;

import jsystem.utils.DateUtils;
import junit.framework.SystemTestCase;

import org.snmp4j.smi.VariableBinding;

import systemobject.aqua.comm.snmp.constant.SnmpVersion;
import systemobject.aqua.comm.snmp.trap.TrapListener;
import systemobject.aqua.comm.snmp.trap.TrapReceived;
import systemobject.aqua.comm.snmp.v3.SnmpV3User;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpV3Tests extends SystemTestCase implements TrapListener {

	private String host = "192.168.176.61";

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	Snmp snmp;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		SnmpV3User snmpV3User = new SnmpV3User();
		snmpV3User.setUser("user");
		snmpV3User.setPassword("password");
		snmp = new Snmp();
		snmp.setHost(host);
		snmp.setMibsDir("C:/work/project/automation/snmpManager/mib");
		snmp.setVersion(SnmpVersion.SNMPV3);
		snmp.user = snmpV3User;
		snmp.setAutoVersion(true);
		snmp.init();
	}

	public void testSetUp() throws Exception {

	}

	public void testReceiveTraps() throws Exception {
		snmp.addTrapListener(this);
		snmp.getTrap().init();
		while (true) {
			sleep(1000000);
		}
	}

	@Override
	public void trapReceived(TrapReceived tr) {
		String host = tr.getHost();
		System.out
				.println("------------------------------------------------------------------------------");
		System.out.println("trap from " + host);
		if (host.equals(host)) {
			StringBuffer sb = new StringBuffer();
			sb.append("Trap From \"" + host + "\", Received At "
					+ DateUtils.getDate(System.currentTimeMillis(), sdf) + "\n");
			for (Object o : tr.getPdu().getVariableBindings()) {
				VariableBinding vb = (VariableBinding) o;
				String oid = vb.getOid().toString();
				sb.append("Symbol Name: ");
				sb.append(snmp.getReader().getMibActualName(oid));
				sb.append(",  Oid: ");
				sb.append(oid);
				sb.append(",  Value: ");
				sb.append(vb.getVariable().toString());
				sb.append("\n");
			}
			sb.append("End Of Trap Info");
			try {
				report.report("Trap Received From Host \"" + host + "\"",
						sb.toString(), true);
			} catch (Exception e) {
			} finally {
				System.out.println(sb.toString());
			}
		}
		System.out
				.println("------------------------------------------------------------------------------");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
