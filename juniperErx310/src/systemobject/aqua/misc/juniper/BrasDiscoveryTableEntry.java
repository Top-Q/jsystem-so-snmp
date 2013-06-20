package systemobject.aqua.misc.juniper;

/**
 * 
 * This class represents a Single Bras Discovery Table entry with all its fields
 * and provides an easy tools to get data about the entry
 * 
 * This class is being used by the Bras itself and the Ctors are all protected
 * since it requires a specific data insertion pattern
 * 
 * @author Itzhak.Hovav
 * 
 */
public class BrasDiscoveryTableEntry {

	/**
	 * This Enum represents the possible states of a single-user-port in the
	 * Bras
	 * 
	 * @author Itzhak.Hovav
	 * 
	 */
	public enum EnumUserState {
		UP, DOWN, UNKNOWN;
	}

	/**
	 * This Enum represents the possible Entry Types of a single-user-port in
	 * the Bras
	 * 
	 * @author Itzhak.Hovav
	 * 
	 */
	public enum EnumEntryType {
		ATM, ETH, UNKNOWN;
	}

	private final int NEIGHBOR_FIRST_INDEX = 0;

	private final int NEIGHBOR_LENGTH = 15;

	private final int LOOP_ID_FIRST_INDEX = 16;

	private final int LOOP_ID_LENGTH = 30;

	private final int RATES_FIRST_INDEX = 46;

	private final int RATES_LENGTH = 19;

	private final int STATE_FIRST_INDEX = 66;

	private final int STATE_LENGTH = 5;

	private final String COLUMN_SEPERATOR = " ";

	private String neighbor = "";

	private String accessLoopId = "";

	private String actualDownUpStream = "";

	private String state = "";

	/**
	 * Default Ctor - Do not init variables
	 */
	public BrasDiscoveryTableEntry() {
		super();
	}

	/**
	 * Ctor
	 * 
	 * @param entry
	 *            entry in the Bras discovery table in the right convention
	 * @throws Exception
	 *             if the "entry" is null, if the entry length is not compatible
	 *             with the Bras convention, if the entry starts with " " or if
	 *             the disassembling gave more or less than 4 sub-strings.
	 */
	protected BrasDiscoveryTableEntry(String entry) throws Exception {
		this(entry, null);
	}

	/**
	 * Ctor
	 * 
	 * @param entry
	 *            entry in the Bras discovery table in the right convention
	 * @param entryCont
	 *            the continue of the entry if exist, or null if not.
	 * @throws Exception
	 *             if the "entry" is null, if the entry length is not compatible
	 *             with the Bras convention, if the "entry" (main string) starts
	 *             with " " or if the disassembling gave more or less than 4
	 *             sub-strings.
	 */
	protected BrasDiscoveryTableEntry(String entry, String entryCont)
			throws Exception {
		if (entry == null || entry.length() != 72 || entry.startsWith(" ")) {
			throw new Exception(
					"Bras Entry Exception : First Entry String Given With A Wrong Values:"
							+ '\n'
							+ (entry == null ? "entry String is null" + '\n'
									: (entry.length() != 72 ? ("entry String length should be 72 but it is "
											+ entry.length() + '\n')
											: ("entry String starts with \" \"" + '\n'))));
		}

		String[] first = disassembleEntryString(entry);

		if (first.length != 4) {
			throw new Exception(
					"Bras Entry Exception : Entry Parsing Failed, Num Of Components Should Be 4, Actual: "
							+ first.length);
		}

		String[] second = disassembleEntryString(entryCont);

		for (int i = 0; i < first.length && i < second.length; i++) {
			first[i] = first[i].trim() + second[i].trim();
		}

		neighbor = first[0].trim();
		accessLoopId = first[1].trim();
		actualDownUpStream = first[2].trim();
		state = first[3].trim();
	}

