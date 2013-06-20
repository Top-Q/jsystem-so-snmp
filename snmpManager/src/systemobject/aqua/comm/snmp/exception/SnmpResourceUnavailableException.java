package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpResourceUnavailableException extends SnmpException {

	private static final long serialVersionUID = 7119032055853983357L;

	public SnmpResourceUnavailableException(PDU sent, int numOfErrors,
			long deleyBetweenRetriesMillis) {
		super("Snmp Resource Unavailable: Error Returned " + numOfErrors
				+ " Times (Delay Between Retries = "
				+ (deleyBetweenRetriesMillis / 1000) + " Sec)",
				"\nSent PDU: \n\t" + sent.toString());
	}

}
