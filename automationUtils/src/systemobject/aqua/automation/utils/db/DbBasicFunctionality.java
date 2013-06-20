package systemobject.aqua.automation.utils.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import systemobject.aqua.automation.utils.db.DBConnectionFactory.EnumDbType;
import systemobject.aqua.automation.utils.db.report.DbUtilsConsoleReporter;
import systemobject.aqua.automation.utils.db.report.DbUtilsReporter;
import systemobject.aqua.automation.utils.db.utils.condition.SqlCondition;

/**
 * @author Uri.Koaz
 */
public class DbBasicFunctionality {

	private DbUtilsReporter report = new DbUtilsConsoleReporter();

	private String paramEndDelimiter = "<paramEndDelim>";

	private String paramDelimiter = "<paramDelim>";

	private SqlConnection conn = null;

	private boolean reportQuery = true;

	private String ip = null;

	private String port = null;

	private String schema = null;

	private String userName = null;

	private String password = null;

	private String sid = null;

	private EnumDbType dbType = EnumDbType.MySql;

	public DbBasicFunctionality(String ip, String port, String schema,
			String userName, String password, String sid, EnumDbType dbType)
			throws SQLException {
		super();
		setIp(ip);
		setPort(port);
		setSchema(schema);
		setUserName(userName);
		setPassword(password);
		setSid(sid);
		setDbType(dbType);

		initConnection();
	}

	public DbBasicFunctionality(String ip, String port, String schema,
			String userName, String password, EnumDbType dbType)
			throws SQLException {
		this(ip, port, schema, userName, password, "default", dbType);
	}

	public DbBasicFunctionality(String ip, String port, String schema,
			String userName, String password) throws SQLException {
		this(ip, port, schema, userName, password, EnumDbType.MySql);
	}

	public void initConnection() throws SQLException {
		if (conn != null) {
			closeConnection();
		}
		conn = DBConnectionFactory.getInstance().getNewConnction(getIp(),
				getPort(), getSchema(), getUserName(), getPassword(), getSid(),
				getDbType());
	}

	public void closeConnection() throws SQLException {
		conn.close();
	}

	public int updateFieldInTable(String tableName, String fieldName,
			String newValue, String[] condtions) throws SQLException {
		return updateFieldsInTable(tableName, new String[] { fieldName },
				new String[] { newValue }, condtions);
	}

	public int updateFieldsInTable(String tableName, String[] fieldsNames,
			String[] newValues, String[] condtions) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(tableName);
		sb.append(" SET ");
		for (int i = 0; i < fieldsNames.length; i++) {
			sb.append(fieldsNames[i]);
			sb.append(" = '");
			sb.append(newValues[i]);
			sb.append("'");
			if (i != (fieldsNames.length - 1)) {
				sb.append(", ");
			}
		}
		sb.append(conditions(condtions, true, false));

