package systemobject.aqua.automation.utils.db;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * @author Uri.Koaz
 */
public class SqlConnection implements Connection {

	private Connection conn = null;

	private String host = null;

	private String port = "3306";

	private String schema = null;

	private String user = null;

	private String password = null;

	private String url = null;

	public SqlConnection(Connection conn, String host, String port,
			String schema, String user, String password, String url) {
		this.conn = conn;
		setHost(host);
		setPort(port);
		setSchema(schema);
		setUser(user);
		setPassword(password);
		setUrl(url);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Host:");
		sb.append(getHost());
		sb.append(", Port:");
		sb.append(getPort());
		sb.append(", Schema:");
		sb.append(getSchema());
		sb.append(", User:");
		sb.append(getUser());
		sb.append(", Password:");
		sb.append(getPassword());
		return sb.toString();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.conn.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.conn.isWrapperFor(iface);
	}

	public Statement createStatement() throws SQLException {
		return this.conn.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.conn.prepareCall(sql);
	}

	public String nativeSQL(String sql) throws SQLException {
		return this.conn.nativeSQL(sql);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.conn.setAutoCommit(autoCommit);
	}

	public boolean getAutoCommit() throws SQLException {
		return this.conn.getAutoCommit();
	}

	public void commit() throws SQLException {
		this.conn.commit();
	}

	public void rollback() throws SQLException {
		this.conn.rollback();
	}

	public void close() throws SQLException {
		this.conn.close();
	}

	public boolean isClosed() throws SQLException {
		return this.conn.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return this.conn.getMetaData();
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		this.conn.setReadOnly(readOnly);
	}

	public boolean isReadOnly() throws SQLException {
		return this.conn.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException {
		this.conn.setCatalog(catalog);
	}

	public String getCatalog() throws SQLException {
		return this.conn.getCatalog();
	}

	public void setTransactionIsolation(int level) throws SQLException {
		this.conn.setTransactionIsolation(level);
	}

	public int getTransactionIsolation() throws SQLException {
		return this.conn.getTransactionIsolation();
	}

	public SQLWarning getWarnings() throws SQLException {
		return this.conn.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		this.conn.clearWarnings();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return this.conn.createStatement(resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return this.conn.prepareStatement(sql, resultSetType,
				resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.conn.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.conn.setTypeMap(map);
	}

	public void setHoldability(int holdability) throws SQLException {
		this.conn.setHoldability(holdability);
	}

	public int getHoldability() throws SQLException {
		return this.conn.getHoldability();
	}

	public Savepoint setSavepoint() throws SQLException {
		return this.conn.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return this.conn.setSavepoint(name);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		this.conn.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.conn.releaseSavepoint(savepoint);
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.conn.createStatement(resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.conn.prepareStatement(sql, resultSetType,
				resultSetConcurrency, resultSetHoldability);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return this.conn.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		return this.conn.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		return this.conn.prepareStatement(sql, columnNames);
	}

	public Clob createClob() throws SQLException {
		return this.conn.createClob();
	}

	public Blob createBlob() throws SQLException {
		return this.conn.createBlob();
	}

	public NClob createNClob() throws SQLException {
		return this.conn.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return this.conn.createSQLXML();
	}

	public boolean isValid(int timeout) throws SQLException {
		return this.conn.isValid(timeout);
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		this.conn.setClientInfo(name, value);
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		this.conn.setClientInfo(properties);
	}

	public String getClientInfo(String name) throws SQLException {
		return this.conn.getClientInfo(name);
	}

	public Properties getClientInfo() throws SQLException {
		return this.conn.getClientInfo();
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return this.conn.createArrayOf(typeName, elements);
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return this.conn.createStruct(typeName, attributes);
	}

	public Connection getConn() {
		return this.conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
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

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
