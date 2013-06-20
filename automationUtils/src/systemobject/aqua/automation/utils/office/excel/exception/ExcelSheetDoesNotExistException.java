package systemobject.aqua.automation.utils.office.excel.exception;

/**
 * @author Itzhak.Hovav
 */
public class ExcelSheetDoesNotExistException extends ExcelException {

	private static final long serialVersionUID = -6709799668647075166L;

	public ExcelSheetDoesNotExistException(String sheetName) {
		super("The Sheet \"" + sheetName + "\" Does Not Exist");
	}
}
