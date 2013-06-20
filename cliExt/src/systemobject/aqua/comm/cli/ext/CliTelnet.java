package systemobject.aqua.comm.cli.ext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jsystem.framework.monitor.MonitorsManager;
import jsystem.framework.report.Reporter;
import systemobject.aqua.automation.utils.utils.exception.ExceptionUtils;
import systemobject.aqua.comm.cli.ext.cliBuffer.CliBuffer;
import systemobject.aqua.comm.cli.ext.cliListener.CliListener;
import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger;
import systemobject.aqua.comm.cli.ext.externalTask.ExternalCliTask;
import systemobject.aqua.comm.cli.ext.preConnect.PreCliConnect;
import systemobject.terminal.Cli;
import systemobject.terminal.Prompt;
import systemobject.terminal.SSH;
import systemobject.terminal.Telnet;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;
import com.aqua.sysobj.conn.Position;

/**
 * This class represent a TELNET CLI connection with capturing and triggering
 * mechanisms
 * 
 * @author Itzhak.Hovav
 */
public class CliTelnet extends CliConnectionImpl {

	public PreCliConnect preCliConnect = null;

	private boolean autoConnMethodSwitch = true;

	private boolean ssh = false;

	private boolean terminalServer = false;

	private String sshUser = null;

	private String sshPassword = null;

	private Position[] pos = new Position[0];

	private CliCommand lastCliCommand = null;

	/**
	 * time of the last connect attempt of the CLI connection could be used by
	 * an outside application to initiate a new connect request after a given
	 * timeout
	 */
	private long lastConnectAttempt = Long.MIN_VALUE;

	/**
	 * size of the terminal buffer
	 */
	private int bufferSize = 40;

	/**
	 * name of the owner device
	 */
	private String owner = null;

	/**
	 * non default prompts, if will not be initiated by the user the CLI
	 * connection will use its default ones
	 */
	private Prompt[] prompts = null;

	private ConnectChecker connectChecker = null;

	private CliCommand[] initialCommands = null;

	@Override
	public void init() throws Exception {
		super.init();
		addCaptureLink();
	}

	@Override
	public void close() {
		disconnect();
		super.close();
	}

	public void disconnect() {
		if (buffer != null) {
			try {
				buffer.close();
			} catch (IOException e) {
			}
		}
		super.disconnect();
	}

	/**
	 * close the current capture file and opens another one. if the "addLink"
	 * parameter is "true" it adds a link to the closed capture file in the
	 * report
	 * 
	 * @param addLink
	 *            true to add link to the closed file, false to close without
	 *            link
	 */
	public void restartCapture(boolean addLink) {
		if (buffer != null) {
			((CliBuffer) buffer).restartCapture(addLink);
		}
	}

	/**
	 * enable to start and add a new ExternalCliTask to the CLI connection
	 * 
	 * @param task
	 *            ExternalCliTask object to start and add
	 */
	public void startExternalTask(ExternalCliTask task) {
		MonitorsManager.getInstance().startMonitor(task);
	}

	/**
	 * enable to stop and remove an existing ExternalCliTask from the CLI
	 * connection
	 * 
	 * @param task
	 *            ExternalCliTask object to stop and remove
	 */
	public void stopExternalTask(ExternalCliTask task) {
		MonitorsManager.getInstance().stopMontior(task);
	}

	/**
	 * add a link to the current CLI capture file in the report
	 */
	public void addCaptureLink() {
		if (buffer != null) {
			((CliBuffer) buffer).addCaptureLink();
		}
	}

	/**
	 * if the "prompts" local field is "null" - it returns the default prompts
	 * defined in the method. if it is not "null" it returns the prompts in the
	 * local field
	 */
	public Prompt[] getPrompts() {
		if (prompts != null) {
			return prompts;
		}

		Prompt[] p = new Prompt[7];

		p[0] = new Prompt();
		p[0].setPrompt(" ?>>? ?");
		p[0].setCommandEnd(true);
		p[0].setRegularExpression(true);

		p[1] = new Prompt();
		p[1].setPrompt("assword ?: ?");
		p[1].setStringToSend(password);
		p[1].setRegularExpression(true);

		p[2] = new Prompt();
		p[2].setPrompt("ogin ?: ?");
		p[2].setStringToSend(user);
		p[2].setRegularExpression(true);

		p[3] = new Prompt();
		p[3].setPrompt("[cC]onnection [cC]losed.*");
		p[3].setStringToSend(getEnterStr());
		p[3].setAddEnter(false);
		p[3].setRegularExpression(true);

		p[4] = new Prompt();
		p[4].setPrompt("[wW]elcome.*");
		p[4].setStringToSend(getEnterStr());
		p[4].setAddEnter(false);
		p[4].setRegularExpression(true);

		p[5] = new Prompt();
		p[5].setPrompt("[gG]ood ?[bB]ye.*");
		p[5].setStringToSend(getEnterStr());
		p[5].setAddEnter(false);
		p[5].setRegularExpression(true);

		p[6] = new Prompt();
		p[6].setPrompt("[lL]ogout.*");
		p[6].setStringToSend(getEnterStr());
		p[6].setAddEnter(false);
		p[6].setRegularExpression(true);

		return p;

	}

