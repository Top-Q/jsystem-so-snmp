package systemobject.aqua.comm.cli.ext;

import systemobject.aqua.comm.cli.ext.cliBuffer.CliBuffer;
import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger;
import systemobject.terminal.Cli;
import systemobject.terminal.Prompt;
import systemobject.terminal.Telnet;

/**
 * This class represent a terminal server CLI connection with capturing and
 * triggering mechanisms
 * 
 * @author Itzhak.Hovav
 */
public class CliTerminal extends CliTelnet {

	private String terminalUser = null;

	private String terminalPassword = null;

	/**
	 * DEFAULT CTOR
	 */
	public CliTerminal() {
		super(5);
		useTelnetInputStream = true;
	}

	@Override
	public String toString() {
		return (getOwner() == null ? "" : getOwner() + " (") + "Terminal "
				+ host + "_" + port + (getOwner() == null ? "" : ") ");
	}

	@Override
	protected boolean connectToHost() throws Exception {
		if (port == 23) {
			return super.connectToHost();
		} else {
			setTerminalServer(true);
			if (host == null) {
				connected = false;
				closeCli();
				return false;
			}

			report.report("Init: " + toString());

			setLastConnectAttempt(System.currentTimeMillis());
			for (int i = 0; i < 5; i++) {
				try {
					terminal = new Telnet(host, port, useTelnetInputStream);
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

			cli.setEnterStr(getEnterStr());

			/**
			 * if there is a terminal user & password defined it will first
			 * login into the terminal server.
			 */
			if (getTerminalUser() != null && getTerminalPassword() != null) {

				Prompt userPrompt = new Prompt();
				userPrompt
						.setPrompt("(([Ll]ogin)|([Uu]ser[\\:\\>\\#\\-\\$\\%\\_\\=]?[Nn]?a?m?e?)) *[\\:\\>\\#\\-\\$\\%\\_\\=]? *");
				userPrompt.setRegularExpression(true);
				userPrompt.setStringToSend(getTerminalUser());

				Prompt passPrompt = new Prompt();
				passPrompt.setPrompt("assword *[\\:\\>\\#\\-\\$\\%\\_\\=]? *");
				passPrompt.setRegularExpression(true);
				passPrompt.setCommandEnd(true);

				cli.command("" + getEnterStr() + getEnterStr() + getEnterStr(),
						5000, true, false, null, new Prompt[] { userPrompt,
								passPrompt });
				try {
					cli.command(getTerminalPassword() + getEnterStr(), 1000,
							true, false);
				} catch (Exception e) {/*
										 * do nothing, will swallow the
										 * exception
										 */
				}
			}

			Prompt[] prompts = getPrompts();
			for (int i = 0; i < prompts.length; i++) {
				cli.addPrompt(prompts[i]);
			}

			for (int i = 0; i < 12; i++) {
				try {
					cli.command("", 5000, true, false);
					cli.command("", 5000, true, false);
					break;
				} catch (Exception e) {
					if (i >= 11) {
						connected = false;
						closeCli();
						return false;
					}
				}
			}

			if (useBuffer) {
				if (buffer == null) {
					buffer = new CliBuffer(this, (Trigger[]) null);
				} else {
					buffer = new CliBuffer(this, (CliBuffer) buffer);
				}
				terminal.addFilter(buffer);
				buffer.startThread();
				read();
			}

			for (int i = 0; i < 12; i++) {
				try {
					cli.command("", 5000, true, false);
					read();
					cli.command("", 5000, true, false);
					break;
				} catch (Exception e) {
					if (i >= 11) {
						connected = false;
						closeCli();
						return false;
					}
				}
			}
			connected = true;
			read();
			return true;
		}
	}

	public String getTerminalUser() {
		return this.terminalUser;
	}

	public void setTerminalUser(String terminalUser) {
		this.terminalUser = terminalUser;
	}

	public String getTerminalPassword() {
		return this.terminalPassword;
	}

	public void setTerminalPassword(String terminalPassword) {
		this.terminalPassword = terminalPassword;
	}
}
