package systemobject.aqua.comm.snmp.exception;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpFailReasonException extends SnmpException {

	private static final long serialVersionUID = 7809962875411860634L;

	public SnmpFailReasonException() {
		super("Failed To Get Snmp Failure Reason");
	}

	public SnmpFailReasonException(Throwable cause) {
		super("Failed To Get Snmp Failure Reason", cause);
	}
}