	@Override
	public Position[] getPositions() {
		return pos;
	}

	public List<Trigger> getAllTriggers() {
		if (buffer != null) {
			return ((CliBuffer) buffer).getTriggers();
		}
		return null;
	}

	/**
	 * search and retrieves a trigger by it's string
	 * 
	 * @param trigger
	 *            the string of the wanted trigger
	 * @return the wanted trigger, null if wasn't found
	 */
	public Trigger getTrigger(String trigger) {
		if (buffer != null) {
			return ((CliBuffer) buffer).getTrigger(trigger);
		}
		return null;
	}

	/**
	 * replace a trigger with a new one
	 * 
	 * @param oldTrig
	 *            the trigger to be changed
	 * @param newTrig
	 *            the new trigger
	 */
	public void replaceTrigger(String oldTrig, Trigger newTrig) {
		removeTrigger(oldTrig);
		addTrigger(newTrig);
	}

	/**
	 * replace a trigger with a new one
	 * 
	 * @param oldTrig
	 *            the trigger to be changed
	 * @param newTrig
	 *            the new trigger
	 */
	public void replaceTrigger(Trigger oldTrig, Trigger newTrig) {
		removeTrigger(oldTrig);
		addTrigger(newTrig);
	}

	/**
	 * add a new trigger to the triggers array
	 * 
	 * @param newTrig
	 *            the trigger to be added
	 */
	public void addTrigger(Trigger newTrig) {
		((CliBuffer) buffer).addTrigger(newTrig);
	}

	/**
	 * removes the requested trigger from the triggers array
	 * 
	 * @param trigger
	 *            the trigger to be removed
	 */
	public void removeTrigger(String trigger) {
		((CliBuffer) buffer).removeTrigger(trigger);
	}

	/**
	 * removes the requested trigger from the triggers array
	 * 
	 * @param trigger
	 *            the trigger to be removed
	 */
	public void removeTrigger(Trigger trigger) {
		((CliBuffer) buffer).removeTrigger(trigger);
	}

	@Override
	public synchronized void command(CliCommand command) {

		setTestAgainstObject(null);
		if (command.getPrompts() == null) {
			command.setPrompts(getPrompts());
		}
		super.command(command);
		setTestAgainstObject(command.getResult());
		setLastCliCommand(command);
	}

	/**
	 * this method defines a list of prompts and a maximum time to wait for
	 * them. the method listen on the CLI output and try to "catch" one of this
	 * prompts. if the wait has failed on time out - the method will print an
	 * error to the report if the wait has failed for other reason - the method
	 * will throw an exception
	 * 
	 * @param str
	 *            array of strings contains prompts to be waited for instead of
	 *            the prompts configured in the beginning
	 * @param timeOut
	 *            maximum time to wait for the prompts
	 */
	public void waitForNotifications(String[] str, long timeOut)
			throws Exception {

		CliCommand c = new CliCommand("");
		c.setAddEnter(false);
		c.setTimeout(timeOut);
		c.setDelayTyping(false);
		Prompt[] p = new Prompt[str.length];
		for (int i = 0; i < str.length; i++) {
			p[i] = new Prompt(str[i], false);
		}
		c.setPrompts(p);
		try {
			super.waitForNotifications(str, timeOut);
			c.setFailed(false);
		} catch (IOException e) {
			c.setFailed(true);
			c.setFailCause(e.getMessage());
			if (!e.getMessage().toLowerCase().contains("timeout")) {
				report.stopLevel();
				throw e;
			} else {
				report.report("Wait For Notification Failed.",
						ExceptionUtils.setStackTrace(e), true);
			}
		} finally {
			setLastCliCommand(c);
		}
	}

