package systemobject.aqua.automation.utils.utils.jarExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Generic Class, can be used outside JSystem Project This Class should be used
 * to extract JAR content in order to get file/s from its content (such as MIB
 * files)
 * 
 * @author Uri.Koaz
 */
public class JarExtractor {

	private static void writeFile(byte[] buffer, InputStream in, File file)
			throws IOException {
		File dir = file.getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		int read = -1;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		out.flush();
		out.close();
	}

	/**
	 * extracts a jar file content
	 * 
	 * @param jarLocation
	 *            full path of the jar file location
	 * @param dest
	 *            location to extract the content of the jar file to
	 * @throws IOException
	 */
	public static void extractJar(String jarLocation, String dest)
			throws IOException {
		File ear = new File(jarLocation);
		File deploy = new File(dest);

		JarFile jar = new JarFile(ear);

		byte[] buffer = new byte[8192];
		for (Enumeration<?> e = jar.entries(); e.hasMoreElements();) {
			JarEntry entry = (JarEntry) e.nextElement();
			File entryFile = new File(deploy, entry.getName());
			if (entry.isDirectory()) {
				entryFile.mkdirs();
			} else {
				InputStream in = jar.getInputStream(entry);
				writeFile(buffer, in, entryFile);
				in.close();
			}
		}

		jar.close();
	}

	public static List<StringBuilder> readMultipleTxtFilesContentInsideJar(
			Object caller, String... listOfRelativePathesInJar)
			throws IOException {
		List<StringBuilder> sb = null;
		if (listOfRelativePathesInJar != null
				&& listOfRelativePathesInJar.length > 0) {
			sb = new ArrayList<StringBuilder>(listOfRelativePathesInJar.length);
			for (int i = 0; i < listOfRelativePathesInJar.length; i++) {
				sb.add(readTxtFileContentInsideJar(caller,
						listOfRelativePathesInJar[i]));
			}
		}
		return sb;
	}

	public static StringBuilder readTxtFileContentInsideJar(Object caller,
			String relativePathInJar) throws IOException {
		relativePathInJar = relativePathInJar.replace("\\", "/"); // convert to
																	// packages
																	// format
		StringBuilder sb = new StringBuilder();
		List<Byte> list = readFileContentInsideJar(caller, relativePathInJar);
		Iterator<Byte> i = list.iterator();
		while (i.hasNext()) {
			sb.append((char) i.next().byteValue());
		}
		return sb;
	}

	public static List<List<Byte>> readMultipleFilesContentInsideJar(
			Object caller, String... listOfRelativePathesInJar)
			throws IOException {
		List<List<Byte>> list = null;
		if (listOfRelativePathesInJar != null
				&& listOfRelativePathesInJar.length > 0) {
			list = new ArrayList<List<Byte>>(listOfRelativePathesInJar.length);
			for (int i = 0; i < listOfRelativePathesInJar.length; i++) {
				list.add(readFileContentInsideJar(caller,
						listOfRelativePathesInJar[i]));
			}
		}
		return list;
	}

	public static List<Byte> readFileContentInsideJar(Object caller,
			String relativePathInJar) throws IOException {
		relativePathInJar = relativePathInJar.replace("\\", "/"); // convert to
																	// packages
																	// format
		InputStream in = caller.getClass().getClassLoader()
				.getResourceAsStream(relativePathInJar);
		byte[] buf = new byte[1024];
		int len;
		List<Byte> list = new LinkedList<Byte>();
		while ((len = in.read(buf)) > 0) {
			for (int i = 0; i < len; i++) {
				list.add(buf[i]);
			}
		}
		in.close();
		return list;
	}

	public static void copyFileFromJar(Object caller, String relativePathInJar,
			String dstPath) throws IOException {
		relativePathInJar = relativePathInJar.replace("\\", "/"); // convert to
																	// packages
																	// format
		dstPath = dstPath.replace("/", "\\");

		InputStream in = caller.getClass().getClassLoader()
				.getResourceAsStream(relativePathInJar);
		OutputStream out = new FileOutputStream(dstPath);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

}
