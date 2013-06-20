package systemobject.aqua.automation.utils.utils.tg;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

/**
 * @author Itzhak.Hovav
 */
public class TgUtils {

	public static void closeAllWishTasks() throws Exception {

		Reporter report = ListenerstManager.getInstance();

		report.startLevel("Close All Tcl Wish Tasks",
				EnumReportLevel.CurrentPlace);

		killTask("wish83.exe");
		killTask("wish84.exe");

		report.stopLevel();
	}

	private static void killTask(String taskName) throws Exception {

		Reporter report = ListenerstManager.getInstance();

		Command cmd = new Command();

		cmd.setCmd(new String[] { "taskkill", "/IM", taskName, "/F" });

		Execute.execute(cmd, false, true, true);

		Thread.sleep(5000);

		report.startLevel("STD Out", EnumReportLevel.CurrentPlace);
		report.report(cmd.getStdout().toString());
		report.stopLevel();

		report.startLevel("STD Err", EnumReportLevel.CurrentPlace);
		report.report(cmd.getStderr().toString());
		report.stopLevel();

	}
}