	private synchronized CliCommand reConnectIfDisconnectedEnter() {

		CliCommand c = new CliCommand(getEnterStr());
		c.setTimeout(2000);
		c.setNumberOfRetries(1);
		c.setAddEnter(false);
		String oldTestAgainstObject = (String) (getTestAgainstObject() == null ? ""
				: getTestAgainstObject());
		command(c);
		if (oldTestAgainstObject != null && getTestAgainstObject() != null) {
			setTestAgainstObject(String.format("%s%s", oldTestAgainstObject,
					(String) getTestAgainstObject()));
		}
		return c;
	}

	protected synchronized boolean reConnectIfDisconnected() {

		for (int i = 0; i < 50; i++) {
			try {
				if (cli == null) {
					connect();
				}
				read();
				if (!reConnectIfDisconnectedEnter().isFailed()) {
					String oldTestAgainstObject = (String) (getTestAgainstObject() == null ? ""
							: getTestAgainstObject());
					String newTestAgainstObject = read();
					if (oldTestAgainstObject != null
							&& newTestAgainstObject != null) {
						setTestAgainstObject(String.format("%s%s",
								oldTestAgainstObject, newTestAgainstObject));
					}
					return true;
				}
			} catch (Exception e) {
				reconnect();
				if (!reConnectIfDisconnectedEnter().isFailed()) {
					String oldTestAgainstObject = (String) (getTestAgainstObject() == null ? ""
							: getTestAgainstObject());
					String newTestAgainstObject = read();
					if (oldTestAgainstObject != null
							&& newTestAgainstObject != null) {
						setTestAgainstObject(String.format("%s%s",
								oldTestAgainstObject, newTestAgainstObject));
					}
					return true;
				}
			}
		}
		report.report(
				"Failed To To Re-Connect Cli: " + toString(),
				(String) (getTestAgainstObject() == null ? "test against is \"null\""
						: getTestAgainstObject()), Reporter.WARNING);
		return false;
	}

	/**
	 * performs a CLI command for a given number of times until the command
	 * succeeded and the given "wait for" prompts has been captured.
	 * 
	 * @param command
	 *            the CLI command to perform
	 * @param waitFor
	 *            the prompt to wait for
	 * @param timeOut
	 *            maximum time to wait for the prompt for each interval
	 * @param numOfRetrys
	 *            number of intervals to try this command
	 * @param checkConnectivity
	 *            true for checking connectivity before command, false for no
	 *            check
	 * @return true if find the prompt, false if didn't find it for all the
	 *         retries
	 * @throws Exception
	 */
	protected synchronized boolean command(String command, String waitFor,
			long timeOut, int numOfRetrys, boolean checkConnectivity)
			throws Exception {

		setTestAgainstObject(null);
		if (checkConnectivity) {
			if (!reConnectIfDisconnected()) {
				return false;
			}
		}
		CliCommand c = new CliCommand();
		c.setCommands(new String[] { command });
		c.setDelayTyping(true);
		c.setAddEnter(true);
		c.setTimeout(timeOut);
		c.setNumberOfRetries(numOfRetrys);
		c.setPromptString(waitFor);
		String oldTestAgainstObject = (String) getTestAgainstObject();
		String newTestAgainstObject = read();
		if (oldTestAgainstObject != null && newTestAgainstObject != null) {
			setTestAgainstObject(String.format("%s%s", oldTestAgainstObject,
					newTestAgainstObject));
		}
		if (newTestAgainstObject == null) {
			reConnectIfDisconnected();
			oldTestAgainstObject = (String) getTestAgainstObject();
			newTestAgainstObject = read();
			if (oldTestAgainstObject != null && newTestAgainstObject != null) {
				setTestAgainstObject(String.format("%s%s",
						oldTestAgainstObject, newTestAgainstObject));
			}
		}
		command(c);
		return !c.isFailed();
	}

	/**
	 * performs a CLI command for a given number of times until the command
	 * succeeded and a predefined prompt has been found
	 * 
	 * @param command
	 *            the CLI command to perform
	 * @param timeOut
	 *            maximum time to wait for prompt for each interval
	 * @param numOfRetrys
	 *            number of intervals to try this command
	 * @return true if find a prompt, false if didn't find it for all the
	 *         retries
	 * @throws Exception
	 */
	public boolean command(String command, long timeOut, int numOfRetrys)
			throws Exception {
		return command(command, timeOut, numOfRetrys, null);
	}

