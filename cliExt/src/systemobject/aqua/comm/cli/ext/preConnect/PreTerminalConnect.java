package systemobject.aqua.comm.cli.ext.preConnect;

import jsystem.framework.report.Reporter.EnumReportLevel;
import systemobject.aqua.comm.cli.ext.CliTelnet;
import systemobject.terminal.Prompt;

/**
 * @author Itzhak.Hovav
 */
public class PreTerminalConnect extends CliTelnet implements PreCliConnect {

	private int tsPort = 0;

	public void init() throws Exception {
		startLevel("kill Terminal Connection", EnumReportLevel.CurrentPlace);
		super.init();
		preCliConnect();
		stopLevel();
	}

	public void preCliConnect() throws Exception {
		command("enable", 10000, 3);
		command("tunnel " + getTsPort(), 10000, 3);
		command("accept", 10000, 3);
		command("kill connection", 10000, 3);
		// disconnect();
	}

	public Prompt[] getPrompts() {

		Prompt[] p = new Prompt[2];

		p[0] = new Prompt("\\#.*", true);
		p[0].setCommandEnd(true);

		p[1] = new Prompt(">", false);
		p[1].setCommandEnd(true);

		return p;

	}

	public int getTsPort() {
		return tsPort;
	}

	public void setTsPort(int tsPort) {
		this.tsPort = tsPort;
	}
}
