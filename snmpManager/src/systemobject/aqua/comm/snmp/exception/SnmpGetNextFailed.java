package systemobject.aqua.comm.snmp.exception;

import org.snmp4j.PDU;

import systemobject.aqua.comm.snmp.compiler.MibReader;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpGetNextFailed extends SnmpOperationFailed {

	private static final long serialVersionUID = 7618945733280017600L;

	public SnmpGetNextFailed(PDU sentPdu, MibReader reader) {
		super("Get-Next", sentPdu, reader);
	}

	public SnmpGetNextFailed(PDU sentPdu, MibReader reader, Throwable cause) {
		super("Get-Next", sentPdu, reader, cause);
	}

}