	/**
	 * performs a CLI command for a given number of times until the command
	 * succeeded and a predefined prompt has been found
	 * 
	 * @param command
	 *            the CLI command to perform
	 * @param timeOut
	 *            maximum time to wait for prompt for each interval
	 * @param numOfRetrys
	 *            number of intervals to try this command
	 * @param p
	 *            prompts to use or "null" for default prompts
	 * @return true if find a prompt, false if didn't find it for all the
	 *         retries
	 * @throws Exception
	 */
	public boolean command(String command, long timeOut, int numOfRetrys,
			Prompt[] p) throws Exception {
		return command(command, timeOut, numOfRetrys, true, p);
	}

	/**
	 * performs a CLI command for a given number of times until the command
	 * succeeded and a predefined prompt has been found
	 * 
	 * @param command
	 *            the CLI command to perform
	 * @param timeOut
	 *            maximum time to wait for prompt for each interval
	 * @param numOfRetrys
	 *            number of intervals to try this command
	 * @param checkConnectivity
	 *            true for checking connectivity before command, false for no
	 *            check
	 * @param p
	 *            prompts to use or "null" for default prompts
	 * @return true if find a prompt, false if didn't find it for all the
	 *         retries
	 * @throws Exception
	 */
	protected synchronized boolean command(String command, long timeOut,
			int numOfRetrys, boolean checkConnectivity, Prompt[] p)
			throws Exception {

		setTestAgainstObject(null);
		Prompt[] old = getPrompts();
		try {
			if (p != null) {
				setPrompts(p);
			}
			if (checkConnectivity) {
				if (!reConnectIfDisconnected()) {
					return false;
				}
			}
			CliCommand c = new CliCommand();
			c.setCommands(new String[] { command });
			c.setDelayTyping(true);
			c.setAddEnter(true);
			c.setTimeout(timeOut);
			c.setNumberOfRetries(numOfRetrys);
			c.setIgnoreErrors(false);
			c.setSilent(false);
			c.setPrompts(p);
			String oldTestAgainstObject = (String) getTestAgainstObject();
			String newTestAgainstObject = read();
			if (oldTestAgainstObject != null && newTestAgainstObject != null) {
				setTestAgainstObject(String.format("%s%s",
						oldTestAgainstObject, newTestAgainstObject));
			}
			if (newTestAgainstObject == null) {
				reConnectIfDisconnected();
				oldTestAgainstObject = (String) getTestAgainstObject();
				newTestAgainstObject = read();
				if (oldTestAgainstObject != null
						&& newTestAgainstObject != null) {
					setTestAgainstObject(String.format("%s%s",
							oldTestAgainstObject, newTestAgainstObject));
				}
			}
			command(c);
			return !c.isFailed();
		} finally {
			setPrompts(old);
		}
	}

