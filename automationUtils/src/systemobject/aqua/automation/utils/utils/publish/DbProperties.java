package systemobject.aqua.automation.utils.utils.publish;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import jsystem.framework.DBProperties;
import jsystem.utils.FileUtils;

/**
 * @author Itzhak.Hovav
 */
public enum DbProperties {

	DB_HOST("db.host", "192.168.178.101", "localhost", "127.0.0.1"), DB_PORT(
			"db.port", "3306"), DB_TYPE("db.type", "mysql"), DB_DBNAME(
			"db.dbname", "jsystem"), SERVER_IP("serverIP", "192.168.178.101",
			"localhost", "127.0.0.1"), BROWSER_PORT("browser.port", "8080"), DB_DRIVER(
			"db.driver", "com.mysql.jdbc.Driver"), DB_USER("db.user", "aqua",
			"root"), DB_PASSWORD("db.password", "jsystem01", "root"),
	// /////////////////////////RAS/////////////////////////////////////
	RAS_DB_HOST("ras.db.host", "192.168.178.101", "localhost", "127.0.0.1"), RAS_DB_PORT(
			"ras.db.port", "3306"), RAS_DB_DBNAME("ras.db.dbname", "ras1"), RAS_DB_USER(
			"ras.db.user", "aqua", "root"), RAS_DB_PASSWORD("ras.db.password",
			"jsystem01", "root"),
	// /////////////////////////FTP/////////////////////////////////////
	FTP_HOST("ftp.host", "192.168.178.101", "localhost", "127.0.0.1"), FTP_USER(
			"ftp.user", "aqua", "root"), FTP_PASSWORD("ftp.password",
			"jsystem01", "root");

	private String prop = null;

	private String defValue = null;

	private String[] overwriteValues = null;

	private static DBProperties dbProp = null;

	private static boolean dataWasInit = false;

	DbProperties(String prop, String defValue, String... overwriteValues) {
		this.prop = prop;
		this.defValue = defValue;
		this.overwriteValues = overwriteValues;
	}

	public String getValue() {
		try {
			Object o = getDbProp().getProperty(this.prop);
			if (o != null) {
				o = o.toString();
			} else {
				o = ((String) null);
			}
			return (String) o;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return getValue();
	}

	private DBProperties getDbProp() {
		initBasicDbPropFile();
		return dbProp;
	}

	private static void initBasicDbPropFile() {

		if (!dataWasInit) {
			dataWasInit = true;

			File dbFile = new File(DBProperties.DB_PROPERTIES_FILE);
			if (!dbFile.exists()) {
				try {
					dbFile.createNewFile();
				} catch (IOException ioe) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						if (!dbFile.exists()) {
							try {
								dbFile.createNewFile();
							} catch (IOException ioe2) {
								ioe.printStackTrace();
							}
						}
					}
				}
			}

			if (dbProp == null) {
				try {
					dbProp = DBProperties.getInstance();
				} catch (Exception e) {
					try {
						Thread.sleep(1000);
						dbProp = DBProperties.getInstance();
					} catch (Exception e2) {
						dbProp = null;
					}
				}
			}

			/**
			 * build the full map of values for the db.properties file, taking
			 * missing and non-valid (overwrite) values under consideration and
			 * replace them with default values.
			 */
			DbProperties[] arr = values();
			HashMap<String, String> map = new HashMap<String, String>(
					arr.length * 2);
			for (DbProperties p : arr) {
				String val = p.getValue();
				// empty string should be overwritten
				boolean overwrite = (val == null || "".equals(val.trim()));
				if (!overwrite && p.overwriteValues != null
						&& p.overwriteValues.length > 0) {
					for (String s : p.overwriteValues) {
						// value was specified as a value that should be
						// overwritten
						if (s != null && s.trim().equalsIgnoreCase(val.trim())) {
							overwrite = true;
							break;
						}
					}
				}
				if (overwrite) {
					val = p.defValue;
				}
				map.put(p.prop, val);
			}

			Properties prop = new Properties();
			for (String key : map.keySet()) {
				prop.setProperty(key, map.get(key));
			}

			try {
				FileUtils.savePropertiesToFile(prop,
						DBProperties.DB_PROPERTIES_FILE);
			} catch (Exception e1) {
				try {
					FileUtils.savePropertiesToFile(prop,
							DBProperties.DB_PROPERTIES_FILE);
				} catch (Exception e2) {
				}
			}

			try {
				dbProp = DBProperties.getInstance();
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
					dbProp = DBProperties.getInstance();
				} catch (Exception e2) {
					dbProp = null;
				}
			}
		}
	}
}
