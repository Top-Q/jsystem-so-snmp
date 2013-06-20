package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;

import systemobject.aqua.comm.snmp.compiler.MibReader;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpGetFailed extends SnmpOperationFailed {

	private static final long serialVersionUID = -6061461076501897842L;

	public SnmpGetFailed(PDU sentPdu, MibReader reader) {
		super("Get", sentPdu, reader);
	}

	public SnmpGetFailed(PDU sentPdu, MibReader reader, Throwable cause) {
		super("Get", sentPdu, reader, cause);
	}

}
