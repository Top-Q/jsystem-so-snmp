package systemobject.aqua.comm.snmp.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog.LogEntry;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolAccess;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfo;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfoImpl;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolStatus;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolTagType;
import systemobject.aqua.comm.snmp.exception.SnmpException;
import systemobject.aqua.comm.snmp.exception.mibLoader.SnmpLoadDbFileException;
import systemobject.aqua.comm.snmp.exception.mibLoader.SnmpMibLoadException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class DefaultMibCompilerImpl implements BasicMibCompiler {

	private static Logger log = Logger.getLogger(DefaultMibCompilerImpl.class
			.getName());

	private MibLoader loader = null;

	public DefaultMibCompilerImpl() {
		this(false);
	}

	public DefaultMibCompilerImpl(boolean init) {
		if (init) {
			try {
				init();
			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to init Default MIB Compiler");
			}
		}
	}

	public void init() throws SnmpException {
		loader = new MibLoader();
	}

	@Override
	public void initMaps(Object mib, HashMap<String, MibSymbolInfo> mibByName,
			HashMap<String, MibSymbolInfo> mibByOid) throws SnmpException {
		if (mib instanceof File) {
			String line = null;
			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader((File) mib));
				while ((line = in.readLine()) != null) {
					MibSymbolInfoImpl info = new MibSymbolInfoImpl();
					info.initFromString(line);
					mibByName.put(info.getMibName(), info);
					mibByOid.put(info.getOid(), info);
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Exception While Reading Snmp Mib DB: "
						+ e.getMessage());
				throw new SnmpLoadDbFileException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
						log.log(Level.WARNING,
								"Fail to close reader of Snmp Mib DB");
					}
				}
			}
		} else {
			try {
				Object[] arr = ((Mib) mib).getAllSymbols().toArray();
				MibValueSymbol symbol = null;
				MibValue value = null;

				MibSymbolInfoImpl info = null;
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] != null && arr[i] instanceof MibValueSymbol) {

						symbol = (MibValueSymbol) arr[i];

						value = symbol.getValue();

						// set MIB name, MIB OID and MIB description
						info = new MibSymbolInfoImpl(value.getName(),
								value.toString(), symbol.getType().toString());

						// set MIB access and status
						if (symbol.getType() instanceof SnmpObjectType) {
							info.setAccess(MibSymbolAccess
									.get(((SnmpObjectType) symbol.getType())
											.getAccess()));
							info.setStatus(MibSymbolStatus
									.get(((SnmpObjectType) symbol.getType())
											.getStatus()));

							if (info.getAccess() == MibSymbolAccess.ACCESSIBLE_FOR_NOTIFY
									|| info.getAccess() == MibSymbolAccess.READ_CREATE
									|| info.getAccess() == MibSymbolAccess.READ_ONLY
									|| info.getAccess() == MibSymbolAccess.READ_WRITE
									|| info.getAccess() == MibSymbolAccess.WRITE_ONLY) {
								// set MIB tag type
								info.setTagType(MibSymbolTagType
										.get(((SnmpObjectType) symbol.getType())
												.getSyntax()));
							}
						} else {
							info.setAccess(MibSymbolAccess.get(null));
							info.setStatus(MibSymbolStatus.get(null));
						}
						mibByName.put(info.getMibName(), info);
						mibByOid.put(info.getOid(), info);
					}
				}
			} catch (Exception e) {
				throw new SnmpMibLoadException(((Mib) mib).getName(), e);
			}
		}
	}

	public static String compile(String dir) {
		BasicMibCompiler basicMibCompiler = new DefaultMibCompilerImpl(true);
		StringBuilder pass = new StringBuilder("\nSuccessfully Loadded:\n");
		StringBuilder fail = new StringBuilder("\nFailed In Load:\n");

		StringBuilder sb = new StringBuilder();

		File f = new File(dir);
		if (!f.exists()) {
			return "Dir \"" + dir + "\" Does Not Exist, Cannot Run Compilation";
		}
		if (!f.isDirectory()) {
			return "Givan Path \"" + dir
					+ "\" Is Not a Directory, Cannot Run Compilation";
		}
		File[] mibFiles = f.listFiles();
		if (mibFiles == null) {
			return "Failed To Read Files From Dir \"" + dir
					+ "\", Cannot Run Compilation";
		}
		if (mibFiles.length == 0) {
			return "Empty Dir \"" + dir + "\", No Mib Files To Compile";
		}
		basicMibCompiler.addDir(f);
		for (int i = 0; i < mibFiles.length; i++) {
			if (mibFiles[i].isDirectory()) {
				sb.append("\n");
				sb.append(compile(mibFiles[i].getAbsolutePath()));
				sb.append("\n");
			} else {
				String mibName = mibFiles[i].getName();
				if (mibName.indexOf('.') == -1
						|| (mibName.substring(mibName.lastIndexOf('.')))
								.toLowerCase().startsWith(".m")) {
					try {
						pass.append(basicMibCompiler.load(mibFiles[i]) + "\n");
					} catch (SnmpMibLoadException e) {
						fail.append("Failed To Load " + mibName + "\n");
						for (String s : e.getInfo()) {
							fail.append(" -> " + s + "\n");
						}
					} catch (SnmpException e) {
						fail.append("Failed To Load " + mibName + ": "
								+ e.getMessage() + "\n");
					}
				}
			}
		}
		if (!fail.toString().equals("\nSuccessfully Loadded:\n")) {
			sb.append(pass);
		} else {
			sb.append("\nAll MIB files Failed During Compiled\n");
		}
		sb.append("\n");
		if (!fail.toString().equals("\nFailed In Load:\n")) {
			sb.append(fail.toString());
		} else {
			sb.append("\nAll MIB Files Compiled Successfully\n");
		}

		return sb.toString();
	}

	@Override
	public void addDir(File f) {
		loader.addDir(f);
	}

	@Override
	public Object[] getAllMibs() {
		return loader.getAllMibs();
	}

	@Override
	public String load(File f) throws SnmpMibLoadException {
		try {
			Mib m = loader.load(f);
			StringBuilder sb = new StringBuilder("MIB " + m.getName()
					+ " Loadded Successfully");
			if (m != null && m.getLog() != null && m.getLog().entries() != null) {
				Iterator<?> i = m.getLog().entries();
				while (i.hasNext()) {
					sb.append("\n\t-> ");
					sb.append(DefaultMibCompilerImpl.buildInfo((LogEntry) i
							.next()));
				}
			}
			return sb.toString();
		} catch (MibLoaderException e) {
			throw new SnmpMibLoadException(e);
		} catch (Exception e) {
			throw new SnmpMibLoadException(f.getName(), e);
		}
	}

	public static String buildInfo(LogEntry e) {
		StringBuilder sb = new StringBuilder();
		sb.append("MibName=");
		sb.append(e.getFile().getName());
		sb.append(", Line=");
		sb.append(e.getLineNumber());
		sb.append(", Column=");
		sb.append(e.getColumnNumber());
		sb.append(", Type=");
		if (e.getType() == LogEntry.ERROR) {
			sb.append("ERROR");
		} else if (e.getType() == LogEntry.INTERNAL_ERROR) {
			sb.append("INTERNAL ERROR");
		} else if (e.getType() == LogEntry.WARNING) {
			sb.append("WARNING");
		} else {
			sb.append("UNKNOWN");
		}
		sb.append(", Message=");
		sb.append(e.getMessage());

		return sb.toString();
	}

}