	/**
	 * disassemble an entry into 4 sub-strings
	 * 
	 * @param entry
	 *            entry to disassemble
	 * @return String array of 4 strings, non of them null, if empty - it will
	 *         return as ""
	 */
	protected String[] disassembleEntryString(String entry) {
		String[] str = new String[4];
		if (entry == null
				|| entry.length() < NEIGHBOR_FIRST_INDEX + NEIGHBOR_LENGTH) {
			str[0] = "";
		} else {
			str[0] = entry.substring(NEIGHBOR_FIRST_INDEX, NEIGHBOR_FIRST_INDEX
					+ NEIGHBOR_LENGTH);
		}
		if (entry == null
				|| entry.length() < LOOP_ID_FIRST_INDEX + LOOP_ID_LENGTH) {
			str[1] = "";
		} else {
			str[1] = entry.substring(LOOP_ID_FIRST_INDEX, LOOP_ID_FIRST_INDEX
					+ LOOP_ID_LENGTH);
		}
		if (entry == null || entry.length() < RATES_FIRST_INDEX + RATES_LENGTH) {
			str[2] = "";
		} else {
			str[2] = entry.substring(RATES_FIRST_INDEX, RATES_FIRST_INDEX
					+ RATES_LENGTH);
		}
		if (entry == null || entry.length() < STATE_FIRST_INDEX + STATE_LENGTH) {
			str[3] = "";
		} else {
			str[3] = entry.substring(STATE_FIRST_INDEX, STATE_FIRST_INDEX
					+ STATE_LENGTH);
		}

		return str;
	}

	/**
	 * disassemble an entry access loop id field into 6 sub-strings
	 * 
	 * @return String array of 6 strings or null
	 */
	protected String[] disassemblAaccessLoopId() {
		if (accessLoopId == null || accessLoopId.length() < 25) {
			return null;
		}

		String temp = accessLoopId.replace(':', ';');
		temp = temp.replace('/', ';');
		temp = temp.replace(' ', ';');

		return temp.split(";");
	}

	/**
	 * returns the entry as string, not in the bras entry-length
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(neighbor);
		sb.append(COLUMN_SEPERATOR);
		sb.append(accessLoopId);
		sb.append(COLUMN_SEPERATOR);
		sb.append(actualDownUpStream);
		sb.append(COLUMN_SEPERATOR);
		sb.append(state);
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * returns the entry US rate
	 * 
	 * @return entry US rate in KBPS
	 * @throws Exception
	 */
	public long getActualUpStreamRateKbps() throws Exception {
		return Long.parseLong(actualDownUpStream.substring(
				actualDownUpStream.indexOf((int) '/') + 1).trim());
	}

	/**
	 * returns the entry DS rate
	 * 
	 * @return entry DS rate in KBPS
	 * @throws Exception
	 */
	public long getActualDownStreamRateKbps() throws Exception {
		return Long.parseLong(actualDownUpStream.substring(0,
				actualDownUpStream.indexOf((int) '/')).trim());
	}

	/**
	 * returns the entry master ip address
	 * 
	 * @return master ip address separated with "." (Bras convention)
	 * @throws Exception
	 */
	public String getMasterIp() throws Exception {
		return (disassemblAaccessLoopId())[0].trim();
	}

	/**
	 * returns the entry slave ip address
	 * 
	 * @return slave ip address separated with "." (Bras convention)
	 * @throws Exception
	 */
	public String getSlaveIp() throws Exception {
		return (disassemblAaccessLoopId())[1].trim();
	}

	/**
	 * return which type is the entry port type
	 * 
	 * @return Enum represents the entry type (ETH/ATM)
	 * @throws Exception
	 */
	public EnumEntryType getEntryType() throws Exception {
		return (disassemblAaccessLoopId()[2].trim().equals("atm") ? EnumEntryType.ATM
				: EnumEntryType.ETH);
	}

	/**
	 * returns the entry slot number in the user device
	 * 
	 * @return slot as int
	 * @throws Exception
	 */
	public int getSlot() throws Exception {
		return Integer.parseInt(disassemblAaccessLoopId()[3].trim());
	}

	/**
	 * returns the entry port number in the user device
	 * 
	 * @return port as int
	 * @throws Exception
	 */
	public int getPort() throws Exception {
		return Integer.parseInt(disassemblAaccessLoopId()[4].trim());
	}

