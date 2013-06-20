package systemobject.aqua.comm.snmp.compiler.mibSymbolInfo;

/**
 * Implementation of the MibSymbolInfo interface. suitable for the
 * DefaultMibCompilerImpl class.
 * 
 * @author Itzhak.Hovav
 */
public class MibSymbolInfoImpl implements MibSymbolInfo {

	private static final String NEW_LINE_REPLACEMENT = "<;NL;>";

	private static final String FIELDS_SEPERATOR = "<;SP;>";

	private static final String FIELDS_SEPERATOR_REGEX = "\\<\\;SP\\;\\>";

	/**
	 * MIB symbol name as String
	 */
	private String mibName = null;

	/**
	 * MIB symbol OID as String
	 */
	private String oid = null;

	/**
	 * MIB symbol description (comments) as String
	 */
	private String description = null;

	/**
	 * MIB symbol access authorization as MibSymbolAccess ENUM
	 */
	private MibSymbolAccess access = MibSymbolAccess.NOT_AVAILABLE;

	/**
	 * MIB symbol tag type as MibSymbolTagType ENUM
	 */
	private MibSymbolTagType tagType = MibSymbolTagType.NOT_AVAILABLE;

	/**
	 * MIB symbol status as MibSymbolStatus ENUM
	 */
	private MibSymbolStatus status = MibSymbolStatus.NOT_AVAILABLE;

	private static final int NAME_INDEX = 0;

	private static final int OID_INDEX = 1;

	private static final int ACCESS_INDEX = 2;

	private static final int TYPE_INDEX = 3;

	private static final int STATUS_INDEX = 4;

	private static final int DESCRIPTION_INDEX = 5;

	/**
	 * decodes symbol info from a single line text
	 * 
	 * @param str
	 *            String to init fields value from
	 */
	public void initFromString(String str) {
		if (str != null) {
			String[] fields = str.split(FIELDS_SEPERATOR_REGEX);
			if (fields != null) {
				setMibName(fields[NAME_INDEX]);

				setOid(fields[OID_INDEX]);

				setAccess(MibSymbolAccess.values()[((int) (fields[ACCESS_INDEX]
						.charAt(0) - '0'))]);

				setTagType(MibSymbolTagType.values()[((int) (fields[TYPE_INDEX]
						.charAt(0) - '0'))]);

				setStatus(MibSymbolStatus.values()[((int) (fields[STATUS_INDEX]
						.charAt(0) - '0'))]);

				String strDesc = fields[DESCRIPTION_INDEX];
				strDesc = strDesc.replace(NEW_LINE_REPLACEMENT, "\n");
				setDescription(strDesc);
			}
		}
	}

	/**
	 * encodes symbol info into a single line text
	 * 
	 * @return Single line String represents all symbol info
	 */
	public String toDbString() {
		StringBuffer sb = new StringBuffer();

		// name = index 0
		sb.append(getMibName());
		sb.append(FIELDS_SEPERATOR);

		// oid = index 1
		sb.append(getOid());
		sb.append(FIELDS_SEPERATOR);

		// access = index 2
		sb.append((char) (getAccess().ordinal() + '0'));
		sb.append(FIELDS_SEPERATOR);

		// type = index 3
		sb.append((char) (getTagType().ordinal() + '0'));
		sb.append(FIELDS_SEPERATOR);

		// status = index 4
		sb.append((char) (getStatus().ordinal() + '0'));
		sb.append(FIELDS_SEPERATOR);

		// description = index 5
		String strDesc = getDescription().replace("\r", "\n");
		while (strDesc.contains("\n\n")) {
			strDesc = strDesc.replace("\n\n", "\n");
		}
		strDesc = strDesc.replace("\n", NEW_LINE_REPLACEMENT);
		sb.append(strDesc);
		sb.append(FIELDS_SEPERATOR);

		return sb.toString();
	}

	/**
	 * DCTOR, all parameters will be initiated to "null"
	 */
	public MibSymbolInfoImpl() {
		this(null);
	}

	/**
	 * CTOR, all non given parameters will be initiated to "null"
	 * 
	 * @param mibName
	 *            MIB symbol name as String
	 */
	public MibSymbolInfoImpl(String mibName) {
		this(mibName, null);
	}

	/**
	 * CTOR, all non given parameters will be initiated to "null"
	 * 
	 * @param mibName
	 *            MIB symbol name as String
	 * @param oid
	 *            MIB symbol OID as String
	 */
	public MibSymbolInfoImpl(String mibName, String oid) {
		this(mibName, oid, null);
	}

	/**
	 * CTOR, all non given parameters will be initiated to "null"
	 * 
	 * @param mibName
	 *            MIB symbol name as String
	 * @param oid
	 *            MIB symbol OID as String
	 * @param description
	 *            MIB symbol description (comments) as String
	 */
	public MibSymbolInfoImpl(String mibName, String oid, String description) {
		super();
		setMibName(mibName);
		setOid(oid);
		setDescription(description);
		setAccess(MibSymbolAccess.NOT_AVAILABLE);
		setTagType(MibSymbolTagType.NOT_AVAILABLE);
		setStatus(MibSymbolStatus.NOT_AVAILABLE);
	}

	public String getMibName() {
		return mibName;
	}

	public void setMibName(String mibName) {
		this.mibName = mibName;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MibSymbolAccess getAccess() {
		return access;
	}

	public void setAccess(MibSymbolAccess access) {
		this.access = access;
	}

	public MibSymbolTagType getTagType() {
		return tagType;
	}

	public void setTagType(MibSymbolTagType tagType) {
		this.tagType = tagType;
	}

	public MibSymbolStatus getStatus() {
		return status;
	}

	public void setStatus(MibSymbolStatus status) {
		this.status = status;
	}
}
