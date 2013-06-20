package systemobject.aqua.comm.snmp.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.TreeSet;

import systemobject.aqua.comm.snmp.compiler.BasicMibCompiler;
import systemobject.aqua.comm.snmp.compiler.DefaultMibCompilerImpl;
import systemobject.aqua.comm.snmp.compiler.MibReader;
import systemobject.aqua.comm.snmp.compiler.MibReaderDb;
import systemobject.aqua.comm.snmp.compiler.mibSymbolInfo.MibSymbolInfo;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class SnmpMibVersionsCompare {

	public static void main(String[] args) throws Exception {
		compare("C:/work/project/automation/snmpManager/mibOld",
				"C:/work/project/automation/snmpManager/mibNew",
				"c:/compare_log.txt");
	}

	public static void compare(String mibFolder1, String mibFolder2,
			String logFileName) throws Exception {
		FileWriter logFile = null;
		try {
			int counter = 0;
			File file = new File(logFileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			logFile = new FileWriter(file, true);
			mibFolder1 = mibFolder1.replace("/", "\\");
			while (mibFolder1.endsWith("\\")) {
				mibFolder1 = mibFolder1.substring(0, mibFolder1.length() - 1);
			}
			String fold1 = mibFolder1
					.substring(mibFolder1.lastIndexOf("\\") + 1);
			mibFolder2 = mibFolder2.replace("/", "\\");
			while (mibFolder2.endsWith("\\")) {
				mibFolder2 = mibFolder2.substring(0, mibFolder2.length() - 1);
			}
			String fold2 = mibFolder2
					.substring(mibFolder2.lastIndexOf("\\") + 1);

			BasicMibCompiler mibCompiler = new DefaultMibCompilerImpl(true);
			MibReader reader1 = MibReaderDb.getReader(mibFolder1, mibCompiler);
			MibReader reader2 = MibReaderDb.getReader(mibFolder2, mibCompiler);

			HashMap<String, MibSymbolInfo> mibs1 = reader1.getMibsByName();
			HashMap<String, MibSymbolInfo> mibs2 = reader2.getMibsByName();
			HashMap<String, MibSymbolInfo> oids1 = reader1.getMibsByOid();
			HashMap<String, MibSymbolInfo> oids2 = reader2.getMibsByOid();
			TreeSet<String> inFlod1 = new TreeSet<String>();
			TreeSet<String> inFlod2 = new TreeSet<String>();

			print(logFile,
					"*************************************************************************************************************************");
			print(logFile, "**\t\tDifference Only In OID:");
			print(logFile,
					"*************************************************************************************************************************");
			counter = 0;
			for (String mib : mibs1.keySet()) {
				if (!mibs2.containsKey(mib)) {
					inFlod1.add(mib);
				} else {
					String oid1 = mibs1.get(mib).getOid();
					String oid2 = mibs2.get(mib).getOid();
					if (!oid1.equals(oid2)) {
						print(logFile, String.format(
								"MIB=%-40s: %s=%-40s, %s=%-40s", mib, fold1,
								oid1, fold2, oid2));
						counter++;
					}
				}
			}
			for (String mib : mibs2.keySet()) {
				if (!mibs1.containsKey(mib)) {
					inFlod2.add(mib);
				}
			}
			if (counter == 0) {
				print(logFile, "None");
			}
			print(logFile, "");
			print(logFile, "");

			print(logFile,
					"*************************************************************************************************************************");
			print(logFile, "**\t\tDifference Only In MIB Name:");
			print(logFile,
					"*************************************************************************************************************************");
			counter = 0;
			TreeSet<String> inFlod1temp = new TreeSet<String>();
			TreeSet<String> inFlod2temp = new TreeSet<String>();
			for (String mib : inFlod1) {
				String oid = mibs1.get(mib).getOid();
				if (oids2.containsKey(oid)) {
					print(logFile, String.format(
							"OID=%-40s, %s=%-40s, %s=%-40s", oid, fold1, mib,
							fold2, oids2.get(oid).getMibName()));
					inFlod1temp.add(mib);
					counter++;
				}
			}
			for (String mib : inFlod2) {
				String oid = mibs2.get(mib).getOid();
				if (oids1.containsKey(oid)
						&& !inFlod1temp.contains(oids1.get(oid).getMibName())) {
					print(logFile, String.format(
							"OID=%-40s, %s=%-40s, %s=%-40s", oid, fold1, oids1
									.get(oid).getMibName(), fold2, mib));
					inFlod2temp.add(mib);
					counter++;
				}
			}
			for (String s : inFlod1temp) {
				inFlod1.remove(s);
			}
			for (String s : inFlod2temp) {
				inFlod2.remove(s);
			}
			if (counter == 0) {
				print(logFile, "None");
			}
			print(logFile, "");
			print(logFile, "");

			print(logFile,
					"*************************************************************************************************************************");
			print(logFile, "**\t\tMIBs Existing Only In " + fold1 + ":");
			print(logFile,
					"*************************************************************************************************************************");
			counter = 0;
			for (String mib : inFlod1) {
				print(logFile, String.format("%-40s", mib));
				counter++;
			}
			if (counter == 0) {
				print(logFile, "None");
			}
			print(logFile, "");
			print(logFile, "");

			print(logFile,
					"*************************************************************************************************************************");
			print(logFile, "**\t\tMIBs Existing Only In " + fold2 + ":");
			print(logFile,
					"*************************************************************************************************************************");
			counter = 0;
			for (String mib : inFlod2) {
				print(logFile, String.format("%-40s", mib));
				counter++;
			}
			if (counter == 0) {
				print(logFile, "None");
			}
		} finally {
			if (logFile != null) {
				logFile.close();
			}
		}
	}

	private static void print(FileWriter logFile, String line) throws Exception {
		System.out.println(line);
		logFile.append(line);
		logFile.append("\r\n");
		logFile.flush();
	}

}