		return executeUpdate(sb.toString());
	}

	public int insertQuery(String query) throws SQLException {
		return executeUpdate(query);
	}

	public int insertQueryReturnKey(String query) throws SQLException {
		return executeUpdateReturnKey(query);
	}

	public int insertListToTable(String tableName, List<String> data)
			throws SQLException {
		return insertListToTable(tableName, null, data);
	}

	public int insertListToTable(String tableName, String[] fields,
			List<String> data) throws SQLException {
		List<List<String>> List = new ArrayList<List<String>>(1);
		List.add(data);
		return insertListofRowsToTable(tableName, fields, List);
	}

	public int insertListofRowsToTable(String tableName, List<List<String>> data)
			throws SQLException {
		return insertListofRowsToTable(tableName, null, data);
	}

	public int insertListofRowsToTable(String tableName, String[] fields,
			List<List<String>> data) throws SQLException {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(tableName);
		if (fields != null && fields.length > 0) {
			sb.append("(");
			for (int i = 0; i < fields.length; i++) {
				sb.append("`");
				sb.append(fields[i]);
				sb.append("`");
				if (i != (fields.length - 1)) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		sb.append(" VALUES ");
		for (int i = 0; i < data.size(); i++) {

			sb.append("(");
			List<String> row = data.get(i);
			for (int j = 0; j < row.size(); j++) {

				if (row.get(j) == null
						|| ((String) row.get(j)).trim().equals("null")) {

					sb.append(row.get(j));

				} else {

					sb.append("'");
					sb.append(row.get(j).replaceAll("'", "\\\\'"));
					sb.append("'");
				}
				if (j != (row.size() - 1)) {
					sb.append(", ");
				}
			}

			sb.append(")");
			if (i != (data.size() - 1)) {
				sb.append(",");
			}
		}

		return executeUpdate(sb.toString());
	}

	public List<List<String>> getListForQuery(String query) throws SQLException {
		return getListForQuery(query, 16); // initial capacity of List is 16 by
											// default
	}

	public List<List<String>> getListForQuery(String query, int numOfLines)
			throws SQLException {
		ResultSet rs = executeQuery(query);
		int colsCount = rs.getMetaData().getColumnCount();
		List<List<String>> allData = new ArrayList<List<String>>(numOfLines);
		while (rs.next()) {
			List<String> currentRow = new ArrayList<String>();
			for (int i = 0; i < colsCount; i++) {
				currentRow.add(rs.getString(i + 1));
			}
			allData.add(currentRow);
		}
		return allData;
	}

	public int excuteListForQuery(String query) throws SQLException {
		if (isReportQuery()) {
			getReport().report(query);
		}
		Statement stmt = conn.createStatement();

		return stmt.executeUpdate(query);
	}

	public int getRowCountForQuery(String query) throws SQLException {
		ResultSet rs = executeQuery(query);
		rs.last();
		return rs.getRow();
	}

	public List<List<String>> getListForQueryAndCountData(String query,
			String addtionalText, int[] fieldsToCount,
			HashMap<String, Integer>[] results, int[] mustHaveFieldsIndex,
			String[] mustHaveStrings) throws SQLException {
		ResultSet rs = executeQuery(query + " " + addtionalText);
		int colsCount = rs.getMetaData().getColumnCount();
		List<List<String>> allData = new ArrayList<List<String>>();
		while (rs.next()) {
			List<String> currentRow = new ArrayList<String>();
			boolean enterToData = true;
			for (int i = 0; i < colsCount; i++) {
				currentRow.add(rs.getString(i + 1));
				for (int j = 0; j < fieldsToCount.length; j++) {
					if ((i + 1) == fieldsToCount[j]) {
						if (results[j] == null) {
							results[j] = new HashMap<String, Integer>();
						}
						if (results[j].get(rs.getString(i + 1)) == null) {
							results[j].put(rs.getString(i + 1), 1);
						} else {
							results[j].put(rs.getString(i + 1),
									((Integer) results[j].get(rs
											.getString(i + 1))).intValue() + 1);
						}
					}
				}
				if (mustHaveFieldsIndex != null) {
					for (int j = 0; j < mustHaveStrings.length; j++) {
						if ((i + 1) == mustHaveFieldsIndex[j]) {
							if (!mustHaveStrings[j].equals(rs.getString(i + 1))) {
								enterToData = false;
							}
						}
					}
				}
			}
			if (enterToData) {
				allData.add(currentRow);
			}
		}
		return allData;
	}

	public void getListForQueryAndCollectData(String query,
			String addtionalText, int keyField, int[] fieldsToCount,
			String delimiter, Map<String, String> results) throws SQLException {
		ResultSet rs = executeQuery(query + " " + addtionalText);
		int colsCount = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			for (int i = 0; i < colsCount; i++) {
				for (int j = 0; j < fieldsToCount.length; j++) {
					if ((i + 1) == fieldsToCount[j]) {
						if (results.get(rs.getString(keyField)) == null) {
							results.put(rs.getString(keyField),
									rs.getString(i + 1));
						} else {
							String newValue = (String) results.get(rs
									.getString(keyField));
							if (!newValue.endsWith(paramEndDelimiter)) {
								newValue += paramDelimiter;
							}
							newValue += rs.getString(i + 1);
							results.put(rs.getString(keyField), newValue);
						}
					}
				}
			}
			if (results.get(rs.getString(keyField)) != null) {
				results.put(rs.getString(keyField),
						results.get(rs.getString(keyField)) + paramEndDelimiter);
			}
		}
	}

	public List<List<String>> getAllTable(String tableName, int fromResult,
			int amount) throws SQLException {
		return getFieldsFromTable(tableName, null, fromResult, amount);
	}

	public List<List<String>> getAllTable(String tableName) throws SQLException {
		return getFieldsFromTable(tableName, null, -1, -1);
	}

	public List<List<String>> getFieldsFromTable(String tableName,
			String[] fields) throws SQLException {
		return getFieldsFromTable(tableName, fields, -1, -1);
	}

	public List<List<String>> getFieldsFromTable(String tableName,
			String[] fields, int fromResults, int amount) throws SQLException {
		return getSpecificFieldsWithConditions(tableName, fields, null,
				fromResults, amount);
	}

	public List<List<String>> getTablewithCondition(String tableName,
			String field, String condition) throws SQLException {
		return getSpecificFieldsWithConditions(tableName, null,
				new String[] { field + condition }, -1, -1);
	}

	public List<List<String>> getTablewithFewCondition(String tableName,
			String[] field, String[] condition) throws SQLException {
		return getSpecificFieldsWithConditions(tableName, null, condition, -1,
				-1);
	}

	public List<List<String>> getSpecificFieldsWithConditions(String tableName,
			String[] fields, String[] condition) throws SQLException {
		return getSpecificFieldsWithConditions(tableName, fields, condition,
				-1, -1);
	}

	public List<List<String>> getSpecificFieldsWithConditions(String tableName,
			String[] fields, String[] condition, int fromResults, int amount)
			throws SQLException {
		StringBuilder sb = new StringBuilder("SELECT ");
		if (fields == null || fields.length == 0) {
			sb.append("*");
		} else {
			for (int i = 0; i < fields.length; i++) {
				sb.append(fields[i]);
				if (i != (fields.length - 1)) {
					sb.append(",");
				}
			}
		}
		sb.append(" FROM ");
		sb.append(tableName);
		sb.append(conditions(condition, true, false));

		if (fromResults >= 0 && amount > 0) {
			sb.append(" limit " + fromResults + ", " + amount);
		}
		return getListForQuery(sb.toString());
	}

	public List<List<String>> getInnerJoinData(String leftTableName,
			String[] rightTableName, String[] fieldsToShow,
			String[] leftJoinField, String[] rightJoinfield,
			String[] conditions, String addtionalText, boolean condtionsAtEnd)
			throws SQLException {
		String allFieldsToShow = "";
		for (int i = 0; i < fieldsToShow.length; i++) {
			allFieldsToShow += fieldsToShow[i];
			if (i != fieldsToShow.length - 1) {
				allFieldsToShow += ", ";
			}
		}
		String sqlQuery = "SELECT " + allFieldsToShow + " FROM "
				+ leftTableName.split(";")[0] + " "
				+ leftTableName.split(";")[1];
		for (int i = 0; i < rightTableName.length; i++) {
			sqlQuery += " INNER JOIN " + rightTableName[i].split(";")[0] + " "
					+ rightTableName[i].split(";")[1] + " ON "
					+ leftJoinField[i] + " = " + rightJoinfield[i] + " ";
		}
		if (conditions == null) {
			return getListForQuery(sqlQuery);
		} else {
			String allConditions = conditions(conditions, true, condtionsAtEnd);
			if (!condtionsAtEnd) {
				return getListForQuery(sqlQuery + allConditions + addtionalText);
			}
			return getListForQuery(sqlQuery + addtionalText + allConditions);
		}
	}

	public String getInnerJoinDataQuery(String leftTableName,
			String[] rightTableName, String[] fieldsToShow,
			String[] leftJoinField, String[] rightJoinfield,
			String[] conditions, String addtionalText, boolean condtionsAtEnd) {
		String allFieldsToShow = "";
		for (int i = 0; i < fieldsToShow.length; i++) {
			allFieldsToShow += fieldsToShow[i];
			if (i != fieldsToShow.length - 1) {
				allFieldsToShow += ", ";
			}
		}
		String sqlQuery = "SELECT " + allFieldsToShow + " FROM "
				+ leftTableName.split(";")[0] + " "
				+ leftTableName.split(";")[1];
		for (int i = 0; i < rightTableName.length; i++) {
			sqlQuery += " INNER JOIN " + rightTableName[i].split(";")[0] + " "
					+ rightTableName[i].split(";")[1] + " ON "
					+ leftJoinField[i] + " = " + rightJoinfield[i] + " ";
		}
		if (conditions == null) {
			return sqlQuery;
		} else {
			String allConditions = conditions(conditions, true, condtionsAtEnd);
			if (!condtionsAtEnd) {
				return sqlQuery + allConditions + addtionalText;
			}
			return sqlQuery + addtionalText + allConditions;
		}
	}

	public List<List<String>> getInnerJoinDataAndCountData(
			String leftTableName, String[] rightTableName,
			String[] fieldsToShow, String[] leftJoinField,
			String[] rightJoinfield, String[] conditions, String addtionalText,
			boolean condtionsAtEnd, int[] fieldsToCount,
			HashMap<String, Integer>[] results) throws SQLException {
		return getListForQueryAndCountData(
				getInnerJoinDataQuery(leftTableName, rightTableName,
						fieldsToShow, leftJoinField, rightJoinfield,
						conditions, addtionalText, condtionsAtEnd), "",
				fieldsToCount, results, null, null);
	}

	public void getInnerJoinDataAndCollectData(String leftTableName,
			String[] rightTableName, String[] fieldsToShow,
			String[] leftJoinField, String[] rightJoinfield,
			String[] conditions, String addtionalText, boolean condtionsAtEnd,
			int keyField, int[] fieldsToCount, String delimiter,
			Map<String, String> results) throws SQLException {
		getListForQueryAndCollectData(
				getInnerJoinDataQuery(leftTableName, rightTableName,
						fieldsToShow, leftJoinField, rightJoinfield,
						conditions, addtionalText, condtionsAtEnd), "",
				keyField, fieldsToCount, delimiter, results);
	}

	public String getInConditionForEnum(Enum<?>[] enm, String field,
			String[] textToReplace, String[] replaceWith) {
		String inCondition = "";
		for (int i = 0; i < enm.length; i++) {
			if (i == 0) {
				inCondition += field + " IN (";
			}
			if (!enm[i].toString().equals("ALL")) {
				if (textToReplace != null) {
					String tempEnum = enm[i].toString();
					for (int j = 0; j < textToReplace.length; j++) {
						tempEnum = tempEnum.replace(textToReplace[j],
								replaceWith[j]);
					}
					inCondition += "'" + tempEnum + "'";
				} else {
					inCondition += "'" + enm[i].toString() + "'";
				}
				if (i != enm.length - 1) {
					inCondition += ", ";
				}
			}
			if (i == enm.length - 1) {
				inCondition += ")";
			}
		}
		return inCondition;
	}

	public String getInConditionForList(List<String> v, String field,
			String[] textToReplace, String[] replaceWith) {
		String inCondition = "";
		for (int i = 0; i < v.size(); i++) {
			if (i == 0) {
				inCondition += field + " IN (";
			}
			if (!v.get(i).toString().equals("ALL")) {
				if (textToReplace != null) {
					String tempEnum = v.get(i).toString();
					for (int j = 0; j < textToReplace.length; j++) {
						tempEnum = tempEnum.replace(textToReplace[j],
								replaceWith[j]);
					}
					inCondition += "'" + tempEnum + "'";
				} else {
					inCondition += "'" + v.get(i).toString() + "'";
				}
				if (i != v.size() - 1) {
					inCondition += ", ";
				}
			}
			if (i == v.size() - 1) {
				inCondition += ")";
			}
		}
		return inCondition;
	}

	public List<String> getTableColumnNames(String tableName) throws Exception {
		List<List<String>> List = getListForQuery("SHOW COLUMNS FROM "
				+ conn.getSchema() + "." + tableName);
		List<String> l = new ArrayList<String>();
		for (List<String> v : List) {
			l.add(v.get(0));
		}
		return l;
	}

	public int getTableColumnCount(String tableName) throws Exception {
		List<String> l = getTableColumnNames(tableName);
		return (l == null ? 0 : l.size());
	}

	public int getCountForQueryViaSql(String query) throws SQLException {
		ResultSet rs = executeQuery("SELECT count(*) AS countRows FROM ("
				+ query + ") koko");
		rs.next();
		return (rs.getInt(1));
	}

	public void deleteFromTable(String tableName, String[] condtions)
			throws SQLException {
		deleteFromTable(tableName, condtions, true);
	}

	public void deleteFromTable(String tableName, String[] condtions,
			boolean isAnd) throws SQLException {
		executeUpdate("DELETE FROM " + tableName
				+ conditions(condtions, isAnd, false));
	}

	public void copyTable(String fromTable, String toTable, String[] fields)
			throws SQLException {
		String query = "SELECT ";
		for (int i = 0; i < fields.length; i++) {
			query += fromTable + "." + fields[i];
			if (i != fields.length - 1) {
				query += ", ";
			}
		}
		query += (" FROM " + fromTable);
		List<List<String>> rows = getListForQuery(query);
		for (int i = 0; i < rows.size(); i++) {
			insertListToTable(toTable, fields, rows.get(i));
		}
	}

	public ArrayList<ArrayList<String>> getFromDb(String tableName,
			boolean distinct, String[] columns, SqlCondition[] cond,
			SqlOperator[] oper, String[] orderBy, boolean asc) throws Exception {
		StringBuilder sb = new StringBuilder("SELECT ");
		if (distinct) {
			sb.append("DISTINCT ");
		}
		sb.append(fields(columns));
		sb.append(" FROM ");
		sb.append(tableName);
		if (cond != null
				&& cond.length > 0
				&& (oper != null || ((oper == null || oper.length == 0) && cond.length == 1))) {
			sb.append(" WHERE ");
			for (int i = 0; i < cond.length; i++) {
				sb.append(cond[i].toString());
				if (i < (cond.length - 1)) {
					sb.append(" ");
					sb.append(oper[i >= oper.length ? oper.length - 1 : i]
							.toString());
					sb.append(" ");
				}
			}
		}
		if (orderBy != null && orderBy.length > 0) {
			sb.append(" ORDER BY ");
			for (int i = 0; i < orderBy.length; i++) {
				sb.append(orderBy[i].toString());
				sb.append(" ");
				sb.append(asc ? "ASC" : "DESC");
				if (i < (orderBy.length - 1)) {
					sb.append(",");
				}
			}
		}
		ResultSet rs = executeQuery(sb.toString());
		int colsCount = rs.getMetaData().getColumnCount();
		ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
		while (rs.next()) {
			ArrayList<String> currentRow = new ArrayList<String>(columns.length);
			boolean enterToData = true;
			for (int i = 0; i < colsCount; i++) {
				currentRow.add(rs.getString(i + 1));
			}
			if (enterToData) {
				allData.add(currentRow);
			}
		}
		return allData;
	}

	private String fields(String[] f) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < f.length; i++) {
			sb.append(",");
			sb.append(f[i]);
		}
		return sb.toString().substring(1);
	}

	private ResultSet executeQuery(String query) throws SQLException {
		if (isReportQuery()) {
			getReport().report(query);
		}
		Statement stmt = conn.createStatement();
		return (ResultSet) stmt.executeQuery(query);
	}

	private int executeUpdate(String query) throws SQLException {
		if (isReportQuery()) {
			getReport().report(query);
		}
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(query);
	}

	public int executeCommit() throws SQLException {
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate("commit");
	}

	private int executeUpdateReturnKey(String query) throws SQLException {
		if (isReportQuery()) {
			getReport().report(query);
		}
		PreparedStatement pstmt;
		int key = 0;

		pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();

		keys.next();
		key = keys.getInt(1);
		keys.close();

		return key;
	}

	private String conditions(String[] c, boolean isAnd, boolean condtionsAtEnd) {
		StringBuilder sb = new StringBuilder("");
		if (c != null && c.length > 0) {
			sb.append(condtionsAtEnd ? " HAVING " : " WHERE ");
			for (int i = 0; i < c.length; i++) {
				sb.append(c[i]);
				if (i != (c.length - 1)) {
					sb.append(isAnd ? " AND " : " OR ");
				}
			}
		}
		return sb.toString();
	}

	public int countRows(String tableName) throws SQLException {
		List<List<String>> v = getListForQuery("SELECT COUNT(*) FROM "
				+ tableName);

		return Integer.parseInt(v.get(0).get(0));
	}

	public SqlConnection getConn() {
		return conn;
	}

	public String getParamEndDelimiter() {
		return paramEndDelimiter;
	}

	public void setParamEndDelimiter(String paramEndDelimiter) {
		this.paramEndDelimiter = paramEndDelimiter;
	}

	public String getParamDelimiter() {
		return paramDelimiter;
	}

	public void setParamDelimiter(String paramDelimiter) {
		this.paramDelimiter = paramDelimiter;
	}

	public DbUtilsReporter getReport() {
		return this.report;
	}

	public void setReport(DbUtilsReporter report) {
		this.report = report;
	}

	public void dropTable(String tableName) throws SQLException {
		insertQuery("DROP TABLE " + tableName);
	}

	public boolean isReportQuery() {
		return reportQuery;
	}

	public void setReportQuery(boolean reportQuery) {
		this.reportQuery = reportQuery;
	}

	public String getIp() {
		return this.ip;
	}

	protected void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return this.port;
	}

	protected void setPort(String port) {
		this.port = port;
	}

	public String getSchema() {
		return this.schema;
	}

	protected void setSchema(String schema) {
		this.schema = schema;
	}

	public String getUserName() {
		return this.userName;
	}

	protected void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	public EnumDbType getDbType() {
		return this.dbType;
	}

	protected void setDbType(EnumDbType dbType) {
		this.dbType = dbType;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
}