package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpTimeoutException extends SnmpException {

	private static final long serialVersionUID = 7119032055853983357L;

	public SnmpTimeoutException(PDU sent, long timeoutMillis, int retries) {
		super("Snmp Timeout: No Reply After " + (timeoutMillis / 1000)
				+ " Sec (Total " + retries + " Attempts)", "\nSent PDU: \n\t"
				+ sent.toString());
	}

}
