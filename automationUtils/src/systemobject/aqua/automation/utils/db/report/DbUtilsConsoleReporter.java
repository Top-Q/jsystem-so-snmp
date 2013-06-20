package systemobject.aqua.automation.utils.db.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Itzhak.Hovav
 */
public class DbUtilsConsoleReporter implements DbUtilsReporter {

	private Calendar cal = Calendar.getInstance();

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	public void report(String query) {
		System.out.println(sdf.format(cal.getTime()) + ": " + query);
	}

}
