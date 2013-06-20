package systemobject.aqua.comm.snmp.exception;

import java.io.IOException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpException extends IOException {

	private static final long serialVersionUID = 770181242962090239L;

	public SnmpException(String prefix) {
		this("Snmp Exception, " + prefix, (String) null);
	}

	public SnmpException(String prefix, String msg) {
		super(prefix + (msg == null || msg.trim().equals("") ? "" : msg));
	}

	public SnmpException(String prefix, Throwable cause) {
		this("Snmp Exception" + prefix, null, cause);
	}

	public SnmpException(String prefix, String msg, Throwable cause) {
		super(prefix + (msg == null || msg.trim().equals("") ? "" : " " + msg),
				cause);

	}

}
