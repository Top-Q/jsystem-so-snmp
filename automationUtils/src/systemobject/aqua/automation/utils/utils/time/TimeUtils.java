package systemobject.aqua.automation.utils.utils.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class TimeUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public static String getDate() {
		return Calendar.getInstance(TimeZone.getDefault()).getTime().toString();
	}

	public static String getDate(long date) {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(date);
		return c.getTime().toString();
	}

	public static String getDate(long date, DateFormat format) {
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTimeInMillis(date);
		Date d = c.getTime();
		return format.format(d);

	}

	public static String getCurrentTime() {
		return getDate(System.currentTimeMillis(), sdf);
	}

	public static String formatTime(long milliseconds) {
		return formatTime(milliseconds, false);
	}

	public static String formatTime(long milliseconds, boolean withMillis) {
		long time = milliseconds;
		long millis = (time % 1000);
		time = (time / 1000);
		long seconds = (time % 60);
		time = (time / 60);
		long minutes = (time % 60);
		time = (time / 60);
		long hours = (time % 24);
		time = (time / 24);
		String format = Long.toString(minutes + 100).substring(1) + ":"
				+ Long.toString(seconds + 100).substring(1);
		if (withMillis) {
			format = format + "." + Long.toString(millis + 1000).substring(1);
		}
		if (hours > 0 || time > 0) {
			format = Long.toString(hours + 100).substring(1) + ":" + format;
		}
		if (time > 0) {
			format = Long.toString(time + 100).substring(1) + ":" + format;
		}
		format = format + " ( " + (time > 0 ? "DD:" : "")
				+ (hours > 0 || time > 0 ? "HH:" : "") + "MIN:SEC"
				+ (withMillis ? ".MS" : "") + " )";
		return format;
	}

	/**
	 * Returns milliseconds time in "mm:ss:SSS" format
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String fromMilliToTimeFormat(long milliseconds) {
		String TIME_FORMAT = "mm:ss:SSS";

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
		String time = sdf.format(cal.getTime());
		return time;
	}
}
