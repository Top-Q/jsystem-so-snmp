package systemobject.aqua.comm.snmp.trap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import systemobject.aqua.comm.snmp.constant.SnmpConstant;
import systemobject.aqua.comm.snmp.constant.SnmpVersion;
import systemobject.aqua.comm.snmp.exception.SnmpException;
import systemobject.aqua.comm.snmp.manager.v3.V3User;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpTrap implements CommandResponder {

	private static SnmpTrap trapper = new SnmpTrap();

	private ArrayList<TrapListener> trapListeners = null;

	private LinkedList<TrapReceived> trapList = null;

	private org.snmp4j.Snmp manager = null;

	private V3User user = null;

	private boolean running = false;

	private SnmpVersion version = SnmpConstant.DEFAULT_VERSION;

	private SnmpTrap() {
		initListeners();
	}

	public void close() {

		setRunning(false);

		if (manager != null) {
			try {
				manager.close();
			} catch (IOException e) {

			}
		}
	}

	public void init() throws SnmpException {

		setRunning(false);

		reset();

		if (manager != null) {
			close();
		}

		TransportMapping transport;
		try {
			transport = new DefaultUdpTransportMapping(new UdpAddress(
					"0.0.0.0/" + SnmpConstant.DEFAULT_TRAP_PORT));
		} catch (IOException e) {
			throw new SnmpException("Failed To Init Snmp Trapper",
					"Failed To Create UDP Transport Mapping On 0.0.0.0/"
							+ SnmpConstant.DEFAULT_TRAP_PORT, e);
		}
		manager = new org.snmp4j.Snmp(transport);
		try {
			transport.listen();
		} catch (IOException e) {
			throw new SnmpException("Failed To Start Trapper Listening",
					e.getMessage(), e);
		}

		if (getUser() != null && getVersion() == SnmpVersion.SNMPV3) {
			addSnmpV3User(getUser());
		}
		manager.addCommandResponder(this);

		setRunning(true);
	}

	public void reset() {
		trapList = new LinkedList<TrapReceived>();
	}

	public static SnmpTrap getInstance() {
		return trapper;
	}

	public void addSnmpV3User(V3User u) throws SnmpException {
		if (u != null) {
			if (manager == null) {
				init();
			}
			if (manager.getUSM() == null) {
				USM usm = new USM(SecurityProtocols.getInstance(),
						new OctetString(MPv3.createLocalEngineID()), 0);
				SecurityModels.getInstance().addSecurityModel(usm);
			}
			manager.getUSM().addUser(
					new OctetString(u.getUserName()),
					new UsmUser(new OctetString(u.getSecurityName()), u
							.getAuthProtocol().oid(), new OctetString(u
							.getAuthPassword()), u.getPrivacyProtocol().oid(),
							new OctetString(u.getPrivacyPassword())));
		}
	}

	public synchronized void waitForTrap(long timeout) throws SnmpException {
		try {
			wait(timeout);
		} catch (Exception e) {
			throw new SnmpException("Failed In Wait For Trap", e);
		}
	}

	public void processPdu(CommandResponderEvent event) {
		TrapReceived tr = new TrapReceived(event);
		trapList.add(tr);

		for (TrapListener listener : trapListeners) {
			listener.trapReceived(tr);
		}
	}

	public void addListener(TrapListener listener) {
		trapListeners.add(listener);
	}

	public void initListeners() {
		trapListeners = new ArrayList<TrapListener>();
	}

	public LinkedList<TrapReceived> getTrapList() {
		return trapList;
	}

	public V3User getUser() {
		return this.user;
	}

	public void setUser(V3User user) {
		this.user = user;
	}

	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
		this.running = running;
	}

	public SnmpVersion getVersion() {
		return version;
	}

	public void setVersion(SnmpVersion version) throws SnmpException {
		this.version = version;

		if (isRunning()) {
			init();
		}
	}
}
