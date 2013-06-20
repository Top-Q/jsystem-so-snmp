package systemobject.aqua.comm.cli.ext.externalTask;

import systemobject.aqua.comm.cli.ext.CliTelnet;

import com.aqua.sysobj.conn.CliCommand;

/**
 * @author Itzhak.Hovav
 */
public class KeepAliveMonitor extends ExternalCliTask {

	private long keepAliveTimeout = 0L;

	public KeepAliveMonitor(CliTelnet conn, long keepAliveTimeout) {
		super("Keep Alive Monitor: " + conn.toString(), conn);
		this.keepAliveTimeout = keepAliveTimeout;
	}

	@Override
	public void run() {
		while (true) {
			sleep(keepAliveTimeout);
			try {
				synchronized (getConn()) {
					CliCommand command = new CliCommand("");
					command.setNumberOfRetries(1);
					command.setAddEnter(true);
					command.setIgnoreErrors(true);
					command.setSilent(true);
					command.setTimeout(250);
					getConn().command(command);
				}
			} catch (Throwable t) {
				continue;
			}
		}
	}

}
