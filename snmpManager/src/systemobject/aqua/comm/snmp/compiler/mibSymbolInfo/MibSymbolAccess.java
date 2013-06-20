package systemobject.aqua.comm.snmp.compiler.mibSymbolInfo;

import net.percederberg.mibble.snmp.SnmpAccess;

/**
 * This ENUM represents a MIB entry access authorization.
 * 
 * @author Itzhak.Hovav
 */
public enum MibSymbolAccess {

	NOT_AVAILABLE(null), NOT_IMPLEMENTED(SnmpAccess.NOT_IMPLEMENTED), NOT_ACCESSIBLE(
			SnmpAccess.NOT_ACCESSIBLE), ACCESSIBLE_FOR_NOTIFY(
			SnmpAccess.ACCESSIBLE_FOR_NOTIFY), READ_ONLY(SnmpAccess.READ_ONLY), READ_WRITE(
			SnmpAccess.READ_WRITE), READ_CREATE(SnmpAccess.READ_CREATE), WRITE_ONLY(
			SnmpAccess.WRITE_ONLY);

	private SnmpAccess access;

	MibSymbolAccess(SnmpAccess access) {
		this.access = access;
	}

	/**
	 * returns the matching MibSymbolAccess to the given SnmpAccess object.
	 * 
	 * @param snmpAccess
	 *            SnmpAccess object or null
	 * @return the matching MibSymbolAccess to the given SnmpAccess object or
	 *         MibSymbolAccess.NOT_AVAILABLE if the given parameter is "null"
	 */
	public static MibSymbolAccess get(SnmpAccess snmpAccess) {
		MibSymbolAccess[] arr = values();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].access == snmpAccess) {
				return arr[i];
			}
		}
		return MibSymbolAccess.NOT_AVAILABLE;
	}

	/**
	 * checks the read permissions of the current MibSymbolAccess object.
	 * 
	 * @return true if have any read permission, false if not.
	 */
	public boolean canRead() {
		return this == READ_ONLY || this == READ_WRITE || this == READ_CREATE;
	}

	/**
	 * checks the write permissions of the current MibSymbolAccess object.
	 * 
	 * @return true if have any write permission, false if not.
	 */
	public boolean canWrite() {
		return this == READ_WRITE || this == READ_CREATE || this == WRITE_ONLY;
	}
}
