package systemobject.aqua.automation.utils.utils.ping;

import java.io.InputStream;
import java.net.InetAddress;

import junit.framework.SystemTestCase;
import systemobject.aqua.automation.utils.utils.time.TimeUtils;

import com.aqua.stations.StationDefaultImpl;
import com.aqua.stations.StationsFactory;
import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliFactory.EnumOperatinSystem;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public abstract class GenericPingUtils {

	public static final String REQUEST_TIMED_OUT = "request timed out.";

	public static final String DESTINATION_HOST_UNREACHABLE = "destination host unreachable.";

	public static final String REPLY_FROM_HOST = "reply from %s: bytes=";

	/**
	 * perform echo request on TCP port no.7 and wait for response within the
	 * given timeout
	 * 
	 * @param host
	 *            host to perform the echo request to
	 * @param timeout
	 *            max timeout to wait for response
	 * @return true if expected echo response found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean isReachable(String host, long timeout)
			throws Exception {
		return isReachable(host, timeout, true);
	}

	/**
	 * perform echo request on TCP port no.7 and wait for no response within the
	 * given timeout
	 * 
	 * @param host
	 *            host to perform the echo request to
	 * @param timeout
	 *            max timeout to wait for response
	 * @return true if expected no echo response found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean isNotReachable(String host, long timeout)
			throws Exception {
		return isReachable(host, timeout, false);
	}

	/**
	 * perform echo request on TCP port no.7 and wait for response or no
	 * response within the given timeout
	 * 
	 * @param host
	 *            host to perform the echo request to
	 * @param timeout
	 *            max timeout to wait for response
	 * @param reply
	 *            boolean, true for wait to echo response, false for wait for
	 *            no-response
	 * @return true if expected echo response found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean isReachable(String host, long timeout,
			boolean reachable) throws Exception {
		System.out.println("Wait Fot Host \"" + host + "\" To Become "
				+ (reachable ? "" : "Not ") + "Reachable For Max "
				+ TimeUtils.formatTime(timeout));
		boolean flag = true;
		long start = System.currentTimeMillis();
		if (reachable) {
			while (!(flag = InetAddress.getByName(host).isReachable(4000))
					&& ((System.currentTimeMillis() - start) < timeout)) {
				Thread.sleep(1000);
			}
		} else {
			while ((flag = InetAddress.getByName(host).isReachable(4000))
					&& ((System.currentTimeMillis() - start) < timeout)) {
				Thread.sleep(1000);
			}
			flag = !flag;
		}
		if (flag) {
			System.out.println("Host \"" + host + "\" "
					+ (reachable ? "" : "Not ") + "Reachable After "
					+ TimeUtils.formatTime(System.currentTimeMillis() - start));
		} else {
			System.out.println("Host \"" + host + "\" "
					+ (reachable ? "Not " : "") + "Reachable After "
					+ TimeUtils.formatTime(System.currentTimeMillis() - start));
		}
		return flag;
	}

	/**
	 * perform ping and wait for reply within the given timeout
	 * 
	 * @param host
	 *            host to perform ping to
	 * @param timeout
	 *            max timeout to wait for result
	 * @return true if expected ping results found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean ping(String host, long timeout) throws Exception {
		return ping(host, timeout, true);
	}

	/**
	 * perform ping and wait for no reply within the given timeout
	 * 
	 * @param host
	 *            host to perform ping to
	 * @param timeout
	 *            max timeout to wait for result
	 * @return true if expected ping results found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean noPing(String host, long timeout) throws Exception {
		return ping(host, timeout, false);
	}

	/**
	 * perform ping and wait for reply or no reply within the given timeout
	 * 
	 * @param host
	 *            host to perform ping to
	 * @param timeout
	 *            max timeout to wait for result
	 * @param reply
	 *            boolean, true for wait to ping reply, false for wait for
	 *            no-reply
	 * @return true if expected ping results found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean ping(String host, long timeout, boolean reply)
			throws Exception {
		long start = System.currentTimeMillis();
		boolean flag = false;
		Runtime runT = Runtime.getRuntime();
		String command = ("ping " + host + " -t");
		Process p = null;
		InputStream in;
		int c = 0;
		StringBuilder msg;
		System.out.println("Pinging to host \"" + host + "\" And Wait For "
				+ (reply ? "" : "No ") + " Reply For Max "
				+ TimeUtils.formatTime(timeout));
		try {
			p = runT.exec(command);

			do {
				msg = new StringBuilder();
				try {
					p.exitValue();
					flag = false;
				} catch (IllegalThreadStateException e) {
					Thread.sleep(1000);
					in = p.getInputStream();
					do {
						c = in.read();
						if (c == '\n') {
							flag = check(reply, msg, host);
							System.out.println(msg);
							msg = new StringBuilder();
						} else if (c > 0 && c != '\r') {
							msg.append((char) c);
						}
					} while (!flag && c != (-1)
							&& ((System.currentTimeMillis() - start) < timeout));
				}
			} while (!flag && ((System.currentTimeMillis() - start) < timeout));
		} finally {
			in = null;
			p.destroy();
			p = null;
		}
		return flag;
	}

	private static boolean check(boolean reply, StringBuilder line, String host) {
		String temp = line.toString().toLowerCase();
		return (reply == temp.contains(String.format(REPLY_FROM_HOST, host)) && reply != (temp
				.contains(REQUEST_TIMED_OUT) || temp
				.contains(DESTINATION_HOST_UNREACHABLE)));
	}

	/**
	 * perform ping from a remote host by TELNET and wait for reply or no reply
	 * within the given timeout
	 * 
	 * @param destination
	 *            destination to perform ping to
	 * @param timeout
	 *            max timeout to wait for result
	 * @param reply
	 *            boolean, true for wait to ping reply, false for wait for
	 *            no-reply
	 * @param remoteHost
	 *            remote host to perform ping from
	 * @param userName
	 *            user name for the TELNET connection
	 * @param Password
	 *            password for the telnet connection
	 * @return true if expected ping results found within the given timeout,
	 *         false if not
	 * @throws Exception
	 */
	public static boolean remotePing(String destination, long timeout,
			boolean reply, String remoteHost, String userName, String Password)
			throws Exception {
		long start = System.currentTimeMillis();
		boolean flag = false;
		String command = ("ping " + destination);
		CliApplication cliApplication = null;
		String msg;
		SystemTestCase.report.report("Pinging From Remote Host \"" + remoteHost
				+ "\" To \"" + destination + "\" And Wait For "
				+ (reply ? "" : "No ") + " Reply For Max "
				+ TimeUtils.formatTime(timeout));
		StationDefaultImpl station = StationsFactory.createStation(remoteHost,
				EnumOperatinSystem.WINDOWS, "telnet", "AUTO_DOMAIN\\autoUser",
				"AutOPassworD", null);

		station.init();

		cliApplication = station.getCliSession(true);
		String SuccessStr = (reply ? "Reply from " + destination + ": bytes="
				: "equest timed out.");
		do {
			cliApplication.cliCommand(command);
			Thread.sleep(1000);
			msg = (String) cliApplication.getTestAgainstObject();
			flag = msg.contains(SuccessStr);

		} while (!flag && ((System.currentTimeMillis() - start) < timeout));

		station.close();
		return flag;
	}

}
