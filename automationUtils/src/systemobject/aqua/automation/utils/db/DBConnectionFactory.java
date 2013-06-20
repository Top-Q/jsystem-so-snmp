package systemobject.aqua.automation.utils.db;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Uri.Koaz
 */
public class DBConnectionFactory {

	private static DBConnectionFactory instance = new DBConnectionFactory();

	// private static final String jdbcDriverName = "com.mysql.jdbc.Driver";

	private DBConnectionFactory() {
		super();
	}

	public static DBConnectionFactory getInstance() {
		return instance;
	}

	public SqlConnection getNewConnction(String sqlServerIp,
			String dbServerPort, String schemaName, String userName,
			String password, EnumDbType dbType) throws SQLException {
		return getNewConnction(sqlServerIp, dbServerPort, schemaName, userName,
				password, "ora10", dbType);
	}

	public SqlConnection getNewConnction(String sqlServerIp,
			String dbServerPort, String schemaName, String userName,
			String password, String sId, EnumDbType dbType) throws SQLException {

		try {

			Class.forName(dbType.getDriverClass());

		} catch (ClassNotFoundException e) {

			throw new SQLException(
					"Failed To Get Current Class Object Associated With \""
							+ dbType.getDriverClass() + "\"", e);
		}

		String url = "";

		if (dbType.getDriverClass().equals(EnumDbType.MySql.getDriverClass())) {
			url = "jdbc:mysql://" + sqlServerIp + ":" + dbServerPort + "/"
					+ schemaName;
		}
		if (dbType.getDriverClass().equals(EnumDbType.Oracle.getDriverClass())) {
			url = "jdbc:oracle:thin:" + schemaName + "@//" + sqlServerIp + ":"
					+ dbServerPort + "/" + sId;
		}
		if (dbType.getDriverClass().equals(
				EnumDbType.SqlServer.getDriverClass())) {
			url = "jdbc:sqlserver://" + sqlServerIp + ";database=" + schemaName
					+ ";";
		}

		SqlConnection conn = new SqlConnection(DriverManager.getConnection(url,
				userName, password), sqlServerIp, dbServerPort, schemaName,
				userName, password, url);

		System.out.println("\nConnected to DB: " + conn.toString());

		return conn;
	}

	public SqlConnection getNewConnction(String sqlServerIp,
			String dbServerPort, String schemaName, String userName,
			String password) throws SQLException {

		return getNewConnction(sqlServerIp, dbServerPort, schemaName, userName,
				password, EnumDbType.MySql);
	}

	public enum EnumDbType {
		MySql("com.mysql.jdbc.Driver"), Oracle(
				"oracle.jdbc.driver.OracleDriver"), SqlServer(
				"com.microsoft.sqlserver.jdbc.SQLServerDriver");

		private String driverClass;

		public String getDriverClass() {
			return driverClass;
		}

		private EnumDbType(String driveClass) {
			this.driverClass = driveClass;
		}
	}

}