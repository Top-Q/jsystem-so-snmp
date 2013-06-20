package systemobject.aqua.comm.snmp.compiler;

import java.util.HashMap;

import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class MibReaderDb {

	private static HashMap<String, MibReader> map = new HashMap<String, MibReader>();

	public static MibReader getReader(String mibsDir,
			BasicMibCompiler mibCompiler) throws SnmpException {
		MibReader reader = map.get(mibsDir);
		if (reader == null) {
			reader = new MibReader(mibsDir, mibCompiler);
			map.put(mibsDir, reader);
		}

		return reader;
	}
}
