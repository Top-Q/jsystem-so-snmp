package systemobject.aqua.automation.utils.snmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import systemobject.aqua.automation.utils.utils.string.StringUtils;
import systemobject.aqua.comm.snmp.constant.SnmpRowStatus;

/**
 * This Class Represent a single MIB table
 * 
 * @author Itzhak.Hovav
 */
public class MibTable extends HashMap<String, VariableBinding> implements
		Cloneable {

	public static final char DOT = '.';

	private static final long serialVersionUID = -5016547013244164956L;

	/**
	 * name of the table in ASCII
	 */
	private String name = null;

	/**
	 * name of the table in OCTET STRING
	 */
	private String nameOctetString = null;

	/**
	 * OID of the table's row status entry
	 */
	private String tableRowStatusOid = null;

	/**
	 * Full OID of the table's row status entry
	 */
	private String tableRowStatusFullOid = null;

	/**
	 * All tables that belongs to this table (the entries can be in different
	 * table entries)
	 */
	private String[] relatedMibTablesOid = null;

	/**
	 * entries extensions (if any)
	 */
	private String[] extensions = null;

	/**
	 * true for adding a create operation in the beginning of the
	 * VariableBinding array
	 */
	private boolean addOpenTable = true;

	/**
	 * true for adding a close operation in the end of the VariableBinding array
	 */
	private boolean addCloseTable = true;

	/**
	 * ArrayList of all entries that should be in the beginning of the
	 * VariableBinding array
	 */
	private ArrayList<VariableBinding> startEntries = null;

	/**
	 * ArrayList of all entries that should be in the end of the VariableBinding
	 * array
	 */
	private ArrayList<VariableBinding> endEntries = null;

	/**
	 * OID to exclude from the table even though they are in the
	 * "relatedMibTablesOid" tables
	 */
	private String[] excludeOid = null;

	public MibTable() {

	}

	/**
	 * CTOR
	 * 
	 * @param name
	 *            String, name of the current MIB table
	 * @param tableRowStatusOid
	 *            String, the OID of the table row status
	 * @param relatedMibTablesOid
	 *            String[], OIDs of all MIB tables related with this specific
	 *            table
	 */
	public MibTable(String name, String tableRowStatusOid,
			String[] relatedMibTablesOid) {
		this(name, tableRowStatusOid, relatedMibTablesOid, true, true, null);
	}

	/**
	 * @param name
	 *            String, name of the current MIB table
	 * @param tableRowStatusOid
	 *            String, the OID of the table row status
	 * @param relatedMibTablesOid
	 *            String[], OIDs of all MIB tables related with this specific
	 *            table
	 * @param addOpenTable
	 *            boolean, true for adding open operation to this table in the
	 *            beginning of the VariableBinding array
	 * @param addCloseTable
	 *            boolean, true for adding close operation to this table in the
	 *            end of the VariableBinding array
	 * @param excludeOid
	 *            String[], OID that should be excluded from this table even
	 *            though they are in the "relatedMibTablesOid" tables
	 */
	public MibTable(String name, String tableRowStatusOid,
			String[] relatedMibTablesOid, boolean addOpenTable,
			boolean addCloseTable, String[] excludeOid) {
		super();
		this.name = name;
		try {
			// Roman: if name IS an integer then octet string name should be as
			// name
			String[] parts = this.name.split("\\.");
			for (int i = 0; i < parts.length; i++) {
				Integer.parseInt(parts[i]);
			}
			this.nameOctetString = parts[0];
			this.name = parts[0];
		} catch (Exception e) {
			this.nameOctetString = StringUtils
					.fromTextStringToAsciiDottedString(this.name);
		}
		this.tableRowStatusOid = tableRowStatusOid;
		this.tableRowStatusFullOid = String.format("%s%c%s", tableRowStatusOid,
				DOT, nameOctetString);
		this.relatedMibTablesOid = relatedMibTablesOid;
		// Roman: extensions length should be 0 in the beginning and not 1!
		this.extensions = new String[0];
		this.addOpenTable = addOpenTable;
		this.addCloseTable = addCloseTable;
		this.startEntries = new ArrayList<VariableBinding>();
		this.endEntries = new ArrayList<VariableBinding>();

		if (excludeOid == null) {
			excludeOid = new String[0];
		}
		this.excludeOid = Arrays.copyOf(excludeOid, excludeOid.length + 1);
		this.excludeOid[this.excludeOid.length - 1] = tableRowStatusOid;

		if (this.nameOctetString != null
				&& this.nameOctetString.startsWith(".")) {
			this.nameOctetString = this.nameOctetString.substring(1);
		}

		if (this.tableRowStatusOid != null
				&& this.tableRowStatusOid.startsWith(".")) {
			this.tableRowStatusOid = this.tableRowStatusOid.substring(1);
		}

		if (this.tableRowStatusFullOid != null
				&& this.tableRowStatusFullOid.startsWith(".")) {
			this.tableRowStatusFullOid = this.tableRowStatusFullOid
					.substring(1);
		}

		for (int i = 0; this.relatedMibTablesOid != null
				&& i < this.relatedMibTablesOid.length; i++) {
			if (this.relatedMibTablesOid[i].startsWith(".")) {
				this.relatedMibTablesOid[i] = this.relatedMibTablesOid[i]
						.substring(1);
			}
		}

		for (int i = 0; this.excludeOid != null && i < this.excludeOid.length; i++) {
			if (this.excludeOid[i].startsWith(".")) {
				this.excludeOid[i] = this.excludeOid[i].substring(1);
			}
		}
		if (addOpenTable) {
			startEntries.add(getRowStatusEntry(SnmpRowStatus.CREATE_AND_WAIT));
		}
	}

	public void setRowStatusMib(String tableRowStatusOid) {
		this.tableRowStatusOid = tableRowStatusOid;
		this.tableRowStatusFullOid = String.format("%s%c%s", tableRowStatusOid,
				DOT, nameOctetString);

		startEntries.remove(getRowStatusEntry(SnmpRowStatus.CREATE_AND_WAIT));
		startEntries.add(getRowStatusEntry(SnmpRowStatus.CREATE_AND_WAIT));

		if (this.tableRowStatusOid != null
				&& this.tableRowStatusOid.startsWith(".")) {
			this.tableRowStatusOid = this.tableRowStatusOid.substring(1);
		}

		if (this.tableRowStatusFullOid != null
				&& this.tableRowStatusFullOid.startsWith(".")) {
			this.tableRowStatusFullOid = this.tableRowStatusFullOid
					.substring(1);
		}
	}

	@Override
	public Object clone() {
		MibTable o = new MibTable();
		o.name = name;
		o.nameOctetString = this.nameOctetString;
		o.tableRowStatusOid = this.tableRowStatusOid;
		o.tableRowStatusFullOid = this.tableRowStatusFullOid;
		o.relatedMibTablesOid = (this.relatedMibTablesOid == null ? null
				: Arrays.copyOf(this.relatedMibTablesOid,
						this.relatedMibTablesOid.length));
		o.extensions = (this.extensions == null ? null : Arrays.copyOf(
				this.extensions, this.extensions.length));
		o.addOpenTable = this.addOpenTable;
		o.addCloseTable = this.addCloseTable;
		o.startEntries = cloneList(this.startEntries);
		o.endEntries = cloneList(this.endEntries);
		o.excludeOid = (this.excludeOid == null ? null : Arrays.copyOf(
				this.excludeOid, this.excludeOid.length));
		cloneMap(o);
		return o;
	}

	public void copy(MibTable o) {
		this.name = o.name;
		this.nameOctetString = o.nameOctetString;
		this.tableRowStatusOid = o.tableRowStatusOid;
		this.tableRowStatusFullOid = o.tableRowStatusFullOid;
		this.relatedMibTablesOid = (o.relatedMibTablesOid == null ? null
				: Arrays.copyOf(o.relatedMibTablesOid,
						o.relatedMibTablesOid.length));
		this.extensions = (o.extensions == null ? null : Arrays.copyOf(
				o.extensions, o.extensions.length));
		this.addOpenTable = o.addOpenTable;
		this.addCloseTable = o.addCloseTable;
		this.startEntries = cloneList(o.startEntries);
		this.endEntries = cloneList(o.endEntries);
		this.excludeOid = (o.excludeOid == null ? null : Arrays.copyOf(
				o.excludeOid, o.excludeOid.length));
		cloneMap(o, this);
	}

	private void cloneMap(HashMap<String, VariableBinding> map) {
		cloneMap(this, map);
	}

	private void cloneMap(HashMap<String, VariableBinding> mapFrom,
			HashMap<String, VariableBinding> mapTo) {
		String[] keys = mapFrom.keySet().toArray(new String[mapFrom.size()]);
		for (String k : keys) {
			String key = k;
			mapTo.put(key, (VariableBinding) mapFrom.get(k).clone());
		}
	}

	private ArrayList<VariableBinding> cloneList(ArrayList<VariableBinding> list) {
		ArrayList<VariableBinding> newList = null;
		if (list != null) {
			newList = new ArrayList<VariableBinding>();
			for (VariableBinding vb : list) {
				newList.add((VariableBinding) vb.clone());
			}
		}
		return newList;
	}

	@Override
	public boolean equals(Object o) {
		MibTable t = null;
		if (o instanceof MibTable) {
			t = (MibTable) o;
		}
		if (t != null && t.getName().equals(name)) {
			VariableBinding thisVb = null;
			VariableBinding otherVb = null;
			String[] keys = t.keySet().toArray(new String[t.size()]);
			for (int i = 0; i < keys.length; i++) {
				thisVb = this.get(keys[i]);
				otherVb = t.get(keys[i]);
				if (!equals(thisVb, otherVb)) {
					if (thisVb != null && otherVb != null) {
						return false;
					}
				}
			}
			keys = this.keySet().toArray(new String[this.size()]);
			for (int i = 0; i < keys.length; i++) {
				thisVb = this.get(keys[i]);
				otherVb = t.get(keys[i]);
				if (!equals(thisVb, otherVb)) {
					if (thisVb != null && otherVb != null) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * checks if the given entry is a table row status entry
	 * 
	 * @param vb
	 *            VariableBinding, entry to check if it is a table row status
	 *            entry
	 * @return boolean, true if the given entry is a row status one
	 */
	public boolean isRowStatusEntry(VariableBinding vb) {
		if (vb != null) {
			String oid = vb.getOid().toString();
			if (oid.startsWith(".")) {
				oid = oid.substring(1);
			}
			return (oid.equals(tableRowStatusFullOid));
		}
		return false;
	}

	/**
	 * adds the given entry to the start entries ArrayList. the entry will be
	 * added in the beginning of the table creation array before all the table
	 * standard values
	 * 
	 * @param vb
	 *            VariableBinding, entry to add to the start ArrayList
	 */
	public void addStartEntry(VariableBinding vb) {
		startEntries.add(vb);
	}

	/**
	 * adds the given entry to the end entries ArrayList. the entry will be
	 * added in the end of the table creation array after all the table standard
	 * values
	 * 
	 * @param vb
	 *            VariableBinding, entry to add to the end ArrayList
	 */
	public void addEndEntry(VariableBinding vb) {
		endEntries.add(vb);
	}

	/**
	 * returns the current table name in ASCII text
	 * 
	 * @return String, current name of the table as ASCII text
	 */
	public String getName() {
		return name;
	}

	/**
	 * changes the tables name and entries OID to the new given name value
	 * 
	 * @param name
	 *            String, new name for the table as ASCII text
	 */
	public void setName(String name) {
		String oldNameOctetString = this.nameOctetString;
		this.name = name;
		try {
			Long.parseLong(this.name);
			this.nameOctetString = this.name;
		} catch (Exception e) {
			this.nameOctetString = StringUtils
					.fromTextStringToAsciiDottedString(this.name);
		}

		this.tableRowStatusFullOid = String.format("%s%c%s", tableRowStatusOid,
				DOT, nameOctetString);
		Iterator<VariableBinding> iterator = values().iterator();
		if (iterator != null) {
			while (iterator.hasNext()) {
				VariableBinding vb = iterator.next();
				String strOid = vb.getOid().toString();
				if (strOid.startsWith(".")) {
					strOid = strOid.substring(1);
				}
				if (extensions.length > 0) {
					for (int j = 0; j < extensions.length; j++) {
						String temp = (extensions[j] != null
								&& extensions[j].length() > 0 ? String.format(
								"%c%s", DOT, extensions[j]) : "");
						String temp1 = String.format("%s%s",
								oldNameOctetString, temp);
						if (strOid.endsWith(temp1)) {
							int index1 = strOid.lastIndexOf(temp1);
							strOid = String.format("%s%s%s",
									strOid.substring(0, index1),
									this.nameOctetString, temp);
							vb.setOid(new OID(strOid));
							break;
						}
					}
				} else {
					int index1 = strOid.lastIndexOf(oldNameOctetString);
					strOid = String.format("%s%s", strOid.substring(0, index1),
							this.nameOctetString);
					vb.setOid(new OID(strOid));
				}
			}
		}
		if (startEntries != null) {
			for (int i = 0; i < startEntries.size(); i++) {
				VariableBinding vb = startEntries.get(i);
				String strOid = vb.getOid().toString();
				if (strOid.startsWith(".")) {
					strOid = strOid.substring(1);
				}
				if (extensions.length > 0) {
					for (int j = 0; j < extensions.length; j++) {
						String temp = (extensions[j] != null
								&& extensions[j].length() > 0 ? String.format(
								"%c%s", DOT, extensions[j]) : "");
						String temp1 = String.format("%s%s",
								oldNameOctetString, temp);
						if (strOid.endsWith(temp1)) {
							int index1 = strOid.lastIndexOf(temp1);
							strOid = String.format("%s%s%s",
									strOid.substring(0, index1),
									this.nameOctetString, temp);
							vb.setOid(new OID(strOid));
							break;
						}
					}
				} else {
					int index1 = strOid.lastIndexOf(oldNameOctetString);
					strOid = String.format("%s%s", strOid.substring(0, index1),
							this.nameOctetString);
					vb.setOid(new OID(strOid));
				}
			}
		}
		if (endEntries != null) {
			for (int i = 0; i < endEntries.size(); i++) {
				VariableBinding vb = endEntries.get(i);
				String strOid = vb.getOid().toString();
				if (strOid.startsWith(".")) {
					strOid = strOid.substring(1);
				}
				if (extensions.length > 0) {
					for (int j = 0; j < extensions.length; j++) {
						String temp = (extensions[j] != null
								&& extensions[j].length() > 0 ? String.format(
								"%c%s", DOT, extensions[j]) : "");
						String temp1 = String.format("%s%s",
								oldNameOctetString, temp);
						if (strOid.endsWith(temp1)) {
							int index1 = strOid.lastIndexOf(temp1);
							strOid = String.format("%s%s%s",
									strOid.substring(0, index1),
									this.nameOctetString, temp);
							vb.setOid(new OID(strOid));
						}
					}
				} else {
					int index1 = strOid.lastIndexOf(oldNameOctetString);
					strOid = String.format("%s%s", strOid.substring(0, index1),
							this.nameOctetString);
					vb.setOid(new OID(strOid));
				}
			}
		}
	}

	/**
	 * checks if the given entry actually belongs to the current table or to a
	 * different table in the same row status entry
	 * 
	 * @param allTablesOidNames
	 *            String[], array of all table names as Octet String in the
	 *            current tables' row status
	 * @param value
	 *            VariableBinding, entry to check
	 * @return boolean, true if the given entry belongs to other table in the
	 *         current table row status
	 */
	public boolean isOtherTableEntry(String[] allTablesOidNames,
			VariableBinding value) {
		return isOtherTableEntry(allTablesOidNames, value.getOid().toString());
	}

	public boolean isOtherTableEntry(String[] allTablesOidNames, String oid) {
		if (allTablesOidNames != null) {
			if (oid.startsWith(".")) {
				oid = oid.substring(1);
			}
			for (int i = 0; i < allTablesOidNames.length; i++) {
				if (allTablesOidNames[i].startsWith(".")) {
					allTablesOidNames[i] = allTablesOidNames[i].substring(1);
				}
			}
			for (int i = allTablesOidNames.length - 1; i > 0; i--) {
				for (int j = 0; j < i; j++) {
					if (allTablesOidNames[j].length() < allTablesOidNames[j + 1]
							.length()) {
						String temp = allTablesOidNames[j];
						allTablesOidNames[j] = allTablesOidNames[j + 1];
						allTablesOidNames[j + 1] = temp;
					}
				}
			}
			for (int i = 0; i < relatedMibTablesOid.length; i++) {
				for (int j = 0; j < allTablesOidNames.length; j++) {
					if (subOid(oid, relatedMibTablesOid[i],
							allTablesOidNames[j]) != null) {
						return (!allTablesOidNames[j].equals(nameOctetString));
					}
				}
			}
		}
		return false;
	}

	/**
	 * checks if the given entry related to the current table (OID of the
	 * related MIB tables and not in the excluded OIDs)
	 * 
	 * @param value
	 *            VariableBinding, entry to check
	 * @return boolean, true if the given entry related to the current table
	 */
	public boolean isRelated(VariableBinding value) {
		return isRelated(value.getOid().toString());
	}

	public boolean isRelated(String oid) {
		if (oid.startsWith(".")) {
			oid = oid.substring(1);
		}
		for (int i = 0; i < relatedMibTablesOid.length; i++) {
			for (int j = 0; excludeOid != null && j < excludeOid.length; j++) {
				if (oid.startsWith(excludeOid[j])) {
					return false;
				}
			}
			if (subOid(oid, relatedMibTablesOid[i], nameOctetString) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public VariableBinding put(String key, VariableBinding value) {
		String oid = value.getOid().toString();
		if (oid.startsWith(".")) {
			oid = oid.substring(1);
		}
		key = formatMibName(oid, key);
		if (key != null) {
			return super.put(key, value);
		}

		return value;
	}

	/**
	 * format the given MIB name: if the MIB entry contains an extension - it
	 * adds the extension to the MIB name separated with '.'.
	 * 
	 * @param fullOid
	 *            String, full entry OID as OCTET STRING
	 * @param mibName
	 *            String, name of the MIB entry
	 * @return String, MIB name after format
	 */
	public String formatMibName(String fullOid, String mibName) {
		for (int i = 0; i < this.relatedMibTablesOid.length; i++) {
			String subOid = subOid(fullOid, this.relatedMibTablesOid[i],
					this.nameOctetString);
			if (subOid != null) {
				if (!subOid.equals(this.nameOctetString)) {
					int index = this.nameOctetString.length() + 1;
					String newExtension = subOid.substring(index);
					if (this.extensions == null || this.extensions.length == 0) {
						this.extensions = new String[] { newExtension };
					} else {
						for (int j = 0; j < extensions.length; j++) {
							if (this.extensions[j].equals(newExtension)) {
								break;
							} else if (j == this.extensions.length - 1) {
								String[] newExt = Arrays.copyOf(
										this.extensions,
										this.extensions.length + 1);
								newExt[newExt.length - 1] = newExtension;
								this.extensions = newExt;
							}
						}
					}
					String temp = String.format("%c%s", DOT, newExtension);
					return String.format("%s%s", mibName,
							(mibName.endsWith(temp) ? "" : temp));
				} else {
					return mibName;
				}
			}
		}
		return null;
	}

	/**
	 * returns the array of the VariableBinding object in the creation order
	 * 
	 * @return array of VariableBinding Objects
	 */
	public VariableBinding[] getValues() {
		if (addCloseTable) {
			endEntries.add(getRowStatusEntry(SnmpRowStatus.ACTIVE));
		}
		VariableBinding[] arr = new VariableBinding[size()
				+ (startEntries.isEmpty() ? 0 : startEntries.size())
				+ (endEntries.isEmpty() ? 0 : endEntries.size())];
		int index = 0;
		for (int i = 0; i < startEntries.size(); i++, index++) {
			arr[index] = startEntries.get(i);
		}

		VariableBinding[] temp = super.values().toArray(
				new VariableBinding[size()]);
		for (int i = 0; i < temp.length; i++, index++) {
			arr[index] = temp[i];
		}

		for (int i = 0; i < endEntries.size(); i++, index++) {
			arr[index] = endEntries.get(i);
		}

		return arr;
	}

	/**
	 * returns the table name as OCTET STRING
	 * 
	 * @return String, table name as OCTET STRING
	 */
	public String getNameOctetString() {
		return nameOctetString;
	}

	/**
	 * returns the table row status OID
	 * 
	 * @return String, table row status OID
	 */
	public String getTableRowStatusOid() {
		return tableRowStatusOid;
	}

	/**
	 * returns the table row status Full OID
	 * 
	 * @return String, table row status Full OID
	 */
	public String getTableRowStatusFullOid() {
		return tableRowStatusFullOid;
	}

	/**
	 * checks if the two given Variable Objects are equal
	 * 
	 * @param syntax1
	 *            Variable, to compare
	 * @param syntax2
	 *            Variable, to compare
	 * @return boolean, true if equal
	 */
	protected boolean equals(VariableBinding syntax1, VariableBinding syntax2) {
		if (syntax1 == null && syntax2 == null) {
			return true;
		} else if (syntax1 == null || syntax2 == null) {
			return false;
		}
		return syntax1.getOid().equals(syntax2.getOid())
				&& syntax1.getVariable().equals(syntax2.getVariable());
	}

	/**
	 * @param status
	 *            SnmpRowStatus, status of the requested VariableBinding
	 * @return VariableBinding, table row status operation with the requested
	 *         operation type
	 */
	protected VariableBinding getRowStatusEntry(SnmpRowStatus status) {
		return getRowStatusEntry(tableRowStatusFullOid, status);
	}

	public static VariableBinding getRowStatusEntry(String oid,
			SnmpRowStatus status) {
		return new VariableBinding(new OID(oid), new Integer32(status.value()));
	}

	/**
	 * formats and returns the section of the OID from the table OID name to the
	 * end (cuts all the starting bytes)
	 * 
	 * @param fullOid
	 *            String, entry full OID
	 * @param baseOid
	 *            String, entry base OID
	 * @param tableNameOid
	 *            String, table name as OCTET STRING
	 * @return String, the OID from the table OID name to the end
	 */
	protected String subOid(String fullOid, String baseOid, String tableNameOid) {
		if (fullOid != null && baseOid != null && tableNameOid != null) {
			if (fullOid.startsWith(".")) {
				fullOid = fullOid.substring(1);
			}
			if (fullOid.startsWith(baseOid)) {
				fullOid = fullOid.substring(baseOid.length());
				if (fullOid.startsWith(".")) {
					fullOid = fullOid.substring(1);
				}
				while (fullOid.length() >= tableNameOid.length()) {
					if (fullOid.startsWith(tableNameOid + ".")
							|| fullOid.equals(tableNameOid)) {
						return fullOid;
					}
					int index = fullOid.indexOf('.');
					if (index != -1) {
						fullOid = fullOid.substring(index + 1);
					}
				}
			}
		}
		return null;
	}

	public boolean isAddCloseTable() {
		return addCloseTable;
	}

	public void setAddCloseTable(boolean addCloseTable) {
		this.addCloseTable = addCloseTable;
	}

	public String[] getRelatedMibTablesOid() {
		return relatedMibTablesOid;
	}

	public String[] getExtensions() {
		return extensions;
	}

	public String[] getExcludeOid() {
		return excludeOid;
	}

	public boolean isAddOpenTable() {
		return addOpenTable;
	}

	public void setAddOpenTable(boolean addOpenTable) {
		this.addOpenTable = addOpenTable;
	}
}
