package systemobject.aqua.automation.utils.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.DateUtils;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import systemobject.aqua.automation.utils.utils.exception.ExceptionUtils;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class FtpConnector extends SystemObjectImpl {

	private FileWriter ftpLogFile = null;

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private String server;

	private String username;

	private String password;

	private boolean connModePassive;

	private FTPClient ftp;

	/**
	 * @author Itzhak.Hovav
	 */
	private class PrintCommandListener implements ProtocolCommandListener {

		public void protocolCommandSent(ProtocolCommandEvent event) {
			log("Sent: \n" + event.getMessage());
		}

		public void protocolReplyReceived(ProtocolCommandEvent event) {
			log("Responce: \n" + event.getMessage());
		}

		private void log(String msg) {
			try {
				if (ftpLogFile == null) {
					ftpLogFile = new FileWriter(new File(
							report.getCurrentTestFolder() + "/ftpLogFile.txt"),
							true);
				} else {
					ftpLogFile.append("\n");
				}
				msg = msg.replace("\r", "\n");
				while (msg.contains("\n\n")) {
					msg = msg.replace("\n\n", "\n");
				}
				if (msg.endsWith("\n")) {
					msg = msg.substring(0, msg.length() - 1);
				}
				msg = msg.replace("\n", "\n\t\t\t\t");
				ftpLogFile.append(DateUtils.getDate(System.currentTimeMillis(),
						sdf));
				ftpLogFile.append(" - ");
				ftpLogFile.append(msg);
				ftpLogFile.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {
		if (this.ftp != null) {
			try {
				this.ftp.logout();
			} catch (Exception f) {
				report.report(
						"Ftp: Error on Logging Out from server: "
								+ f.getMessage(),
						"Reply String: " + getReplyStr() + "\n\n\n"
								+ ExceptionUtils.setStackTrace(f), true);
			} finally {
				if (this.ftp.isConnected()) {
					try {
						this.ftp.disconnect();
					} catch (Exception f) {
						report.report(
								"Ftp: Error on disconnecting from server: "
										+ f.getMessage(), "Reply String: "
										+ getReplyStr() + "\n\n\n"
										+ ExceptionUtils.setStackTrace(f), true);
					}
				}
			}
			ftp = null;
		}
		super.close();
	}

	/**
	 * @param server
	 *            FTP server IP
	 * @param username
	 *            FTP server user name
	 * @param password
	 *            FTP server password
	 * @param connModePassive
	 *            "true" for passive connection, "false" for active connection
	 */
	public FtpConnector(String server, String username, String password,
			boolean connModePassive) {
		this.connModePassive = connModePassive;
		this.password = password;
		this.server = server;
		this.username = username;
		this.ftp = new FTPClient();
		this.ftp.addProtocolCommandListener(new PrintCommandListener());
	}

	/**
	 * connects to the server if not connected
	 * 
	 * @throws IOException
	 */
	public void connectToServer() throws IOException {
		if (this.ftp != null && !this.ftp.isConnected()) {
			try {
				this.ftp.connect(this.server);
				report.report("Ftp: Connected to " + this.server,
						getReplyStr(), true);
				if (!FTPReply.isPositiveCompletion(this.ftp.getReplyCode())) {
					try {
						throw new IOException("FTP server refused connection: "
								+ getReplyStr());
					} finally {
						this.ftp.disconnect();
					}
				}
			} catch (Exception e) {
				report.report(
						"Ftp: Error on Logging In to server: " + e.getMessage(),
						"Reply String: " + getReplyStr() + "\n\n\n"
								+ ExceptionUtils.setStackTrace(e), true);
				close();
				throw new IOException("Could not connect to server", e);
			}
			if (!this.ftp.login(this.username, this.password)) {
				close();
				throw new IOException("Could not login to server: "
						+ getReplyStr());
			}
			report.report("Ftp: Remote system Info", this.ftp.getSystemName(),
					true);
			if (this.connModePassive) {
				this.ftp.enterLocalPassiveMode();
			} else {
				this.ftp.enterLocalActiveMode();
			}
		}
	}

	private String getReplyStr() {
		StringBuilder sb = new StringBuilder("Reply Strings From Ftp Server:");
		String[] arr = null;
		try {
			arr = this.ftp.getReplyStrings();
		} catch (Exception e) {
			arr = new String[] { "Exception While Getting Reply Strings" };
		}
		if (arr == null) {
			arr = new String[] { "Reply Strings Returned \"null\"" };
		}
		for (String s : arr) {
			sb.append("\n\t");
			sb.append(s);
		}
		return sb.toString();
	}

	public boolean deleteRemoteFile(String remoteFile) throws IOException {
		try {
			remoteFile = fixFtpPath(remoteFile);
			connectToServer();
			report.report("Ftp: Delete Remote File " + remoteFile,
					getReplyStr(), true);
			return this.ftp.deleteFile(remoteFile);
		} catch (FTPConnectionClosedException e) {
			throw new IOException("Server closed connection", e);
		} catch (Exception e) {
			throw new IOException("Could Not Delete File: " + remoteFile, e);
		}
	}

	public boolean deleteRemoteFolder(String remoteDir) throws IOException {
		remoteDir = fixFtpPath(remoteDir);
		boolean status = false;
		report.startLevel("Ftp: Delete Remote Directory " + remoteDir,
				EnumReportLevel.CurrentPlace);
		status = deleteInnerRemoteDirs(remoteDir);
		connectToServer();
		status &= this.ftp.removeDirectory(fixFtpPath(remoteDir));
		report.report("Ftp: Delete Romote Directory " + remoteDir,
				getReplyStr(), true);
		report.stopLevel();
		return status;
	}

	private boolean deleteInnerRemoteDirs(String remoteDir) throws IOException {
		try {
			remoteDir = fixFtpPath(remoteDir);
			boolean status = true;
			FTPFile[] files = getAllFiles(remoteDir, false);
			if (files != null) {
				for (FTPFile f : files) {
					if (f != null) {
						if (f.isDirectory()) {
							status &= deleteInnerRemoteDirs(buildFileName(
									remoteDir, f.getName()));
							connectToServer();
							status &= this.ftp.removeDirectory(buildFileName(
									remoteDir, f.getName()));
						} else {
							status &= deleteRemoteFile(buildFileName(remoteDir,
									f.getName()));
						}
					}
				}
			}
			return status;
		} catch (FTPConnectionClosedException e) {
			throw new IOException("Server closed connection", e);
		} catch (Exception e) {
			throw new IOException("Exception In Ftp Client: " + getReplyStr(),
					e);
		}
	}

	public boolean createRemoteFolder(String remoteFolder) throws IOException {
		try {
			remoteFolder = fixFtpPath(remoteFolder);
			connectToServer();
			return this.ftp.makeDirectory(remoteFolder);
		} catch (FTPConnectionClosedException e) {
			throw new IOException("Server closed connection", e);
		} catch (Exception e) {
			throw new IOException("Exception In Ftp Client: " + getReplyStr(),
					e);
		}
	}

	public void transferFileToServer(String remoteFile, String localFile)
			throws IOException {
		transferFile(remoteFile, localFile, true, true);
	}

	public void transferFileFromServer(String remoteFile, String localFile)
			throws IOException {
		transferFile(remoteFile, localFile, false, true);
	}

	/**
	 * for transfer a file from/to the server (ASCII/Binary mode)
	 * 
	 * @param remoteFile
	 *            Remote file name on server (including path inside the FTP
	 *            root)
	 * @param localFile
	 *            local file name (including path)
	 * @param toFtpServer
	 *            "true" to put the local file on server, "false" to take file
	 *            from server to local machine
	 * @param binaryTransfer
	 *            "true" for binary file transfer, "false" for ASCII file
	 *            transfer
	 * @throws IOException
	 */
	private void transferFile(String remoteFile, String localFile,
			boolean toFtpServer, boolean binaryTransfer) throws IOException {
		try {
			remoteFile = fixFtpPath(remoteFile);
			localFile = fixFtpPath(localFile);

			connectToServer();

			if (binaryTransfer) {
				this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
			}
			if (toFtpServer) {
				InputStream input = null;
				try {
					input = new FileInputStream(localFile);
					if (!this.ftp.storeFile(remoteFile, input)) {
						throw new IOException(
								"Error on storing file on server: "
										+ getReplyStr());
					}
				} finally {
					if (input != null) {
						input.close();
					}
				}
			} else {
				OutputStream output = null;
				try {
					output = new FileOutputStream(localFile);
					if (!this.ftp.retrieveFile(remoteFile, output)) {
						throw new IOException(
								"Error on retrieving file from server: "
										+ getReplyStr());
					}
				} finally {
					output.close();
				}
			}
		} catch (FTPConnectionClosedException e) {
			throw new IOException("Server closed connection", e);
		} catch (Exception e) {
			throw new IOException("Exception In Ftp Client: " + getReplyStr(),
					e);
		}
		report.report(
				String.format(
						"Ftp: Transfer \"%s\" File %s Ftp Server %s Local Machine \"%s\"",
						remoteFile, (toFtpServer ? "To" : "From"),
						(toFtpServer ? "From" : "To"), localFile),
				getReplyStr(), true);
	}

	public void transferFolderFromServer(String remoteDir, String localDir)
			throws IOException {
		remoteDir = fixFtpPath(remoteDir);
		localDir = fixFtpPath(localDir);
		File dir = new File(localDir);
		dir.mkdirs();
		report.startLevel("Transfer Directory From Remote Location \""
				+ remoteDir + "\" To Local Machine \"" + localDir + "\"",
				EnumReportLevel.CurrentPlace);
		FTPFile[] files = getAllFiles(remoteDir, false);
		if (files != null) {
			for (FTPFile f : files) {
				if (f != null) {
					if (f.isDirectory()) {
						transferFolderFromServer(
								buildFileName(remoteDir, f.getName()),
								buildFileName(localDir, f.getName()));
					} else {
						transferFileFromServer(
								buildFileName(remoteDir, f.getName()),
								buildFileName(localDir, f.getName()));
					}
				}
			}
		}
		report.stopLevel();
	}

	public void transferFolderToServer(String remoteDir, String localDir)
			throws IOException {
		remoteDir = fixFtpPath(remoteDir);
		localDir = fixFtpPath(localDir);
		report.startLevel("Transfer Directory To Remote Location \""
				+ remoteDir + "\" From Local Machine \"" + localDir + "\"",
				EnumReportLevel.CurrentPlace);
		File dir = new File(localDir);
		createRemoteFolder(remoteDir);
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f != null) {
					if (f.isDirectory()) {
						transferFolderToServer(
								buildFileName(remoteDir, f.getName()),
								buildFileName(localDir, f.getName()));
					} else {
						transferFileToServer(
								buildFileName(remoteDir, f.getName()),
								buildFileName(localDir, f.getName()));
					}
				}
			}
		}
		report.stopLevel();
	}

	public boolean isFolder(String path) {
		path = fixFtpPath(path);
		if (path.trim().equals("") || path.trim().equals("/")) {
			return true;
		}
		FTPFile f = getFilePointer(path, false);
		return (f != null && f.isDirectory());
	}

	public boolean isFile(String path) {
		path = fixFtpPath(path);
		FTPFile f = getFilePointer(path, false);
		return (f != null && f.isFile());
	}

	private FTPFile getFilePointer(String path, boolean file) {
		String fileName = path;
		if (!path.equals("") && !path.equals("/")) {
			int lastIndex = path.lastIndexOf("/");
			int end = path.length();
			if (lastIndex == path.length() - 1) {
				end = lastIndex;
				lastIndex = ((path.substring(0, path.lastIndexOf("/")))
						.lastIndexOf("/"));
			}

			if (lastIndex != -1) {
				fileName = path.substring(lastIndex + 1, end);
			} else {
				fileName = path;
			}
		}
		if (file) {
			path = fixFtpPath(path);
		} else {
			path = fixFtpPath(path);
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
			int index = path.lastIndexOf("/");
			if (index == -1) {
				path = "/";
			} else {
				path = path.substring(0, index);
			}
		}
		if (path == null) {
			return null;
		}
		FTPFile[] files = getAllFiles(path, file);
		if (files == null) {
			return null;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i] != null) {
				if (files[i].getName().equalsIgnoreCase(fileName)) {
					return files[i];
				}
			}
		}
		return null;
	}

	private FTPFile[] getAllFiles(String path, boolean file) {
		if (path == null) {
			path = "";
		}
		path = fixFtpPath(path);
		String root = path;
		if (!path.equals("") && !path.equals("/")) {
			if (file) {
				while (path.endsWith("/")) {
					path = path.substring(0, path.length() - 1);
				}
				int index = path.lastIndexOf("/");
				if (index == -1) {
					root = "/";
				} else {
					root = path.substring(0, index);
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("All Directories And Files In ");
		sb.append(root);
		sb.append(":");
		FTPFile[] files = null;
		String[] fileNames = null;
		ArrayList<FTPFile> list = new ArrayList<FTPFile>();
		try {
			connectToServer();
			files = this.ftp.listFiles(root);
			fileNames = this.ftp.listNames(root);
			if (files == null && fileNames == null) {
				sb.append("\nServer Returned \"null\" As a Responce For Files List Request");
			} else {
				HashSet<String> set = new HashSet<String>();
				if (files != null) {
					for (FTPFile f : files) {
						if (f != null) {
							set.add(f.getName());
							list.add(f);
						}
					}
					for (FTPFile f : files) {
						if (f != null) {
							sb.append("\n\t* Name=");
							sb.append(f.getName());
							if (f.isDirectory()) {
								sb.append(" (Directory)");
							} else if (f.isFile()) {
								sb.append(" (File)");
								sb.append(", Size=");
								sb.append(f.getSize());
							}
						}
					}
					for (String s : fileNames) {
						if (s != null) {
							while (s.startsWith("/")) {
								s = s.substring(1);
							}
							while (s.endsWith("/")) {
								s = s.substring(0, s.length() - 1);
							}
							if (set.contains(s) == false && !s.contains("/")) {
								FTPFile f = new FTPFile();
								f.setName(s);
								f.setType(0);
								list.add(f);
								sb.append("\n\t* Name=");
								sb.append(s);
								sb.append(" (File)");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			sb.append("\nFtp: Exception While Getting List Of Files From Server");
		}

		sb.append("\n\n\n-------------------------------------------\n\n");
		sb.append(getReplyStr());

		report.report("Ftp: Get List Of All Files And Directories In \"" + root
				+ "\"", sb.toString(), true);

		return list.toArray(new FTPFile[list.size()]);
	}

	public boolean isExistOnServer(String path) throws IOException {
		path = fixFtpPath(path);
		FTPFile[] arr = getAllFiles(path, true);
		while (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		int index = path.lastIndexOf("/");
		String name = path.substring(index + 1, path.length());
		index = name.lastIndexOf('.');
		if (index == -1) {
			index = name.length();
		}
		String extToFind = name.substring(index);
		name = name.substring(0, index);
		boolean exist = false;
		if (arr != null) {
			for (FTPFile f : arr) {
				if (f != null) {
					String fName = f.getName();
					index = fName.lastIndexOf("/");
					fName = fName.substring(index + 1, fName.length());
					String extOnServer = "";
					if (f.isFile()) {
						index = fName.lastIndexOf('.');
						if (index == -1) {
							index = fName.length();
						}
						extOnServer = fName.substring(index);
						fName = fName.substring(0, index);
					}
					exist = (fName.equals(name) && extOnServer
							.equalsIgnoreCase(extToFind));
					if (exist) {
						break;
					}
				}
			}
		}
		report.report(String.format("Ftp: \"%s\" %sExist On Server", path,
				(exist ? "" : "Does Not ")), getReplyStr(), true);
		return exist;
	}

	public FTPFile[] getListOfFiles(String remoteDir) throws IOException {
		return getListAll(remoteDir, true, false);
	}

	public FTPFile[] getListOfFolders(String remoteDir) throws IOException {
		return getListAll(remoteDir, false, true);
	}

	public FTPFile[] getListAll(String remoteDir, boolean onlyFiles,
			boolean onlyDirs) throws IOException {
		remoteDir = fixFtpPath(remoteDir);
		if (!isFolder(remoteDir)) {
			return new FTPFile[] {};
		}
		FTPFile[] files = getAllFiles(remoteDir, false);
		if (files == null) {
			return new FTPFile[] {};
		}
		ArrayList<FTPFile> list = new ArrayList<FTPFile>();
		for (FTPFile f : files) {
			if (f != null) {
				if ((onlyFiles && f.isFile()) || (onlyDirs && f.isDirectory())
						|| (!onlyDirs && !onlyFiles)) {
					list.add(f);
				}
			}
		}
		return list.toArray(new FTPFile[list.size()]);
	}

	private String buildFileName(String path, String file) {
		return fixFtpPath(String.format("%s/%s", path, file));
	}

	private String fixFtpPath(String str) {
		if (str != null) {
			str = str.replace("\\", "/");
			if (str.trim().equals("") || str.trim().equals("/")) {
				str = "/";
			} else {
				while (str.contains("//")) {
					str = str.replace(("//"), "/");
				}
				if (str.startsWith("/")) {
					str = str.substring(1);
				}
				if (str.endsWith("/")) {
					str = str.substring(0, str.length() - 1);
				}
			}
		}
		return str;
	}

	public void changeWorkingDirectory(String path) throws IOException {
		if (path == null) {
			changeToParentDirectory();
		} else {
			connectToServer();
			report.report("Ftp: Change Working Directory To \"/" + path + "\"",
					getReplyStr(), true);
			ftp.changeWorkingDirectory(path);
		}
	}

	public void changeToParentDirectory() throws IOException {
		connectToServer();
		report.report("Ftp: Change Working Directory To Parent Directory",
				getReplyStr(), true);
		ftp.changeToParentDirectory();
	}
}
