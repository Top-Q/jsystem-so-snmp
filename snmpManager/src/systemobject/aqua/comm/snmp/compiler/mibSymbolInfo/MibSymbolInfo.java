package systemobject.aqua.comm.snmp.compiler.mibSymbolInfo;

/**
 * This Interface represents the basic information about a single MIB symbol. it
 * should be extended according to the MIB compiler
 * 
 * @author Itzhak.Hovav
 */
public interface MibSymbolInfo {

	/**
	 * returns the MIB symbol name as String
	 * 
	 * @return MIB symbol name as String
	 */
	public String getMibName();

	/**
	 * returns the MIB symbol OID as String
	 * 
	 * @return MIB symbol OID as String
	 */
	public String getOid();

	/**
	 * returns the MIB symbol description (comments) as String
	 * 
	 * @return MIB symbol description (comments) as String
	 */
	public String getDescription();

	/**
	 * decodes symbol info from a single line text
	 * 
	 * @param str
	 *            String to init fields value from
	 */
	public void initFromString(String str);

	/**
	 * encodes symbol info into a single line text
	 * 
	 * @return Single line String represents all symbol info
	 */
	public String toDbString();

	/**
	 * returns the MIB symbol status
	 * 
	 * @return Status of this node
	 */
	public MibSymbolStatus getStatus();

	/**
	 * returns the MIB symbol max access
	 * 
	 * @return Access permission of this node
	 */
	public MibSymbolAccess getAccess();

	/**
	 * returns the MIB symbol Type
	 * 
	 * @return Type of this node
	 */
	public MibSymbolTagType getTagType();

}
