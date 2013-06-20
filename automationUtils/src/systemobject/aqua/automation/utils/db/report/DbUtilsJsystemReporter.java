package systemobject.aqua.automation.utils.db.report;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.utils.FileUtils;
import systemobject.aqua.automation.utils.db.DbBasicFunctionality;

/**
 * @author Itzhak.Hovav
 */
public class DbUtilsJsystemReporter implements DbUtilsReporter {

	private static Reporter report = ListenerstManager.getInstance();

	private static int sqlQueryCounter = 0;

	@Override
	public void report(String query) {
		boolean silent = report.isSilent();
		try {
			report.setSilent(false);
			report.report(query);
		} finally {
			report.setSilent(silent);
		}
	}

	public void startLevel(String query) throws IOException {
		boolean silent = report.isSilent();
		try {
			report.setSilent(false);
			report.startLevel(query);
		} finally {
			report.setSilent(silent);
		}
	}

	public void stopLevel() throws IOException {
		boolean silent = report.isSilent();
		try {
			report.setSilent(false);
			report.stopLevel();
		} finally {
			report.setSilent(silent);
		}
	}

	public void printSelectQueryResult(DbBasicFunctionality conn, String host,
			String schema, String query, Vector<Vector<String>> v)
			throws Exception {
		String upperCaseQuery = query.toUpperCase();
		String tableName = upperCaseQuery.substring(
				upperCaseQuery.indexOf(" FROM ") + " FROM ".length()).trim();
		tableName = tableName.substring(
				0,
				tableName.contains(" WHERE ") ? tableName.indexOf(" WHERE ")
						: tableName.length()).trim();
		String content = formatSqlResults(query, tableName, v);
		String fileName = ("SelectFrom_" + tableName + "_QueryResult_"
				+ +(++sqlQueryCounter) + ".txt");
		File currentTestDir = new File(report.getCurrentTestFolder());
		FileUtils.write(currentTestDir.getPath() + "/" + fileName, content);
		String fileLocation = currentTestDir.getAbsolutePath();
		String currentFolderLocation = currentTestDir.getParent();
		fileLocation = fileLocation.substring(
				fileLocation.lastIndexOf(currentFolderLocation)
						+ currentFolderLocation.length() + 1)
				.replace('\\', '/');
		boolean silent = report.isSilent();
		try {
			report.setSilent(false);
			report.addLink(reportLinkHeader(fileName, v), fileLocation + "/"
					+ fileName);
		} finally {
			report.setSilent(silent);
		}
	}

	private String reportLinkHeader(String fileName, Vector<Vector<String>> v)
			throws Exception {
		StringBuilder sb = new StringBuilder("SQL Query");
		sb.append(" (File Name \"");
		sb.append(fileName);
		sb.append("\", Returned ");
		sb.append(v.size());
		sb.append(" Line");
		if (v.size() != 1) {
			sb.append("s");
		}
		sb.append(")");
		return sb.toString();
	}

	private String formatSqlResults(String query, String tableName,
			Vector<Vector<String>> v) throws Exception {

		StringBuilder sb = new StringBuilder(query);
		query = query.toUpperCase();
		int numOfEntries = v.size();
		int numOfColumns = (numOfEntries == 0 ? 0 : v.get(0).size());
		sb.append("\n\nReturned ");
		sb.append(numOfEntries);
		sb.append(" Line");
		if (numOfEntries != 1) {
			sb.append("s");
		}
		sb.append(":\n");
		if (numOfEntries > 0) {
			query = query.substring(0, query.indexOf(" FROM ")).trim();
			if (query.contains(" DISTINCT ")) {
				query = query.substring(query.indexOf(' ')).trim();
			}
			query = query.substring(query.indexOf(' ')).trim();
			String[] col = query.split("\\,");
			if (col.length < numOfColumns) {
				int prevLength = col.length;
				col = Arrays.copyOf(col, numOfColumns);
				for (int i = prevLength; i < col.length; i++) {
					col[i] = "";
				}
			}
			int[] size = new int[numOfColumns];
			for (int i = 0; i < size.length; i++) {
				size[i] = (i >= col.length || col[i] == null ? "null" : col[i])
						.length();
			}
			for (Vector<String> vctr : v) {
				for (int i = 0; i < numOfColumns; i++) {
					String s = vctr.get(i);
					size[i] = Math.max(size[i],
							(s == null ? "null" : s).length());
				}
			}
			StringBuilder format = new StringBuilder("\n");
			for (int i : size) {
				format.append("|%-");
				format.append(i);
				format.append("s");
			}
			format.append("|");

			String f = format.toString();
			if (!col[0].equals("*")) {
				sb.append(String.format(f, (Object[]) col));
			}
			for (Vector<String> vctr : v) {
				String[] line = vctr.toArray(new String[numOfColumns]);
				for (int i = 0; i < line.length; i++) {
					if (line[i] != null) {
						line[i] = line[i].replace('\n', ' ').replace('\r', ' ');
					} else {
						line[i] = "null";
					}
				}
				sb.append(String.format(f, (Object[]) line));
			}
		}
		return sb.toString();
	}

}
