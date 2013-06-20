package systemobject.aqua.automation.utils.office.excel.exception;

/**
 * @author Itzhak.Hovav
 */
public class ExcelWriteToFileException extends ExcelException {

	private static final long serialVersionUID = -6293724290973249523L;

	public ExcelWriteToFileException(Throwable t) {
		super("Failed To Write To Excel File", t);
	}
}
