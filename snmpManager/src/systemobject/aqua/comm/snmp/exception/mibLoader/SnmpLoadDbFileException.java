package systemobject.aqua.comm.snmp.exception.mibLoader;

import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpLoadDbFileException extends SnmpException {

	private static final long serialVersionUID = -6451206440032206444L;

	public SnmpLoadDbFileException(Throwable cause) {
		super("Load Snmp DB File Exception, Failed to load DB file", cause);
	}

}
