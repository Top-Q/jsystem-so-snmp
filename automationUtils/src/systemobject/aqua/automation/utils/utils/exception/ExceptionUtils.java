package systemobject.aqua.automation.utils.utils.exception;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public abstract class ExceptionUtils {

	public static String setStackTrace(Throwable thrw) {
		StringBuilder sb = new StringBuilder();
		try {
			while (thrw != null) {
				sb.append("Exception Class : " + thrw.getClass().getName()
						+ "\n\n");
				sb.append((thrw.getMessage() == null ? "Exception Message Is \"null\""
						: thrw.getMessage())
						+ "\n\n");
				StackTraceElement[] trace = thrw.getStackTrace();
				if (trace != null) {
					for (StackTraceElement t : trace) {
						if (t != null) {
							sb.append("\tat " + t + "\n");
						}
					}
				}
				thrw = thrw.getCause();
				if (thrw != null) {
					sb.append("\n\n\n Caused By:\n");
				}
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
}
