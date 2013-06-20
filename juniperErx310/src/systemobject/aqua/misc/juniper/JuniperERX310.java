package systemobject.aqua.misc.juniper;

import java.util.Vector;

import systemobject.aqua.comm.cli.ext.CliTelnet;
import systemobject.terminal.Prompt;

import com.aqua.sysobj.conn.CliCommand;

/**
 * @author Itzhak.Hovav
 */
public class JuniperERX310 extends CliTelnet {

	/**
	 * This Enum represents all possible Bras Error Codes. for more details:
	 * 
	 * @see 
	 *      <a>http://tools.ietf.org/id/draft-wadhwa-gsmp-l2control-configuration
	 *      -02.txt</a>
	 * 
	 * @author Itzhak.Hovav
	 * 
	 */
	public enum EnumOamErrorCodes {
		UNKNOWN(0), SEND_REQUEST_FAILED(-1), LOCAL_COMMAND_TIMED_OUT(-2), SPECIFIED_ACCESS_LINE_DOES_NOT_EXIST_0x500(
				0x500), LOOPBACK_TEST_TIMED_OUT_0x501(0x501), DSL_LINE_STATUS_SHOWTIME_0x503(
				0x503), DSL_LINE_STATUS_IDLE_0x504(0x504), DSL_LINE_STATUS_SILENT_0x505(
				0x505), DSL_LINE_STATUS_TRAINING_0x506(0x506), DSL_LINE_INTEGRITY_ERROR_0x507(
				0x507), DSLAM_RESOURCE_NOT_AVAILABLE_0x508(0x508), INVALID_TEST_PARAMETER_0x509(
				0x509);

		private int value;

		EnumOamErrorCodes(int value) {
			this.value = value;
		}

