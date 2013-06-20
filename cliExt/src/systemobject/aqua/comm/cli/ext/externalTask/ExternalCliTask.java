package systemobject.aqua.comm.cli.ext.externalTask;

import jsystem.framework.monitor.Monitor;
import systemobject.aqua.comm.cli.ext.CliTelnet;

/**
 * @author Itzhak.Hovav
 */
public abstract class ExternalCliTask extends Monitor {

	private CliTelnet conn = null;

	private boolean pause = false;

	public ExternalCliTask(String name, CliTelnet conn) {
		super(name);
		this.conn = conn;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	/**
	 * override the default "sleep" method with a silent one if you want to
	 * report about the sleep "sleep(time, false)" method
	 */
	public void sleep(long time) {
		sleep(time, true);
	}

	public void sleep(long time, boolean silent) {
		if (silent) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
			}
		} else {
			super.sleep(time);
		}

	}

	public CliTelnet getConn() {
		return conn;
	}

	public void setConn(CliTelnet conn) {
		this.conn = conn;
	}

}
