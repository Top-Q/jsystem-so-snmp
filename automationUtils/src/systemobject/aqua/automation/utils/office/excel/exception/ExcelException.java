package systemobject.aqua.automation.utils.office.excel.exception;

/**
 * @author Itzhak.Hovav
 */
public class ExcelException extends Exception {

	private static final long serialVersionUID = -4758217901305989539L;

	public ExcelException(String msg) {
		super(msg);
	}

	public ExcelException(Throwable t) {
		super(t);
	}

	public ExcelException(String msg, Throwable t) {
		super(msg, t);
	}
}
