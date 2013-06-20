package systemobject.aqua.automation.utils.utils.process;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

/**
 * @author Itzhak.Hovav
 */
public abstract class ProcessUtils {

	private static Reporter report = ListenerstManager.getInstance();

	public static void main(String[] args) throws IOException {
		killProcess("wish8");
	}

	public static String[] getAllRunningTasks(String... nameContains)
			throws IOException {
		Pattern[] p = null;
		if (nameContains != null) {
			p = new Pattern[nameContains.length];
			for (int i = 0; i < nameContains.length; i++) {
				p[i] = Pattern.compile(
						nameContains[i].replace(".", "\\.").replace("_", "\\_")
								.replace("-", "\\-").replace("~", "\\~")
								.replace("!", "\\!").replace("@", "\\@")
								.replace("#", "\\#").replace("$", "\\$")
								.replace("%", "\\%").replace("^", "\\^")
								.replace("&", "\\&").replace("*", "\\*")
								.replace("(", "\\(").replace(")", "\\)")
								.replace("+", "\\+").replace("=", "\\=")
								.replace("|", "\\|").replace("[", "\\[")
								.replace("]", "\\]").replace("{", "\\{")
								.replace("}", "\\}").replace(";", "\\;")
								.replace("?", "\\?").replace("<", "\\<")
								.replace(">", "\\>").replace(",", "\\,"),
						Pattern.CASE_INSENSITIVE);
			}
		}
		return getAllRunningTasks(p);
	}

