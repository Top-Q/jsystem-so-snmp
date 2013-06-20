package systemobject.aqua.automation.utils.utils.runner;

import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

/**
 * @author Itzhak.Hovav
 */
public abstract class RunnerUtils {

	public static void pauseRunner(String reportHeader, int reportStatus) {
		pauseRunner(reportHeader, null, reportStatus);
	}

	public static void pauseRunner(String reportHeader, String reportMsg,
			int reportStatus) {
		JSystemListeners report = ListenerstManager.getInstance();

		boolean prev = report.isSilent();
		report.setSilent(false);

		if (reportHeader == null) {
			reportHeader = "Pause Runner";
		} else {
			reportHeader = ("Pause Runner: " + reportHeader);
		}
		if (reportMsg != null) {
			report.report(reportHeader, reportMsg, reportStatus);
		} else {
			report.report(reportHeader, reportStatus);
		}
		if (report instanceof ListenerstManager) {
			try {
				((ListenerstManager) ListenerstManager.getInstance()).pause();
			} catch (Exception e) {
				report.report("Failed To Pause Runner", e);
			}
		} else {
			report.report(
					"Running In Debug Mode, For Stop Runner a Runner Mode Required",
					Reporter.WARNING);
		}

		report.setSilent(prev);
	}

	public static void stopRunner(String reportHeader, int reportStatus) {
		stopRunner(reportHeader, null, reportStatus);
	}

	public static void stopRunner(String reportHeader, String reportMsg,
			int reportStatus) {
		JSystemListeners report = ListenerstManager.getInstance();

		report.setSilent(false);

		if (reportHeader == null) {
			reportHeader = "Stop Runner";
		} else {
			reportHeader = ("Stop Runner: " + reportHeader);
		}
		if (reportMsg != null) {
			report.report(reportHeader, reportMsg, reportStatus);
		} else {
			report.report(reportHeader, reportStatus);
		}
		if (report instanceof ListenerstManager) {
			try {
				((ListenerstManager) ListenerstManager.getInstance())
						.gracefulStop();
			} catch (Exception e) {
				report.report("Failed To Stop Runner", e);
			}
		} else {
			report.report(
					"Running In Debug Mode, For Stop Runner a Runner Mode Required",
					Reporter.WARNING);
		}
	}

	public static boolean isRunningFromRunner() {
		String runnerRoot = System.getenv("RUNNER_ROOT");
		if (runnerRoot == null) {
			runnerRoot = "C:\\Program Files\\aqua\\runner";
		}
		if (System.getProperty("user.dir").toLowerCase()
				.contains(runnerRoot.toLowerCase())) {
			return true;
		} else {
			return false;
		}
	}

}
