package systemobject.aqua.automation.utils.analyzers.traffic;

import java.io.IOException;

import jsystem.framework.analyzer.AnalyzerParameterImpl;
import jsystem.sysobj.protocolAnalyzer.traffic.Frame;
import jsystem.sysobj.protocolAnalyzer.traffic.FramesBuffer;
import jsystem.sysobj.protocolAnalyzer.traffic.TrafficUtil;

/**
 * @author Itzhak.Hovav
 */
public class IgmpFrameAnalyzer extends AnalyzerParameterImpl {

	private int expected;

	private int amount = 0;;

	private final int offsetMembershipReport = 42;

	private final int offsetMembershipQuery = 38;

	private int offset = 42;

	private final int offsetIGMPGroup = 42;

	private final int offsetIGMPLeaveGroup = 42;

	private final int offsetDaIp = 34;

	private final int daIp[] = { 224, 0, 0, 1 };

	private int maximumResponseTime = 250;

	@SuppressWarnings("unused")
	private int ttl = 2;

	private int[] framesNumber;

	public static void main(String[] args) throws IOException {
		// Process p = Runtime.getRuntime().exec("cmd.exe /c echo %MYVAR%");

		System.out.println(System.getProperty("java.library.path"));
	}

	/**
	 * @author Itzhak.Hovav
	 */
	public static enum EnumIgmpType {
		Membership_Specific_Query(17), Membership_General_Query(17), Membership_Report(
				22), Leave_Group(23);

		EnumIgmpType(int value) {
			this.value = value;

		}

		private int value;

		public int value() {
			return value;
		}
	}

	EnumIgmpType type;

	private int frameFields[];

	private int responseTime;

	private int priority;

	private String[] ipGroupArr = null;

	private String[] temp;

	private int pri;

	private boolean[] foundIpGroup;

	private int error = -1;

	public IgmpFrameAnalyzer(EnumIgmpType type, int expectedAmount) {
		this.type = type;
		this.expected = expectedAmount;
		switch (type) {
		case Membership_Report:
			offset = offsetMembershipReport;
			break;
		case Membership_General_Query:
			offset = offsetMembershipQuery;
			break;
		case Membership_Specific_Query:
			offset = offsetMembershipQuery;
			break;
		case Leave_Group:
			offset = offsetIGMPLeaveGroup;
			break;
		default:
			break;
		}

	}

	public IgmpFrameAnalyzer(EnumIgmpType type, int expectedAmount, int error) {
		this.type = type;
		this.expected = expectedAmount;
		this.error = error;
		switch (type) {
		case Membership_Report:
			offset = offsetMembershipReport;
			break;
		case Membership_General_Query:
			offset = offsetMembershipQuery;
			break;
		case Membership_Specific_Query:
			offset = offsetMembershipQuery;
			break;
		default:
			break;
		}

	}

	/**
	 * for specific querys.check also that all ip groups found
	 * 
	 * @param type
	 * @param expectedAmount
	 * @param ipGroup
	 *            array of Ip Groups
	 */
	public IgmpFrameAnalyzer(EnumIgmpType type, int expectedAmount,
			String ipGroup[]) {
		this(type, expectedAmount);

		this.ipGroupArr = ipGroup;
		foundIpGroup = new boolean[ipGroup.length];
		framesNumber = new int[ipGroup.length];
		for (int i = 0; i < foundIpGroup.length; i++) {
			foundIpGroup[i] = false;
		}

	}

	public void analyze() {

		switch (type) {
		case Membership_Report:
			analyzeMembershipReport();
			break;
		case Membership_General_Query:
			analyzeGeneralMembershipQuery();
			;
			break;
		case Membership_Specific_Query:
			analyzeSpecificMembershipQuery();
			;
			break;
		default:
			analyzeMembershipReport();

		}

	}

