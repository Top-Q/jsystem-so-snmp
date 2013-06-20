package systemobject.aqua.automation.utils.ftp;

import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

import org.apache.commons.net.ftp.FTPFile;

public class UnitTestFtp extends SystemTestCase {

	private FtpConnector ftp = null;

	private String user = "l2sw";

	private String pass = "123456";

	private String host = "192.168.20.25";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ftp = new FtpConnector(host, user, pass, false);
	}

	public void testFtpCreateRemoteFolder() throws Exception {
		long t = System.currentTimeMillis();
		String folderName = "f_" + (t++);
		try {
			ftp.createRemoteFolder(folderName);
			boolean exist = ftp.isExistOnServer(folderName);
			report.report("expected folder exist on server, actual folder "
					+ (exist ? "exist" : "not exist"), exist);
		} finally {
			ftp.deleteRemoteFolder(folderName);
		}
	}

	public void testFtpDeleteRemoteFolder() throws Exception {
		long t = System.currentTimeMillis();
		String folderName = "f_" + (t++);
		try {
			ftp.createRemoteFolder(folderName);
			boolean exist = ftp.isExistOnServer(folderName);
			report.report("expected folder exist from server, actual folder "
					+ (exist ? "exist" : "not exist"), exist);
			ftp.deleteRemoteFolder(folderName);
			exist = ftp.isExistOnServer(folderName);
			report.report(
					"expected folder not exist from server, actual folder "
							+ (exist ? "exist" : "not exist"), !exist);
		} finally {
			ftp.deleteRemoteFolder(folderName);
		}
	}

	public void testFtpDeleteRemoteFile() throws Exception {
		String fileName = "" + System.currentTimeMillis() + ".txt";
		try {
			FileUtils.write(fileName, "temp file");
			ftp.transferFileToServer(fileName, fileName);
			boolean exist = ftp.isExistOnServer(fileName);
			report.report("expected file exist from server, actual file "
					+ (exist ? "exist" : "not exist"), exist);
			ftp.deleteRemoteFile(fileName);
			exist = ftp.isExistOnServer(fileName);
			report.report("expected file not exist from server, actual file "
					+ (exist ? "exist" : "not exist"), !exist);
		} finally {
			FileUtils.deleteFile(fileName);
			ftp.deleteRemoteFile(fileName);
		}
	}

	public void testFtpGetListAll() throws Exception {
		long t = System.currentTimeMillis();
		String[] fileName = new String[] { (t++) + ".txt", (t++) + ".bin" };
		String folderName = "f_" + (t++);

		try {
			FTPFile[] before = ftp.getListAll("/", false, false);
			ftp.createRemoteFolder(folderName);
			FTPFile[] after = ftp.getListAll("/", false, false);
			report.report("expected " + (before.length + 1)
					+ " files and folders on remote machine, actual "
					+ after.length, (before.length + 1) == after.length);
			before = after;
			for (int i = 0; i < fileName.length; i++) {
				FileUtils.write(fileName[i], "temp file " + i);
				ftp.transferFileToServer(folderName + "/" + fileName[i],
						fileName[i]);
				FileUtils.deleteFile(fileName[i]);
			}
			after = ftp.getListAll("/", false, false);
			report.report("expected " + before.length
					+ " files and folders on remote machine, actual "
					+ after.length, before.length == after.length);
			after = ftp.getListAll(folderName, false, false);
			report.report("expected " + fileName.length
					+ " files and folders on remote machine, actual "
					+ after.length, fileName.length == after.length);
			ftp.createRemoteFolder(folderName + "/" + folderName);
			after = ftp.getListAll(folderName, false, false);
			report.report("expected " + (fileName.length + 1)
					+ " files and folders on remote machine, actual "
					+ after.length, (fileName.length + 1) == after.length);
		} finally {
			ftp.deleteRemoteFolder(folderName);
		}
	}

	public void testFtpGetListOfFiles() throws Exception {
		long t = System.currentTimeMillis();
		String[] fileName = new String[] { (t++) + ".txt", (t++) + ".bin" };
		String folderName = "f_" + (t++);

		try {
			FTPFile[] before = ftp.getListOfFiles("/");
			ftp.createRemoteFolder(folderName);
			FTPFile[] after = ftp.getListOfFiles("/");
			report.report("expected " + before.length
					+ " files on remote machine, actual " + after.length,
					before.length == after.length);
			before = after;
			for (int i = 0; i < fileName.length; i++) {
				FileUtils.write(fileName[i], "temp file " + i);
				ftp.transferFileToServer(folderName + "/" + fileName[i],
						fileName[i]);
				FileUtils.deleteFile(fileName[i]);
			}
			after = ftp.getListOfFiles("/");
			report.report("expected " + before.length
					+ " files on remote machine, actual " + after.length,
					before.length == after.length);
			after = ftp.getListOfFiles(folderName);
			report.report("expected " + fileName.length
					+ " files on remote machine, actual " + after.length,
					fileName.length == after.length);
			ftp.createRemoteFolder(folderName + "/" + folderName);
			after = ftp.getListOfFiles(folderName);
			report.report("expected " + fileName.length
					+ " files on remote machine, actual " + after.length,
					fileName.length == after.length);
		} finally {
			ftp.deleteRemoteFolder(folderName);
		}
	}

	public void testFtpGetListOfFolders() throws Exception {
		long t = System.currentTimeMillis();
		String[] fileName = new String[] { (t++) + ".txt", (t++) + ".bin" };
		String folderName = "f_" + (t++);

		try {
			FTPFile[] before = ftp.getListOfFolders("/");
			ftp.createRemoteFolder(folderName);
			FTPFile[] after = ftp.getListOfFolders("/");
			report.report("expected " + (before.length + 1)
					+ " folders on remote machine, actual " + after.length,
					(before.length + 1) == after.length);
			before = after;
			for (int i = 0; i < fileName.length; i++) {
				FileUtils.write(fileName[i], "temp file " + i);
				ftp.transferFileToServer(folderName + "/" + fileName[i],
						fileName[i]);
				FileUtils.deleteFile(fileName[i]);
			}
			after = ftp.getListOfFolders("/");
			report.report("expected " + before.length
					+ " folders on remote machine, actual " + after.length,
					before.length == after.length);
			after = ftp.getListOfFolders(folderName);
			report.report("expected 0 folders on remote machine, actual "
					+ after.length, 0 == after.length);
			ftp.createRemoteFolder(folderName + "/" + folderName);
			after = ftp.getListOfFolders(folderName);
			report.report("expected 1 folders on remote machine, actual "
					+ after.length, 1 == after.length);
		} finally {
			ftp.deleteRemoteFolder(folderName);
		}
	}

	public void testFtpCheckType() throws Exception {
		long t = System.currentTimeMillis();
		String fileName = (t++) + ".txt";
		String folderName = "f_" + (t++);

		try {
			ftp.createRemoteFolder(folderName);
			FileUtils.write(fileName, "temp file ");
			ftp.transferFileToServer(fileName, fileName);
			boolean status = ftp.isFile(fileName);
			report.report("check file: expected type file, actual "
					+ (status ? "" : "Not ") + "File", status);
			status = ftp.isFolder(fileName);
			report.report("check file: expected type not folder, actual "
					+ (status ? "" : "Not ") + "Folder", !status);
			status = ftp.isFile(folderName);
			report.report("check folder: expected type not file, actual "
					+ (status ? "" : "Not ") + "File", !status);
			status = ftp.isFolder(folderName);
			report.report("check folder: expected type folder, actual "
					+ (status ? "" : "Not ") + "Folder", status);
		} finally {
			FileUtils.deleteFile(fileName);
			ftp.deleteRemoteFile(fileName);
			ftp.deleteRemoteFolder(folderName);
		}
	}

	public void testFtpTransferFileToServer() throws Exception {
		String fileName = "" + System.currentTimeMillis() + ".txt";
		try {
			FileUtils.write(fileName, "temp file");
			ftp.transferFileToServer(fileName, fileName);
			boolean exist = ftp.isExistOnServer(fileName);
			report.report("expected file exist from server, actual file "
					+ (exist ? "exist" : "not exist"), exist);
		} finally {
			FileUtils.deleteFile(fileName);
			ftp.deleteRemoteFile(fileName);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		if (ftp != null) {
			ftp.close();
		}
		super.tearDown();
	}

}