		public static EnumOamErrorCodes get(int value) {
			EnumOamErrorCodes[] arr = EnumOamErrorCodes.values();
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].value() == value) {
					return arr[i];
				}
			}
			return UNKNOWN;
		}

		public String toString() {
			return "0x" + Integer.toHexString(value);
		}

		public int value() {
			return value;
		}
	}

	private StringBuffer cliResult = new StringBuffer();

	private boolean cliCapture = false;

	private boolean currentCommandAddEnter = true;

	public BrasInterface[] interfaces = null;

	public JuniperERX310() {
		super(10);
	}

	public void init() throws Exception {
		super.init();
		if (connectOnInit && !isConnected()) {
			connect();
		}
	}

	public Prompt[] getPrompts() {
		Prompt[] p = new Prompt[5];

		p[0] = new Prompt();
		p[0].setPrompt("\\:[a-zA-Z_\\- \\t]*\\>");
		p[0].setRegularExpression(true);
		p[0].setStringToSend("enable");

		p[1] = new Prompt();
		p[1].setPrompt(":" + user + ".*#");
		p[1].setRegularExpression(true);
		p[1].setCommandEnd(true);

		p[2] = new Prompt();
		p[2].setPrompt("assword: ");
		p[2].setStringToSend(password);

		p[3] = new Prompt();
		p[3].setPrompt("--More--");
		p[3].setStringToSend(" ");
		p[3].setAddEnter(false);

		p[4] = new Prompt();
		p[4].setPrompt(">");
		p[4].setCommandEnd(true);

		return p;
	}

	public void disconnect() {
		try {
			Prompt p = cli.getPrompt(":[a-zA-Z_\\- \\t]*\\#");
			p.setRegularExpression(true);
			p.setStringToSend("exit");
			cli.command("" + ((char) 12), 3000, true);
		} catch (Exception e) {
		}

		super.disconnect();
	}

	@Override
	protected CliCommand[] initInitialCommands() {
		return null;
	}

	/**
	 * performs a cli-command for a given number of times until the command
	 * succided and a pre-defined prompt has been found if the "waitFor"
	 * parameter is null or until the "waitFor" parameter is found if not null
	 * or until the given timeout has passed. the method will also check for cli
	 * connectivity if the user asked for it
	 * 
	 * @param command
	 *            the cli command to perform
	 * @param waitFor
	 *            the prompt to wait for
	 * @param timeOut
	 *            maximum time to wait for prompt for each interval
	 * @param numOfRetrys
	 *            number of intervals to try this command
	 * @param checkConnectivity
	 *            true for checking connectivity before command, false for no
	 *            check
	 * @return true if find a prompt, false if didn't find it for all the
	 *         retries
	 */
	protected boolean command(String command, String waitFor, long timeOut,
			int numOfRetrys, boolean checkConnectivity) {
		CliCommand c = new CliCommand();
		c.setCommands(new String[] { "" });
		c.setTimeout(5000);
		command(c);
		if (c.isFailed()) {
			command(c);
			if (c.isFailed()) {
				try {
					connect();
				} catch (Exception e) {
					setConnected(false);
				}
			}
		}
		if (checkConnectivity && !isConnected()) {
			report.setFailToWarning(true);
			report.report(
					"Juniper CLI is not connected, Cannot perform CLI command.",
					false);
			report.setFailToWarning(false);
			return false;
		}
		c = new CliCommand();
		c.setCommands(new String[] { command });
		c.setDelayTyping(true);
		c.setAddEnter(currentCommandAddEnter);
		currentCommandAddEnter = true;
		c.setTimeout(timeOut);
		c.setNumberOfRetries(numOfRetrys);
		if (waitFor != null) {
			c.setPromptString(waitFor);
		}
		command(c);
		report.report(
				"Bras CLI > "
						+ (command.length() > 40 ? command.substring(0, 40)
								+ "..." : command), "<b>" + command + "</b>"
						+ "\n\n" + c.getResult(), true, true);

		return !c.isFailed();
	}

	/**
	 * performs an enter to clean the cli buffer and than performs the given
	 * cli-command once and wait until the command succided and a pre-defined
	 * prompt has been found or until the given timeout has passed. the method
	 * will also check for cli connectivity before command execution
	 * 
	 * @param command
	 *            the cli command to perform
	 * @return true if succeeded and found prompt, false if not (within the
	 *         given timeOut=20000)
	 * @throws Exception
	 */
	public boolean command(String command) throws Exception {
		command("", 20000, 1);
		return command(command, 20000, 1);
	}

	@Override
	public synchronized void command(CliCommand command) {
		super.command(command);
		if (!cliCapture) {
			cliResult = new StringBuffer();
		}
		cliResult.append(command.getResult());
		setTestAgainstObject(cliResult);
	}

	/**
	 * 
	 * @param neighborName
	 *            MAC of the neighbor (BRAS pattern) or a specific name defined
	 *            for this neighbor for example "0016.fa56.7561", "AlexS" ETC...
	 * 
	 *            once you committed this command - all your current entries
	 *            will be deleted and will not be printed in the
	 *            "show l2c discovery br" command
	 * 
	 * @throws Exception
	 */
	public void clearL2cDiscoveryTableEntries(String neighborName)
			throws Exception {
		command("clear l2c discovery-table neighbor " + neighborName);
	}

	/**
	 * performs a "show l2c discovery-table brief" command and scroll it to the
	 * end, after that takes out all the entries that belongs to the given
	 * neighbor and returns them as BrasDiscoveryTableEntry array in the Bras
	 * printing order
	 * 
	 * @param neighborName
	 *            the neighbor name (or MAC) to look for as shown in the Bras
	 *            presentation
	 * @return array of BrasDiscoveryTableEntry contains all the entries
	 *         relevant to the neighbor given to the method
	 * @throws Exception
	 */
	public BrasDiscoveryTableEntry[] getCurrentL2cDiscoveryTableEntries(
			String neighborName) throws Exception {
		command("");
		command("show l2c discovery-table neighbor " + neighborName + " brief",
				90000, 1);
		String[] entries = clearAndParseLastCliResultString();

		if (entries == null || entries.length == 0) {
			return null;
		}
		String last = "";
		Vector<String> vector = new Vector<String>();
		for (int i = 0; i < entries.length; i++) {
			if (!entries[i].startsWith("   Neighbor")
					&& !entries[i].startsWith("---------------")) {
				if (entries[i] != null
						&& entries[i].startsWith(neighborName)
						|| (entries[i].startsWith("               ") && last
								.startsWith(neighborName))) {
					vector.add(entries[i]);
				}
				last = entries[i];
			}
		}
		String[] arr = new String[vector.size()];
		arr = vector.toArray(arr);
		Vector<BrasDiscoveryTableEntry> brasEntries = new Vector<BrasDiscoveryTableEntry>();

		for (int i = 0; i < arr.length; i++) {
			if ((i < arr.length - 1)
					&& (arr[i + 1].startsWith("               "))) {
				brasEntries
						.add(new BrasDiscoveryTableEntry(arr[i], arr[i + 1]));
				i++;
			} else {
				brasEntries.add(new BrasDiscoveryTableEntry(arr[i]));
			}
		}
		BrasDiscoveryTableEntry[] res = new BrasDiscoveryTableEntry[brasEntries
				.size()];

		return brasEntries.toArray(res);

	}

	/**
	 * performs a "show l2c discovery-table" command and scroll it to the end,
	 * after that takes out all the entries that belongs to the given neighbor
	 * and returns them as "BrasDiscoveryTableEntryFullDetails" array in the
	 * Bras printing order
	 * 
	 * @param neighborName
	 *            the neighbor name (or MAC) to look for as shown in the Bras
	 *            presentation
	 * @return array of BrasDiscoveryTableEntryFullDetails contains all the
	 *         entries relevant to the neighbor given to the method
	 * @throws Exception
	 */
	public BrasDiscoveryTableEntryFullDetails[] getCurrentL2cDiscoveryTableEntriesFullDetails(
			String neighborName) throws Exception {
		command("");
		command("show l2c discovery-table neighbor " + neighborName, 120000, 1);
		String[] entries = clearLastCliResultString().split(
				"Total Line Attributes: ");
		String[] lines;
		if (entries == null || entries.length == 0) {
			return null;
		}
		Vector<BrasDiscoveryTableEntryFullDetails> allEntries = new Vector<BrasDiscoveryTableEntryFullDetails>();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != null) {
				lines = entries[i].trim().split("" + '\n');
				if (lines != null && lines.length > 2
						&& lines[1].contains("Access-Loop-Id")
						&& lines[2].contains("Neighbor: ")
						&& lines[2].contains(neighborName)) {
					allEntries
							.add(new BrasDiscoveryTableEntryFullDetails(lines));
				}
			}
		}

		BrasDiscoveryTableEntryFullDetails[] res = new BrasDiscoveryTableEntryFullDetails[allEntries
				.size()];

		return allEntries.toArray(res);

	}

	/**
	 * Gets all the current neighbor table learned in the Bras
	 * 
	 * @throws Exception
	 */
	public void getCurrentL2cNeighborTableEntries() throws Exception {
		command("show l2c neighbor brief");
	}

	/**
	 * sends an Atm Oam loopback containing user data in the message command
	 * structure: l2c oam neighbor NEIGHBOR end-user-id
	 * "NEIGHBOR_IP/ACCESS_NODE_IDENTIFIER ATM SLOT/PORT:VP.VC"
	 * 
	 * @param neighborName
	 *            the neighbor name (or MAC) to look for as shown in the Bras
	 *            presentation
	 * @param neighborIp
	 *            the neighbor IP to look for as shown in the Bras presentation
	 * @param accessNodeIdentifier
	 *            next node's / Slave's IP (default="0.0.0.0")
	 * @param slot
	 *            destination's slot number
	 * @param port
	 *            destination's port number
	 * @param vp
	 *            destination's line VP
	 * @param vc
	 *            destination's line VC
	 * @return the loopback's result according to the Enum
	 * @throws Exception
	 */
	public EnumOamErrorCodes sendAtmOamLoopback(String neighborName,
			String neighborIp, String accessNodeIdentifier, int slot, int port,
			int vp, int vc) throws Exception {
		StringBuffer message = new StringBuffer();
		message.append(neighborIp);
		message.append("/");
		message.append(accessNodeIdentifier);
		message.append(" atm ");
		message.append(slot);
		message.append("/");
		message.append(port);
		message.append(":");
		message.append(vp);
		message.append(".");
		message.append(vc);
		return sendOamLoopback(neighborName, message.toString());
	}

	/**
	 * sends an Ethernet Oam loopback containing user data in the message
	 * command structure: l2c oam neighbor NEIGHBOR end-user-id
	 * "NEIGHBOR_IP/ACCESS_NODE_IDENTIFIER ETH SLOT/PORT:VLAN"
	 * 
	 * @param neighborName
	 *            the neighbor name (or MAC) to look for as shown in the Bras
	 *            presentation
	 * @param neighborIp
	 *            the neighbor IP to look for as shown in the Bras presentation
	 * @param accessNodeIdentifier
	 *            next node's / Slave's IP (default="0.0.0.0")
	 * @param slot
	 *            destination's slot number
	 * @param port
	 *            destination's port number
	 * @param vlan
	 *            destination's connection vlan
	 * @return the loopback's result according to the Enum
	 * @throws Exception
	 */
	public EnumOamErrorCodes sendEthernetOamLoopback(String neighborName,
			String neighborIp, String accessNodeIdentifier, int slot, int port,
			int vlan) throws Exception {
		StringBuffer message = new StringBuffer();
		message.append(neighborIp);
		message.append("/");
		message.append(accessNodeIdentifier);
		message.append(" eth ");
		message.append(slot);
		message.append("/");
		message.append(port);
		message.append(":");
		message.append(vlan);
		return sendOamLoopback(neighborName, message.toString());
	}

	/**
	 * gets a specific loopback message content, sends the loopback and returns
	 * the loopback's result
	 * 
	 * @param neighborName
	 *            the neighbor name (or MAC) to look for as shown in the Bras
	 *            presentation
	 * @param loopbackMessage
	 *            the relevant loopback message - see "sendAtmOamLoopback" and
	 *            "sendEthernetOamLoopback" methods
	 * @return result of the loopback from the Enum - or "UNKNOWN" for any
	 *         unexpected result
	 * @throws Exception
	 */
	protected EnumOamErrorCodes sendOamLoopback(String neighborName,
			String loopbackMessage) throws Exception {
		StringBuffer message = new StringBuffer();
		message.append("l2c oam neighbor ");
		message.append(neighborName);
		message.append(" end-user-id \"");
		message.append(loopbackMessage);
		message.append("\"");

		command(message.toString());
		String result = cliResult.toString();
		if (!result.contains("request succeeded")
				&& !result.contains("request failed")
				&& !result.contains("local command timed out")
				&& !result.contains("failed to send request")) {
			return EnumOamErrorCodes.UNKNOWN;
		}

		int pos = result.lastIndexOf("0x50");
		if (pos < 0 || result.length() <= pos + 5) {
			if (result.lastIndexOf("local command timed out") >= 0) {
				return EnumOamErrorCodes.LOCAL_COMMAND_TIMED_OUT;
			} else if (result.lastIndexOf("failed to send request") >= 0) {
				return EnumOamErrorCodes.SEND_REQUEST_FAILED;
			}
			return EnumOamErrorCodes.UNKNOWN;
		}
		result = result.substring(pos + 2, pos + 5);
		return (EnumOamErrorCodes.get(Integer.parseInt(result, 16)));
	}

	/**
	 * perform a "show l2c statistics" command
	 * 
	 * @throws Exception
	 */
	public void getL2cStatistics() throws Exception {
		command("show l2c statistics");
	}

	/**
	 * returns the current session timeout value as long (in milliseconds)
	 * 
	 * @return Bras current session timeout as long in milliseconds
	 * @throws Exception
	 *             if the basic "show l2c statistics" command faild and no
	 *             string has returned or if - for any reason - it couldn't
	 *             parse out the value or if the timeout type couldn't be parsed
	 *             out
	 */
	public long getL2cSessionTimeoutMillis() throws Exception {
		getL2cStatistics();
		String[] result = clearAndParseLastCliResultString();

		if (result == null) {
			throw new Exception(
					"Bras Unexpected Error: Could not get Session Timeout");
		}

		for (int i = 0; i < result.length; i++) {
			if (result[i] != null) {
				int index = result[i].indexOf("Current session timeout:");
				if (index >= 0 && index < result[i].length()) {
					index += ("Current session timeout:").length();
					while (index < result[i].length()
							&& !(result[i].charAt(index) >= '0' && result[i]
									.charAt(index) <= '9')) {
						index++;
					}
					if (index < result[i].length()) {
						String number = "";
						while (result[i].charAt(index) >= '0'
								&& result[i].charAt(index) <= '9') {
							number += result[i].charAt(index);
							index++;
						}
						double multiple;
						if (result[i].substring(index).toLowerCase()
								.contains("sec")) { // seconds
							multiple = 1000;
						} else if (result[i].substring(index).toLowerCase()
								.contains("mil")) { // milli
							// seconds
							multiple = 1;
						} else if (result[i].substring(index).toLowerCase()
								.contains("mic")) { // micro
							// seconds
							multiple = 0.001;
						} else if (result[i].substring(index).toLowerCase()
								.contains("nan")) { // nano
							// seconds
							multiple = 0.000001;
						} else {
							throw new Exception(
									"Bras Unexpected Error: Could not get Session Timeout Type (Second/Milli/Micro/Nano");
						}
						return (long) (Long.parseLong(number) * multiple);
					}
				}
			}
		}
		throw new Exception(
				"Bras Unexpected Error: Could not Find Session Timeout Value");
	}

	/**
	 * enables to add a new prompt to the pre-defined prompts of the cli
	 * 
	 * @param p
	 *            - the new prompt
	 * @throws Exception
	 */
	public void addPrompt(Prompt p) throws Exception {
		cli.addPrompt(p);
	}

	/**
	 * @return if the next command will be sent with an "Enter" after it or not
	 */
	public boolean isCurrentCommandAddEnter() {
		return currentCommandAddEnter;
	}

	/**
	 * set the next command to be sent with NO Enter after it. the commands that
	 * will follow will be sent with Enter again by default
	 * 
	 */
	public void setNoEnterOnNextCommand() {
		this.currentCommandAddEnter = false;
	}

	/**
	 * 
	 * @return the current content of the "cliResult" conteiner (for the last
	 *         command only or for several commands - if "cliCapture" flag is
	 *         true)
	 */
	public String getCliResult() {
		return cliResult.toString();
	}

	/**
	 * This Tool Should Be used to convert MAC Address from any pattern (such
	 * as: "00.00.00.00.00.00", "000000000000", "00 00 00 00 00 00" ETC...) into
	 * Bras Pattern : "0000.0000.0000"
	 * 
	 * @param oldMac
	 *            MAC address to convert, should have 12 Hex values like in
	 *            normal MAC address
	 * @return converted MAC in Bras Pattern
	 * @throws Exception
	 *             if the given String is null or if the given MAC do not has 12
	 *             Hex values
	 */
	public static String convertMacToBrasPattern(String oldMac)
			throws Exception {
		if (oldMac == null) {
			throw new Exception(
					"Bras MAC Convert Error: MAC String Given To Convert To BRAS Pattern Is null");
		}
		oldMac = oldMac.toLowerCase();
		String newMac = "";
		for (int i = 0; i < oldMac.length(); i++) {
			if ((oldMac.charAt(i) >= '0' && oldMac.charAt(i) <= '9')
					|| (oldMac.charAt(i) >= 'a' && oldMac.charAt(i) <= 'f')) {
				newMac += oldMac.charAt(i);
			}
		}
		if (newMac.length() != 12) {
			throw new Exception(
					"Bras MAC Convert Error: MAC String Given To Convert To BRAS Pattern Has "
							+ newMac.length()
							+ " Hex Values, MAC Requires 12 Hex Values.");
		}
		newMac = newMac.substring(0, 4) + "." + newMac.substring(4, 8) + "."
				+ newMac.substring(8);

		return newMac;
	}

	/**
	 * This Tool Should Be used to convert MAC Address from any pattern (such
	 * as: "00.00.00.00.00.00", "000000000000", "00 00 00 00 00 00" ETC...) into
	 * Bras Pattern : "0000.0000.0000"
	 * 
	 * @param oldMac
	 *            MAC address to convert, should have 12 Hex values like in
	 *            normal MAC address
	 * @return converted MAC in Bras Pattern
	 * @throws Exception
	 *             if the given String is null or if the given MAC do not has 12
	 *             Hex values
	 */
	public static String convertIpToBrasPattern(String ip) throws Exception {

		if (ip == null) {
			throw new Exception(
					"Bras IP Convert Error: IP String Given To Convert To BRAS Pattern Is null");
		}

		String newMac = "0000";
		String[] splitedIp = ip.split("\\.");

		for (int i = 0; i < splitedIp.length; i++) {

			if ((i % 2) == 0) {
				newMac += ".";
			}

			newMac += Integer.toHexString(Integer.parseInt(splitedIp[i]));
		}

		return newMac;
	}

	public static void main(String[] args) throws Exception {

		System.out.println(convertIpToBrasPattern("192.168.40.216"));

	}

	/**
	 * clears the Bras last cli command output string from all '\r', "/r", "/n"
	 * and removes all Bras " --More-- BBBBBBBBBB BBBBBBBBBB" adds and returns
	 * it with only '\n'
	 * 
	 * @return cleared string
	 */
	protected String clearLastCliResultString() {
		String results = cliResult.toString();
		results = results.replace(" --More-- BBBBBBBBBB          BBBBBBBBBB",
				"");
		results = results.replace('\r', '\n');
		results = results.replace("//r", "" + '\n');
		results = results.replace("//n", "" + '\n');
		results = results.replace("\n\n", "" + '\n');
		results = results.replace("" + '\n' + '\n', "" + '\n');

		return results;
	}

	/**
	 * clears the Bras last cli command output string from all '\r', "/r", "/n",
	 * Splits it by the '\n' Character and returns it as a String array
	 * 
	 * @return string array of the Bras last cli command rows
	 */
	protected String[] clearAndParseLastCliResultString() {
		return clearLastCliResultString().split("" + '\n');
	}

	/**
	 * 
	 * @param status
	 *            true for capture the cli results to a conteiner, false to keep
	 *            only the last-command-results
	 */
	public void setCliCapture(boolean status) {
		cliCapture = status;
	}

	public BrasInterface[] getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(BrasInterface[] interfaces) {
		this.interfaces = interfaces;
	}
}