	public void analyzeSpecificMembershipQuery() {

		FramesBuffer fb = (FramesBuffer) testAgainst;
		if ((fb.getFrameCount() == 0) && (expected > 0)) {
			title = "No Frames To Analyze";
			status = false;
			return;
		}
		if ((fb.getFrameCount() == 0) && (expected == 0)) {
			title = "No Frames To Analyze  Looking for " + expected
					+ "  IGMP  " + type.toString() + " frames.";
			status = true;
			return;
		}
		framesNumber = new int[expected];
		for (int i = 0; i < fb.getFrameCount(); i++) {
			fb.setCurrentFrameIndex(i);
			Frame f = fb.getCurrentFrame();

			frameFields = new int[1];
			int[] frame = f.getFrame();

			System.arraycopy(frame, offset, frameFields, 0, 1);
			if (frameFields[0] == type.value() && verifyGroupIp(frame)) {

				if (message != null)
					message = message + "\nFrame " + (i + 1) + "\n"
							+ f.toString();
				else
					message = "Frame " + (i + 1) + "\n" + f.toString();

				if (framesNumber != null && framesNumber.length > amount) {
					framesNumber[amount] = i;
				}
				amount++;

			}
		}

		title = "Looking for :" + expected + "  IGMP  " + type.toString()
				+ " frames. Found " + amount;
		if (error > 0) {
			if ((amount <= (expected + error))
					&& (amount >= (expected - error))) {
				status = true;
				title = title + " Tolerance " + error;
				return;
			}
		}
		if (amount != expected) {

			status = false;
			return;
		}
		if (amount == expected) {

			status = true;
			return;
		}

	}

	public void analyzeMembershipReport() {
		FramesBuffer fb = (FramesBuffer) testAgainst;
		if ((fb.getFrameCount() == 0) && (expected > 0)) {
			title = "No Frames To Analyze";
			status = false;
			return;
		}
		if ((fb.getFrameCount() == 0) && (expected == 0)) {
			title = "No Frames To Analyze  Looking for " + expected
					+ "  IGMP  " + type.toString() + " frames.";
			status = true;
			return;
		}

		framesNumber = new int[expected];
		for (int i = 0; i < fb.getFrameCount(); i++) {
			fb.setCurrentFrameIndex(i);
			Frame f = fb.getCurrentFrame();
			if (message != null)
				message = message + "\nFrame " + (i + 1) + "\n" + f.toString();
			else
				message = "Frame " + (i + 1) + "\n" + f.toString();
			frameFields = new int[1];
			int[] frame = f.getFrame();
			System.arraycopy(frame, offset, frameFields, 0, 1);

			if (frameFields[0] == type.value()) {
				if (amount < framesNumber.length)
					framesNumber[amount] = i;
				amount++;
			}
		}
		title = "Looking for " + expected + "  IGMP  " + type.toString()
				+ " frames. Found " + amount;
		if (error > 0) {
			if ((amount <= (expected + error))
					&& (amount >= (expected - error))) {
				status = true;
				title = title + " Tolerance " + error;
				return;
			}
		}
		if (amount != expected) {

			status = false;
			return;
		}
		if (amount == expected) {

			status = true;
			return;
		}

	}

	public void analyzeGeneralMembershipQuery() {
		FramesBuffer fb = (FramesBuffer) testAgainst;
		if ((fb.getFrameCount() == 0) && (expected > 0)) {
			title = "No Frames To Analyze";
			status = false;
			return;
		}
		if ((fb.getFrameCount() == 0) && (expected == 0)) {
			title = "No Frames To Analyze  Looking for " + expected
					+ "  IGMP  " + type.toString() + " frames.";
			status = true;
			return;
		}
		for (int i = 0; i < fb.getFrameCount(); i++) {
			fb.setCurrentFrameIndex(i);
			Frame f = fb.getCurrentFrame();

			if (message != null)
				message = message + "\nFrame " + (i + 1) + "\n" + f.toString();
			else
				message = "Frame " + (i + 1) + "\n" + f.toString();
			frameFields = new int[1];
			int[] frame = f.getFrame();
			System.arraycopy(frame, offset, frameFields, 0, 1);
			/**
			 * if quiery
			 */
			if (frameFields[0] == type.value()) {
				/**
				 * Verify the maximum response time value inside a general query
				 * message IGMP group IP,and DA IP
				 */
				if (verifyFeild(frame))
					;
				amount++;
			}
		}
		title = "Looking for " + expected + "  IGMP  " + type.toString()
				+ " frames. Found " + amount;
		if (error > 0) {
			if ((amount <= (expected + error))
					&& (amount >= (expected - error))) {
				status = true;
				title = title + " Tolerance " + error;
				return;
			}
		}
		if (amount != expected) {

			status = false;
			return;
		}
		if (amount == expected) {

			status = true;
			return;
		}

	}

