package systemobject.aqua.comm.cli.ext;

import junit.framework.SystemTestCase;

public class CliTest extends SystemTestCase {

	private CliTelnet telnet;

	private CliTerminal terminal;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		terminal = (CliTerminal) system.getSystemObject("terminal");
		// telnet = (CliTelnet) system.getSystemObject("telnet");
		// terminal = (CliTerminal) system.getSystemObject("terminal");
		//
		// telnet.restartCapture(true);
		// terminal.restartCapture(true);
		//
		// sleep(10000);
		//
		// telnet.setOwner("telnet owner");
		// terminal.setOwner("terminal owner");
	}

	public void testname() throws Exception {
		terminal.command("ver", 5000, 1);
	}

	public void test1() throws Exception {
		terminal.command("eer", 5000, 1);
		telnet.command("ver", 5000, 1);
		sleep(10000);
	}

	public void test2() throws Exception {
		terminal.command("redsta", 5000, 1);
		telnet.command("getpop 0", 5000, 1);
		sleep(10000);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}
}
