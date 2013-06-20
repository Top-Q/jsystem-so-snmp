package systemobject.aqua.comm.snmp.exception;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpOperationNotSupported extends SnmpException {

	private static final long serialVersionUID = 1320306570659632933L;

	public SnmpOperationNotSupported() {
		super("Snmp Operation Not Supported");
	}

	public SnmpOperationNotSupported(Throwable cause) {
		super("Snmp Operation Not Supported", cause);
	}

	public SnmpOperationNotSupported(String message) {
		super("Snmp Operation Not Supported", message);
	}

	public SnmpOperationNotSupported(String message, Throwable cause) {
		super("Snmp Operation Not Supported", message, cause);
	}
}
