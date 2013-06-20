package systemobject.aqua.comm.snmp.compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import net.percederberg.mibble.Mib;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfo;
import systemobject.aqua.comm.snmp.exception.SnmpException;
import systemobject.aqua.comm.snmp.exception.SnmpGetOidException;
import systemobject.aqua.comm.snmp.exception.mibLoader.SnmpMibLoadException;

/**
 * This class used for loading MIB files from specific directory and gives the
 * utility of getting a hashMap with MIB names as key and OIDs ad a value.
 * 
 * @author Itzhak.Hovav
 */
public class MibReader {

	/**
	 * MIB Compiler interface
	 */
	protected BasicMibCompiler basicMibCompiler = null;

	/**
	 * String holds the absolute path of the MIBs directory
	 */
	protected String mibsDir = null;

	/**
	 * Map of the MIBs OID and MibSymbolInfo Objects. KEY = MIB symbol OID as
	 * String, VALUE = MIB symbol details as MibSymbolInfo implementation.
	 */
	protected HashMap<String, MibSymbolInfo> mibsByOid;

	/**
	 * Map of the MIBs symbol name and MibSymbolInfo Objects. KEY = MIB symbol
	 * name as String, VALUE = MIB symbol details as MibSymbolInfo
	 * implementation.
	 */
	protected HashMap<String, MibSymbolInfo> mibsByName;

	/**
	 * local MibReader Logger
	 */
	private static Logger log = Logger.getLogger(MibReader.class.getName());

	/**
	 * Constructor for MibReader
	 * 
	 * @param mibsDir
	 *            String holds the absolute path of the MIBs directory
	 * @throws SnmpException
	 */
	public MibReader(String mibsDir, BasicMibCompiler basicMibCompiler)
			throws SnmpException {
		try {
			loadMibsToMap(mibsDir, basicMibCompiler);
		} catch (SnmpMibLoadException e) {
			StringBuilder fail = new StringBuilder("Details\n");
			for (String s : e.getInfo()) {
				fail.append(" -> " + s + "\n");
			}
			throw new SnmpException("Failed To Load Mib To Map",
					fail.toString(), e);
		}
	}

	public void saveSnmpMibDataBaseFile() throws SnmpException {
		File f = new File(mibsDir + "/DB.snmp");
		BufferedWriter out = null;
		if (f.exists()) {
			f.delete();
			f = new File(mibsDir + "/DB.snmp");
		}
		try {
			out = new BufferedWriter(new FileWriter(f));
			Iterator<MibSymbolInfo> iter = mibsByOid.values().iterator();
			while (iter.hasNext()) {
				out.write(iter.next().toDbString());
				if (iter.hasNext()) {
					out.write("\n");
				}
			}
		} catch (IOException e) {
			throw new SnmpException("Failed To Save MIBs DB", e.getMessage(), e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				throw new SnmpException("Failed To Close MIBs DB File",
						e.getMessage(), e);
			}
		}
	}

	/**
	 * compile and loads all MIB symbols from the MIB files into hashMaps. The
	 * maps located in this class.
	 * 
	 * @param mibsDir
	 *            where to find the MIB files.
	 * @param basicMibCompiler
	 *            implementation of the "BasicMibCompiler" interface for MIB
	 *            files compilation.
	 * @throws SnmpException
	 */
	public void loadMibsToMap(String mibsDir, BasicMibCompiler basicMibCompiler)
			throws SnmpException {
		this.mibsDir = mibsDir;
		this.basicMibCompiler = basicMibCompiler;
		File f = new File(mibsDir + "/DB.snmp");
		if (f.exists()) {
			this.mibsByName = new HashMap<String, MibSymbolInfo>();
			this.mibsByOid = new HashMap<String, MibSymbolInfo>();
			this.basicMibCompiler.initMaps(f, mibsByName, mibsByOid);
		} else {
			f = new File(mibsDir);
			File[] mibFiles = f.listFiles();
			this.basicMibCompiler.addDir(f);

			for (int i = 0; i < mibFiles.length; i++) {
				String mibName = mibFiles[i].getName();
				if (mibName.indexOf('.') == -1
						|| (mibName.substring(mibName.lastIndexOf('.')))
								.toLowerCase().startsWith(".m")) {
					log.fine(this.basicMibCompiler.load(mibFiles[i]));
				}
			}

			Object[] mibs = this.basicMibCompiler.getAllMibs();

			int numOfMibs = 0;
			for (int i = 0; i < mibs.length; i++) {
				numOfMibs += ((Mib) mibs[i]).getAllSymbols().size();
			}

			this.mibsByName = new HashMap<String, MibSymbolInfo>(numOfMibs);
			this.mibsByOid = new HashMap<String, MibSymbolInfo>(numOfMibs);

			for (int j = 0; j < mibs.length; j++) {
				this.basicMibCompiler.initMaps((Mib) mibs[j], mibsByName,
						mibsByOid);
			}

			saveSnmpMibDataBaseFile();
		}
	}

