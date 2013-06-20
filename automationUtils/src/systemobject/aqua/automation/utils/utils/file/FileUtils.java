package systemobject.aqua.automation.utils.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Uri.Koaz
 */
public class FileUtils {

	public static List<File> listFiles(File dir, boolean withSubFolders) {
		List<File> list = new ArrayList<File>();
		if (dir.exists()) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File f : files) {
					if (f.isDirectory() && withSubFolders) {
						list.addAll(listFiles(f, withSubFolders));
					} else {
						list.add(f);
					}
				}
			} else {
				list.add(dir);
			}
		}
		return list;
	}

	public static void copyDirectory(File srcPath, File dstPath)
			throws IOException {
		System.out.println("Copy Directory: " + srcPath.getAbsolutePath()
				+ " to " + dstPath.getAbsolutePath());
		if (srcPath.isDirectory()) {
			if (!dstPath.exists()) {
				dstPath.mkdir();
			}
			String files[] = srcPath.list();
			for (int i = 0; i < files.length; i++) {
				copyDirectory(new File(srcPath, files[i]), new File(dstPath,
						files[i]));
			}
		} else {
			if (!srcPath.exists()) {
				System.out.println("File or directory does not exist.");
				return;
			} else {
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
	}

	public static boolean deleteFile(String fileName) {
		return deleteFile(new File(fileName));
	}

	public static boolean deleteFile(File file) {
		if (!file.exists() || file.isDirectory()) {
			return false;
		}
		file.delete();
		return true;
	}

	public static boolean deleteFilesInDirectory(File dirPath) {

		return deleteFilesInDirectory(dirPath.getAbsolutePath());
	}

	public static boolean deleteFilesInDirectory(String dirPath) {

		if (dirPath == null) {
			return false;
		}

		File f = new File(dirPath);

		if (f.exists()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					files[i].delete();
				}
			}
		}

		return true;
	}

	public static boolean deleteDirectory(String path) {
		return deleteDirectory(new File(path));
	}

	public static boolean deleteDirectory(File path) {

		if (path == null) {
			return false;
		}

		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}

		return (path.delete());
	}

	public static void writeStringToFile(String pathToFile, String toWrite)
			throws Exception {
		FileWriter fw = new FileWriter(new File(pathToFile));
		try {
			fw.write(toWrite);
			fw.flush();
		} finally {
			fw.close();
		}
	}

	public static void writeStringToFile(File file, String toWrite)
			throws Exception {
		FileWriter fw = new FileWriter(file);
		try {
			fw.write(toWrite);
			fw.flush();
		} finally {
			fw.close();
		}
	}

	public static String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		try {
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		} finally {
			reader.close();
		}
		return fileData.toString();
	}

	/**
	 * loads and returns the given file as StringBuffer
	 * 
	 * @param fileName
	 *            name and path of the requested file to load
	 * @return file content as StringBuffer
	 * @throws Exception
	 */
	public static StringBuffer getFileContent(String fileName) throws Exception {
		StringBuffer buf = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String str;
		while ((str = in.readLine()) != null) {
			buf.append(str + "\r\n");
		}
		in.close();
		return buf;

	}

	/**
	 * copy old file into new file without old file deletion
	 * 
	 * @param oldFile
	 *            old file path and name
	 * @param newFile
	 *            new file path and name
	 * @throws IOException
	 */
	public static void copyFile(String oldFile, String newFile)
			throws IOException {
		copyFile(oldFile, newFile, false);
	}

	/**
	 * copy old file into new file with/out old file deletion
	 * 
	 * @param oldFile
	 *            old file path and name
	 * @param newFile
	 *            new file path and name
	 * @param delOldFile
	 *            true for delete the old file after copy
	 * @throws IOException
	 */
	public static void copyFile(String oldFile, String newFile,
			boolean delOldFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(oldFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
		String str;
		while ((str = in.readLine()) != null) {
			out.append(str);
			out.flush();
		}
		out.flush();
		out.close();
		in.close();

		if (delOldFile) {
			File f = new File(oldFile);
			if (f != null) {
				f.delete();
			}
		}
	}
}
