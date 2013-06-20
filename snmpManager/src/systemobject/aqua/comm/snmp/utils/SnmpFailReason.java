package systemobject.aqua.comm.snmp.utils;

import java.util.LinkedList;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.VariableBinding;

import systemobject.aqua.comm.snmp.exception.SnmpFailReasonException;
import systemobject.aqua.comm.snmp.exception.SnmpGetOidException;
import systemobject.aqua.comm.snmp.manager.SnmpManager;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public abstract class SnmpFailReason {

	private static Reporter report = ListenerstManager.getInstance();

	private static String[] failureMibNameEnd = new String[] { "FailReason",
			"FailureReason", "ErrorReason", "Failure", "Error", "Fail" };

	private static String getFailureMibName(SnmpManager snmp, String oid,
			String failureMibNameEnd) throws SnmpGetOidException {
		String failReasonMib = null;
		/**
		 * get the table's OID and extract the tables comments
		 */
		String s = snmp.getReader().getMibComments(
				oid.substring(0, oid.lastIndexOf('.')) + ".0");
		if (s != null) {
			/**
			 * Get the actual failure reason MIB name for this table
			 */
			failReasonMib = s.split(failureMibNameEnd)[0];
			String[] arr = failReasonMib.split(",");
			failReasonMib = arr[arr.length - 1].trim() + failureMibNameEnd;
			/**
			 * if the found MIB name is a corrupted one, return null
			 */
			if (snmp.getReader().getOid(failReasonMib) == null) {
				failReasonMib = null;
			}
		}
		return failReasonMib;
	}

	public static void reportFailReason(SnmpManager snmp)
			throws SnmpFailReasonException {
		PDU sent = snmp.getLastPduSent();
		PDU received = snmp.getLastPduReceived();
		LinkedList<ResponseEvent> responce = snmp.getResponse();
		try {
			/**
			 * value of the current, previous and next MIB table's failure
			 * reason if any, null if no such entry
			 */
			String[] value = new String[3];
			/**
			 * OID of the current, previous and next MIB table's failure reason
			 * if any, null if no such entry
			 */
			String[] failReasonOid = new String[3];
			/**
			 * exceptions thrown during getting the current, previous and next
			 * MIB table's failure reason, null if no exception
			 */
			Exception[] exception = new Exception[3];
			/**
			 * exception that ha been thrown during getting the basic details
			 * about the failed MIB (such as getting its OID, MIB name and basic
			 * parsing
			 */
			Exception mainException = null;
			/**
			 * the MIB name of the failed MIB entry for which we are getting the
			 * failure reason
			 */
			String failedMibBase = null;

			try {
				VariableBinding vb = snmp.getLastPduReceived().get(0);
				/**
				 * get the failed MIB entry OID
				 */
				String lastOid = vb.getOid().toString();
				/**
				 * get the failed MIB entry name
				 */
				String mibName = snmp.getReader().getMibActualName(lastOid);
				/**
				 * get the failed MIB entry OID without all the extensions
				 */
				String oid = snmp.getReader().getOid(mibName);
				/**
				 * get the failed MIB entry OID extensions from the full OID
				 */
				String oidInstanceExtension = lastOid.substring(lastOid
						.indexOf(oid) + oid.length());
				if (oidInstanceExtension.startsWith(".")) {
					oidInstanceExtension = oidInstanceExtension.substring(1);
				}
				/**
				 * cut the specific MIB entry identifier (now it points to the
				 * table's base OID)
				 */
				oid = oid.substring(0, oid.lastIndexOf('.'));
				/**
				 * save the table's base OID
				 */
				failedMibBase = oid;

				try {
					/**
					 * Get the actual failure reason MIB name for this table
					 */
					String failReasonMib = null;
					for (String s : failureMibNameEnd) {
						failReasonMib = getFailureMibName(snmp, oid, s);
						if (failReasonMib != null) {
							break;
						}
					}
					/**
					 * get the failure reason OID name for this table
					 */
					failReasonOid[1] = snmp.getReader()
							.getMibByName(failReasonMib).getOid()
							+ "." + oidInstanceExtension;
					/**
					 * get the failure reason value for this table
					 */
					snmp.get(failReasonOid[1]);
					value[1] = snmp.getLastPduReceived().get(0).getVariable()
							.toString();
				} catch (Exception e1) {
					exception[1] = e1;
					try {
						/**
						 * set the table to be the previous table, if the
						 * current table is the first it throws an exception
						 */
						int tableId = Integer.parseInt(failedMibBase
								.substring(failedMibBase.lastIndexOf('.') + 1));
						if (tableId <= 1) {
							throw new Exception(
									"First MIB Table, Cannot Extract previous Table's Failure Reasson");
						} else {
							tableId--;
						}
						oid = failedMibBase.substring(0,
								failedMibBase.lastIndexOf('.'))
								+ "." + tableId;
						/**
						 * Get the actual failure reason MIB name for this table
						 */
						String failReasonMib = null;
						for (String s : failureMibNameEnd) {
							failReasonMib = getFailureMibName(snmp, oid, s);
							if (failReasonMib != null) {
								break;
							}
						}
						/**
						 * get the failure reason OID name for this table
						 */
						failReasonOid[0] = snmp.getReader()
								.getMibByName(failReasonMib).getOid()
								+ "." + oidInstanceExtension;
						/**
						 * get the failure reason value for this table
						 */
						snmp.get(failReasonOid[0]);
						value[0] = snmp.getLastPduReceived().get(0)
								.getVariable().toString();
					} catch (Exception e0) {
						exception[0] = e0;
					}
					if (exception[1] != null) {
						try {
							/**
							 * set the table to be the next table
							 */
							int tableId = Integer
									.parseInt(failedMibBase
											.substring(failedMibBase
													.lastIndexOf('.') + 1));
							tableId++;

							oid = failedMibBase.substring(0,
									failedMibBase.lastIndexOf('.'))
									+ "." + tableId;
							/**
							 * Get the actual failure reason MIB name for this
							 * table
							 */
							String failReasonMib = null;
							for (String s : failureMibNameEnd) {
								failReasonMib = getFailureMibName(snmp, oid, s);
								if (failReasonMib != null) {
									break;
								}
							}
							/**
							 * get the failure reason OID name for this table
							 */
							failReasonOid[2] = snmp.getReader()
									.getMibByName(failReasonMib).getOid()
									+ "." + oidInstanceExtension;
							/**
							 * get the failure reason value for this table
							 */
							snmp.get(failReasonOid[2]);
							value[2] = snmp.getLastPduReceived().get(0)
									.getVariable().toString();
						} catch (Exception e2) {
							exception[2] = e2;
						}
					}
				}

			} catch (Exception e) {
				mainException = e;
			} finally {
				if (mainException != null) {
					report.report(
							"Cannot Extract Failure Reason, Exception was thrown",
							mainException.getMessage(), true);
				} else {
					if (value[0] == null && value[1] == null
							&& value[2] == null) {
						report.report("Failed To Get Failure Reason From Current, previous And Next MIB Tables");
					} else {
						if (value[1] != null) {
							report.report("Current MIB Table Fail Reason: "
									+ value[1], snmp.getReader()
									.getMibComments(failReasonOid[1]), true);
						}
						if (value[0] != null) {
							report.report("Current MIB Table Fail Reason: "
									+ value[0], snmp.getReader()
									.getMibComments(failReasonOid[0]), true);
						}
						if (value[2] != null) {
							report.report("Current MIB Table Fail Reason: "
									+ value[2], snmp.getReader()
									.getMibComments(failReasonOid[2]), true);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new SnmpFailReasonException(e);
		} finally {
			/**
			 * restore previous values
			 */
			snmp.setLastPduSent(sent);
			snmp.setLastPduReceived(received);
			snmp.setResponse(responce);
		}
	}
}
