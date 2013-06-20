package systemobject.aqua.automation.utils.utils.stepReport;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import jsystem.framework.TestFreezeException;
import jsystem.framework.report.GracefulStopException;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.EnumReportLevel;
import junit.framework.SystemTestCase;
import systemobject.aqua.automation.utils.utils.exception.ExceptionUtils;

/**
 * JSystem Class, cannot be used outside JSystem Project this class represent an
 * agent between the tests and the reporter and allow open, close, ignore and
 * all necessary actions to enable steps status buffering and recording during
 * test.
 * 
 * @author Itzhak.Hovav
 */
public class StepReporter {

	/**
	 * reporter to report the steps start/stop/ignore
	 */
	private Reporter stepper = null;

	/**
	 * ignore status, by default = false
	 */
	private boolean ignore = false;

	private int stepCounter = 0;

	private SystemTestCase test = null;

	private Class<? extends SystemTestCase> testClass = null;

	private int idOffset = 0;

	/**
	 * generates ID as String from a given Date represented by the time
	 * components
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @return id from the given time components
	 */
	public static String generateId(int year, int month, int day, int hour,
			int minute) {
		return generateId(year, month, day, hour, minute, 0);
	}

	/**
	 * generates ID as String from a given Date represented by the time
	 * components
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param offset
	 *            number to add to the calculation after converting the date
	 *            into number
	 * @return id from the given time components
	 */
	@SuppressWarnings("deprecation")
	public static String generateId(int year, int month, int day, int hour,
			int minute, int offset) {
		return generateId(new Date(year, month, day, hour, minute, 0), offset);
	}

	/**
	 * generates ID as String from a given Date object that represents time
	 * 
	 * @param time
	 *            Date object to generate the ID from its time
	 * @return id from the given time object
	 */
	public static String generateId(Date time) {
		return generateId(time, 0);
	}

	/**
	 * generates ID as String from a given Date object that represents time
	 * 
	 * @param time
	 *            Date object to generate the ID from its time
	 * @param offset
	 *            number to add to the calculation after converting the date
	 *            into number
	 * @return id from the given time object
	 */
	public static String generateId(Date time, int offset) {
		return Long.toString((time.getTime() / 100) + offset);
	}

	/**
	 * add indentation to an HTML code
	 * 
	 * @param numOfTabs
	 *            number of tabs to indent, each tab = 6 characters
	 * @return indentation HTML code as string
	 */
	private static String indent(int numOfTabs) {
		String space = "&nbsp;";
		String str = "";
		for (int i = 0; i < (6 * numOfTabs); i++) {
			str = str + space;
		}
		return str;
	}

	/**
	 * CTOR
	 * 
	 * @param test
	 *            the current running test
	 * @param release
	 *            the current release as String
	 * @throws Exception
	 */
	public StepReporter(SystemTestCase test, String release, String stepId)
			throws Exception {
		stepper = ListenerstManager.getInstance();
		stepper.startLevel(test.getClass().getName() + "." + test.getName()
				+ Stepper.STEP_KEY_SEPERATOR + release
				+ Stepper.STEP_KEY_SEPERATOR + stepId, Stepper.INITIAL_PARAMS);
		this.test = test;
		this.testClass = test.getClass();
		this.stepCounter = 0;
	}

	/**
	 * stops and closes step
	 */
	public void stopStep() throws Exception {
		stepper.startLevel(null, Stepper.STOP_STEP);
	}

	/**
	 * close an open step if any and start a new one with ID derived from the
	 * given Date object
	 * 
	 * @param time
	 *            Date object represents the step ID
	 * @throws Exception
	 */
	public void startStep(Date time) throws Exception {
		startStep(time, null);
	}

	/**
	 * close an open step if any and start a new one with ID derived from the
	 * given Date parameters and with the given description
	 * 
	 * @param year
	 *            the date's year
	 * @param month
	 *            the date's month
	 * @param day
	 *            the date's day
	 * @param hour
	 *            the date's hour
	 * @param minute
	 *            the date's minute
	 * @param description
	 *            step description
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void startStep(int year, int month, int day, int hour, int minute,
			String description) throws Exception {
		startStep(new Date(year, month, day, hour, minute, 0), description);
	}

	/**
	 * close an open step if any and start a new one with ID derived from the
	 * given Date parameters
	 * 
	 * @param year
	 *            the date's year
	 * @param month
	 *            the date's month
	 * @param day
	 *            the date's day
	 * @param hour
	 *            the date's hour
	 * @param minute
	 *            the date's minute
	 * @throws Exception
	 */
	public void startStep(int year, int month, int day, int hour, int minute)
			throws Exception {
		startStep(year, month, day, hour, minute, null);
	}

	/**
	 * close an open step if any and start a new one with ID derived from the
	 * given Date object and with the given description
	 * 
	 * @param time
	 *            Date object - used for extraction of the unique ID for step
	 * @param description
	 *            description of the step
	 */
	public void startStep(Date time, String description) throws Exception {
		startStep(generateId(time, idOffset), description);
	}

	/**
	 * close an open step if any and start a new one with the given ID String
	 * and with the given description
	 * 
	 * @param stepId
	 *            unique ID for step
	 * @param description
	 *            description of the step
	 */
	public void startStep(String stepId, String description) throws Exception {
		if (description == null) {
			description = "";
		}
		if (!description.equals("")) {
			while (description.contains("  ")) {
				description = description.replace("  ", " ");
			}
			int index;
			description = "<b>Step Description: </b><font size='2'>"
					+ description;
			String temp = "<br>" + indent(1);
			boolean start = true;
			while (description.length() > (100 + (start ? 22 : 0))) {
				index = (description.substring(0, (100 + (start ? 22 : 0))))
						.lastIndexOf(' ');
				temp = (temp + description.substring(0, index) + "<br>" + indent(3));
				description = description.substring(index + 1);
				start = false;
			}
			description = (temp + description);
		}
		description = description + "<br><br>";
		stepper.startLevel(stepId + Stepper.STEP_KEY_SEPERATOR + " <b>("
				+ (++stepCounter) + ") STEP =\"" + stepId
				+ "\":</b></a></b></font>" + description + "</font></b>",
				Stepper.START_STEP);
	}

