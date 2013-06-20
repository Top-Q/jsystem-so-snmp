package systemobject.aqua.automation.utils.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import jsystem.framework.system.SystemObjectImpl;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

/**
 * @author Uri.Koaz
 */
public class SFtpConnector extends SystemObjectImpl {

	private String server;

	private String username;

	private String password;

	private static DefaultFileSystemManager fsManager;

	private static FileSystemOptions fsOptions;

	/**
	 * @param server
	 *            FTP server IP
	 * @param username
	 *            FTP server user name
	 * @param password
	 *            FTP server password
	 * @param connModePassive
	 *            "true" for passive connection, "false" for active connection
	 * @throws FileSystemException
	 */

	public SFtpConnector(String server, String username, String password,
			boolean connModePassive) throws FileSystemException {
		this.password = password;
		this.server = server;
		this.username = username;
		fsOptions = new FileSystemOptions();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
				fsOptions, "no");
		// now we create a new filesystem manager
		fsManager = (DefaultFileSystemManager) VFS.getManager();

	}

	/**
	 * Copies a local file to remote filesystem.
	 */

	public void upload(String localPath, String remotePath) throws IOException {
		// adding the fileName to remote path

		// File local= new File(localPath);
		// remotePath=remotePath + local.getName();

		// the url is of form sftp://user:pass@host/remotepath/
		String uri = "sftp://" + this.username + ":" + this.password + "@"
				+ this.server + "/" + remotePath;
		System.out.println("URI is: " + uri);
		// get file object representing the local file
		FileObject fo = fsManager.resolveFile(uri, fsOptions);

		fo.createFile();

		// open input stream from the remote file
		BufferedOutputStream os = new BufferedOutputStream(fo.getContent()
				.getOutputStream());
		// open output stream to local file
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(
				localPath));

		// do copying
		byte[] buf = new byte[2048];
		int len;
		while ((len = is.read(buf)) > 0) {
			os.write(buf, 0, len);
		}
		os.close();
		is.close();
		// close the file object
		fo.close();
		System.out.println("Finished copying the file");

	}

	/**
	 * Copies a remote file to local filesystem.
	 */
	public void copyRemoteFile(String remotePath, String localPath)
			throws IOException {
		// the url is of form sftp://user:pass@host/remotepath/
		String uri = "sftp://" + this.username + ":" + this.password + "@"
				+ this.server + "/" + remotePath;
		// get file object representing the local file
		FileObject fo = fsManager.resolveFile(uri, fsOptions);
		// open input stream from the remote file
		BufferedInputStream is = new BufferedInputStream(fo.getContent()
				.getInputStream());
		// InputStream is = fo.getContent().getInputStream();

		// open output stream to local file
		OutputStream os = new BufferedOutputStream(new FileOutputStream(
				localPath));
		byte[] buf = new byte[8192];
		// int len;
		// while ((len = is.read(buf)) > 0) {
		// os.write(buf, 0, len);
		// }

		int bytesread = 0, bytesBuffered = 0;
		while ((bytesread = is.read(buf)) > -1) {
			os.write(buf, 0, bytesread);
			bytesBuffered += bytesread;
			if (bytesBuffered > 1024 * 1024) {
				bytesBuffered = 0;
				os.flush();
			}
		}

		os.close();
		is.close();
		// close the file object
		fo.close();
		// NOTE: if you close the file system manager, you won't be able to
		// use VFS again in the same VM. If you wish to copy multiple files,
		// make the fsManager static, initialize it once, and close just
		// before exiting the process.

		System.out.println("Finished copying the file");
	}

	public static void main(String[] args) throws Exception {
		SFtpConnector vfsCon = new SFtpConnector("192.168.107.156", "oracle10",
				"oracle10", false);

		FileObject[] fileList = vfsCon
				.getListOfFiles("/sdh_home/oracle10/admin/ora10/dpdump/");

		fileList = vfsCon
				.getListOfFiles("/sdh_home/oracle10/admin/ora10/dpdump/");

		vfsCon.close();

		vfsCon = new SFtpConnector("192.168.107.156", "oracle10", "oracle10",
				false);

		fileList = vfsCon
				.getListOfFiles("/sdh_home/oracle10/admin/ora10/dpdump/");
		fileList = vfsCon
				.getListOfFiles("/sdh_home/oracle10/admin/ora10/dpdump/");

		System.out.println(fileList.length);

	}

	public void close() {
		// NOTE: if you close the file system manager, you won't be able to
		// use VFS again in the same VM. If you wish to copy multiple files,
		// make the fsManager static, initialize it once, and close just
		// before exiting the process.

		fsManager.close();

	}

	public FileObject[] getListOfFiles(String remoteDir) throws IOException {
		return getListAll(remoteDir, true, false);
	}

	public FileObject[] getListOfFolders(String remoteDir) throws IOException {
		return getListAll(remoteDir, false, true);
	}

	public FileObject[] getListAll(String remoteDir, boolean onlyFiles,
			boolean onlyDirs) throws IOException {
		// remoteDir = fixFtpPath(remoteDir);
		// if (!isFolder(remoteDir)) {
		// return new FTPFile[] {};
		// }
		// FTPFile[] files = getAllFiles(remoteDir, false);
		// if (files == null) {
		// return new FTPFile[] {};
		// }
		// ArrayList<FTPFile> list = new ArrayList<FTPFile>();
		// for (FTPFile f : files) {
		// if (f != null) {
		// if ((onlyFiles && f.isFile()) || (onlyDirs && f.isDirectory())
		// || (!onlyDirs && !onlyFiles)) {
		// list.add(f);
		// }
		// }
		// }
		// return list.toArray(new FTPFile[list.size()]);

		// we first set strict key checking off
		FileSystemOptions fsOptions = new FileSystemOptions();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
				fsOptions, "no");
		// now we create a new filesystem manager

		// the url is of form sftp://user:pass@host/remotepath/
		String uri = "sftp://" + this.username + ":" + password + "@"
				+ this.server + "/" + remoteDir;
		// get file object representing the local file
		FileObject fo = fsManager.resolveFile(uri, fsOptions);
		// open input stream from the remote file
		FileObject[] filesInFolder = fo.getChildren();
		ArrayList<FileObject> list = new ArrayList<FileObject>();
		for (FileObject f : filesInFolder) {
			if (f != null) {
				if ((onlyFiles && f.getType() == FileType.FILE)
						|| (onlyDirs && f.getType() == FileType.FOLDER)
						|| (!onlyDirs && !onlyFiles)) {
					list.add(f);
				}

			}
		}

		System.out.println("Finished retriving file list from directory");
		return list.toArray(new FileObject[list.size()]);
	}

}
