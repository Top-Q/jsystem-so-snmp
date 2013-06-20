package systemobject.aqua.comm.cli.ext.cliListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.utils.DateUtils;
import systemobject.aqua.automation.utils.utils.exception.ExceptionUtils;
import systemobject.aqua.automation.utils.utils.html.HtmlTable;
import systemobject.aqua.automation.utils.utils.html.HtmlTable.EnumHtmlColor;
import systemobject.aqua.automation.utils.utils.runner.RunnerUtils;
import systemobject.aqua.comm.cli.ext.CliTelnet;
import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger;
import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger.TriggerAction;

/**
 * This class represent a CLI capture and listening object
 * 
 * @author Itzhak.Hovav
 */
public class CliListener implements Runnable {

	/**
	 * HTML new line symbol
	 */
	private static final String NEW_LINE = "<br>";

	/**
	 * idle time to write a line to the capture file
	 */
	private static final long IDLE_TIMEOUT_MILLIS = 5000;

	/**
	 * current thread
	 */
	private Thread thread;

	/**
	 * name of the capture file
	 */
	private String fileName = null;

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
	 * string buffer of the current line that being analyzed
	 */
	private StringBuffer sb = null;

	/**
	 * current triggers
	 */
	private ArrayList<Trigger> triggers = null;

	/**
	 * date object for adding time to the capture file
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	/**
	 * CLI tag to add to the capture file right after the next line
	 */
	private String cliTag = null;

	/**
	 * default report object
	 */
	private Reporter report = ListenerstManager.getInstance();

	/**
	 * last 2 characters that have been written by the device
	 */
	private int[] last2Chars;

	/**
	 * the arrival time of the last character - for the idle analyze
	 */
	private long lastCharArrived;

	private long currLineStartPrintTime = -1;;

	/**
	 * name of the owner CLI connection
	 */
	private String owner;