	/**
	 * returns the Vp of the entry, the entry must be an ATM entry in order to
	 * have this field
	 * 
	 * @return vp value as int
	 * @throws Exception
	 *             if the entry is not valid/there was a problem during the
	 *             entry parsing process and there are less or more than the
	 *             required fields or if the type of the entry is not ATM
	 */
	public int getVp() throws Exception {
		String[] arr = disassemblAaccessLoopId();
		if (arr == null || arr.length != 6) {
			if (arr != null && arr.length > 2 && arr[2].trim().equals("eth")) {
				throw new Exception(
						"Bras Entry Exception : Try To Get Vp Form ETH Entry");
			} else {
				throw new Exception(
						"Bras Entry Exception : Could Not Get Vp, AccessLoopId Commponents Length = "
								+ arr.length);
			}
		}
		return Integer
				.parseInt((arr[5].replace('.', ';')).split(";")[0].trim());
	}

	/**
	 * returns the Vc of the entry, the entry must be an ATM entry in order to
	 * have this field
	 * 
	 * @return vc value as int
	 * @throws Exception
	 *             if the entry is not valid/there was a problem during the
	 *             entry parsing process and there are less or more than the
	 *             required fields or if the type of the entry is not ATM
	 */
	public int getVc() throws Exception {
		String[] arr = disassemblAaccessLoopId();
		if (arr == null || arr.length != 6) {
			if (arr != null && arr.length > 2 && arr[2].trim().equals("eth")) {
				throw new Exception(
						"Bras Entry Exception : Try To Get Vc Form ETH Entry");
			} else {
				throw new Exception(
						"Bras Entry Exception : Could Not Get Vc, AccessLoopId Commponents Length = "
								+ arr.length);
			}
		}
		return Integer
				.parseInt((arr[5].replace('.', ';')).split(";")[1].trim());
	}

	/**
	 * returns the Vlan of the entry, the entry must be an ETH entry in order to
	 * have this field
	 * 
	 * @return vlan value as int
	 * @throws Exception
	 *             if the entry is not valid/there was a problem during the
	 *             entry parsing process and there are less or more than the
	 *             required fields or if the type of the entry is not ETH
	 */
	public int getVlan() throws Exception {
		String[] arr = disassemblAaccessLoopId();
		if (arr == null || arr.length != 6) {
			if (arr != null && arr.length > 2 && arr[2].trim().equals("atm")) {
				throw new Exception(
						"Bras Entry Exception : Try To Get Vlan Form ATM Entry");
			} else {
				throw new Exception(
						"Bras Entry Exception : Could Not Get Vlan, AccessLoopId Commponents Length = "
								+ arr.length);
			}
		}
		return Integer.parseInt(arr[5].trim());
	}

	/**
	 * returns the entry state
	 * 
	 * @return port state as Enum (UP/DOWN)
	 */
	public EnumUserState getPortState() {
		return (state.equals(EnumUserState.UP.name()) ? EnumUserState.UP
				: EnumUserState.DOWN);
	}

	/**
	 * returns the entry neigbor name
	 * 
	 * @return neighbor name as String
	 */
	protected String getNeighbor() {
		return neighbor;
	}

	/**
	 * sets the entry neigbor name
	 * 
	 * @param neighbor
	 *            neighbor name as String
	 */
	protected void setNeighbor(String neighbor) {
		this.neighbor = neighbor;
	}

	/**
	 * returns the entry access loop id String
	 * 
	 * @return entry access loop id as String
	 */
	protected String getAccessLoopId() {
		return accessLoopId;
	}

	/**
	 * sets the entry entry access loop id String
	 * 
	 * @param accessLoopId
	 *            entry access loop id as String
	 */
	protected void setAccessLoopId(String accessLoopId) {
		this.accessLoopId = accessLoopId;
	}

	/**
	 * returns the entry down-up rates String
	 * 
	 * @return entry down-up rates as String
	 */
	protected String getActualDownUpStream() {
		return actualDownUpStream;
	}

	/**
	 * sets the entry down-up rates String
	 * 
	 * @param downUpStream
	 *            entry down-up rates as String
	 */
	protected void setActualDownUpStream(String actualDownUpStream) {
		this.actualDownUpStream = actualDownUpStream;
	}

	/**
	 * returns the entry state String
	 * 
	 * @return entry state as String
	 */
	protected String getState() {
		return state;
	}

	/**
	 * sets the entry state String
	 * 
	 * @param state
	 *            entry state as String
	 */
	protected void setState(String state) {
		this.state = state;
	}
}
