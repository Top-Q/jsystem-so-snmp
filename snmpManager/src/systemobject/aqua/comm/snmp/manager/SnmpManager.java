package systemobject.aqua.comm.snmp.manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

import org.snmp4j.AbstractTarget;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import systemobject.aqua.comm.snmp.compiler.BasicMibCompiler;
import systemobject.aqua.comm.snmp.compiler.DefaultMibCompilerImpl;
import systemobject.aqua.comm.snmp.compiler.MibReader;
import systemobject.aqua.comm.snmp.compiler.MibReaderDb;
import systemobject.aqua.comm.snmp.constant.SnmpConstant;
import systemobject.aqua.comm.snmp.constant.SnmpError;
import systemobject.aqua.comm.snmp.constant.SnmpRowStatus;
import systemobject.aqua.comm.snmp.constant.SnmpVersion;
import systemobject.aqua.comm.snmp.constant.v3.SecurityLevel;
import systemobject.aqua.comm.snmp.exception.SnmpException;
import systemobject.aqua.comm.snmp.exception.SnmpGetFailed;
import systemobject.aqua.comm.snmp.exception.SnmpGetNextFailed;
import systemobject.aqua.comm.snmp.exception.SnmpGetOidException;
import systemobject.aqua.comm.snmp.exception.SnmpResourceUnavailableException;
import systemobject.aqua.comm.snmp.exception.SnmpSetFailed;
import systemobject.aqua.comm.snmp.exception.SnmpTimeoutException;
import systemobject.aqua.comm.snmp.exception.SnmpValidatingGetFailed;
import systemobject.aqua.comm.snmp.exception.SnmpWalkFailed;
import systemobject.aqua.comm.snmp.manager.v3.V3User;
import systemobject.aqua.comm.snmp.trap.SnmpTrap;
import systemobject.aqua.comm.snmp.trap.TrapListener;
import systemobject.aqua.comm.snmp.utils.SnmpUtils;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpManager {

	private V3User user = null;

	private boolean failToPass = false;

	private boolean validateSetWithGet = false;

	private LinkedList<ResponseEvent> response = null;

	private PDU lastPduReceived = null;

	private PDU lastPduSent = null;

	private boolean useTrapper = true;

	private SnmpTrap trap = SnmpTrap.getInstance();

	private MibReader reader = null;

	private BasicMibCompiler mibCompiler = null;

	private SnmpVersion version = SnmpConstant.DEFAULT_VERSION;

	private String community = SnmpConstant.DEFAULT_COMUNITY;

	private int retries = SnmpConstant.DEFAULT_RETRIES;

	private int timeout = SnmpConstant.DEFAULT_TIMEOUT;

	private int port = SnmpConstant.DEFAULT_PORT;

	private String host = null;

	private org.snmp4j.Snmp manager = null;

	private AbstractTarget target = null;

	private SecurityLevel snmpV3SecurityLevel = SnmpConstant.DEFAULT_SNMP_V3_SECURITY_LEVEL;

	private String snmpV3SecurityName = null;

	private String mibsDir = null;

	private boolean autoVersion = false;

	private boolean ignoreTypeConflict = false;

	private int maxRepetitions = 10;

	private int nonRepeaters = 0;

	private boolean walkUseGetBulk = true;

	private boolean initiated = false;

	private int resourceUnavailableRetries = 5;

	private int resourceUnavailableDelay = 5000;

	public SnmpManager(boolean useTrapper) {

		super();

		this.useTrapper = useTrapper;
	}

	public void init() throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {

		setInitiated(false);

		if (mibsDir != null) {
			loadMibsToMap();
		}

		/**
		 * try to connect 3 times, each one takes "isAutoVersion" under
		 * consideration and every time it gives more time for the connection,
		 * if all 3 attempts fails, it throws an Exception.
		 */
		try {
			/**
			 * connect with 500 millis timeout
			 */
			getSysUpTime(500);
		} catch (SnmpException e1) {
			try {
				/**
				 * connect with 1000 millis timeout
				 */
				getSysUpTime(1000);
			} catch (SnmpException e2) {
				/**
				 * connect with 2000 millis timeout
				 */
				getSysUpTime(2000);
			}
		}
		setInitiated(true);
	}

	private void getSysUpTime(int timeout) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {

		initSession();

		int prevTimeout = getTimeout();
		int prevRetries = getRetries();
		boolean prevFailToPass = isFailToPass();

		try {
			setTimeout(timeout);
			setRetries(1);
			setFailToPass(false);
			try {
				get(SnmpConstants.sysUpTime);
				if (isFailed()) {
					throw new SnmpException(
							"Snmp Initial Get System Up Time Failed (Connectivity Check), Version = "
									+ getVersion() + ", Auto Version = "
									+ isAutoVersion());
				}
			} catch (SnmpException e) {
				if (isAutoVersion()) {
					if (getVersion() == SnmpVersion.SNMPV3) {
						setVersion(SnmpVersion.SNMPV2);
					} else if (user != null) {
						setVersion(SnmpVersion.SNMPV3);
					}
					initSession();
					try {
						get(SnmpConstants.sysUpTime);
						if (isFailed()) {
							throw new SnmpException(
									"Snmp Get System Up Time After Init Version Failed (Connectivity Check), Version = "
											+ getVersion()
											+ ", Auto Version = "
											+ isAutoVersion());
						}
					} catch (SnmpException e2) {
						throw new SnmpException(
								"Failed To Connect To Host And Get SysUpTime Symbol",
								e2);
					}
				} else {
					throw e;
				}
			}
		} finally {
			setTimeout(prevTimeout);
			setRetries(prevRetries);
			setFailToPass(prevFailToPass);
		}
	}

	public void setAll(String root, Variable value, Variable... expectedAfter)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		walk(root);
		VariableBinding[] arr = getVarBind();
		try {
			for (VariableBinding vb : arr) {
				set(vb.getOid(), value, expectedAfter);
			}
		} catch (Exception e) {
			throw new SnmpException("Failed To Set All Values Under Root \""
					+ root + "\" To " + value.toString() + " ("
					+ value.getClass().getSimpleName() + ")", e);
		}
	}

	public boolean isExist(String oid, long timeout)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		int prevTimeout = getTimeout();
		try {
			setTimeout(Math.min(5000, prevTimeout));
			return busyWaitForRecover(oid, timeout, 0);
		} finally {
			setTimeout(prevTimeout);
		}
	}

	public boolean busyWaitForRecover(String oid, long timeout,
			long pollingTimeout) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		OID o = null;
		long start = System.currentTimeMillis();
		do {
			if (o == null) {
				o = new OID(oid);
			} else {
				try {
					Thread.sleep(pollingTimeout);
				} catch (InterruptedException e) {
					continue;
				}
			}
			try {
				busyWaitCmnd(oid);
			} catch (SnmpTimeoutException e) {
				continue;
			} catch (SnmpResourceUnavailableException e) {
				continue;
			}
		} while ((isFailed() || !o.equals(getLastPduReceived().get(0).getOid()))
				&& ((System.currentTimeMillis() - start) < timeout));

		if (isTimeout()) {
			throw new SnmpTimeoutException(getLastPduSent(),
					System.currentTimeMillis() - start, getRetries() + 1);
		} else if (isResourceUnavailable()) {
			throw new SnmpResourceUnavailableException(getLastPduSent(),
					getResourceUnavailableRetries(),
					getResourceUnavailableDelay());
		}

		return (o != null && !isFailed() && o.equals(getLastPduReceived()
				.get(0).getOid()));
	}

	public boolean busyWaitForFail(String oid, long timeout, long pollingTimeout)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		OID o = null;
		long start = System.currentTimeMillis();
		do {
			if (o == null) {
				o = new OID(oid);
			} else {
				try {
					Thread.sleep(pollingTimeout);
				} catch (InterruptedException e) {
					continue;
				}
			}
			busyWaitCmnd(oid);
		} while (!isFailed() && o.equals(getLastPduReceived().get(0).getOid())
				&& ((System.currentTimeMillis() - start) < timeout));
		return (o != null && (isFailed() || !o.equals(getLastPduReceived().get(
				0).getOid())));
	}

	public boolean busyWaitForValue(String oid, long timeout,
			long pollingTimeout, Variable... expected)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		OID o = null;
		long start = System.currentTimeMillis();
		do {
			if (o == null) {
				o = new OID(oid);
			} else {
				try {
					Thread.sleep(pollingTimeout);
				} catch (InterruptedException e) {
					continue;
				}
			}
			try {
				busyWaitCmnd(oid);
			} catch (SnmpTimeoutException e) {
				continue;
			} catch (SnmpResourceUnavailableException e) {
				continue;
			}
		} while (!compareValue(true, expected)
				&& ((System.currentTimeMillis() - start) < timeout));

		if (isTimeout()) {
			throw new SnmpTimeoutException(getLastPduSent(),
					System.currentTimeMillis() - start, getRetries() + 1);
		} else if (isResourceUnavailable()) {
			throw new SnmpResourceUnavailableException(getLastPduSent(),
					getResourceUnavailableRetries(),
					getResourceUnavailableDelay());
		}

		return (o != null && compareValue(true, expected));
	}

	public boolean busyWaitForNotValue(String oid, long timeout,
			long pollingTimeout, Variable... expectedNot)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		OID o = null;
		long start = System.currentTimeMillis();
		do {
			if (o == null) {
				o = new OID(oid);
			} else {
				try {
					Thread.sleep(pollingTimeout);
				} catch (InterruptedException e) {
					continue;
				}
			}
			busyWaitCmnd(oid);
		} while (!compareValue(false, expectedNot)
				&& ((System.currentTimeMillis() - start) < timeout));
		return (o != null && compareValue(false, expectedNot));
	}

	private boolean compareValue(boolean shouldBeEqual, Variable... expected) {
		if (isFailed()) {
			return false;
		}
		Variable a = (getLastPduReceived().get(0).getVariable());
		boolean status = !shouldBeEqual;
		for (Variable v : expected) {
			if (shouldBeEqual) {
				status |= a.equals(v);
			} else {
				status &= !a.equals(v);
			}
		}
		return status;
	}

	private void busyWaitCmnd(String oid) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}
		PDU p = getVersion().build(PDU.GETNEXT);
		int index = oid.lastIndexOf('.');
		long lastOctet = Long.parseLong(oid.substring(index + 1));
		oid = oid.substring(0, index);
		if (lastOctet > 0) {
			oid = String.format("%s.%d.%d.%d", oid, (lastOctet - 1),
					SnmpUtils.oidOctetToLong(0xFFFFFFFF),
					SnmpUtils.oidOctetToLong(0xFFFFFFFF));
		}
		p.add(new VariableBinding(new OID(oid)));
		command(p, true);
	}

	public void walk(String root) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}

		OID currOid = new OID(root);
		int[] stopIds = currOid.getValue();
		stopIds = Arrays.copyOf(stopIds, stopIds.length);
		++stopIds[stopIds.length - 1];
		OID stopOid = new OID(stopIds);
		response = new LinkedList<ResponseEvent>();
		if (isWalkUseGetBulk()) {
			getBulkWalk(currOid, stopOid);
		} else {
			getNextWalk(currOid, stopOid);
		}

	}

	private boolean isInWalk(OID currOid, OID stopOid, OID prevOid) {
		return (currOid.compareTo(stopOid) < 0 && currOid.compareTo(prevOid) > 0);
	}

	@SuppressWarnings("unchecked")
	private void getBulkWalk(OID currOid, OID stopOid)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		OID prevOid = null;
		Vector<VariableBinding> vb = null;
		try {
			do {
				command(PDU.GETBULK, currOid, false);
				prevOid = currOid;
				currOid = null;
				vb = (Vector<VariableBinding>) getLastPduReceived()
						.getVariableBindings();
				currOid = vb.get(vb.size() - 1).getOid();
			} while (isInWalk(currOid, stopOid, prevOid));
		} catch (SnmpException e) {
			throw new SnmpWalkFailed(lastPduSent, reader, e);
		}

		if (response != null && response.size() > 0) {
			/**
			 * remove not relevant elements from last element that stopped the
			 * walk
			 */
			ResponseEvent re = response.getLast();
			vb = (Vector<VariableBinding>) re.getResponse()
					.getVariableBindings();
			int i = 0;

			for (; i < vb.size(); i++) {
				currOid = vb.get(i).getOid();
				if (!isInWalk(currOid, stopOid, prevOid)) {
					break;
				}
				prevOid = currOid;
			}
			while (vb.size() > i) {
				vb.remove(vb.size() - 1);
			}
			if (vb.size() == 0) {
				/**
				 * all the last PDU contained non-relevant entries and since the
				 * last response contains only relevant ones it will be the last
				 */
				re = response.removeLast();
				if (response.size() > 0) {
					re = response.getLast();
					lastPduSent = re.getRequest();
					setLastPduReceived(re.getResponse());
				}
			}
		}
	}

	private void getNextWalk(OID currOid, OID stopOid)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		OID prevOid = null;
		VariableBinding vb = null;
		try {
			do {
				command(PDU.GETNEXT, currOid, false);
				prevOid = currOid;
				currOid = null;
				vb = getLastPduReceived().get(0);
				currOid = vb.getOid();
			} while (isInWalk(currOid, stopOid, prevOid));
		} catch (SnmpException e) {
			throw new SnmpWalkFailed(lastPduSent, reader, e);
		}

		if (response != null && response.size() > 0) {
			response.removeLast();
			if (response.size() > 1) {
				/**
				 * remove last element that stopped the walk
				 */
				ResponseEvent e = response.getLast();
				lastPduSent = e.getRequest();
				setLastPduReceived(e.getResponse());
			}
		}
	}

	public void get(String oid) throws SnmpException {
		get(new OID(oid));
	}

	private void get(OID oid) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}
		try {
			command(PDU.GET, oid, true);
		} catch (SnmpException e) {
			throw new SnmpGetFailed(lastPduSent, reader, e);
		}
	}

	public void getNext(String oid) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}
		try {
			command(PDU.GETNEXT, oid, true);
		} catch (SnmpException e) {
			throw new SnmpGetNextFailed(lastPduSent, reader, e);
		}
	}

	public void getBulk(String oid) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}
		try {
			command(PDU.GETBULK, oid, true);
		} catch (SnmpException e) {
			throw new SnmpGetNextFailed(lastPduSent, reader, e);
		}
	}

	public void setRowStatus(String oid, SnmpRowStatus value)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		setRowStatus(new OID(oid), value);
	}

	public void set(String oid, Variable value) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		set(oid, value, value);
	}

	public void set(String oid, Variable value, Variable... expectedAfter)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		set(new OID(oid), value, expectedAfter);
	}

	public void setRowStatus(OID oid, SnmpRowStatus value)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		set(oid, value.variable(), value.expected());
	}

	public void set(OID oid, Variable value) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		set(oid, value, value);
	}

	public void set(OID oid, Variable value, Variable... expectedAfter)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		if (manager == null) {
			init();
		}
		try {
			command(PDU.SET, oid, value, true);
		} catch (SnmpException e) {
			throw new SnmpSetFailed(lastPduSent, getLastPduReceived(), reader,
					e);
		}
		if (isFailed() && !isFailToPass()) {
			throw new SnmpSetFailed(lastPduSent, getLastPduReceived(), reader);
		}
		if (isValidateSetWithGet() && !isFailed() && expectedAfter != null) {
			analyzeAfterSet(oid, null, value, expectedAfter);
		}
	}

	public void analyzeAfterSet(OID oid, SnmpException snmpSetFailedException,
			Variable value, Variable... expectedAfter)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		PDU setPduSent = getLastPduSent();
		PDU setPduReceived = getLastPduReceived();
		try {
			get(oid);
		} catch (SnmpGetFailed e) {
			throw new SnmpValidatingGetFailed(getLastPduSent(), reader,
					expectedAfter, null, e);
		}
		Variable actual = getLastPduReceived().get(0).getVariable();

		/**
		 * in case of destroy, change values so they will fit
		 */
		if (actual.getSyntax() == SnmpError.NO_SUCH_INSTANCE.value()
				&& value.equals(SnmpRowStatus.DESTROY.variable())
				&& expectedAfter.length == 1
				&& expectedAfter[0].equals(SnmpRowStatus.DESTROY.expected())) {
			actual = SnmpRowStatus.DESTROY.expected();
		}

		try {
			boolean cmp = false;
			for (Variable v : expectedAfter) {
				boolean cmpResult = actual.equals(v);
				if (!cmpResult) {
					if (!actual.getClass().getName()
							.equals(v.getClass().getName())) {
						if (isIgnoreTypeConflict()
								&& actual.toString().equals(v.toString())) {
							cmpResult = true;
						}
					} else if (actual instanceof OctetString) {
						String expStr = v.toString();
						String actStr = actual.toString();
						if (expStr == null) {
							expStr = "";
						}
						if (actStr == null) {
							actStr = "";
						}
						if (expStr.length() >= 3) {
							char actSep = actStr.charAt(2);
							char expSep = expStr.charAt(2);
							if ((expSep != actSep)
									&& (expSep == ':' || expSep == ' ' || expSep == '.')
									&& (actSep == ':' || actSep == ' ' || actSep == '.')) {
								expStr = expStr.replace(expSep, actSep)
										.toUpperCase();
								actStr = actStr.toUpperCase();
							}
						}
						cmpResult = actStr.startsWith(expStr);
					}
				}
				cmp |= cmpResult;
			}
			if (cmp == isFailToPass()) {
				if (snmpSetFailedException != null) {
					throw new SnmpValidatingGetFailed(reader, expectedAfter,
							actual, buildSetErrInfoMsg(actual, expectedAfter),
							snmpSetFailedException);
				} else {
					throw new SnmpValidatingGetFailed(reader, expectedAfter,
							actual, buildSetErrInfoMsg(actual, expectedAfter));
				}
			}
		} finally {
			setLastPduSent(setPduSent);
			setLastPduReceived(setPduReceived);
		}
	}

	private StringBuilder buildSetErrInfoMsg(Variable actual,
			Variable... expected) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\nExpected");
		if (expected.length > 1) {
			sb.append(" One Of The Following Values");
		}
		sb.append(":");
		for (Variable v : expected) {
			sb.append("\n\t");
			sb.append(v.getClass().getSimpleName());
			sb.append(" : \"");
			sb.append(v.toString());
			sb.append("\"");
		}
		sb.append("\nActual:\n\t");
		sb.append(actual.getClass().getSimpleName());
		sb.append(" : \"");
		sb.append(actual.toString());
		sb.append("\"\n\n");
		return sb;
	}

	public void initSession() throws SnmpException {
		if (manager != null) {
			close();
		}

		TransportMapping transport = null;
		try {
			transport = new DefaultUdpTransportMapping();
		} catch (IOException e) {
			throw new SnmpException("Failed To Init Snmp Session",
					"Failed To Create UDP Transport Mapping", e);
		}
		manager = new org.snmp4j.Snmp(transport);
		try {
			transport.listen();
		} catch (IOException e1) {
			throw new SnmpException("Failed To Start Manager Listening",
					e1.getMessage(), e1);
		}

		if (getVersion() == SnmpVersion.SNMPV3) {
			addSnmpV3User(user);
		}

		target = initTarget();
	}

	public AbstractTarget initTarget() {
		AbstractTarget t = null;
		if (getVersion() == SnmpVersion.SNMPV3) {
			t = new UserTarget();
			((UserTarget) t).setSecurityLevel(getSnmpV3SecurityLevel().value());
			((UserTarget) t).setSecurityName(new OctetString(
					getSnmpV3SecurityName()));
		} else {
			t = new CommunityTarget();
			((CommunityTarget) t).setCommunity(new OctetString(getCommunity()));
		}
		t.setAddress(new UdpAddress(getHost() + "/" + getPort()));
		t.setRetries(getRetries());
		t.setTimeout(getTimeout());
		t.setVersion(getVersion().value());
		return t;
	}

	public void addSnmpV3User(V3User u) {
		if (u != null) {
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

	public void close() {

		if (isUseTrapper() && getTrap() != null) {
			getTrap().close();
		}

		if (manager != null) {
			try {
				manager.close();
			} catch (IOException e) {

			}
			manager = null;
		}
	}

	public void loadMibsToMap() throws SnmpException {
		loadMibsToMap(true);
	}

	public void loadMibsToMap(boolean initDefaultMibCompiler)
			throws SnmpException {
		if (mibCompiler == null) {
			mibCompiler = new DefaultMibCompilerImpl(initDefaultMibCompiler);
			if (!initDefaultMibCompiler) {
				((DefaultMibCompilerImpl) mibCompiler).init();
			}
		}
		reader = MibReaderDb.getReader(mibsDir, mibCompiler);
	}

	public boolean isResourceUnavailable() {
		return isResourceUnavailable(getLastPduReceived());
	}

	public static boolean isResourceUnavailable(VariableBinding vb) {
		return (vb instanceof SnmpResourceUnavailableVb);
	}

	public static boolean isResourceUnavailable(PDU p) {
		if (p != null && p.size() > 0) {
			VariableBinding vb = p.get(0);
			if (vb != null && vb.getOid() != null && vb.getVariable() != null) {
				return (p.getErrorStatus() == SnmpError.SNMP_ERROR_RESOURCE_UNAVAILABLE
						.value());
			}
		}
		return false;
	}

	public boolean isTimeout() {
		return isTimeout(getLastPduReceived());
	}

	public static boolean isTimeout(PDU p) {
		if (p != null && p.size() > 0) {
			VariableBinding vb = p.get(0);
			if (vb != null && vb.getOid() != null && vb.getVariable() != null) {
				return (p.getErrorStatus() == SnmpError.SNMP_ERROR_TIMEOUT
						.value());
			}
		}
		return false;
	}

	public static boolean isTimeout(VariableBinding vb) {
		return (vb instanceof SnmpTimeoutVb);
	}

	public boolean isFailed() {
		PDU p = getLastPduReceived();
		if (p != null && p.size() > 0) {
			VariableBinding vb = p.get(0);
			if (vb != null && vb.getOid() != null && vb.getVariable() != null) {
				return ((p.getErrorIndex() != SnmpError.NO_ERROR.value() && p
						.getErrorIndex() != SnmpError.SNMP_ERROR_TOO_BIG
						.value())
						|| p.getErrorStatus() != SnmpError.NO_ERROR.value()
						|| vb.getSyntax() == SnmpError.NO_SUCH_INSTANCE.value()
						|| vb.getSyntax() == SnmpError.NO_SUCH_OBJECT.value() || vb
							.getSyntax() == SnmpError.END_OF_MIB_VIEW.value());
			}
		}
		return true;
	}

	public String getOid(String mibName) throws SnmpGetOidException {
		return reader.getOid(mibName);
	}

	public BasicMibCompiler getMibCompiler() {
		return mibCompiler;
	}

	public BasicMibCompiler setMibCompiler(BasicMibCompiler basicMibCompiler) {
		BasicMibCompiler prev = this.mibCompiler;
		this.mibCompiler = basicMibCompiler;
		return prev;
	}

	public void addTrapListener(TrapListener listener) throws SnmpException {

		if (!isUseTrapper()) {
			useTrapper = true;
		}

		if (!getTrap().isRunning()) {
			getTrap().init();
		}
		getTrap().addListener(listener);
	}

	public String getMibsDir() {
		return mibsDir;
	}

	public String setMibsDir(String mibsDir) throws SnmpException {
		String prev = this.mibsDir;
		this.mibsDir = mibsDir;
		if (mibsDir != null) {
			loadMibsToMap();
		}
		return prev;
	}

	public SnmpVersion setVersion(SnmpVersion version) throws SnmpException {
		SnmpVersion oldVersion = this.version;
		this.version = version;
		if (this.target != null) {
			this.target.setVersion(version.value());
			if (user != null && oldVersion != SnmpVersion.SNMPV3
					&& version == SnmpVersion.SNMPV3) {
				addSnmpV3User(user);
			}
		}

		if (isUseTrapper() && getTrap() != null) {
			getTrap().setVersion(version);
		}
		if (isInitiated()) {
			init();
		}
		return oldVersion;
	}

	public SnmpVersion getVersion() {
		return version;
	}

	public String setCommunity(String community) {
		String prev = this.community;
		this.community = community;
		if (this.target != null && (this.target instanceof CommunityTarget)) {
			((CommunityTarget) this.target).setCommunity(new OctetString(
					community));
		}
		return prev;
	}

	public String getCommunity() {
		return community;
	}

	public int setRetries(int retries) {
		int prev = this.retries;
		this.retries = retries;
		if (this.target != null) {
			this.target.setRetries(retries);
		}
		return prev;
	}

	public int getRetries() {
		return retries;
	}

	public int setTimeout(int timeout) {
		int prev = this.timeout;
		this.timeout = timeout;
		if (this.target != null) {
			this.target.setTimeout(timeout);
		}
		return prev;
	}

	public int getTimeout() {
		return timeout;
	}

	public int setPort(int port) {
		int prev = this.port;
		this.port = port;
		if (this.target != null) {
			this.target.setAddress(new UdpAddress(getHost() + "/" + port));
		}
		return prev;
	}

	public int getPort() {
		return port;
	}

	public String setHost(String host) {
		String prev = this.host;
		this.host = host;
		if (this.target != null) {
			this.target.setAddress(new UdpAddress(host + "/" + getPort()));
		}
		return prev;
	}

	public String getHost() {
		return host;
	}

	public LinkedList<ResponseEvent> getResponse() {
		return response;
	}

	@SuppressWarnings("unchecked")
	public LinkedList<VariableBinding> getVarBindList() throws SnmpException {
		if (response == null) {
			return null;
		}
		try {
			LinkedList<VariableBinding> vb = new LinkedList<VariableBinding>();
			for (ResponseEvent e : response) {
				PDU p = e.getResponse();
				if (isTimeout(p)) {
					vb.add(new SnmpTimeoutVb(e.getRequest().get(0)));
				} else if (isResourceUnavailable(p)) {
					vb.add(new SnmpResourceUnavailableVb(e.getRequest().get(0)));
				} else {
					try {
						vb.addAll((Vector<VariableBinding>) p
								.getVariableBindings());
					} catch (OutOfMemoryError err) {
						System.gc();
						Thread.sleep(500);
						System.gc();
						Thread.sleep(500);
						for (Object vBind : p.getVariableBindings()) {
							vb.add((VariableBinding) vBind);
						}
					}
				}
			}
			return vb;
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Get All Variable Bindings Into List", e);
		}
	}

	public VariableBinding[] getVarBind() throws SnmpException {
		try {
			LinkedList<VariableBinding> vb = getVarBindList();
			if (vb == null) {
				return null;
			}
			return vb.toArray(new VariableBinding[vb.size()]);
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Get All Variable Bindings Into Array", e);
		}
	}

	public SecurityLevel getSnmpV3SecurityLevel() {
		return snmpV3SecurityLevel;
	}

	public SecurityLevel setSnmpV3SecurityLevel(
			SecurityLevel snmpV3SecurityLevel) {
		SecurityLevel prev = this.snmpV3SecurityLevel;
		this.snmpV3SecurityLevel = snmpV3SecurityLevel;
		return prev;
	}

	public String getSnmpV3SecurityName() {
		if (snmpV3SecurityName == null && user != null) {
			return user.getUserName();
		}
		return snmpV3SecurityName;
	}

	public String setSnmpV3SecurityName(String snmpV3SecurityName) {
		String prev = this.snmpV3SecurityName;
		this.snmpV3SecurityName = snmpV3SecurityName;
		return prev;
	}

	public PDU getLastPduReceived() {
		return lastPduReceived;
	}

	public PDU setLastPduReceived(PDU lastPduReceived) {
		PDU prev = this.lastPduReceived;
		if (lastPduReceived == null) {
			lastPduReceived = new SnmpTimeoutPdu(getLastPduSent());
		}
		this.lastPduReceived = lastPduReceived;
		return prev;
	}

	public PDU getLastPduSent() {
		return lastPduSent;
	}

	public PDU setLastPduSent(PDU lastPduSent) {
		PDU prev = this.lastPduSent;
		this.lastPduSent = lastPduSent;
		return prev;
	}

	public org.snmp4j.Snmp getManager() {
		return manager;
	}

	public AbstractTarget getTarget() {
		return target;
	}

	public AbstractTarget setTarget(AbstractTarget target) {
		AbstractTarget prev = this.target;
		this.target = target;
		return prev;
	}

	private void command(int type, String oid, boolean resetList)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		command(type, new OID(oid), resetList);
	}

	private void command(int type, OID oid, boolean resetList)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		PDU command = getVersion().build(type);
		command.add(new VariableBinding(oid));
		if (type == PDU.GETBULK) {
			command.setMaxRepetitions(getMaxRepetitions());
			command.setNonRepeaters(getNonRepeaters());
		}
		command(command, resetList);
	}

	private void command(int type, OID oid, Variable value, boolean resetList)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		PDU command = getVersion().build(type);
		command.add(new VariableBinding(oid, value));
		command(command, resetList);
	}

	public synchronized void command(PDU command, boolean resetList)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		LinkedList<ResponseEvent> list = response;
		response = null;
		lastPduSent = command;
		ResponseEvent e;
		int counter = 0;
		long startTime = System.currentTimeMillis();
		do {
			if ((counter++) > 0) {
				System.out.println("RESOURCE UNAVAILABLE: Wait "
						+ getResourceUnavailableDelay()
						+ " Millis Before Retry no." + counter);
				try {
					Thread.sleep(getResourceUnavailableDelay());
				} catch (InterruptedException interException) {

				}
			}
			try {
				e = manager.send(command, target);
			} catch (IOException e1) {
				throw new SnmpException("Failed To Send Command To Agent",
						e1.getMessage(), e1);
			}
			if (resetList) {
				response = new LinkedList<ResponseEvent>();
			} else {
				response = list;
			}
			response.add(e);
			setLastPduReceived(e.getResponse());
		} while (isResourceUnavailable()
				&& counter < getResourceUnavailableRetries());
		if (isTimeout()) {
			throw new SnmpTimeoutException(getLastPduSent(),
					System.currentTimeMillis() - startTime, getRetries() + 1);
		} else if (isResourceUnavailable()) {
			throw new SnmpResourceUnavailableException(getLastPduSent(),
					getResourceUnavailableRetries(),
					getResourceUnavailableDelay());
		}
	}

	public V3User getUser() {
		return user;
	}

	public V3User setUser(V3User user) {
		V3User prev = this.user;
		this.user = user;
		if (isUseTrapper()) {
			getTrap().setUser(user);
		}
		return prev;
	}

	public boolean isFailToPass() {
		return failToPass;
	}

	public boolean setFailToPass(boolean failToPass) {
		boolean prev = this.failToPass;
		this.failToPass = failToPass;
		return prev;
	}

	public boolean isValidateSetWithGet() {
		return validateSetWithGet;
	}

	public boolean setValidateSetWithGet(boolean validateSetWithGet) {
		boolean prev = this.validateSetWithGet;
		this.validateSetWithGet = validateSetWithGet;
		return prev;
	}

	public SnmpTrap getTrap() {
		return trap;
	}

	public SnmpTrap setTrap(SnmpTrap trap) {
		SnmpTrap prev = this.trap;
		this.trap = trap;
		return prev;
	}

	public MibReader getReader() {
		return reader;
	}

	public MibReader setReader(MibReader reader) {
		MibReader prev = this.reader;
		this.reader = reader;
		return prev;
	}

	public LinkedList<ResponseEvent> setResponse(
			LinkedList<ResponseEvent> response) {
		LinkedList<ResponseEvent> prev = this.response;
		this.response = response;
		return prev;
	}

	public org.snmp4j.Snmp setManager(org.snmp4j.Snmp manager) {
		org.snmp4j.Snmp prev = this.manager;
		this.manager = manager;
		return prev;
	}

	public boolean isAutoVersion() {
		return autoVersion;
	}

	public boolean setAutoVersion(boolean autoVersion) {
		boolean prev = this.autoVersion;
		this.autoVersion = autoVersion;
		return prev;
	}

	public boolean isIgnoreTypeConflict() {
		return ignoreTypeConflict;
	}

	public boolean setIgnoreTypeConflict(boolean ignoreTypeConflict) {
		boolean prev = this.ignoreTypeConflict;
		this.ignoreTypeConflict = ignoreTypeConflict;
		return prev;
	}

	public int getMaxRepetitions() {
		return maxRepetitions;
	}

	public int setMaxRepetitions(int maxRepetitions) {
		int prev = this.maxRepetitions;
		this.maxRepetitions = maxRepetitions;
		return prev;
	}

	public int getNonRepeaters() {
		return nonRepeaters;
	}

	public int setNonRepeaters(int nonRepeaters) {
		int prev = this.nonRepeaters;
		this.nonRepeaters = nonRepeaters;
		return prev;
	}

	public boolean isWalkUseGetBulk() {
		return walkUseGetBulk;
	}

	public boolean setWalkUseGetBulk(boolean walkUseGetBulk) {
		boolean prev = this.walkUseGetBulk;
		this.walkUseGetBulk = walkUseGetBulk;
		return prev;
	}

	public char getOidAndValueSeperator() {
		return '=';
	}

	public boolean isInitiated() {
		return initiated;
	}

	private boolean setInitiated(boolean initiated) {
		boolean prev = this.initiated;
		this.initiated = initiated;
		return prev;
	}

	public int getResourceUnavailableRetries() {
		return resourceUnavailableRetries;
	}

	public int setResourceUnavailableRetries(int resourceUnavailableRetries) {
		int prev = this.resourceUnavailableRetries;
		this.resourceUnavailableRetries = resourceUnavailableRetries;
		return prev;
	}

	public int getResourceUnavailableDelay() {
		return resourceUnavailableDelay;
	}

	public void setResourceUnavailableDelay(int resourceUnavailableDelay) {
		this.resourceUnavailableDelay = resourceUnavailableDelay;
	}

	/**
	 * @author Itzhak.Hovav
	 */
	private class SnmpTimeoutVb extends VariableBinding {

		private static final long serialVersionUID = -8689817667520890046L;

		public SnmpTimeoutVb(VariableBinding sent) {
			super(sent.getOid(), sent.getVariable());
		}

		public String toString() {
			return ("Timeout: " + super.toString());
		}

		public Object clone() {
			return new SnmpTimeoutVb(new VariableBinding(getOid(),
					getVariable()));
		}

		public boolean equals(Object o) {
			return (super.equals(o) && (o instanceof SnmpTimeoutVb));
		}
	}

	/**
	 * @author Itzhak.Hovav
	 */
	private class SnmpResourceUnavailableVb extends VariableBinding {

		private static final long serialVersionUID = -640686993596766633L;

		public SnmpResourceUnavailableVb(VariableBinding sent) {
			super(sent.getOid(), sent.getVariable());
		}

		public String toString() {
			return ("Resource Unavailable: " + super.toString());
		}

		public Object clone() {
			return new SnmpResourceUnavailableVb(new VariableBinding(getOid(),
					getVariable()));
		}

		public boolean equals(Object o) {
			return (super.equals(o) && (o instanceof SnmpResourceUnavailableVb));
		}
	}

	/**
	 * @author Itzhak.Hovav
	 */
	private class SnmpTimeoutPdu extends PDU {

		private static final long serialVersionUID = 2910841482149658921L;

		SnmpTimeoutPdu(PDU sent) {
			super(sent);
		}

		@Override
		public int getErrorStatus() {
			return SnmpError.SNMP_ERROR_TIMEOUT.value();
		}

		@Override
		public String getErrorStatusText() {
			return "Request Timeout";
		}
	}

	public boolean isUseTrapper() {
		return this.useTrapper;
	}

}
