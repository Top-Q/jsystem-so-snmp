package systemobject.aqua.comm.snmp.constant.v3;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public enum SecurityLevel {
	NO_AUTH_NO_PRIV(org.snmp4j.security.SecurityLevel.NOAUTH_NOPRIV), AUTH_NO_PRIV(
			org.snmp4j.security.SecurityLevel.AUTH_NOPRIV), AUTH_PRIV(
			org.snmp4j.security.SecurityLevel.AUTH_PRIV);

	private int value;

	SecurityLevel(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}
}
