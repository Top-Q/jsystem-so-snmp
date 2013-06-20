package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;

import systemobject.aqua.comm.snmp.compiler.MibReader;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpSetFailed extends SnmpOperationFailed {

	private static final long serialVersionUID = -3519479316287205630L;

	public SnmpSetFailed(PDU sentPdu, PDU receivedPdu, MibReader reader) {
		super("Set", sentPdu, receivedPdu, reader);
	}

	public SnmpSetFailed(PDU sentPdu, PDU receivedPdu, MibReader reader,
			Throwable cause) {
		super("Set", sentPdu, receivedPdu, reader, cause);
	}

}
