package systemobject.aqua.comm.snmp.utils.compiler.db;

import java.io.File;
import java.util.Collection;

import jsystem.utils.FileUtils;
import systemobject.aqua.automation.utils.db.DbBasicFunctionality;
import systemobject.aqua.automation.utils.db.report.DbUtilsNoReporter;
import systemobject.aqua.comm.snmp.compiler.DefaultMibCompilerImpl;
import systemobject.aqua.comm.snmp.compiler.MibReader;
import systemobject.aqua.comm.snmp.compiler.db.EnumMibDbFields;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfo;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpMibDbImporter {

	private static String MIB_ROOT_FOLDER = "C:/work/project/automation/snmpManager/mib";

	private static String HOST = "192.168.178.101";

	private static String PORT = "3306";

	private static String SCHEMA = "mib";

	private static String USER = "aqua";

	private static String PASSWORD = "jsystem01";

	public static void main(String[] args) throws Exception {

		File dir = new File(MIB_ROOT_FOLDER);
		String[] mibFolder = FileUtils.listDirs(dir);

		DbBasicFunctionality db = null;
		try {
			db = new DbBasicFunctionality(HOST, PORT, SCHEMA, USER, PASSWORD);
			for (String mibDir : mibFolder) {
				if (mibDir.toLowerCase().startsWith("rel")) {
					System.out.print("Compile Mib Folder " + mibDir
							+ "...........");
					MibReader reader = null;
					try {
						reader = new MibReader(MIB_ROOT_FOLDER + "/" + mibDir,
								new DefaultMibCompilerImpl(true));
						System.out.print("Done...........");

						db.setReport(new DbUtilsNoReporter());
						Collection<MibSymbolInfo> values = reader
								.getMibsByOid().values();
						for (MibSymbolInfo val : values) {
							String query = buildQuery(mibDir, mibDir, val);
							try {
								db.insertQuery(query);
							} catch (Exception e) {
								throw new Exception(
										"Failed While Running The Following Query: \n\n"
												+ query, e);
							}
						}

						values = null;

						System.out
								.println("All Values Were Successfully Inserted Into DB.");

					} catch (Exception e) {
						System.out.println("Failed!!!!!\n");
						throw e;
					}

					reader = null;

					System.gc();
				}
			}
		} finally {
			if (db != null) {
				db.closeConnection();
				System.out.println("Db Connection successfully closed");
				db = null;
			}
		}
	}

	public static String buildQuery(String mibDir, String format,
			MibSymbolInfo val) {

		StringBuilder queryFormat = new StringBuilder("INSERT INTO ");
		queryFormat.append(SCHEMA);
		queryFormat.append(".");
		queryFormat.append(mibDir);
		queryFormat.append(" (");
		StringBuilder queryValues = new StringBuilder(" VALUES (");
		for (EnumMibDbFields e : EnumMibDbFields.values()) {
			queryFormat.append("`");
			queryFormat.append(e.name());
			queryFormat.append("`,");

			queryValues.append("'");
			queryValues.append(e.getValue(val));
			queryValues.append("',");
		}
		queryFormat = queryFormat.deleteCharAt(queryFormat.length() - 1);
		queryFormat.append(")");
		queryFormat.append(queryValues.deleteCharAt(queryValues.length() - 1));
		queryFormat.append(");");
		return queryFormat.toString();
	}

}
