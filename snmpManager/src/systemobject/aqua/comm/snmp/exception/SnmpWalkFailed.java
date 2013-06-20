package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;

import systemobject.aqua.comm.snmp.compiler.MibReader;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpWalkFailed extends SnmpOperationFailed {

	private static final long serialVersionUID = -1714361516135210710L;

	public SnmpWalkFailed(PDU sentPdu, MibReader reader) {
		super("Walk", sentPdu, reader);
	}

	public SnmpWalkFailed(PDU sentPdu, MibReader reader, Throwable cause) {
		super("Walk", sentPdu, reader, cause);
	}

}
