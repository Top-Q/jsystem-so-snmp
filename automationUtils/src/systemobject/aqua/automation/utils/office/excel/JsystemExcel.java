package systemobject.aqua.automation.utils.office.excel;

import java.io.File;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import systemobject.aqua.automation.utils.office.excel.exception.ExcelException;

/**
 * @author Itzhak.Hovav
 */
public class JsystemExcel extends Excel {

	private static Reporter report = ListenerstManager.getInstance();

	private static String fileNameInLogFolder(String fileName) {
		if (!fileName.toLowerCase().endsWith(".xls")) {
			fileName = fileName + ".xls";
		}
		fileName = fileName.replace("/", "\\");
		while (fileName.contains("\\\\")) {
			fileName = fileName.replace("\\\\", "\\");
		}
		if (!fileName.contains("\\")) {
			File currentTestDir = new File(ListenerstManager.getInstance()
					.getCurrentTestFolder());
			fileName = new File(currentTestDir.getPath() + "\\" + fileName)
					.getAbsolutePath().replace('/', '\\');
		}
		return fileName;
	}

	public JsystemExcel(String fileName) throws ExcelException {
		super(fileNameInLogFolder(fileName));
	}

	public JsystemExcel(String fileName, String sheetName)
			throws ExcelException {
		super(fileNameInLogFolder(fileName), sheetName);
		fileName = fileNameInLogFolder(fileName);
		File currentTestDir = new File(ListenerstManager.getInstance()
				.getCurrentTestFolder());
		fileName = new File(currentTestDir.getPath() + "\\" + fileName)
				.getAbsolutePath().replace('/', '\\');
		if (fileName.contains(currentTestDir + "\\")) {
			boolean prev = report.isSilent();
			try {
				report.setSilent(false);
				fileName = (fileName.substring(fileName.lastIndexOf("\\") + 1));
				report.reportHtml("Link To Excel File: " + fileName,
						"<iframe src=\"" + fileName
								+ "\" width=\"100%\" height=\"500\"></iframe>",
						true);
			} finally {
				report.setSilent(prev);
			}
		}
	}
}