	public static String[] getAllRunningTasks(Pattern... procNamePattern)
			throws IOException {

		String[] arr = null;
		Command cmd = new Command();
		cmd.setCmd(new String[] { "tasklist" });
		try {
			Execute.execute(cmd, false, true, true);
			String stdoutStr = null;
			String stdoutPrefix = null;
			long start = System.currentTimeMillis();
			String prev = null;
			int index = 0;
			do {
				Thread.sleep(1000);
				prev = stdoutStr;
				stdoutStr = cmd.getStdout().toString();
				if (stdoutStr == null) {
					stdoutStr = "";
				}
				index = stdoutStr.indexOf("System Idle Process");
				if (index == -1) {
					stdoutStr = "";
				} else {
					stdoutPrefix = stdoutStr.substring(0, index);
					stdoutStr = stdoutStr.substring(index);
				}
			} while (((System.currentTimeMillis() - start) < 60000)
					&& ((index == -1) || (stdoutStr.replace("\n", "")
							.replace("\r", "").replace("\t", "")
							.replace(" ", "").length() > 0 && !stdoutStr
							.equals(prev))));
			if (stdoutStr != null
					&& stdoutStr.startsWith("System Idle Process")) {
				stdoutStr = stdoutStr.replace('\r', '\n').replace('\t', ' ');
				while (stdoutStr.contains("\n\n")) {
					stdoutStr = stdoutStr.replace("\n\n", "\n");
				}
				arr = stdoutStr.split("\n");
				ArrayList<String> list = new ArrayList<String>();
				if (arr != null && arr.length > 0) {
					int beginIndex = 0, endIndex = 0;
					if (stdoutPrefix != null) {
						String[] startLines = (stdoutPrefix.replace('\r', '\n')
								.replace('\t', ' ')).split("\n");
						if (startLines != null) {
							for (String line : startLines) {
								if (line != null) {
									line = line.trim();
									if (line.contains("=")) {
										Pattern p = Pattern.compile("\\=+");
										Matcher m = p.matcher(line);
										if (m.find()) {
											beginIndex = m.start();
											endIndex = (beginIndex + m.group()
													.length());
											break;
										}
									}
								}
							}
						}
					}
					for (String str : arr) {
						if (str != null) {
							str = str.trim();
							if (!str.equals("")) {
								String procName = null;
								if (beginIndex < endIndex) {
									procName = str.substring(beginIndex,
											endIndex < str.length() ? endIndex
													: str.length());
								} else {
									Pattern p = Pattern
											.compile("\\ +[\\d]+\\ +[a-zA-Z]");
									Matcher m = p.matcher(str);
									if (m.find()) {
										procName = str.substring(0,
												str.indexOf(m.group()));
									}
								}
								if (procName != null) {
									list.add(procName.trim());
								}
							}
						}
					}
					if (list.size() > 0) {
						arr = list.toArray(new String[list.size()]);
						if (arr != null && arr.length > 0
								&& procNamePattern != null) {
							list = new ArrayList<String>();
							for (String s : arr) {
								if (s != null) {
									boolean status = true;
									for (int i = 0; status
											&& i < procNamePattern.length; i++) {
										if (procNamePattern[i] != null) {
											Matcher m = procNamePattern[i]
													.matcher(s);
											status &= (m.find());
										}
									}
									if (status) {
										list.add(s);
									}
								}
							}
							arr = list.toArray(new String[list.size()]);
							for (int i = 0; arr != null && i < arr.length; i++) {
								/**
								 * complete long process names with extension
								 * (where it can be recovered)
								 */
								if (arr[i] != null) {
									if (arr[i].endsWith(".ex")) {
										arr[i] += "e";
									} else if (arr[i].endsWith(".co")) {
										arr[i] += "m";
									} else if (arr[i].endsWith(".e")) {
										arr[i] += "xe";
									} else if (arr[i].endsWith(".c")) {
										arr[i] += "om";
									} else if (arr[i].endsWith(".")) {
										arr[i] += "exe";
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			cmd.setStdout(new StringBuffer(e.getMessage()));
		}
		report.report("Current Running Tasks", cmd.getStdout().toString(), true);
		if (arr != null && arr.length > 0) {
			StringBuffer sb = new StringBuffer("Found Task(s):\n");
			for (String s : arr) {
				if (s != null) {
					sb.append(s);
					sb.append("\n");
				}
			}
			report.report("Compitable Task Found", sb.toString(), true);
		} else {
			report.report("No Compitable Task Has Been Found");
		}
		return arr;
	}

	public static void killProcess(String processName) throws IOException {
		killProcess(processName, null, false);
	}

	public static void killProcess(String processName, String extension)
			throws IOException {
		killProcess(processName, extension, false);
	}

	public static void killProcess(String processName, boolean forceKill)
			throws IOException {
		killProcess(processName, null, forceKill);
	}

	public static void killProcess(String processName, String extension,
			boolean forceKill) throws IOException {

		if ("".equals(extension)) {
			extension = null;
		}

		processName = (processName + (extension == null ? "" : (processName
				.endsWith("." + extension) ? "" : "." + extension)));

		report.startLevel("Kill \"" + processName + "\" Process(es)",
				EnumReportLevel.CurrentPlace);

		if (forceKill) {
			if (!taskKill(processName)) {
				psKill(processName);
			}
		} else {
			String[] arr = getAllRunningTasks(processName);
			if (arr == null || arr.length == 0) {
				report.report("Process \"" + processName + "\" Does not Exist");
			} else {
				for (int i = 0; arr != null && i < arr.length; i++) {
					if (!taskKill(arr[0])) {
						psKill(arr[0]);
					}
					arr = getAllRunningTasks(processName);
				}
			}
		}
		report.stopLevel();

	}

	protected static boolean psKill(String processName) {
		/**
		 * "PSKILL" - for windows 2000 / windows NT 4 requires the relevant SW
		 * to be installed
		 */
		int counter = 0;
		Command cmd = null;
		String outStr = null;
		String errStr = null;
		Pattern pFinish = Pattern.compile("process +does +not +exist",
				Pattern.CASE_INSENSITIVE);
		Pattern pKilled = Pattern.compile("process +([a-z]+)? *" + processName
				+ " +killed", Pattern.CASE_INSENSITIVE);
		boolean finished = false, killed = false;
		do {
			finished = false;
			killed = false;
			counter++;
			cmd = new Command();
			cmd.setCmd(new String[] { "pskill", "-t", processName });
			try {
				Execute.execute(cmd, false, true, true);
				Thread.sleep(1000);
			} catch (Exception e) {
				cmd.setStdout(new StringBuffer(e.getMessage()));
			}
			outStr = cmd.getStdout().toString();
			errStr = cmd.getStderr().toString();
			report.report("PsKill (" + counter + ") : Output",
					"<b>STDOUT:</b>\n\n" + outStr
							+ "\n\n\n\n<b>STDERR:</b>\n\n" + errStr, true);

			Matcher m = pFinish.matcher(outStr);
			finished = m.find();
			if (!finished) {
				m = pKilled.matcher(outStr);
				killed = m.find();
			}

		} while (!finished && killed);
		return finished;
	}

	protected static boolean taskKill(String processName) {
		/**
		 * "TASKKILL" - for windows XP
		 */
		String outStr = null;
		String errStr = null;
		Pattern pSuccess = Pattern.compile("success\\:? +",
				Pattern.CASE_INSENSITIVE);
		Pattern pError = Pattern.compile("error\\:? +",
				Pattern.CASE_INSENSITIVE);
		boolean error = false, success = false;
		Command cmd = new Command();
		cmd.setCmd(new String[] { "taskkill", "/IM", processName, "/F" });
		try {
			Execute.execute(cmd, false, true, true);
			long start = System.currentTimeMillis();
			do {
				error = false;
				success = false;
				Thread.sleep(1000);
				outStr = cmd.getStdout().toString();
				errStr = cmd.getStderr().toString();
				Matcher m1 = pError.matcher(outStr);
				Matcher m2 = pError.matcher(errStr);
				error = (m1.find() || m2.find());
				m1 = pSuccess.matcher(outStr);
				m2 = pSuccess.matcher(errStr);
				success = (m1.find() || m2.find());
			} while (!success && !error
					&& ((System.currentTimeMillis() - start) < 60000));
		} catch (Exception e) {
			cmd.setStdout(new StringBuffer(e.getMessage()));
		}
		report.report("TaskKill (Win XP) : Output", "<b>STDOUT:</b>\n\n"
				+ cmd.getStdout().toString() + "\n\n\n\n<b>STDERR:</b>\n\n"
				+ cmd.getStderr().toString(), true);
		return success || error;
	}

	@SuppressWarnings("unchecked")
	public static void setEnv(Map<String, String> newenv) {
		try {
			Class<?> processEnvironmentClass = Class
					.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass
					.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField
					.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
					.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField
					.get(null);
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			try {
				Class<?>[] classes = Collections.class.getDeclaredClasses();
				Map<String, String> env = System.getenv();
				for (Class<?> cl : classes) {
					if ("java.util.Collections$UnmodifiableMap".equals(cl
							.getName())) {
						Field field = cl.getDeclaredField("m");
						field.setAccessible(true);
						Object obj = field.get(env);
						Map<String, String> map = (Map<String, String>) obj;
						map.clear();
						map.putAll(newenv);
					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void addEnvVar(String key, String value) {

		Properties prop = System.getProperties();
		HashMap<String, String> newenv = new HashMap<String, String>(
				prop.size() * 2);
		for (Object o : prop.keySet()) {
			newenv.put(o.toString(), prop.getProperty(o.toString()));
		}
		newenv.put(key, value);
		setEnv(newenv);
	}
}
