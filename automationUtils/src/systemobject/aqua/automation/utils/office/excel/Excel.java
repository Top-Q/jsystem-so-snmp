package systemobject.aqua.automation.utils.office.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.RichTextString;

import systemobject.aqua.automation.utils.office.excel.constant.ExcelColor;
import systemobject.aqua.automation.utils.office.excel.exception.ExcelException;
import systemobject.aqua.automation.utils.office.excel.exception.ExcelWriteToFileException;
import systemobject.aqua.automation.utils.utils.file.FileUtils;

/**
 * @author Itzhak.Hovav
 */
public class Excel {

	private File xlsFile = null;

	private HSSFWorkbook workbook = null;

	private static HashSet<String> supportedExtensions = null;

	public static final String DEFAULT_EXCEL_FILE_EXTENSION = "xls";

	static {
		supportedExtensions = new HashSet<String>(32);
		supportedExtensions.add("xls");
		supportedExtensions.add("xlsx");
		supportedExtensions.add("xlsm");
		supportedExtensions.add("xlsb");
		supportedExtensions.add("xlt");
		supportedExtensions.add("xltx");
		supportedExtensions.add("xltm");
		supportedExtensions.add("xla");
		supportedExtensions.add("xlax");
		supportedExtensions.add("xlam");
		supportedExtensions.add("csv");
		supportedExtensions.add("ods");
	}

	public Excel(String fileName) throws ExcelException {
		this(fileName, null);
	}

	private boolean isLegalFileExtension(String fileName) {
		boolean isLegal = false;
		if (fileName != null) {
			String[] tok = fileName.split("\\.");
			if (tok.length > 0) {
				isLegal = supportedExtensions.contains(tok[tok.length - 1]
						.toLowerCase());
			}
		}
		return isLegal;
	}

	public Excel(String fileName, String sheetName) throws ExcelException {
		super();
		if (!isLegalFileExtension(fileName)) {
			fileName = fileName + "." + DEFAULT_EXCEL_FILE_EXTENSION;
		}
		fileName = fileName.replace("/", "\\");
		while (fileName.contains("\\\\")) {
			fileName = fileName.replace("\\\\", "\\");
		}
		xlsFile = new File(fileName);

		if (!xlsFile.exists()) {
			if (sheetName == null) {
				sheetName = "temp_" + System.currentTimeMillis();
			}
			int index = fileName.lastIndexOf("\\");
			if (index != -1) {
				File f = new File(fileName.substring(0, index));
				if (!f.exists()) {
					f.mkdirs();
				}
			}
			try {
				xlsFile.createNewFile();
			} catch (Exception e) {
				throw new ExcelException("Failed To Create New File \""
						+ fileName + "\"", e);
			}
		} else {
			if (xlsFile.length() > 0) {
				try {
					FileUtils.copyFile(fileName, fileName + ".backup");
				} catch (Exception e) {
					System.out.println("Failed To Backup Excel File");
				}
			}
		}

		FileInputStream fileInStream = null;
		try {
			fileInStream = new FileInputStream(xlsFile);
			POIFSFileSystem fs = new POIFSFileSystem(fileInStream);
			workbook = new HSSFWorkbook(fs);
			fs = null;
		} catch (Exception e) {
			workbook = new HSSFWorkbook();
			getSheet(sheetName, true);
		} finally {
			try {
				fileInStream.close();
				fileInStream = null;
			} catch (Exception e) {
				throw new ExcelException("Failed To Close File Input Stream", e);
			}
		}
	}

	public HSSFRow addRow(Object[] rowValue) throws ExcelException {
		return addRow(null, rowValue);
	}

	public HSSFRow addRow(String sheetName, Object[] rowValue)
			throws ExcelException {
		return addRow(sheetName, rowValue, -1, ExcelColor.WHITE,
				ExcelColor.BLACK, false);
	}

	public HSSFRow addRow(Object[] rowValue, int rowIndex)
			throws ExcelException {
		return addRow(null, rowValue, rowIndex);
	}