	/**
	 * returns the OID by the MIB symbol name - or "null" if the requested MIB
	 * does not exist in MAP
	 * 
	 * @param mibName
	 *            actual MIB symbol name
	 * @return OID ("X.X.X...X" format) according to the given MIB symbol name -
	 *         or null if the requested MIB does not exist in the MAP
	 */
	public String getOid(String mibName) throws SnmpGetOidException {
		MibSymbolInfo mib = mibsByName.get(mibName);
		if (mib == null) {
			throw new SnmpGetOidException(mibName);
		}
		return mib.getOid();
	}

	/**
	 * return MIB Symbol actual name by absolute OID (exact OID or with extra
	 * identifiers).
	 * 
	 * @param oid
	 *            MIB OID ("X.X.X...X" format)
	 * @return MIB actual name as String
	 */
	public String getMibActualName(String oid) {
		if (oid != null && oid.length() != 0 && mibsByOid != null
				&& !mibsByOid.isEmpty()) {
			if (oid.startsWith(".")) {
				oid = oid.substring(1);
			}
			if (oid.endsWith(".0")) {
				oid = oid.substring(0, oid.length() - 1);
			}
			if (oid.endsWith(".")) {
				oid = oid.substring(0, oid.length() - 1);
			}
			if (mibsByOid.containsKey(oid)) {
				return mibsByOid.get(oid).getMibName();
			} else {
				int end = oid.length();
				while (end != -1 && !mibsByOid.containsKey(oid)) {
					end = oid.lastIndexOf('.');
					if (end != -1) {
						oid = oid.substring(0, end);
					} else {
						return null;
					}
				}
				if (end != -1) {
					return mibsByOid.get(oid).getMibName();
				}
			}

		}
		return null;
	}

	/**
	 * return MIB Symbol comments ( description ).
	 * 
	 * @param oid
	 *            MIB OID ("X.X.X.X.X......X.X" format)
	 * @return comments as String
	 */
	public String getMibComments(String oid) {
		MibSymbolInfo info = getMibByFullOid(oid);
		if (info == null) {
			return null;
		}
		return info.getDescription();
	}

	/**
	 * return MIB Symbol comments ( description ).
	 * 
	 * @param symbolName
	 *            MIB actual name (symbol name)
	 * @return comments as String
	 */
	public String getMibCommentsBySymbolName(String symbolName) {
		MibSymbolInfo info = getMibByName(symbolName);
		if (info == null) {
			return null;
		}
		return info.getDescription();
	}

	/**
	 * returns the MIBs map with the MIB OID as key. MAP KEY = MIB OID
	 * ("x.x.x.x.x.x" format) as String MAP VALUE = MibSymbolInfo implementation
	 * 
	 * @return HashMap<String, MibSymbolInfo>, the MIB symbols map.
	 */
	public HashMap<String, MibSymbolInfo> getMibsByOid() {
		return mibsByOid;
	}

	/**
	 * returns the MIBs map with the MIB symbol name as key. MAP KEY = MIB
	 * symbol name as String MAP VALUE = MibSymbolInfo implementation
	 * 
	 * @return HashMap<String, MibSymbolInfo>, the MIB symbols map.
	 */
	public HashMap<String, MibSymbolInfo> getMibsByName() {
		return mibsByName;
	}

	/**
	 * returns a single MIB symbol details
	 * 
	 * @param mibName
	 *            MIB symbol name as String
	 * @return MibSymbolInfo implementation representing the MIB symbol details
	 *         or null of not found
	 */
	public MibSymbolInfo getMibByName(String mibName) {
		if (mibsByName == null) {
			return null;
		}
		return mibsByName.get(mibName);
	}

	/**
	 * returns a single MIB symbol details. note that you should pass the exact
	 * OID, if the given OID is the extended one (with an instance extension)
	 * the returned value will be null
	 * 
	 * @see #getMibByFullOid(String)
	 * @param oid
	 *            MIB symbol exact OID as String
	 * @return MibSymbolInfo implementation representing the MIB symbol details
	 *         or null of not found
	 */
	public MibSymbolInfo getMibByOid(String oid) {
		if (mibsByOid == null) {
			return null;
		}
		return mibsByOid.get(oid);
	}

	/**
	 * returns a single MIB symbol details. note that you can pass the exact OID
	 * the extended one (with an instance extension).
	 * 
	 * @see #getMibByOid(String)
	 * @param oid
	 *            MIB symbol exact or extended OID as String
	 * @return MibSymbolInfo implementation representing the MIB symbol details
	 *         or null of not found
	 */
	public MibSymbolInfo getMibByFullOid(String oid) {
		if (mibsByName == null) {
			return null;
		}
		String mibName = getMibActualName(oid);
		if (mibName == null) {
			return null;
		}
		return mibsByName.get(mibName);
	}

	public String getMibsDir() {
		return mibsDir;
	}

	public void setMibsDir(String mibsDir) {
		this.mibsDir = mibsDir;
	}

}
