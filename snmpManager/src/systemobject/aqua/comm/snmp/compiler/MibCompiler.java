package systemobject.aqua.comm.snmp.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Itzhak.Hovav
 * 
 */
public class MibCompiler {

	public static void main(String[] args) throws IOException {
		String logFileName = "log.txt";
		File file = new File(logFileName);
		if (file.exists()) {
			file.delete();
		}

		if (args == null || args.length == 0) {
			System.out
					.println("No Target Directories Have Been Given, Cannot Run Compiler");
			System.exit(1);
		}
		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			if (s != null && s.length() > 0) {
				sb.append("Start Compilation : \"" + s + "\"\n");
				sb.append(DefaultMibCompilerImpl.compile(s));
				sb.append("\nEnd Compilation : \"" + s + "\"\n\n");
			}
		}
		sb.append("\n\nDONE.\n");

		FileWriter fw = new FileWriter(new File(logFileName));
		try {
			fw.write(sb.toString());
			fw.flush();
		} finally {
			fw.close();
		}

		System.out.println(sb.toString());
	}

}