	public boolean verifyGroupIp(int[] frame) {
		if (expected > 0 || ipGroupArr != null) {
			/**
			 * Verify that the DSLAM generates specific queries with specific
			 * IGMP group IP
			 */
			frameFields = new int[4];
			System.arraycopy(frame, offsetIGMPGroup, frameFields, 0, 4);
			String igmpGroupAsString = frameFields[0] + "." + frameFields[1]
					+ "." + frameFields[2] + "." + frameFields[3];
			if (message != null)
				message = message + "\n IGMP group IP : " + igmpGroupAsString
						+ "in offset " + offsetIGMPGroup;
			else
				message = "\n IGMP group IP : " + igmpGroupAsString
						+ "in offset " + offsetIGMPGroup;

			for (int i = 0; i < ipGroupArr.length; i++) {
				if (igmpGroupAsString.equals(ipGroupArr[i])) {

					foundIpGroup[i] = true;
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public boolean verifyFrame(int[] frame) {

		/**
		 * Verify the maximum response time value inside a general query message
		 */
		System.arraycopy(frame, offset + 1, frameFields, 0, 1);
		message = message + "   Max response Time In Frame " + frameFields[0];
		if (frameFields[0] != responseTime)
			return false;
		/**
		 * Verify that the DSLAM generates specific queries with specific IGMP
		 * group IP
		 */
		frameFields = new int[4];
		System.arraycopy(frame, offsetIGMPGroup, frameFields, 0, 4);
		message = message + "\n IGMP group IP  " + frameFields[0] + "."
				+ frameFields[1] + "." + frameFields[2] + "." + frameFields[3];
		for (int i = 0; i < frameFields.length; i++) {
			if (frameFields[i] != Integer.valueOf(ipGroupArr[i]))
				return false;
		}

		/**
		 * verify frame priority
		 */
		frameFields = new int[2];
		System.arraycopy(frame, 16, frameFields, 0, 2);
		String vlanField = TrafficUtil.convertIntArrToHexString(frameFields);
		temp = vlanField.split(" ");
		pri = Integer.parseInt(temp[temp.length - 2].substring(0, 1), 16);
		if (pri != 0)
			pri = pri / 2;

		message = message + "\n expected  priority  " + this.priority
				+ " actual: " + pri;
		if (pri != priority)
			return false;

		/**
		 * verify frame size
		 */
		message = message + "\n expected  size  -68  actual: " + frame.length;
		if (frame.length != 68)
			return false;

		return true;
	}

	public boolean verifyFeild(int[] frame) {

		/**
		 * Verify the maximum response time value inside a general query message
		 */
		System.arraycopy(frame, offset + 1, frameFields, 0, 1);
		message = message + "   Max response Time In Frame " + frameFields[0];
		if (frameFields[0] != maximumResponseTime)
			return false;
		/**
		 * Verify that the DSLAM generates general queries with IGMP group IP
		 * 0.0.0.0
		 */
		frameFields = new int[4];
		System.arraycopy(frame, offsetIGMPGroup, frameFields, 0, 4);
		message = message + " IGMP group IP  " + frameFields[0] + "."
				+ frameFields[1] + "." + frameFields[2] + "." + frameFields[3];
		for (int i = 0; i < frameFields.length; i++) {
			if (frameFields[i] != 0)
				return false;
		}
		/**
		 * Verify that the DSLAM generates general queries with DA IP 224.0.0.1
		 */
		System.arraycopy(frame, offsetDaIp, frameFields, 0, 4);
		message = message + "  DA IP  " + frameFields[0] + "." + frameFields[1]
				+ "." + frameFields[2] + "." + frameFields[3];
		for (int i = 0; i < frameFields.length; i++) {
			if (frameFields[i] != daIp[i])
				return false;
		}

		return true;
	}

	public int[] getFramesNumber() {
		return framesNumber;
	}

	public void setFramesNumber(int[] framesNumber) {
		this.framesNumber = framesNumber;
	}

	public int getMaximumResponseTime() {
		return maximumResponseTime;
	}

	public void setMaximumResponseTime(int time) {
		maximumResponseTime = time;
	}
}