	/**
	 * set the ignore flag status that allow ignoring status reports inside a
	 * step when true - the reporter will not set results into the XML file
	 * 
	 * @param ignore
	 *            true for ignore, false for not
	 * @throws IOException
	 */
	public void ignore(boolean ignore) throws IOException {
		stepper.startLevel(Boolean.toString(ignore), Stepper.IGNORE);
		this.ignore = ignore;
	}

	/**
	 * returns if the reporter is in "ignore" status or not
	 * 
	 * @return boolean - ignore status
	 */
	public boolean isIgnore() {
		return ignore;
	}

	public void runStep(String stepMethod, int year, int month, int day,
			int hour, int minute, String description) throws Exception {
		runStep(stepMethod, null, year, month, day, hour, minute, description);
	}

	@SuppressWarnings("deprecation")
	public void runStep(String stepMethod, String failTo, int year, int month,
			int day, int hour, int minute, String description) throws Exception {
		runStep(stepMethod, failTo,
				new Date(year, month, day, hour, minute, 0), description);
	}

	public void runStep(String stepMethod, Date time, String description)
			throws Exception {
		runStep(stepMethod, null, time, description);
	}

	public void runStep(String stepMethod, String failTo, Date time,
			String description) throws Exception {
		runStep(stepMethod, failTo, generateId(time, idOffset), description);
	}

	public void runStep(String stepMethod, String stepId, String description)
			throws Exception {
		runStep(stepMethod, null, stepId, description);
	}

	public void runStep(String stepMethod, String failTo, String stepId,
			String description) throws Exception {
		Throwable thrw = null;
		try {
			description = (description + " (Step Method Name \"" + stepMethod + "\")");
			startStep(stepId, description);
			Method step = testClass.getMethod(stepMethod, new Class[] {});
			step.invoke(test, new Object[] {});
		} catch (NoSuchMethodException e) {
			throwableError("Step Method \"" + stepMethod
					+ "\" Does Not Exist, Contact The Developer", e);
			thrw = e;
		} catch (IllegalAccessException e) {
			throwableError("Illegal Access Exception On Step Method \""
					+ stepMethod + "\", Contact The Developer", e);
			thrw = e;
		} catch (TestFreezeException e) {
			thrw = e;
			throw e;
		} catch (GracefulStopException e) {
			thrw = e;
			throw e;
		} catch (Exception e) {
			throwableError("Exception While Executing Step Method \""
					+ stepMethod + "\", Contact The Developer", e);
			thrw = e;
		} catch (Throwable t) {
			throwableError("Throwable While Executing Step Method \""
					+ stepMethod + "\", Contact The Developer", t);
			thrw = t;
		} finally {
			stopStep();
			List<ReportElement> r = ListenerstManager.getInstance()
					.getReportsBuffer();
			long startDelayTime = System.currentTimeMillis();
			while (r != null && !r.isEmpty()
					&& ((System.currentTimeMillis() - startDelayTime) < 30000)) {
				Thread.sleep(2000);
			}
			if (thrw != null && !(thrw instanceof TestFreezeException)) {
				if (failTo != null && failTo.length() > 0
						&& !failTo.trim().equals("")) {
					stepper.startLevel("Running Recovery Method \"" + failTo
							+ "\" For Step \"" + stepMethod + "\" With Id."
							+ stepId, EnumReportLevel.CurrentPlace);
					try {
						Method failToMethod = testClass.getMethod(failTo,
								new Class[] {});
						failToMethod.invoke(test, new Object[] {});
					} catch (NoSuchMethodException e) {
						throwableError("Step Fail To Method \"" + failTo
								+ "\" Does Not Exist, Contact The Developer", e);
					} catch (IllegalAccessException e) {
						throwableError(
								"Illegal Access Exception On Step Fail To Method \""
										+ failTo + "\", Contact The Developer",
								e);
					} catch (Exception e) {
						throwableError(
								"Exception While Executing Step Fail To Method \""
										+ failTo + "\", Contact The Developer",
								e);
					} catch (Throwable t) {
						throwableError(
								"Throwable While Executing Step Fail To Method \""
										+ failTo + "\", Contact The Developer",
								t);
					} finally {
						stepper.stopLevel();
					}
				} else {
					stepper.report("No Recovery Method For Step \""
							+ stepMethod + "\" With Id." + stepId);
				}
			}
		}
	}

	private void throwableError(String msg, Throwable thrw) {
		StringBuffer sb = null;
		StringBuffer sbOld = null;
		while (thrw != null) {
			sbOld = sb;
			sb = new StringBuffer(ExceptionUtils.setStackTrace(thrw));
			if (sbOld != null) {
				sb.append("\n\n\n");
				sb.append(sbOld);
			}
			thrw = thrw.getCause();
		}
		stepper.report(msg, sb.toString(), Reporter.FAIL);
	}

	public int getStepCounter() {
		return stepCounter;
	}

	public int getIdOffset() {
		return idOffset;
	}

	public void setIdOffset(int idOffset) {
		this.idOffset = idOffset;
	}

}