	@Override
	public synchronized String read() {
		try {
			return super.read();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * enables to add a new prompt to the predefined prompts of the CLI
	 * 
	 * @param p
	 *            the new prompt
	 * @throws Exception
	 */
	public void addPrompt(Prompt p) throws Exception {
		cli.addPrompt(p);
	}

	/**
	 * DEFAULT CTOR
	 */
	public CliTelnet() {
		this(100);
		setInitialCommands(initInitialCommands());
	}

	/**
	 * CTOR
	 * 
	 * @param bufferSize
	 *            size of the terminal buffer
	 */
	public CliTelnet(int bufferSize) {
		super();
		setBufferSize(bufferSize);
		this.buffer = null;
		setUseBuffer(true);
		setEnterStr("\r\n");
	}

	@Override
	public String toString() {
		String protocol = (isSsh() ? "SSH" : "TELNET");
		if (getOwner() == null) {
			return protocol + " " + host;
		} else {
			return getOwner() + " (" + protocol + ")";
		}
	}

	/**
	 * perform a single connect with no retries
	 */
	public void connect() throws Exception {
		connect(3);
	}

	/**
	 * creates a CLI connection to the "host" and sets all the prompts including
	 * getting and recognizing first prompt
	 * 
	 * @param numOfRetries
	 *            number of connect retries to perform
	 * @throws Exception
	 */
	public void connect(int numOfRetries) throws Exception {

		int startNumOfRetries = numOfRetries;
		boolean connected = false;
		do {
			numOfRetries--;
			connected = connectToHost();
			if (!connected && getConnectChecker() != null) {
				connected = getConnectChecker().isConnected(this);
			}
			if (!connected
					&& isAutoConnMethodSwitch()
					&& !isTerminalServer()
					&& (isSsh() || (getSshUser() != null && getSshPassword() != null))) {
				/**
				 * if it is in SSH mode (will switch to TELNET and so will not
				 * require user or password) or in TELNET mode but the SSH user
				 * and password defined, it switches its connection method to
				 * the other method and try with it.
				 */
				report.report(toString() + ": Failed To Connect Using "
						+ (isSsh() ? "SSH" : "TELNET"));
				setSsh(!isSsh());
				connected = connectToHost();
			}
			if (numOfRetries > 0 && !connected) {
				Thread.sleep(5000);
			}
		} while (!connected && numOfRetries > 0);

		setConnected(connected);

		report.report(toString() + ": "
				+ (connected ? "Connected" : "Connect Failed")
				+ " To Host After " + (startNumOfRetries - numOfRetries)
				+ " Attempt"
				+ (startNumOfRetries - numOfRetries > 1 ? "s" : ""),
				(connected ? Reporter.PASS : Reporter.WARNING));

		if (connected) {
			CliCommand[] arrInitialCommands = getInitialCommands();
			for (int i = 0; arrInitialCommands != null
					&& i < arrInitialCommands.length; i++) {
				command(arrInitialCommands[i]);
			}
		}

	}

	protected void closeCli() {
		if (cli != null) {
			try {
				cli.close();
			} catch (IOException e) {
			}
			cli = null;
		}
	}

	protected boolean connectToHost() throws Exception {

		if (getHost() == null) {
			connected = false;
			return false;
		}

		report.report("Init: " + toString());

		setLastConnectAttempt(System.currentTimeMillis());
		for (int i = 0; i < 5; i++) {
			try {
				if (isSsh()) {
					terminal = new SSH(getHost(), getSshUser(),
							getSshPassword());
				} else {
					terminal = new Telnet(getHost(), getPort(),
							isUseTelnetInputStream());
				}
				break;
			} catch (Exception e) {
				if (i == 4) {
					connected = false;
					closeCli();
					return false;
				}
				Thread.sleep(1000);
			}
		}

		terminal.setBufChar(getBufferSize());

		for (int i = 0; i < 5; i++) {
			try {
				cli = new Cli(terminal);
				break;
			} catch (Exception e) {
				if (i == 4) {
					connected = false;
					closeCli();
					return false;
				}
				Thread.sleep(1000);
			}
		}

		if (getTestAgainstObject() == null) {
			setTestAgainstObject("");
		}

		cli.setEnterStr(getEnterStr());

		if (useBuffer) {
			CliListener l = null;
			if (buffer == null) {
				buffer = new CliBuffer(this, (Trigger[]) null);
			} else {
				l = ((CliBuffer) buffer).getListener();
				buffer = new CliBuffer(this, l);
			}
			terminal.addFilter(buffer);
			buffer.startThread();
			setTestAgainstObject(getTestAgainstObject() + read());
			if (l != null) {
				addCliTag("Reconnect : " + toString());
			}
		}

		Prompt[] prompts = getPrompts();
		for (int i = 0; i < prompts.length; i++) {
			cli.addPrompt(prompts[i]);
		}

		if (isSsh()) {
			Thread.sleep(3000);
			setTestAgainstObject(((String) getTestAgainstObject()) + read());
		}

		for (int i = 0; i < 100; i++) {
			try {
				try {
					cli.command(getEnterStr(), 10, true, false);
				} catch (Exception e) {
				}
				setTestAgainstObject(((String) getTestAgainstObject())
						+ cli.getResult());

				try {
					cli.command(getEnterStr(), 10, true, false);
				} catch (Exception e) {
				}
				setTestAgainstObject(((String) getTestAgainstObject())
						+ cli.getResult());

				cli.command(getEnterStr(), 5000, true, false);
				setTestAgainstObject(((String) getTestAgainstObject())
						+ cli.getResult());

				break;
			} catch (Exception e) {
				if (i >= 99) {
					connected = false;
					cli = null;
					return false;
				}
			}
		}

		connected = true;
		read();
		return true;
	}

	protected CliCommand[] initInitialCommands() {
		return null;
	}

	/**
	 * set the first commands that will be performed right after the connect
	 * 
	 * @return array of commands to perform right after connect or "null" if no
	 *         command should be performed
	 */
	public CliCommand[] getInitialCommands() {
		return initialCommands;
	}

	public void setInitialCommands(CliCommand[] initialCommands) {
		this.initialCommands = initialCommands;
	}

	@Override
	public void reconnect() {

		CliListener listener = null;
		if (useBuffer) {
			if (buffer != null) {
				listener = ((CliBuffer) buffer).getListener();
				((CliBuffer) buffer).setListener(null);
				listener.setCliTag("Reconnect Cli");
			}
		}
		super.reconnect();
		if (listener != null) {
			buffer = new CliBuffer(this, listener);
			terminal.addFilter(buffer);
			buffer.startThread();
		}
	}

	/**
	 * combines two arrays of triggers
	 * 
	 * @param first
	 *            array of triggers
	 * @param sec
	 *            array of triggers
	 * @return the combined array
	 */
	protected Trigger[] combineTriggers(Trigger[] first, Trigger[] sec) {
		Set<Trigger> set = new TreeSet<Trigger>();
		if (first != null && first.length > 0) {
			Collections.addAll(set, first);
		}
		if (sec != null && sec.length > 0) {
			Collections.addAll(set, sec);
		}
		return set.toArray(new Trigger[set.size()]);
	}

	/**
	 * returns the time of the last connect attempt
	 * 
	 * @return last connect attempt time
	 */
	public long getLastConnectAttempt() {
		return lastConnectAttempt;
	}

	/**
	 * checks if the CLI connection is in capture
	 * 
	 * @return true if the CLI is in capture mode
	 */
	public boolean isCapture() {
		return ((buffer != null) && (buffer instanceof CliBuffer) && ((CliBuffer) buffer)
				.isCapture());
	}

	/**
	 * returns the buffer size of the CLI connection
	 * 
	 * @return CLI buffer size
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * sets the buffer size of the CLI connection, relevant only before the
	 * connect method call
	 * 
	 * @param bufferSize
	 *            buffer size to set
	 */
	protected void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * checks if the connection was connected before
	 * 
	 * @return true if the connection was connected before
	 */
	public boolean wasConnected() {
		return connected;
	}

	/**
	 * add a CLI tag that will be added to the capture file right after the next
	 * line will be written to the file. the tag will be bold and the font will
	 * be larger than the default font size so the tag will be more visible
	 * 
	 * @param cliTag
	 *            tag to add as String
	 */
	public void addCliTag(String cliTag) {
		if (buffer != null && (buffer instanceof CliBuffer)) {
			((CliBuffer) buffer).addCliTag(cliTag);
		}
	}

	/**
	 * returns the owner's name as String (owner = the device that holds and
	 * uses the CLI connection)
	 * 
	 * @return owner's name as String
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * sets the owner name (owner = the device that holds and uses the CLI
	 * connection)
	 * 
	 * @param owner
	 *            name of the owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
		if (buffer != null) {
			((CliBuffer) buffer).setOwner(this);
		}
	}

	/**
	 * gets the last CLI command result
	 * 
	 * @return true for success
	 */
	public boolean isLastCommandResult() {
		if (getLastCliCommand() == null) {
			return false;
		}

		return !getLastCliCommand().isFailed();
	}

	/**
	 * set the prompts to be used by the CLI console (replace the default ones)
	 */
	public void setPrompts(Prompt[] prompts) {
		this.prompts = prompts;
	}

	public CliCommand getLastCliCommand() {
		return lastCliCommand;
	}

	public void setLastCliCommand(CliCommand lastCliCommand) {
		this.lastCliCommand = lastCliCommand;
	}

	public void setLastConnectAttempt(long lastConnectAttempt) {
		this.lastConnectAttempt = lastConnectAttempt;
	}

	public boolean isSsh() {
		return ssh;
	}

	public void setSsh(boolean ssh) {
		this.ssh = ssh;
	}

	public String getSshUser() {
		if (sshUser == null) {
			return getUser();
		}
		return sshUser;
	}

	public void setSshUser(String sshUser) {
		this.sshUser = sshUser;
	}

	public String getSshPassword() {
		if (sshPassword == null) {
			return getPassword();
		}
		return sshPassword;
	}

	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}

	public boolean isAutoConnMethodSwitch() {
		return autoConnMethodSwitch;
	}

	public void setAutoConnMethodSwitch(boolean autoConnMethodSwitch) {
		this.autoConnMethodSwitch = autoConnMethodSwitch;
	}

	public ConnectChecker getConnectChecker() {
		return connectChecker;
	}

	public void setConnectChecker(ConnectChecker connectChecker) {
		this.connectChecker = connectChecker;
	}

	public boolean isTerminalServer() {
		return this.terminalServer;
	}

	public void setTerminalServer(boolean terminalServer) {
		this.terminalServer = terminalServer;
	}

}
