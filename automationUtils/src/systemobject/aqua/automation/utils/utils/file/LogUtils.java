package systemobject.aqua.automation.utils.utils.file;

import java.io.File;

import jsystem.framework.report.Reporter;
import jsystem.framework.report.RunnerListenersManager;

/**
 * JSystem Class, cannot be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class LogUtils {

	private static Reporter report = RunnerListenersManager.getInstance();

	/**
	 * returns the path to the requested folder according to the given
	 * parameters
	 * 
	 * @param relative
	 *            true for relative path inside the ".../log/current/" log
	 *            folders
	 * @param innerFolder
	 *            name of the inner folder, "null" for no inner folder
	 * @param createIfNotExist
	 *            if the "innerFolder" is not null and the value is "true" it
	 *            creates the inner folder if not exist, if false and the inner
	 *            folder does not exist it returns "null"
	 * @return full/relative path to the current log folder/inner log folder or
	 *         "null" if the inner folder does not exist and the
	 *         "createIfNotExist" parameter is "false"
	 */
	public static String logFolder(boolean relative, String innerFolder,
			boolean createIfNotExist) {

		String folder = new File(report.getCurrentTestFolder())
				.getAbsolutePath().replace("\\", "/").trim()
				+ "/";

		do {
			folder = folder.replace("//", "/");
		} while (folder.contains("//"));

		// remove trailing "/"
		String relativeLocation = folder.substring(0, folder.length() - 1);

		// "cut" the local path from the string, leave the test folder and trail
		// it with "/"
		relativeLocation = relativeLocation.substring(relativeLocation
				.lastIndexOf('/') + 1) + "/";

		if (innerFolder != null) {
			innerFolder = innerFolder.replace("\\", "/").trim();
			do {
				innerFolder = innerFolder.replace("//", "/");
			} while (innerFolder.contains("//"));
			if (innerFolder.startsWith("/")) {
				innerFolder = innerFolder.substring(1).trim();
			}
			if (innerFolder.endsWith("/")) {
				innerFolder = innerFolder
						.substring(0, innerFolder.length() - 1).trim();
			}
			String[] folders = innerFolder.split("\\/");

			for (String fold : folders) {
				if (fold != null && fold.length() > 0) {
					folder = folder + fold + "/";
					relativeLocation = relativeLocation + fold + "/";
					File reportDir = new File(folder);

					if (!reportDir.exists()) {
						if (createIfNotExist) {
							reportDir.mkdir(); // create the non existing
							// folder
						} else {
							return null; // folder does not exist and it
							// should
							// not
							// create it
						}
					}
				}
			}
		}

		if (relative) {
			return relativeLocation;
		}

		return folder;
	}

	/**
	 * gets the full path to the given folder inside the current log folder, if
	 * the inner folder does not exist it creates it
	 * 
	 * @param innerFolder
	 *            folder inside the current test log folder, creates it if not
	 *            exist
	 * @return full path to the folder inside the current test log folder
	 */
	public static String logFolderFullPath(String innerFolder) {
		return logFolder(false, innerFolder, true);
	}

	/**
	 * gets the relative path inside the ".../log/current/" to the given folder
	 * inside the current log folder, if the inner folder does not exist it
	 * creates it
	 * 
	 * @param innerFolder
	 *            folder inside the current test log folder, creates it if not
	 *            exist
	 * @return relative path to the folder inside the current test log folder
	 */
	public static String logFolderRelativePath(String innerFolder) {
		return logFolder(true, innerFolder, true);
	}

	/**
	 * gets the full path to the current log folder
	 * 
	 * @return full path to the current test log folder
	 */
	public static String logFolderFullPath() {
		return logFolderFullPath(null);
	}

	/**
	 * gets the relative path inside the ".../log/current/" to the current log
	 * folder
	 * 
	 * @return relative path to the current test log folder
	 */
	public static String logFolderRelativePath() {
		return logFolderRelativePath(null);
	}

}
