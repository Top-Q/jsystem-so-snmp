package systemobject.aqua.automation.utils.utils.ping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.monitor.Monitor;
import jsystem.utils.DateUtils;
import systemobject.aqua.automation.utils.utils.exception.ExceptionUtils;

/**
 * @author Itzhak.Hovav
 */
public class PingMonitor extends Monitor {

	private static Pattern pingReplyPattern = Pattern.compile(
			"reply\\s+from\\s+[0-9\\.]+\\s*\\:\\s+bytes\\s*\\=",
			Pattern.CASE_INSENSITIVE);

	private static Pattern pingNoReplyPattern = Pattern
			.compile(
					"((request\\s+timed\\s+out)|(destination\\s+host\\s+unreachable)\\.)",
					Pattern.CASE_INSENSITIVE);

	private String host = null;

	private StringBuilder lastLine = null;

	private ReadWriteLock rwLock = new ReentrantReadWriteLock();

	/**
	 * HTML new line symbol
	 */
	private static final String NEW_LINE = "<br>";

	/**
	 * file writer for the capture file
	 */
	private FileWriter writer = null;

	/**
	 * file object of the capture file
	 */
	private File file = null;

	/**
	 * file representing the current test log directory
	 */
	private File currentTestDir = null;

	/**
	 * date object for adding time to the capture file
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public PingMonitor(String host) {
		super("Ping Monitor For Host " + host);
		this.host = host;
		setLastLine(new StringBuilder());

		openFile();
	}

	@Override
	public void run() {
		Runtime runT = Runtime.getRuntime();
		String command = ("ping " + host + " -t");
		Process p = null;
		InputStream in;
		int c = 0;
		StringBuilder buffer;

		try {
			p = runT.exec(command);
			runT.addShutdownHook(new CloseThread(p));
			buffer = new StringBuilder();
			do {
				try {
					p.exitValue();
				} catch (IllegalThreadStateException e) {
					Thread.sleep(1000);
					in = p.getInputStream();
					do {
						c = in.read();
						if (c == '\n') {
							synchronized (host) {
								setLastLine(new StringBuilder(buffer));
								host.notifyAll();
								writeLastLineToLog();
							}
							buffer = new StringBuilder();
						} else if (c > 0 && c != '\r') {
							buffer.append((char) c);
						}
					} while (c != -1);
				}
			} while (true);
		} catch (Exception e) {
		} finally {
			try {
				writeLastLineToLog();
			} catch (Exception e) {

			}
			closeFile();
			in = null;
			p.destroy();
			p = null;
		}
	}

	public String getHost() {
		return host;
	}

	public boolean waitForReply(long timeout) {
		long start = System.currentTimeMillis();
		do {
			if (reply()) {
				return true;
			}
		} while (timeout < 0 || (System.currentTimeMillis() - start) < timeout);
		return false;
	}

	public boolean waitForNoReply(long timeout) {
		long start = System.currentTimeMillis();
		do {
			if (noReply()) {
				return true;
			}
		} while (timeout < 0 || (System.currentTimeMillis() - start) < timeout);
		return false;
	}

	public boolean reply() {
		return found(pingReplyPattern);
	}

	public boolean noReply() {
		return found(pingNoReplyPattern);
	}

	private boolean found(Pattern p) {
		Matcher m = null;
		synchronized (host) {
			try {
				host.wait(60000);
			} catch (Exception e) {
			}
			m = p.matcher(getLastLine());
		}
		return m.find();
	}

	private StringBuilder getLastLine() {
		try {
			rwLock.readLock().lock();
			return lastLine;
		} finally {
			try {
				rwLock.readLock().unlock();
			} catch (Exception e1) {
				try {
					rwLock.readLock().unlock();
				} catch (Exception e2) {
					e2.printStackTrace(System.out);
				}
			}
		}
	}

	private void setLastLine(StringBuilder lastLine) {
		try {
			rwLock.writeLock().lock();
			this.lastLine = new StringBuilder(lastLine);
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	protected void openFile() {
		try {
			currentTestDir = new File(report.getCurrentTestFolder());
			if (!currentTestDir.exists()) {
				long startTime = System.currentTimeMillis();
				do {
					Thread.sleep(1000);
				} while (!currentTestDir.exists()
						&& ((System.currentTimeMillis() - startTime) < 20000));
			}
			file = new File(currentTestDir.getPath() + "/Ping_History_" + host
					+ ".html");
			writer = new FileWriter(file, true);
			writer.append("<HTML><BODY><CODE>" + NEW_LINE);
			writer.flush();

			addLink();
		} catch (Exception e) {
			report.report(
					"Cli Listener: exception was thrown during new file creation process",
					ExceptionUtils.setStackTrace(e), false);
		}
	}

	protected void writeLastLineToLog() throws IOException {
		String line = getLastLine().toString().replace(" ", "&nbsp;")
				.replace("\n", "").replace("\r", "")
				.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		writer.append(DateUtils.getDate(System.currentTimeMillis(), sdf) + ": "
				+ line + NEW_LINE);
		writer.flush();
	}

	protected void closeFile() {
		if (writer != null) {
			try {
				writer.append("</CODE></BODY></HTML>");
				writer.flush();
				writer.close();
			} catch (Exception e) {
				report.report(
						"Cli Listener: exception was thrown during final writing or file closing",
						ExceptionUtils.setStackTrace(e), false);
			}
			addLink();
			writer = null;
			file = null;
		}
	}

	public void addLink() {
		String fileLocation = file.getAbsolutePath();
		fileLocation = fileLocation.substring(
				fileLocation.lastIndexOf(currentTestDir.getParent())
						+ currentTestDir.getParent().length() + 1).replace(
				'\\', '/');
		report.addLink("Ping History: " + getHost(), fileLocation);
	}

	class CloseThread extends Thread {
		Process p;

		public CloseThread(Process p) {
			this.p = p;
		}

		public void run() {
			if (p != null) {
				p.destroy();
				p = null;
			}
		}
	}

}