	public HSSFRow addRow(String sheetName, Object[] rowValue, int rowIndex)
			throws ExcelException {
		return addRow(sheetName, rowValue, rowIndex, ExcelColor.WHITE,
				ExcelColor.BLACK, false);
	}

	public HSSFRow addRow(Object[] rowValue, int rowIndex,
			ExcelColor backColor, ExcelColor textColor, boolean textBold)
			throws ExcelException {
		return addRow(null, rowValue, rowIndex, backColor, textColor, textBold);
	}

	public HSSFRow addRow(String sheetName, Object[] rowValue, int rowIndex,
			ExcelColor backColor, ExcelColor textColor, boolean textBold)
			throws ExcelException {
		HSSFSheet sheet = getSheet(sheetName, false);
		if (sheet == null) {
			throw new ExcelException("Sheet With Name \"" + sheetName
					+ "\" DOes Not Exist");
		}
		HSSFRow row = null;
		if (rowIndex < 0) {
			rowIndex = sheet.getLastRowNum();
		}

		row = sheet.getRow(rowIndex);

		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		HSSFFont font = workbook.createFont();
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		if (textBold) {
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		} else {
			font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		}
		cellStyle.setFillBackgroundColor(backColor.color());
		cellStyle.setFillForegroundColor(backColor.color());
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		font.setColor(textColor.color());
		cellStyle.setFont(font);

		for (int i = 0; i < rowValue.length; i++) {
			HSSFCell cell = row.createCell(i);
			if (rowValue[i] == null) {
				continue;
			}
			if (rowValue[i] instanceof String) {
				cell.setCellValue((String) rowValue[i]);
			} else if (rowValue[i] instanceof Double) {
				cell.setCellValue((Double) rowValue[i]);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			} else if (rowValue[i] instanceof Calendar) {
				cell.setCellValue((Calendar) rowValue[i]);
			} else if (rowValue[i] instanceof Date) {
				cell.setCellValue((Date) rowValue[i]);
			} else if (rowValue[i] instanceof Boolean) {
				cell.setCellValue((Boolean) rowValue[i]);
				cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
			} else if (rowValue[i] instanceof RichTextString) {
				cell.setCellValue((RichTextString) rowValue[i]);
			} else {
				cell.setCellValue(rowValue[i].toString());
			}
			cell.setCellStyle(cellStyle);
		}
		writeToFile();
		return row;
	}

	public void createSheet(String sheetName) throws ExcelException {
		getSheet(sheetName, true);
	}

	public HSSFSheet getSheet() throws ExcelException {
		return getSheet(null, false);
	}

	public HSSFSheet getSheet(String sheetName, boolean create)
			throws ExcelException {
		HSSFSheet sheet = null;
		try {
			if (sheetName == null) {
				int activeSheet = workbook.getActiveSheetIndex();
				sheetName = workbook.getSheetName(activeSheet);
			}
			if ((sheet = workbook.getSheet(sheetName)) == null && create) {
				sheet = createNewSheet(sheetName);
			}
		} catch (Exception e) {
			try {
				if ((sheet = workbook.getSheet(sheetName)) == null && create) {
					sheet = createNewSheet(sheetName);
				}
			} catch (Exception e2) {
				try {
					if (create) {
						sheet = createNewSheet(sheetName);
					} else {
						throw new ExcelException(
								"Failed To Get WorkBook For \"" + sheetName
										+ "\" Sheet", e2);
					}
				} catch (Exception e3) {
					throw new ExcelException("Failed To Create \"" + sheetName
							+ "\" Sheet", e3);
				}
			}
		}
		return sheet;
	}

	public boolean isSheetExist(String sheetName) {
		try {
			return (workbook.getSheet(sheetName) != null);
		} catch (Exception e) {
			return false;
		}
	}

	public Object getSingleCell(int rowIndex, int cellIndex)
			throws ExcelException {
		return getSingleCell(null, rowIndex, cellIndex);
	}

	public Object getSingleCell(String sheetName, int rowIndex, int cellIndex)
			throws ExcelException {
		Object[] content = getSingleRow(sheetName, rowIndex);
		return content[cellIndex];
	}

