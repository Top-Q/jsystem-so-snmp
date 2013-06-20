package systemobject.aqua.automation.utils.utils.string;

/**
 * @author Itzhak.Hovav
 */
public abstract class StringFormatUtils {

	public static String formatString(String str, int lineMaxLength,
			int numOfIndentationTabs, boolean indentFirstLine,
			boolean removeDoubleSpaces) {
		if (!"".equals(str)) {
			if (removeDoubleSpaces) {
				while (str.contains("  ")) {
					str = str.replace("  ", " ");
				}
			}
			int spaceIndex;
			int newLineIndex;
			int index;
			StringBuilder sb = new StringBuilder();
			if (indentFirstLine) {
				sb.append(indent(numOfIndentationTabs));
			}
			while (str.length() > lineMaxLength) {
				newLineIndex = str.indexOf('\n');
				spaceIndex = (str.substring(0, lineMaxLength)).lastIndexOf(' ');
				if (newLineIndex == -1) {
					index = spaceIndex;
				} else if (spaceIndex == -1) {
					index = newLineIndex;
				} else {
					index = Math.min(newLineIndex, spaceIndex);
				}
				if (index == -1) {
					break;
				} else {
					sb.append(str.substring(0, index));
					sb.append("\n");
					sb.append(indent(numOfIndentationTabs));
					str = str.substring(index + 1);
				}
			}
			sb.append(str);
			str = sb.toString();
		}
		return str;
	}

	private static String indent(int numOfTabs) {
		char space = ' ';
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (4 * numOfTabs); i++) {
			sb.append(space);
		}
		return sb.toString();
	}

}
