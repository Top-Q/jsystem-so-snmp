package systemobject.aqua.comm.snmp.compiler.mibSymbolInfo;

import net.percederberg.mibble.snmp.SnmpStatus;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum MibSymbolStatus {

	NOT_AVAILABLE(null), MANDATORY(SnmpStatus.MANDATORY), OPTIONAL(
			SnmpStatus.OPTIONAL), CURRENT(SnmpStatus.CURRENT), DEPRECATED(
			SnmpStatus.DEPRECATED), OBSOLETE(SnmpStatus.OBSOLETE);

	private SnmpStatus status;

	MibSymbolStatus(SnmpStatus status) {
		this.status = status;
	}

	/**
	 * returns the matching MibSymbolStatus to the given SnmpStatus object.
	 * 
	 * @param snmpStatus
	 *            SnmpStatus object or null
	 * @return the matching MibSymbolStatus to the given SnmpStatus object or
	 *         MibSymbolStatus.NOT_AVAILABLE if the given parameter is "null"
	 */
	public static MibSymbolStatus get(SnmpStatus snmpStatus) {
		MibSymbolStatus[] arr = values();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].status == snmpStatus) {
				return arr[i];
			}
		}
		return MibSymbolStatus.NOT_AVAILABLE;
	}

}
