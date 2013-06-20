package systemobject.aqua.automation.utils.utils.ping;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import systemobject.aqua.automation.utils.utils.time.TimeUtils;

/**
 * JSystem Class, cannot be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class PingUtils {

	private static Reporter report = ListenerstManager.getInstance();

	public static void main(String[] args) throws Exception {
		System.out.println(isNotReachable("192.168.124.15", 240000));
		System.out.println(isReachable("192.168.124.15", 240000));
	}

	public static boolean isReachable(String host, long timeout)
			throws Exception {
		return isReachable(host, timeout, true);
	}

	public static boolean isNotReachable(String host, long timeout)
			throws Exception {
		return isReachable(host, timeout, false);
	}

	public static boolean isReachable(String host, long timeout,
			boolean reachable) throws Exception {
		long start = System.currentTimeMillis();
		boolean flag = GenericPingUtils.isReachable(host, timeout, reachable);
		report.report(
				"Echo Request To Host \""
						+ host
						+ "\": "
						+ ((reachable && flag) || (!reachable && !flag) ? ""
								: "No ")
						+ "Response After "
						+ TimeUtils.formatTime(System.currentTimeMillis()
								- start), "Expected "
						+ (reachable ? "" : "No ")
						+ "Echo Response From Host \"" + host
						+ "\", Max Timeout " + TimeUtils.formatTime(timeout),
				true);
		return flag;
	}

	public static boolean ping(String host, long timeout) throws Exception {
		return ping(host, timeout, true);
	}

	public static boolean noPing(String host, long timeout) throws Exception {
		return ping(host, timeout, false);
	}

	public static boolean ping(String host, long timeout, boolean reply)
			throws Exception {
		long start = System.currentTimeMillis();
		boolean flag = GenericPingUtils.ping(host, timeout, reply);
		report.report(
				"Ping Request To Host \""
						+ host
						+ "\": "
						+ ((reply && flag) || (!reply && !flag) ? "" : "No ")
						+ "Reply After "
						+ TimeUtils.formatTime(System.currentTimeMillis()
								- start), "Expected " + (reply ? "" : "No ")
						+ "Ping Relpy From Host \"" + host + "\", Max Timeout "
						+ TimeUtils.formatTime(timeout), true);
		return flag;
	}

}
