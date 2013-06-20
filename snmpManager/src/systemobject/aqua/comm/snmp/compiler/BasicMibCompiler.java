package systemobject.aqua.comm.snmp.compiler;

import java.io.File;
import java.util.HashMap;

import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfo;
import systemobject.aqua.comm.snmp.exception.SnmpException;

/**
 * This interface allows the AQUA SNMP System Object to compile and process MIB
 * files.
 * 
 * @author Itzhak.Hovav
 */
public interface BasicMibCompiler {

	/**
	 * Add a directory containing MIB files to the compiler path
	 * 
	 * @param f
	 *            file represents a directory containing MIB files
	 */
	public void addDir(File f);

	/**
	 * load a single MIB file and perform a validity check of its content
	 * 
	 * @param f
	 *            file represents a valid MIB file
	 * @return load results
	 * @throws SnmpException
	 */
	public String load(File f) throws SnmpException;

	/**
	 * returns all MIB symbols as an Object array
	 * 
	 * @return array of Objects represents all MIB symbols loaded in the
	 *         compiler
	 */
	public Object[] getAllMibs();

	/**
	 * initiates all MIB symbols values into the given MAPs
	 * 
	 * @param mib
	 *            a single MIB file containing some MIB symbols
	 * @param mibByName
	 *            the key of the MAP is the actual symbol name and the value is
	 *            the MIB symbol MibSymbolInfo implementation.
	 * @param mibByOid
	 *            the key of the MAP is the MIB OID (X.X.X.X...X format) and the
	 *            value is the the MIB symbol MibSymbolInfo implementation.
	 */
	public void initMaps(Object mib, HashMap<String, MibSymbolInfo> mibByName,
			HashMap<String, MibSymbolInfo> mibByOid) throws SnmpException;

}
