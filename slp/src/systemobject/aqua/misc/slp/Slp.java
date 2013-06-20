package systemobject.aqua.misc.slp;

import jsystem.framework.monitor.MonitorsManager;
import systemobject.aqua.comm.cli.ext.CliTelnet;
import systemobject.aqua.comm.cli.ext.externalTask.KeepAliveMonitor;
import systemobject.terminal.Prompt;

/**
 * @author Uri.Koaz
 */
public class Slp extends CliTelnet {

	public static final String SLP_DEFAULT_USER_NAME = "sysadmin";

	public static final String SLP_DEFAULT_PASSWORD = "PASS";

	public SlpPort[] ports;

	private String user = Slp.SLP_DEFAULT_USER_NAME;

	private String password = Slp.SLP_DEFAULT_PASSWORD;

	private long keepAliveTimeout = 4 * 60 * 1000;

	public Slp() {
		super(10);
	}

	@Override
	public void init() throws Exception {
		super.init();
		MonitorsManager.getInstance().startMonitor(
				new KeepAliveMonitor(this, keepAliveTimeout));
	}

	@Override
	public Prompt[] getPrompts() {
		Prompt[] p = new Prompt[4];
		p[0] = new Prompt("ser[ \t]*name[ \t]*\\:[ \t]*", true);
		p[0].setStringToSend(getUser());

		p[1] = new Prompt("assword[ \t]*\\:[ \t]*", true);
		p[1].setStringToSend(getPassword());

		p[2] = new Prompt("[A-Z]+[ \t]*\\:[ \t]*", true);
		p[2].setCommandEnd(true);

		p[3] = new Prompt("\\([yY]\\/es\\s+[nN]\\/o\\)\\s*\\:\\s*", true);
		p[3].setStringToSend("y");
		p[3].setAddEnter(false);

		return p;
	}

	/**
	 * returns if the power is on or not
	 * 
	 * @return true if the power is on
	 * @throws Exception
	 */
	public boolean isOn(SlpPort portToCheck) throws Exception {

		command("status .a" + portToCheck.getPort(), 10000, 1);
		String res = (String) getTestAgainstObject();
		return (res.contains("TowerA_Outlet" + portToCheck) && res
				.contains("On"));
	}

	/**
	 * turns on the power
	 * 
	 * @throws Exception
	 */
	public void on(SlpPort portToSwitchOn) throws Exception {
		Thread.sleep(2000);
		command("on .a" + portToSwitchOn.getPort(), 10000, 1);
	}

	/**
	 * returns if the power is off or not
	 * 
	 * @return true if the power is off
	 * @throws Exception
	 */
	public boolean isOff(SlpPort portToCheck) throws Exception {

		command("status .a" + portToCheck.getPort(), 10000, 1);
		String res = (String) getTestAgainstObject();
		return (res.contains("TowerA_Outlet" + portToCheck) && res
				.contains("Off"));
	}

	/**
	 * Shuts down the specific power outlet
	 * 
	 * @param
	 * 
	 * @throws
	 */

	public void off(SlpPort portToSwitchOff) throws Exception {
		Thread.sleep(2000);
		command("off .a" + portToSwitchOff.getPort(), 10000, 1);
	}

	public void logout() throws Exception {
		Thread.sleep(2000);
		command("logout", 10000, 1);

	}

	public SlpPort[] getPorts() {
		return ports;
	}

	public void setPorts(SlpPort[] ports) {
		this.ports = ports;
	}

	public long getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	public void setKeepAliveTimeout(long keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// ON OFF REBOOT STATUS ISTAT ENVMON LOGIN LOGOUT PASSWORD LIST
	// CONNECT PING VERSION SET SHOW CREATE REMOVE ADD DELETE RESTART

}
