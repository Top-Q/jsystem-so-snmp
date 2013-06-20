package systemobject.aqua.comm.snmp.exception;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpGetOidException extends SnmpException {

	private static final long serialVersionUID = -2352978650955086779L;

	public SnmpGetOidException(String mibName) {
		super("Failed To Find MIB \"" + mibName + "\" In The DB");
	}

	public SnmpGetOidException(String mibName, Throwable cause) {
		super("Failed To Find MIB \"" + mibName + "\" In The DB", cause);
	}

}
