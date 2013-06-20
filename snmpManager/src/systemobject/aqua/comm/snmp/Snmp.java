package systemobject.aqua.comm.snmp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.framework.system.SystemObjectImpl;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import systemobject.aqua.comm.snmp.compiler.BasicMibCompiler;
import systemobject.aqua.comm.snmp.compiler.MibReader;
import systemobject.aqua.comm.snmp.constant.SnmpConstant;
import systemobject.aqua.comm.snmp.constant.SnmpError;
import systemobject.aqua.comm.snmp.constant.SnmpRowStatus;
import systemobject.aqua.comm.snmp.constant.SnmpVersion;
import systemobject.aqua.comm.snmp.constant.v3.SecurityLevel;
import systemobject.aqua.comm.snmp.exception.SnmpException;
import systemobject.aqua.comm.snmp.exception.SnmpGetFailed;
import systemobject.aqua.comm.snmp.exception.SnmpGetOidException;
import systemobject.aqua.comm.snmp.exception.SnmpOperationNotSupported;
import systemobject.aqua.comm.snmp.exception.SnmpResourceUnavailableException;
import systemobject.aqua.comm.snmp.exception.SnmpTimeoutException;
import systemobject.aqua.comm.snmp.manager.SnmpManager;
import systemobject.aqua.comm.snmp.trap.SnmpTrap;
import systemobject.aqua.comm.snmp.trap.TrapListener;
import systemobject.aqua.comm.snmp.utils.SnmpFailReason;
import systemobject.aqua.comm.snmp.utils.SnmpUtils;
import systemobject.aqua.comm.snmp.v3.SnmpV3User;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class Snmp extends SystemObjectImpl {

	/**
	 * @author Itzhak.Hovav
	 */
	private enum Operations {
		Set, Get, GetNext, Walk;
	}

	private String mibsDir = null;

	public SnmpV3User user = null;

	private SnmpManager manager = null;

	private boolean silent = false;

	private boolean failToPass = false;

	private boolean lightReport = true;

	private boolean validateSetWithGet = false;

	private SnmpVersion version = SnmpConstant.DEFAULT_VERSION;

	private String community = SnmpConstant.DEFAULT_COMUNITY;

	private int retries = SnmpConstant.DEFAULT_RETRIES;

	private int timeout = SnmpConstant.DEFAULT_TIMEOUT;

	private int port = SnmpConstant.DEFAULT_PORT;

	private String host = null;

	private SecurityLevel snmpV3SecurityLevel = SnmpConstant.DEFAULT_SNMP_V3_SECURITY_LEVEL;

	private String snmpV3SecurityName = null;

	private boolean autoVersion = true;

	private int operationRetries = 5;

	private long operationRetriesTimeout = 3000;

	private boolean ignoreTypeConflict = false;

	private int resourceUnavailableRetries = 5;

	private int resourceUnavailableDelay = 5000;

	public Snmp() throws SnmpException {
		super();
		setMibsDir(null);
		setUser(null);
		setManager(null);
		setSilent(false);
		setFailToPass(false);
		setLightReport(true);
		setValidateSetWithGet(false);
		setVersion(SnmpConstant.DEFAULT_VERSION);
		setCommunity(SnmpConstant.DEFAULT_COMUNITY);
		setRetries(SnmpConstant.DEFAULT_RETRIES);
		setTimeout(SnmpConstant.DEFAULT_TIMEOUT);
		setPort(SnmpConstant.DEFAULT_PORT);
		setHost(null);
		setSnmpV3SecurityLevel(SnmpConstant.DEFAULT_SNMP_V3_SECURITY_LEVEL);
		setSnmpV3SecurityName(null);
		setAutoVersion(true);
		setOperationRetries(5);
		setOperationRetriesTimeout(2000);
		setIgnoreTypeConflict(false);
		setResourceUnavailableDelay(5000);
		setResourceUnavailableRetries(5);
	}

	public Snmp(String host, String mibsDir) throws SnmpException {
		this(host, mibsDir, null);
	}

	public Snmp(String host, String mibsDir, SnmpV3User user)
			throws SnmpException {
		this(host, mibsDir, user, true);
	}

	public Snmp(String host, String mibsDir, SnmpV3User user,
			boolean autoVersion) throws SnmpException {
		this(host, mibsDir, user, autoVersion, false);
	}

	public Snmp(String host, String mibsDir, boolean init) throws SnmpException {
		this(host, mibsDir, null, true, init);
	}

	public Snmp(String host, String mibsDir, SnmpV3User user,
			boolean autoVersion, boolean init) throws SnmpException {
		this();

		setHost(host);
		setMibsDir(mibsDir);
		setUser(user);
		setAutoVersion(autoVersion);
		if (init) {
			init();
		}
	}

	public Snmp(Snmp o, boolean init) throws SnmpException {
		this(o.getHost(), o.getMibsDir(), o.getUser(), o.isAutoVersion(), init);
	}

	@Override
	public void init() throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		try {
			super.init();
		} catch (Exception e) {
			throw new SnmpException("Fail In System Object Init", e);
		}

		initSession(false);

		manager.init();

		/**
		 * if its auto version is "true" it should update its version according
		 * to the version in the manager.
		 */
		this.version = manager.getVersion();
	}

	/**
	 * perform "walk" on the given root and returns the values as integers (int
	 * array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @return int[] contains all values as ints, empty array (int[0]) in case
	 *         where there are no values found in the walk
	 * @throws SnmpException
	 */
	public int[] getInt32All(String root) throws SnmpException {
		walk(root);
		VariableBinding[] arr = getVarBind();
		int[] val = new int[arr == null ? 0 : arr.length];
		try {
			for (int i = 0; i < val.length; i++) {
				val[i] = arr[i].getVariable().toInt();
			}
		} catch (Exception e) {
			throw new SnmpException("Failed To Get All Values Into Int Array",
					e);
		}
		return val;
	}

	/**
	 * perform "walk" on the given root and returns the values as longs (long
	 * array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @return long[] contains all values as longs, empty array (long[0]) in
	 *         case where there are no values found in the walk
	 * @throws SnmpException
	 */
	public long[] getGauge32All(String root) throws SnmpException {
		walk(root);
		VariableBinding[] arr = getVarBind();
		long[] val = new long[arr == null ? 0 : arr.length];
		try {
			for (int i = 0; i < val.length; i++) {
				val[i] = arr[i].getVariable().toLong();
			}
		} catch (Exception e) {
			throw new SnmpException("Failed To Get All Values Into Long Array",
					e);
		}
		return val;
	}

	/**
	 * Get Row Status MIB of the table
	 */
	public String getRowStatusMib(String table) throws Exception {
		int[] tableOid = new OID(table).getValue();
		walk(table);
		LinkedList<VariableBinding> rows = getVarBindList();
		MibReader mr = getReader();
		for (VariableBinding v : rows) {
			if (mr.getMibActualName(v.getOid().toString())
					.contains("RowStatus")) {
				int[] newOid = Arrays.copyOf(tableOid, tableOid.length + 1);
				newOid[newOid.length - 1] = v.getOid().getValue()[newOid.length - 1];
				return new OID(newOid).toString();
			}
		}
		return null;
	}

	/**
	 * perform "walk" on the given root and returns the values as strings
	 * (string array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @return string[] contains all values as strings, empty array (string[0])
	 *         in case where there are no values found in the walk
	 * @throws SnmpException
	 */
	public String[] getOctetStringAll(String root) throws SnmpException {
		walk(root);
		VariableBinding[] arr = getVarBind();
		String[] val = new String[arr == null ? 0 : arr.length];
		try {
			for (int i = 0; i < val.length; i++) {
				val[i] = arr[i].getVariable().toString();
			}
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Get All Values Into String Array", e);
		}
		return val;
	}

	/**
	 * perform "walk" on the given root and returns the remaining elements in
	 * all the OIDs as integers array (2 dimensional int array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @return int[][] contains all remaining elements in all the OIDs as int
	 *         array, empty array (int[0][]) in case where there are no entries
	 *         found in the walk
	 * @throws SnmpException
	 */
	public int[][] getEndOfOid(String root) throws SnmpException {
		return getEndOfOid(root, true);
	}

	/**
	 * perform "walk" on the given root and returns the remaining elements in
	 * all the OIDs as integers array (2 dimensional int array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @param walk
	 *            true for performing the walk, false for handling last
	 *            operation results
	 * @return int[][] contains all remaining elements in all the OIDs as int
	 *         array, empty array (int[0][]) in case where there are no entries
	 *         found in the walk
	 * @throws SnmpException
	 */
	public int[][] getEndOfOid(String root, boolean walk) throws SnmpException {
		if (walk) {
			walk(root);
		}
		VariableBinding[] arr = getVarBind();
		int[][] val = new int[arr == null ? 0 : arr.length][];
		int from = (new OID(root)).getValue().length;
		try {
			for (int i = 0; i < val.length; i++) {
				int[] temp = arr[i].getOid().getValue();
				val[i] = Arrays.copyOfRange(temp, from, temp.length);
			}
			return val;
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Get All End Of OIDs Into 2 Dimentional Int Array",
					e);
		}
	}

	/**
	 * perform "walk" on the given root and returns the remaining elements in
	 * all the OIDs as integers array (2 dimensional long array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @return long[][] contains all remaining elements in all the OIDs as long
	 *         array, empty array (long[0][]) in case where there are no entries
	 *         found in the walk
	 * @throws SnmpException
	 */
	public long[][] getEndOfOidAsLong(String root) throws SnmpException {
		return getEndOfOidAsLong(root, true);
	}

	/**
	 * perform "walk" on the given root and returns the remaining elements in
	 * all the OIDs as integers array (2 dimensional long array)
	 * 
	 * @param root
	 *            OID to perform walk on
	 * @param walk
	 *            true for performing the walk, false for handling last
	 *            operation results
	 * @return long[][] contains all remaining elements in all the OIDs as long
	 *         array, empty array (long[0][]) in case where there are no entries
	 *         found in the walk
	 * @throws SnmpException
	 */
	public long[][] getEndOfOidAsLong(String root, boolean walk)
			throws SnmpException {
		try {
			int[][] val = getEndOfOid(root, walk);
			long[][] newArr = null;
			if (val != null) {
				newArr = new long[val.length][];
				for (int j = 0; j < val.length; j++) {
					newArr[j] = new long[val[j].length];
					for (int i = 0; i < val[j].length; i++) {
						newArr[j][i] = SnmpUtils.oidOctetToLong(val[j][i]);
					}
				}
			}
			return newArr;
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Get All End Of OIDs Into 2 Dimentional Long Array",
					e);
		}
	}

	public int[] getLastOid() throws SnmpException {
		return getVarBind()[0].getOid().getValue();
	}

	public long[] getLastOidAsLong() throws SnmpException {
		int[] val = getLastOid();
		long[] newArr = new long[val.length];
		for (int i = 0; i < val.length; i++) {
			newArr[i] = SnmpUtils.oidOctetToLong(val[i]);
		}
		return newArr;
	}

	public boolean isExist(String oid, long timeout)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		if (manager == null) {
			initSession(false);
		}
		boolean status = manager.isExist(oid, timeout);
		if (!isSilent()) {
			String mibName = (getReader() == null ? "" : getReader()
					.getMibActualName(oid) + " ");
			report.report(String.format("%sEntry (%s) %sExist%s", mibName, oid,
					(status ? "" : "Not "), (status ? ", Value = "
							+ getLastPduReceived().get(0).getVariable()
									.toString() : "")), String.format(
					"Mib Name=%s\nMib Oid=%s\nGetNext Oid Used=%s", mibName,
					oid, getLastPduReceived().get(0).toString()), true);
		}
		return status;
	}

	public boolean busyWaitForRecover(String oid, long timeout,
			long pollingTimeout) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			initSession(false);
		}
		long start = System.currentTimeMillis();
		boolean status = manager.busyWaitForRecover(oid, timeout,
				pollingTimeout);
		if (!isSilent()) {
			String mibName = (getReader() == null ? "" : getReader()
					.getMibActualName(oid) + " ");
			report.report(String.format("After %s: %sEntry (%s) %sRecovered",
					formatTime(System.currentTimeMillis() - start), mibName,
					oid, (status ? "" : "Not ")), String.format(
					"Mib Name=%s\nMib Oid=%s\nGetNext Oid Used=%s", mibName,
					oid, getLastPduReceived().get(0).toString()), status);
		}
		return status;
	}

	public boolean busyWaitForFail(String oid, long timeout, long pollingTimeout)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		long start = System.currentTimeMillis();
		boolean status = manager.busyWaitForFail(oid, timeout, pollingTimeout);
		if (!isSilent()) {
			String mibName = (getReader() == null ? "" : getReader()
					.getMibActualName(oid) + " ");
			report.report(String.format("After %s: %sEntry (%s) %sFailed",
					formatTime(System.currentTimeMillis() - start), mibName,
					oid, (status ? "" : "Not ")), String.format(
					"Mib Name=%s\nMib Oid=%s\nGetNext Oid Used=%s", mibName,
					oid, getLastPduReceived().get(0).toString()), status);
		}
		return status;
	}

	public boolean busyWaitForValue(String oid, long timeout,
			long pollingTimeout, Variable... expected)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		long start = System.currentTimeMillis();
		boolean status = manager.busyWaitForValue(oid, timeout, pollingTimeout,
				expected);
		if (!isSilent()) {
			String mibName = (getReader() == null ? "" : getReader()
					.getMibActualName(oid) + " ");
			report.report(String.format(
					"After %s: Expected Value %sFound In %sEntry (%s)",
					formatTime(System.currentTimeMillis() - start),
					(status ? "" : "Not "), mibName, oid), String.format(
					"Mib Name=%s\nMib Oid=%s\nGetNext Oid Used=%s", mibName,
					oid, getLastPduReceived().get(0).toString()), status);
		}
		return status;
	}

	public boolean busyWaitForNotValue(String oid, long timeout,
			long pollingTimeout, Variable... expectedNot)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		long start = System.currentTimeMillis();
		boolean status = manager.busyWaitForNotValue(oid, timeout,
				pollingTimeout, expectedNot);
		if (!isSilent()) {
			String mibName = (getReader() == null ? "" : getReader()
					.getMibActualName(oid) + " ");
			report.report(String.format(
					"After %s: Un Expected Value %sFound In %sEntry (%s)",
					formatTime(System.currentTimeMillis() - start),
					(status ? "Not " : ""), mibName, oid), String.format(
					"Mib Name=%s\nMib Oid=%s\nGetNext Oid Used=%s", mibName,
					oid, getLastPduReceived().get(0).toString()), status);
		}
		return status;
	}

	public void walk(String root) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}

		StringBuilder header = new StringBuilder();
		if (getReader() != null) {
			header.append(getReader().getMibActualName(root));
			header.append("  ");
		}
		header.append(root);

		manager.walk(root);

		LinkedList<VariableBinding> list = manager.getVarBindList();
		setTestAgainstObject(list);
		if (!report.isSilent()) {
			report(true, false, Operations.Walk, getLastPduSent(),
					getLastPduReceived(), header.toString(),
					getVbListToString(list));
		}
	}

	public String getVbListToString() throws SnmpException {
		return getVbListToString(manager.getVarBindList());
	}

	public String getVbListToString(LinkedList<VariableBinding> list)
			throws SnmpException {
		StringBuilder message = new StringBuilder();
		if (list != null && list.size() > 0) {
			for (VariableBinding v : list) {
				message.append(v.toString());
				message.append("\n");
			}
		}
		message.append("Done");
		return message.toString();
	}

	public void get(String oid) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}

		int counter = 0;
		do {
			if ((counter++) > 0) {
				try {
					Thread.sleep(getOperationRetriesTimeout());
				} catch (InterruptedException e) {

				}
				report.report(
						"Snmp Get Retry: " + counter,
						"Last Responce: "
								+ (manager.getLastPduReceived() == null ? "No Responce (null)"
										: manager.getLastPduReceived()
												.toString()), true);
			}
			try {
				manager.get(oid);
			} catch (Exception e) {
				SnmpException se = null;
				if (!(e instanceof SnmpException)) {
					se = new SnmpGetFailed(getLastPduSent(), getReader(), e);
				} else {
					se = (SnmpException) e;
				}
				report.report("Fail getting value from " + oid + " mib : ",
						getStackTraceStr(e), false);
				throw se;
			}
		} while (isFailed() && counter < getOperationRetries());
		setTestAgainstObject(getLastPduReceived());
		report(Operations.Get);
	}

	public void getNext(String oid) throws SnmpTimeoutException,
			SnmpResourceUnavailableException, SnmpException {
		if (manager == null) {
			init();
		}

		manager.getNext(oid);
		setTestAgainstObject(getLastPduReceived());
		report(Operations.GetNext);
	}

	private String getSetFailedStr(String st) {
		PDU sent = manager.getLastPduSent();
		PDU received = manager.getLastPduReceived();
		StringBuilder sb = new StringBuilder();
		Vector<?> v = null;
		sb.append("<b>Sent Request:</b> ");
		if (sent == null) {
			sb.append("No Sent (null)");
		} else {
			v = sent.getVariableBindings();
			for (Object o : v) {
				sb.append("\n\t");
				VariableBinding vb = (VariableBinding) o;
				String oid = vb.getOid().toString();
				String close = "";
				if (getReader() != null) {
					sb.append(getReader().getMibActualName(oid));
					sb.append(" [");
					close = "]";
				}
				sb.append(oid);
				sb.append(close);
				sb.append(" : ");
				sb.append(vb.getVariable().toString());
				sb.append(" [");
				sb.append(vb.getVariable().getClass().getSimpleName());
				sb.append("]");
			}
		}
		String comments = "";
		sb.append("\n<b>Get Responce:</b> ");
		if (received == null) {
			sb.append("No Responce (null)");
		} else {
			v = received.getVariableBindings();
			for (Object o : v) {
				sb.append("\n\t");
				VariableBinding vb = (VariableBinding) o;
				String oid = vb.getOid().toString();
				String close = "";
				if (getReader() != null) {
					sb.append(getReader().getMibActualName(oid));
					sb.append(" [");
					close = "]";
					comments = "\n\n*****************************************************************************************\n<b>Mib Symbol Comments:</b>\n"
							+ getReader().getMibComments(oid);
				}
				sb.append(oid);
				sb.append(close);
				sb.append(" : ");
				sb.append(vb.getVariable().toString());
				sb.append(" [");
				sb.append(vb.getVariable().getClass().getSimpleName());
				sb.append("]");
			}
			sb.append("\n\t");
			sb.append("Error Index = ");
			sb.append(received.getErrorIndex());
			sb.append(", Error Status = ");
			sb.append(received.getErrorStatus());
			sb.append(", Error Description = ");
			sb.append(received.getErrorStatusText());
		}
		if (comments != null) {
			sb.append(comments);
		}
		if (st != null) {
			sb.append("\n\n*****************************************************************************************\n<b>Stack Trace:</b>\n");
			sb.append(st);
		}
		return sb.toString();
	}

	private void set(String oid, Variable value, Variable... expectedAfter)
			throws SnmpTimeoutException, SnmpResourceUnavailableException,
			SnmpException {
		if (manager == null) {
			init();
		}

		if (expectedAfter == null || expectedAfter.length == 0) {
			expectedAfter = new Variable[] { (Variable) value.clone() };
		}
		int numOfRetries = getOperationRetries();
		SnmpException se = null;
		StringBuilder reportStr = null;
		int counter = 0;
		boolean continueFlag = false;
		do {
			if ((counter++) > 0) {
				try {
					long waitTime = getOperationRetriesTimeout();
					if (getManager().isResourceUnavailable()) {
						/**
						 * if the failure caused due to "Resource Unavailable"
						 * SNMP error, wait at least 5 seconds
						 */
						waitTime = Math.max(waitTime, 5000);
					}
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {

				}
			}
			try {
				manager.set(oid, value, expectedAfter);
				/**
				 * success set: continue
				 */
				continueFlag = true;
			} catch (SnmpException e1) {
				reportStr = getSetFailedReportStr(reportStr, oid, value);
				report.report("Snmp Set " + counter + "/" + numOfRetries
						+ " Failed: " + reportStr.toString(),
						getSetFailedStr(getStackTraceStr(e1)),
						counter < numOfRetries);
				se = e1;
				try {
					manager.analyzeAfterSet(new OID(oid), se, value,
							expectedAfter);
					/**
					 * success validating get after failure set: continue
					 * (expected value was found)
					 */
					report.report("Snmp Validating Get After Set " + counter
							+ "/" + numOfRetries
							+ " Succedded, Expected Value Was Found");
					continueFlag = true;
				} catch (Exception e2) {
					if (counter < numOfRetries) {
						report.report("Snmp Validating Get After Set "
								+ counter + "/" + numOfRetries
								+ " Failed, Expected Value Wasn't Found");
					} else {
						if (!(e2 instanceof SnmpException)) {
							se = new SnmpGetFailed(getLastPduSent(),
									getReader(), e2);
						}
						PDU setPduSent = getLastPduSent();
						PDU setPduReceived = getLastPduReceived();
						try {
							SnmpFailReason.reportFailReason(manager);
						} finally {
							manager.setLastPduSent(setPduSent);
							manager.setLastPduReceived(setPduReceived);
						}
						throw new SnmpException(
								"Snmp Set And Validating Get Failed (num of retries: "
										+ numOfRetries + ")", e2);
					}
				}
			}
		} while ((manager.isFailed() && !continueFlag)
				&& counter < numOfRetries);
		setTestAgainstObject(getLastPduReceived());
		report(se == null && !isFailed(), counter < numOfRetries,
				Operations.Set, getLastPduSent(), getLastPduReceived(), null,
				(se == null ? null : se.getMessage()));

		if (manager.isFailed()) {
			SnmpFailReason.reportFailReason(manager);
		}
	}

	private StringBuilder getSetFailedReportStr(StringBuilder reportStr,
			String oid, Variable value) {
		if (reportStr == null) {
			reportStr = new StringBuilder();
			if (getReader() != null) {
				reportStr.append(getReader().getMibActualName(oid));
				reportStr.append(" (");
			}
			reportStr.append(oid);
			if (getReader() != null) {
				reportStr.append(") ");
			}
			reportStr.append(" : ");
			reportStr.append(value.toString());
			reportStr.append(" [ ");
			reportStr.append(value.getClass().getSimpleName());
			reportStr.append(" ]");
		}
		return reportStr;
	}

	private void setAll(String header, String tableName, Variable value,
			Variable... expectedAfter) throws SnmpException {
		setAll(header, tableName, value, 0, expectedAfter);
	}

	private void setAll(String header, String tableName, Variable value,
			long delay, Variable... expectedAfter) throws SnmpException {
		boolean performWalk = false;
		String oid = tableName;
		if (tableName != null) {
			if (tableName.contains(".")) {
				tableName = getReader().getMibActualName(tableName);
			} else {
				oid = getOid(tableName);
			}
			if (header == null) {
				header = "Set All Value Of All Entries Under \"" + tableName
						+ "\" To \"" + value.toString() + "\"";
			}
			performWalk = true;
		} else if (header == null) {
			header = "Set All Value Of Previous Walk To \"" + value.toString()
					+ "\"";
		}

		startLevel(header);

		if (performWalk) {
			walk(oid);
		}
		VariableBinding[] vb = getVarBind();
		if (vb != null) {
			for (VariableBinding v : vb) {

				if (v != null) {

					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
					}

					v.setVariable((Variable) value.clone());
					setVariableBinding(v, expectedAfter);

				}
			}
		}
		stopLevel();
	}

	public void setVariableBinding(VariableBinding vb) throws SnmpException {
		setVariableBinding(vb, vb.getVariable());
	}

	public void setVariableBinding(VariableBinding vb,
			Variable... expectedAfter) throws SnmpException {
		set(vb.getOid().toString(), vb.getVariable(), expectedAfter);
	}

	public void setRowStatus(String oid, SnmpRowStatus value)
			throws SnmpException {
		set(oid, value.variable(), value.expected());
	}

	public void setInt32(String oid, int value) throws Exception {
		setInt32(oid, value, value);
	}

	public void setInt32(String oid, int value, int... expected)
			throws SnmpException {
		if (expected == null || expected.length == 0) {
			expected = new int[] { value };
		}
		Variable[] exp = new Variable[expected.length];
		for (int i = 0; i < exp.length; i++) {
			exp[i] = getInt32Variable(expected[i]);
		}
		set(oid, getInt32Variable(value), exp);
	}

	public void setGauge32(String oid, long value) throws SnmpException {
		setGauge32(oid, value, value);
	}

	public void setGauge32(String oid, long value, long... expected)
			throws SnmpException {
		if (expected == null || expected.length == 0) {
			expected = new long[] { value };
		}
		Variable[] exp = new Variable[expected.length];
		for (int i = 0; i < exp.length; i++) {
			exp[i] = getGauge32Variable(expected[i]);
		}
		set(oid, getGauge32Variable(value), exp);
	}

	public void setUInt32(String oid, long value) throws SnmpException {
		setUInt32(oid, value, value);
	}

	public void setUInt32(String oid, long value, long... expected)
			throws SnmpException {
		if (expected == null || expected.length == 0) {
			expected = new long[] { value };
		}
		Variable[] exp = new Variable[expected.length];
		for (int i = 0; i < exp.length; i++) {
			exp[i] = getUInt32Variable(expected[i]);
		}
		set(oid, getUInt32Variable(value), exp);
	}

	public void setTimeTicks(String oid, long value) throws SnmpException {
		Variable var = getTimeTicksVariable(value);
		set(oid, var, var);
	}

	public void setOctetString(String oid, String value) throws SnmpException {
		setOctetString(oid, value, value);
	}

	public void setOctetString(String oid, String value, String... expected)
			throws SnmpException {
		if (expected == null || expected.length == 0) {
			expected = new String[] { value };
		}
		Variable[] exp = new Variable[expected.length];
		for (int i = 0; i < exp.length; i++) {
			exp[i] = getOctetStringVariable(expected[i]);
		}
		set(oid, getOctetStringVariable(value), exp);
	}

	public void setOctetString(String oid, byte[] value, byte[]... expected)
			throws SnmpException {
		if (expected == null || expected.length == 0) {
			expected = new byte[][] { value };
		}
		Variable[] exp = new Variable[expected.length];
		for (int i = 0; i < exp.length; i++) {
			exp[i] = getOctetStringVariable(expected[i]);
		}
		set(oid, getOctetStringVariable(value), exp);
	}

	public void setOctetString(String oid, byte[] value) throws SnmpException {
		setOctetString(oid, value, value);
	}

	public void setIp(String oid, String ip) throws SnmpException {
		Variable var = getIpAddressVariable(ip);
		set(oid, var, var);
	}

	public void setIp(String oid, byte[] ip) throws SnmpException {
		StringBuilder addr = new StringBuilder();
		for (byte b : ip) {
			addr.append('.');
			addr.append(((int) b));
		}
		Variable var = getIpAddressVariable(addr.toString().substring(1));
		set(oid, var, var);
	}

	public void setObjectIdentifier(String oid, String oidValue)
			throws SnmpException {
		Variable var = getObjectIdentifier(oidValue);
		set(oid, var, var);
	}

	public void setObjectIdentifier(String oid, int[] oidValue)
			throws SnmpException {
		Variable var = getObjectIdentifier(oidValue);
		set(oid, var, var);
	}

	public void setRowStatusAll(String header, String oid, SnmpRowStatus value)
			throws SnmpException {
		setRowStatusAll(header, oid, value, 0);
	}

	public void setRowStatusAll(String header, String oid, SnmpRowStatus value,
			long delay) throws SnmpException {
		setRowStatusAll(header, oid, value, delay, value);
	}

	public void setRowStatusAll(String header, String oid, SnmpRowStatus value,
			SnmpRowStatus... expected) throws SnmpException {
		setRowStatusAll(header, oid, value, 0, expected);
	}

	public void setRowStatusAll(String header, String oid, SnmpRowStatus value,
			long delay, SnmpRowStatus... expected) throws SnmpException {
		if (expected == null || expected.length == 0) {
			expected = new SnmpRowStatus[] { value };
		}
		Variable[] exp = new Variable[expected.length];
		for (int i = 0; i < expected.length; i++) {
			exp[i] = expected[i].expected();
		}
		setAll(header, oid, value.variable(), delay, exp);
	}

	public void setInt32All(String header, String oid, int value)
			throws SnmpException {
		Variable var = getInt32Variable(value);
		setAll(header, oid, var, var);
	}

	public void setGauge32All(String header, String oid, long value)
			throws SnmpException {
		Variable var = getGauge32Variable(value);
		setAll(header, oid, var, var);
	}

	public void setOctetStringAll(String header, String oid, String value)
			throws SnmpException {
		Variable var = getOctetStringVariable(value);
		setAll(header, oid, var, var);
	}

	public void setOctetStringAll(String header, String oid, byte[] value)
			throws SnmpException {
		Variable var = getOctetStringVariable(value);
		setAll(header, oid, var, var);
	}

	public void setIpAll(String header, String oid, String ip)
			throws SnmpException {
		Variable var = getIpAddressVariable(ip);
		setAll(header, oid, var, var);
	}

	public void setIpAll(String header, String oid, byte[] ip)
			throws SnmpException {
		StringBuilder addr = new StringBuilder();
		for (byte b : ip) {
			addr.append('.');
			addr.append(((int) b));
		}
		Variable var = getIpAddressVariable(addr.toString().substring(1));
		setAll(header, oid, var, var);
	}

	public void initSession() throws SnmpException {
		initSession(true);
	}

	private void initSession(boolean initSession) throws SnmpException {
		if (manager != null) {
			close(false);
		} else {
			manager = new SnmpManager(true);
		}

		if (user != null) {
			manager.setUser(user.getSnmpV3User());
		}
		manager.setFailToPass(isFailToPass());
		manager.setValidateSetWithGet(isValidateSetWithGet());
		manager.setVersion(getVersion());
		manager.setCommunity(getCommunity());
		manager.setRetries(getRetries());
		manager.setTimeout(getTimeout());
		manager.setPort(getPort());
		manager.setHost(getHost());
		manager.setSnmpV3SecurityLevel(getSnmpV3SecurityLevel());
		manager.setSnmpV3SecurityName(getSnmpV3SecurityName());
		manager.setMibsDir(getMibsDir());
		manager.setAutoVersion(isAutoVersion());
		manager.setResourceUnavailableDelay(getResourceUnavailableDelay());
		manager.setResourceUnavailableRetries(getResourceUnavailableRetries());

		if (initSession) {
			manager.initSession();
		}
	}

	public void addSnmpV3User(SnmpV3User u) throws SnmpOperationNotSupported {
		manager.addSnmpV3User(u.getSnmpV3User());
	}

	public void close() {
		close(true);
	}

	public void close(boolean deepClose) {
		if (manager != null) {
			manager.close();
		}
		if (deepClose) {
			super.close();
		}
	}

	public void loadMibsToMap() throws SnmpException {
		manager.loadMibsToMap();
	}

	public void loadMibsToMap(boolean initDefaultMibCompiler)
			throws SnmpException {
		manager.loadMibsToMap(initDefaultMibCompiler);
	}

	public boolean isFailed() {
		return manager.isFailed();
	}

	public String getOid(String mibName, Object... extensions)
			throws SnmpGetOidException {
		String symbolOid = getReader().getOid(mibName);
		if (symbolOid != null) {
			StringBuilder sb = new StringBuilder(symbolOid);
			if (extensions != null && extensions.length > 0) {
				for (Object o : extensions) {
					sb.append(".");
					sb.append(o.toString());
				}
			}
			symbolOid = sb.toString();
		}
		return symbolOid;
	}

	public BasicMibCompiler getMibCompiler() {
		return manager.getMibCompiler();
	}

	public void addTrapListener(TrapListener listener) throws SnmpException {
		manager.addTrapListener(listener);
	}

	public String getMibsDir() {
		return mibsDir;
	}

	public String setMibsDir(String mibsDir) throws SnmpException {
		String prev = this.mibsDir;
		this.mibsDir = mibsDir;
		if (manager != null) {
			manager.setMibsDir(mibsDir);
		}
		return prev;
	}

	public SnmpVersion setVersion(SnmpVersion version) throws SnmpException {
		SnmpVersion prev = this.version;
		this.version = version;
		if (this.manager != null) {
			this.manager.setVersion(version);
		}
		return prev;
	}

	public SnmpVersion getVersion() {
		return version;
	}

	public int setResourceUnavailableRetries(int resourceUnavailableRetries) {
		int prev = this.resourceUnavailableRetries;
		this.resourceUnavailableRetries = resourceUnavailableRetries;
		if (this.manager != null) {
			this.manager
					.setResourceUnavailableRetries(resourceUnavailableRetries);
		}
		return prev;
	}

	public int getResourceUnavailableRetries() {
		return resourceUnavailableRetries;
	}

	public int setResourceUnavailableDelay(int resourceUnavailableDelay) {
		int prev = this.resourceUnavailableDelay;
		this.resourceUnavailableDelay = resourceUnavailableDelay;
		if (this.manager != null) {
			this.manager.setResourceUnavailableDelay(resourceUnavailableDelay);
		}
		return prev;
	}

	public int getResourceUnavailableDelay() {
		return resourceUnavailableDelay;
	}

	public String setCommunity(String community) throws SnmpException {
		String prev = this.community;
		this.community = community;
		if (this.manager != null) {
			this.manager.setCommunity(community);
		}
		return prev;
	}

	public String getCommunity() {
		return community;
	}

	public int setRetries(int retries) throws SnmpException {
		int prev = this.retries;
		this.retries = retries;
		if (this.manager != null) {
			this.manager.setRetries(retries);
		}
		return prev;
	}

	public int getRetries() {
		return retries;
	}

	public int setTimeout(int timeout) throws SnmpException {
		int prev = this.timeout;
		this.timeout = timeout;
		if (this.manager != null) {
			this.manager.setTimeout(timeout);
		}
		return prev;
	}

	public int getTimeout() {
		return timeout;
	}

	public int setPort(int port) throws SnmpException {
		int prev = this.port;
		this.port = port;
		if (this.manager != null) {
			this.manager.setPort(port);
		}
		return prev;
	}

	public int getPort() {
		return port;
	}

	public String setHost(String host) throws SnmpException {
		String prev = this.host;
		this.host = host;
		if (this.manager != null) {
			this.manager.setHost(host);
		}
		return prev;
	}

	public String getHost() {
		return host;
	}

	public boolean isSilent() {
		return silent;
	}

	public boolean setSilent(boolean silent) {
		boolean prev = this.silent;
		this.silent = silent;
		return prev;
	}

	public ResponseEvent[] getResponse() {
		LinkedList<ResponseEvent> list = manager.getResponse();
		return (list == null ? null : list.toArray(new ResponseEvent[list
				.size()]));
	}

	public LinkedList<ResponseEvent> getResponseList() {
		return manager.getResponse();
	}

	public VariableBinding[] getVarBind() throws SnmpException {
		return manager.getVarBind();
	}

	public LinkedList<VariableBinding> getVarBindList() throws SnmpException {
		return manager.getVarBindList();
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

	public boolean isLightReport() {
		return lightReport;
	}

	public boolean setLightReport(boolean lightReport) {
		boolean prev = this.lightReport;
		this.lightReport = lightReport;
		return prev;
	}

	public boolean isFailToPass() {
		return failToPass;
	}

	public boolean setFailToPass(boolean failToPass) {
		boolean prev = this.failToPass;
		this.failToPass = failToPass;
		if (manager != null) {
			manager.setFailToPass(failToPass);
		}
		return prev;
	}

	public boolean isValidateSetWithGet() {
		return validateSetWithGet;
	}

	public boolean setValidateSetWithGet(boolean validateSetWithGet) {
		boolean prev = this.validateSetWithGet;
		this.validateSetWithGet = validateSetWithGet;
		if (manager != null) {
			manager.setValidateSetWithGet(validateSetWithGet);
		}
		return prev;
	}

	private void report(Operations operation) {
		report(true, false, operation, getLastPduSent(), getLastPduReceived(),
				null, null);
	}

	private void report(boolean pass, boolean failToWarning,
			Operations operation, PDU sent, PDU received, String header,
			String message) {

		int status = (pass || isFailToPass() ? Reporter.PASS
				: (failToWarning ? Reporter.WARNING : Reporter.FAIL));

		if (!isSilent() || !pass) {
			/**
			 * report if not silent or failed with f2p false or passed with f2p
			 * true or in case of walk
			 */
			VariableBinding vb = null;
			StringBuilder sb = null;
			String oid = null;
			sb = new StringBuilder("SNMP ");
			sb.append(operation);
			sb.append(": ");
			if (header == null) {
				if (received != null) {
					vb = received.get(0);
				} else {
					vb = sent.get(0);
				}
				oid = vb.getOid().toString();
				if (getReader() != null) {
					sb.append(getReader().getMibActualName(oid));
					sb.append("  ");
				}
				sb.append(vb.toString());
				int errorStatus = received.getErrorStatus();
				if (errorStatus != SnmpError.NO_ERROR.value()) {
					sb.append(" [ Error=");
					sb.append(received.getErrorStatusText());
					sb.append(" ]");
				}
			} else {
				sb.append(header);
			}
			if (operation == Operations.Walk) {
				sb.append(" (");
				try {
					sb.append(getVarBindList().size());
				} catch (SnmpException e) {
					sb.append("Failed To Get Walk Num Of");
				}
				sb.append(" Entries)");

			}
			header = sb.toString();

			if (isLightReport()
					&& (getReader() == null || operation != Operations.Walk)
					&& (pass || message == null)) {
				report.report(header, status);
			} else {
				if (message == null) {
					if (oid == null) {
						oid = vb.getOid().toString();
					}
					if (operation == Operations.Set) {
						sb.append("\n\nSNMP Response:");
						sb.append(received.get(0).toString());
						sb.append(" [ Error=");
						sb.append(received.getErrorStatusText());
						sb.append(" ]");
					}
					sb.append("\n\n");
					sb.append(getReader().getMibComments(oid));
					message = sb.toString();
				}
				report.report(header, "<b>" + header + "</b>\n\n" + message,
						status);
			}
		}
	}

	public boolean isAutoVersion() {
		return autoVersion;
	}

	public boolean setAutoVersion(boolean autoVersion) {
		boolean prev = this.autoVersion;
		this.autoVersion = autoVersion;
		return prev;
	}

	public MibReader getReader() {
		return manager.getReader();
	}

	public SnmpTrap getTrap() {
		return manager.getTrap();
	}

	public PDU getLastPduReceived() {
		return manager.getLastPduReceived();
	}

	public PDU getLastPduSent() {
		return manager.getLastPduSent();
	}

	public int getOperationRetries() {
		return operationRetries;
	}

	public int setOperationRetries(int operationRetries) {
		int prev = this.operationRetries;
		this.operationRetries = operationRetries;
		return prev;
	}

	public long getOperationRetriesTimeout() {
		return operationRetriesTimeout;
	}

	public long setOperationRetriesTimeout(long operationRetriesTimeout) {
		long prev = this.operationRetriesTimeout;
		this.operationRetriesTimeout = operationRetriesTimeout;
		return prev;
	}

	public String getString(String oid) throws SnmpException {
		if (oid != null) {
			get(oid);
		}
		return (isFailed() ? null : getLastPduReceived().get(0).getVariable()
				.toString());
	}

	public Integer getInt(String oid) throws SnmpException {
		if (oid != null) {
			get(oid);
		}
		return (isFailed() ? null : getLastPduReceived().get(0).getVariable()
				.toInt());
	}

	public Long getLong(String oid) throws SnmpException {
		if (oid != null) {
			get(oid);
		}
		return (isFailed() ? null : getLastPduReceived().get(0).getVariable()
				.toLong());
	}

	private String formatTime(long milliseconds) {
		long time = milliseconds;
		time = (time / 1000);
		long seconds = (time % 60);
		time = (time / 60);
		long minutes = (time % 60);
		time = (time / 60);
		long hours = (time % 24);
		time = (time / 24);
		String format = Long.toString(minutes + 100).substring(1) + ":"
				+ Long.toString(seconds + 100).substring(1);
		if (hours > 0) {
			format = Long.toString(hours + 100).substring(1) + ":" + format;
		}
		if (time > 0) {
			format = Long.toString(time + 100).substring(1) + ":" + format;
		}
		format = format + " ( " + (time > 0 ? "DD:" : "")
				+ (hours > 0 ? "HH:" : "") + "MIN:SEC" + " )";
		return format;
	}

	private String getStackTraceStr(Throwable thrw) {
		StringBuffer sb = new StringBuffer();
		while (thrw != null) {
			sb.append("Exception Class : " + thrw.getClass().getName() + "\n\n");
			sb.append((thrw.getMessage() == null ? "Exception Message Is \"null\""
					: thrw.getMessage())
					+ "\n\n");
			StackTraceElement[] trace = thrw.getStackTrace();
			if (trace != null) {
				for (StackTraceElement t : trace) {
					if (t != null) {
						sb.append("\tat " + t + "\n");
					}
				}
			}
			thrw = thrw.getCause();
			if (thrw != null) {
				sb.append("\n\n\n");
			}
		}
		return sb.toString();
	}

	public SnmpV3User getUser() {
		return user;
	}

	public SnmpV3User setUser(SnmpV3User user) {
		SnmpV3User prev = this.user;
		this.user = user;
		if (this.manager != null) {
			manager.setUser(user.getSnmpV3User());
		}
		return prev;
	}

	public SnmpManager setManager(SnmpManager manager) {
		SnmpManager prev = this.manager;
		this.manager = manager;
		return prev;
	}

	public SnmpManager getManager() {
		return manager;
	}

	protected void startLevel(String level) throws SnmpException {
		try {
			report.startLevel(level, EnumReportLevel.CurrentPlace);
		} catch (Exception e) {
			throw new SnmpException("Reporter Error", e);
		}
	}

	protected void stopLevel() throws SnmpException {
		try {
			report.stopLevel();
		} catch (Exception e) {
			throw new SnmpException("Reporter Error", e);
		}
	}

	public boolean isIgnoreTypeConflict() {
		return ignoreTypeConflict;
	}

	public boolean setIgnoreTypeConflict(boolean ignoreTypeConflict) {
		boolean prev = this.ignoreTypeConflict;
		this.ignoreTypeConflict = ignoreTypeConflict;
		if (this.manager != null) {
			this.manager.setIgnoreTypeConflict(ignoreTypeConflict);
		}
		return prev;
	}

	public char getOidAndValueSeperator() {
		return manager.getOidAndValueSeperator();
	}

	private Variable getTimeTicksVariable(long value) throws SnmpException {
		try {
			return new TimeTicks(value);
		} catch (Exception e) {
			throw new SnmpException("Failed To Create New TimeTicks: Value = "
					+ value);
		}
	}

	private Variable getInt32Variable(int value) throws SnmpException {
		try {
			return new Integer32(value);
		} catch (Exception e) {
			throw new SnmpException("Failed To Create New Int32: Value = "
					+ value);
		}
	}

	private Variable getGauge32Variable(long value) throws SnmpException {
		try {
			return new Gauge32(value);
		} catch (Exception e) {
			throw new SnmpException("Failed To Create New Gauge32: Value = "
					+ value);
		}
	}

	private Variable getUInt32Variable(long value) throws SnmpException {
		try {
			return new UnsignedInteger32(value);
		} catch (Exception e) {
			throw new SnmpException("Failed To Create New UInt32: Value = "
					+ value);
		}
	}

	private Variable getOctetStringVariable(String value) throws SnmpException {
		try {
			return new OctetString(value);
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Create New OctetString: Value = " + value);
		}
	}

	private Variable getOctetStringVariable(byte[] value) throws SnmpException {
		try {
			return new OctetString(value);
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Create New OctetString: Value = "
							+ Arrays.toString(value));
		}
	}

	private Variable getIpAddressVariable(String value) throws SnmpException {
		try {
			return new IpAddress(value);
		} catch (Exception e) {
			throw new SnmpException("Failed To Create New IpAddress: Value = "
					+ value);
		}
	}

	private Variable getObjectIdentifier(String value) throws SnmpException {
		try {
			return new OID(value);
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Create New Object Identifier: Value = " + value);
		}
	}

	private Variable getObjectIdentifier(int[] value) throws SnmpException {
		try {
			return new OID(value);
		} catch (Exception e) {
			throw new SnmpException(
					"Failed To Create New Object Identifier: Value = "
							+ Arrays.toString(value));
		}
	}

}