	/**
	 * CTOR
	 * 
	 * @param cli
	 *            owner CLI connection
	 * @param triggers
	 *            initial triggers
	 */
	public CliListener(CliTelnet cli, Trigger[] triggers) {
		super();

		this.thread = new Thread(this);
		setTriggers(triggers);
		setOwner(cli);
		this.fileName = "Cli.Capture." + this.owner;

		/**
		 * remove all illegal characters
		 */
		this.fileName = (this.fileName.replace('"', ' ').replace('-', ' ')
				.replace(',', ' ').replace('~', ' ').replace('!', ' ')
				.replace('@', ' ').replace('#', ' ').replace('$', ' ')
				.replace('%', ' ').replace('^', ' ').replace('&', ' ')
				.replace('*', ' ').replace('(', ' ').replace(')', ' ')
				.replace('+', ' ').replace('=', ' ').replace('<', ' ')
				.replace('>', ' ').replace('?', ' ').replace(':', ' ')
				.replace(';', ' ').replace('{', ' ').replace('}', ' ')
				.replace('[', ' ').replace(']', ' ').replace('|', ' ')
				.replace('\r', ' ').replace('\n', ' ').replace('\t', ' '));

		while (this.fileName.contains("" + ' ' + ' ')) {
			this.fileName = this.fileName.replace("" + ' ' + ' ', "" + ' ');
		}

		this.fileName += ".html";
		this.lastCharArrived = -1;

		openFile();

		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
				if (lastCharArrived > 0
						&& System.currentTimeMillis() > (lastCharArrived + IDLE_TIMEOUT_MILLIS)) {
					if (last2Chars[1] != -1 || last2Chars[0] != -1) {
						testTriggers();
						lastCharArrived = System.currentTimeMillis();
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * sets the CLI connection as the owner
	 * 
	 * @param cli
	 *            owner CLI connection
	 */
	public void setOwner(CliTelnet cli) {
		this.owner = cli.toString();
	}

	/**
	 * this method adds a single character(ASCII) into the cap file it should be
	 * called every time the CLI buffer recognizes a new character
	 * 
	 * @param c
	 *            character in ASCII
	 * @throws IOException
	 */
	public void putChar(int c) throws IOException {
		sb.append((char) c);
		lastCharArrived = System.currentTimeMillis();
		if (currLineStartPrintTime == -1) {
			currLineStartPrintTime = lastCharArrived;
		}
		if (isEndOfLine(c)) {
			testTriggers();
		}
	}

	/**
	 * stop the capturing, appends and analyzes all remaining data and closes
	 * the file
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {
		thread.interrupt();
		testTriggers();
		closeFile(true);
	}

	/**
	 * add a link to the capture file to the report
	 */
	public void addLink() {
		String fileLocation = file.getAbsolutePath();
		fileLocation = fileLocation.substring(
				fileLocation.lastIndexOf(currentTestDir.getParent())
						+ currentTestDir.getParent().length() + 1).replace(
				'\\', '/');
		report.addLink("Cli Capture " + this.owner, fileLocation);
	}

	/**
	 * close the old capture file if any and open a new capture file
	 * 
	 * @param addLink
	 *            tur to add link to the capture file
	 */
	public void restartCapture(boolean addLink) {
		closeFile(addLink);
		openFile();
	}

	/**
	 * retrieves the array of triggers that it currently holds
	 * 
	 * @return array of "Trigger" objects
	 */
	public ArrayList<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(ArrayList<Trigger> triggers) {
		this.triggers = triggers;
	}

	public void setTriggers(Trigger[] triggers) {
		this.triggers = new ArrayList<Trigger>();
		addTriggers(triggers);
	}

	public Trigger getTrigger(String trigger) {
		if (this.triggers != null) {
			for (Trigger trig : this.triggers) {
				Trigger t = trig.getTrigger(trigger);
				if (t != null) {
					return t;
				}
			}
		}
		return null;
	}

	public void addTriggers(Trigger[] triggers) {
		if (triggers != null && this.triggers != null) {
			Collections.addAll(this.triggers, triggers);
		}
	}

	public void removeTrigger(String trigger) {
		removeTrigger(getTrigger(trigger));
	}

	public Trigger removeTrigger(Trigger trigger) {
		Trigger t = null;
		if (this.triggers != null && trigger != null) {
			for (Trigger trig : this.triggers) {
				t = trig.getTrigger(trigger.getTrigger());
				if (t != null) {
					if (t == trig) {
						this.triggers.remove(t);
					} else {
						trig.remove(t);
					}
					break;
				}
			}
		}
		return t;
	}

	/**
	 * checks if the last 2 characters printed was "end of line" character
	 * sequence
	 * 
	 * @param c
	 *            current received character
	 * @return true if end of line, false if not
	 */
	protected boolean isEndOfLine(int c) {
		last2Chars[1] = last2Chars[0];
		last2Chars[0] = c;
		return (last2Chars[1] == 13 && (last2Chars[0] == 0 || last2Chars[0] == 10));
	}

	/**
	 * initiate all related variables for new line and retrieves the previous
	 * line.
	 * 
	 * @return the line that just finished as String
	 */
	protected String initLine() {
		this.last2Chars = new int[] { -1, -1 };
		String line = (sb != null ? sb.toString() : null);
		sb = new StringBuffer();
		if (line != null) {
			if (line.length() >= 2 && line.charAt(line.length() - 2) == 13) {
				line = line.substring(0, line.length() - 2);
			}
			line = line.replace("\n", "").replace("\r", "");
		}
		return line;
	}

	protected void writeToFile(String line, EnumHtmlColor color)
			throws IOException {
		line = line.replace(" ", "&nbsp;").replace("\n", "").replace("\r", "")
				.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		if (color != null) {
			line = ("<font color='" + color.color() + "'>" + line + "</font>");
		}
		writer.append(line
				+ NEW_LINE
				+ (cliTag != null ? "<font size = 4><b>ADD USER TAG --> "
						+ cliTag + "</b></font>" + NEW_LINE : "")
				+ DateUtils.getDate(currLineStartPrintTime, sdf) + ": ");
		writer.flush();
		cliTag = null;
		currLineStartPrintTime = -1;
	}

	protected void testTriggers() throws IOException {
		String line = initLine();
		Trigger status = null;
		if (this.triggers != null) {
			for (Trigger trig : this.triggers) {
				Trigger temp = trig.test(line);
				if (temp != null
						&& (status == null || status.getAction().weight() < temp
								.getAction().weight())) {
					status = temp;
				}
			}
			if (status != null && status.getAction() != TriggerAction.SILENT) {
				boolean prevSilent = report.isSilent();
				if (!prevSilent) {
					report.setSilent(false);
					HtmlTable t = new HtmlTable();
					t.addLine(
							status.getAction().reportColor(),
							" Trigger Found:  ",
							this.owner.replace(") (", ", "),
							line + "\n\nCapturing trigger: "
									+ status.toString());

					report.report(t.toString(true, false), status.getAction()
							.status());

					if (status.getAction() == TriggerAction.FAIL_AND_STOP_RUNNER) {
						RunnerUtils.stopRunner("Fail Test And Stop Runner",
								"The Line That Caused The Stop:\n\n" + line,
								Reporter.FAIL);
					} else if (status.getAction() == TriggerAction.FAIL_AND_PAUSE_RUNNER
							|| status.getAction() == TriggerAction.WARNING_AND_PAUSE_RUNNER) {
						RunnerUtils.pauseRunner("Fail Test And Pause Runner",
								"The Line That Caused The Pause:\n\n" + line,
								Reporter.FAIL);
					}
				}
				report.setSilent(prevSilent);
			}
		}

		writeToFile(line, status == null ? null : status.getAction()
				.reportColor());
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
			file = new File(currentTestDir.getPath() + "/" + fileName);
			writer = new FileWriter(file, true);
			writeToFile("<HTML><BODY><CODE>", null);
			initLine();
		} catch (Exception e) {
			report.report(
					"Cli Listener: exception was thrown during new file creation process",
					ExceptionUtils.setStackTrace(e), false);
		}
	}

	protected void closeFile(boolean addLink) {
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
			if (addLink) {
				addLink();
			}
			writer = null;
			file = null;
		}
	}

	public boolean isCupture() {
		return file != null && writer != null;
	}

	public String getCliTag() {
		return cliTag;
	}

	public void setCliTag(String cliTag) {
		this.cliTag = cliTag;
	}
}
