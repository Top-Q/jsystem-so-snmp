package systemobject.aqua.comm.snmp.exception.mibLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog.LogEntry;
import systemobject.aqua.comm.snmp.compiler.DefaultMibCompilerImpl;
import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpMibLoadException extends SnmpException {

	private static final long serialVersionUID = -6013754723461535983L;

	private List<String> info = new ArrayList<String>();

	public SnmpMibLoadException(String mibName, Throwable cause) {
		super("Mib Load Exception, Failed To Load Mib: " + mibName, cause);
		info.add(mibName + ": " + cause.getMessage());
	}

	public SnmpMibLoadException(MibLoaderException cause) {
		super(": Mib Load Exception", cause);
		Iterator<?> i = cause.getLog().entries();
		while (i.hasNext()) {
			info.add(DefaultMibCompilerImpl.buildInfo((LogEntry) i.next()));
		}
	}

	public List<String> getInfo() {
		return info;
	}

	public void setInfo(List<String> info) {
		this.info = info;
	}
}
