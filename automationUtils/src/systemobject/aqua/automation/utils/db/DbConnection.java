package systemobject.aqua.automation.utils.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;
import systemobject.aqua.automation.utils.db.report.DbUtilsReporter;

/**
 * @author Itzhak.Hovav
 */
public class DbConnection extends SystemObjectImpl {

	private String host = null;

	private int port = 3306;

	private String schema = null;

	private String user = null;

	private String password = null;

	private boolean connectOnInit = true;

	private HashMap<String, DbBasicFunctionality> allConnMap = new HashMap<String, DbBasicFunctionality>();

	public DbUtilsReporter getReport() {
		Iterator<DbBasicFunctionality> iterator = allConnMap.values()
				.iterator();
		if (iterator.hasNext()) {
			return iterator.next().getReport();
		}
		return null;
	}

	public void setReport(DbUtilsReporter report) {
		Iterator<DbBasicFunctionality> iterator = allConnMap.values()
				.iterator();
		while (iterator.hasNext()) {
			iterator.next().setReport(report);
		}
	}

	public DbConnection() {
		super();
	}

	@Override
	public void init() throws Exception {
		super.init();

		if (isConnectOnInit()) {
			getConnection();
		}
	}

	public boolean isConnected() throws SQLException {
		return isConnected(getSchema());
	}

	public boolean isConnected(String schema) throws SQLException {
		if (schema == null) {
			schema = getSchema();
		}
		return isConnected(getHost(), schema);
	}

	public boolean isConnected(String host, String schema) throws SQLException {
		if (host == null) {
			host = getHost();
		}
		if (schema == null) {
			schema = getSchema();
		}
		DbBasicFunctionality conn = getConnection(host + ":" + schema);
		return (conn != null && !conn.getConn().isClosed());
	}

	public DbBasicFunctionality getConnection() throws SQLException {
		return getConnection(getSchema());
	}

	public DbBasicFunctionality getConnection(String schema)
			throws SQLException {
		if (schema == null) {
			schema = getSchema();
		}
		return getConnection(getHost(), schema);
	}

	public DbBasicFunctionality getConnection(String host, String schema)
			throws SQLException {
		if (host == null) {
			host = getHost();
		}
		if (schema == null) {
			schema = getSchema();
		}
		DbBasicFunctionality connection = allConnMap.get(getMapKey(host,
				getPortStr(), schema, getUser(), getPassword()));
		if (connection == null || connection.getConn().isClosed()) {
			disconnect(connection);
			connection = new DbBasicFunctionality(host, getPortStr(), schema,
					getUser(), getPassword());
			allConnMap.put(
					getMapKey(host, getPortStr(), schema, getUser(),
							getPassword()), connection);
			report.report("Connected To DB:" + connection.getConn().toString());
		}
		return connection;
	}

	public void disconnect() throws SQLException {
		for (DbBasicFunctionality connection : allConnMap.values()) {
			disconnect(connection);
		}
		allConnMap.clear();
		allConnMap = null;
	}

	protected String getMapKey(String host, String port, String schema,
			String user, String password) {
		return String.format("%s:%s:%s:%s:%s", host, port, schema, user,
				password);
	}

	protected void disconnect(DbBasicFunctionality connection) {
		if (connection != null) {
			try {
				connection.closeConnection();
				report.report("Disconnected From DB: "
						+ connection.getConn().toString());
			} catch (SQLException e) {
				report.report("Failed to Disconnect From DB: "
						+ connection.getConn().toString(), Reporter.WARNING);
			}
		}
	}

	@Override
	public void close() {
		try {
			disconnect();
		} catch (Exception e) { /* do nothing */
		}

		super.close();
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public String getPortStr() {
		return Integer.toString(getPort() + 10000).substring(1);
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @deprecated use {@link #getSchema()}
	 */
	public String getScheme() {
		return getSchema();
	}

	/**
	 * @deprecated use {@link #setSchema(String)}
	 */
	public void setScheme(String schema) {
		setSchema(schema);
	}

	public String getSchema() {
		return this.schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isConnectOnInit() {
		return this.connectOnInit;
	}

	public void setConnectOnInit(boolean connectOnInit) {
		this.connectOnInit = connectOnInit;
	}

}