	public Object[] getSingleRow(int rowIndex) throws ExcelException {
		return getSingleRow(null, rowIndex);
	}

	public Object[] getSingleRow(String sheetName, int rowIndex)
			throws ExcelException {
		Object[][] content = getSheetValue(sheetName);
		return content[rowIndex];
	}

	public Object[][] getSheetValue() throws ExcelException {
		return getSheetValue(null);
	}

	public Object[][] getSheetValue(String sheetName) throws ExcelException {
		HSSFSheet sheet = getSheet(sheetName, false);
		Object[][] content = null;
		if (sheet == null) {
			content = new Object[0][0];
		} else {
			content = new Object[sheet.getLastRowNum() + 1][];
			for (short i = 0; i < content.length; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null && row.getLastCellNum() >= 0) {
					content[i] = new Object[row.getLastCellNum() + 1];
					for (int j = 0; j <= row.getLastCellNum(); j++) {
						HSSFCell cell = row.getCell(j);
						if (cell != null) {
							int cellType = cell.getCellType();
							if (cellType == HSSFCell.CELL_TYPE_STRING) {
								content[i][j] = new String(cell
										.getRichStringCellValue().getString());
							} else if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
								content[i][j] = new Double(
										cell.getNumericCellValue());
							} else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
								content[i][j] = "";
							} else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {
								content[i][j] = new Boolean(
										cell.getBooleanCellValue());
							} else if (cellType == HSSFCell.CELL_TYPE_FORMULA) {
								content[i][j] = new String(
										cell.getCellFormula());
							} else if (cellType == HSSFCell.CELL_TYPE_ERROR) {
								content[i][j] = new String(
										"Error Code 0x"
												+ Integer
														.toHexString(
																cell.getErrorCellValue() | 0x100)
														.substring(1));
							} else {
								content[i][j] = "";
							}
						} else {
							content[i][j] = "";
						}
					}
				} else {
					content[i] = new Object[] {};
				}
			}
		}
		return content;
	}

	public int getNumOfRows() throws ExcelException {
		return getNumOfRows(null);
	}

	public int getNumOfRows(String sheetName) throws ExcelException {
		HSSFSheet sheet = getSheet(sheetName, false);
		if (sheet == null) {
			return 0;
		}
		return sheet.getLastRowNum() + 1;
	}

	public int getNumOfCells(int rowIndex) throws ExcelException {
		return getNumOfCells(null, rowIndex);
	}

	public int getNumOfCells(String sheetName, int rowIndex)
			throws ExcelException {
		HSSFSheet sheet = getSheet(sheetName, false);
		if (sheet == null || (sheet.getLastRowNum() < rowIndex || rowIndex < 0)) {
			return 0;
		}
		return sheet.getRow(rowIndex).getLastCellNum() + 1;
	}

	public HSSFCell getCell(int rowNum, int cellNum) throws ExcelException {
		return getCell(null, rowNum, cellNum);
	}

	public HSSFCell getCell(String sheetName, int rowNum, int cellNum)
			throws ExcelException {
		HSSFRow row = getRow(sheetName, rowNum);
		if (row == null) {
			return null;
		}
		return row.getCell(cellNum);
	}

	public HSSFRow getRow(int rowNum) throws ExcelException {
		return getRow(null, rowNum);
	}

	public HSSFRow getRow(String sheetName, int rowNum) throws ExcelException {
		HSSFSheet sheet = getSheet(sheetName, false);
		if (sheet == null || rowNum > sheet.getLastRowNum()) {
			return null;
		}
		return sheet.getRow(rowNum);
	}

	/**
	 * Service Methods
	 */

	private HSSFSheet createNewSheet(String sheetName) throws ExcelException {
		HSSFSheet sheet = workbook.createSheet(sheetName);
		writeToFile();
		return sheet;
	}

	private void writeToFile() throws ExcelException {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(xlsFile, false);
			workbook.write(fileOut);
		} catch (Exception e) {
			throw new ExcelWriteToFileException(e);
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (Exception e) {
					throw new ExcelException("Failed To Close Out Stream", e);
				}
			}
		}
	}

}
