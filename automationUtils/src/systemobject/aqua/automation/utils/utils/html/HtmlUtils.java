package systemobject.aqua.automation.utils.utils.html;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Uri.Koaz
 */
public abstract class HtmlUtils {

	public static final String SEPERATOR = ";";

	/**
	 * Transform Array of String values into HTML table. Each Array member is a
	 * line in the table and Each value in the column seperated with SEPERATOR.
	 * header is optional, If No header required, give null.
	 * 
	 * @param header
	 * @param values
	 * @return
	 */
	public static String toHtmlTable(String[] header, String[] values,
			boolean mainPage) {

		StringBuffer sb = new StringBuffer();

		if (mainPage) {
			sb.append("!DOCTYPE HTML");
		} else {
			sb.append("<!DOCTYPE HTML>");
		}

		sb.append("<table border='1'>");

		if (header != null) {
			sb.append("<tr>");
			for (int i = 0; i < header.length; i++) {
				sb.append("<td  align='center'><b>");
				sb.append(header[i]);
				sb.append("</b></td>");
			}

			sb.append("</tr>");
		}

		for (int i = 0; i < values.length; i++) {

			if (values[i] != null) {

				sb.append("<tr>");

				String[] afterSplit = values[i].split(SEPERATOR);

				for (int j = 0; j < afterSplit.length; j++) {
					sb.append("<td  align='center'>");
					sb.append(afterSplit[j]);
					sb.append("</td>");
				}

				sb.append("</tr>");
			}
		}

		sb.append("</table>");

		return sb.toString();
	}

	public static String toHtmlTable(String[] header, String[] values) {
		return toHtmlTable(header, values, false);
	}

	public static String toHtmlTableWithColor(String[] header, String[] values,
			Boolean[] isPassArray, boolean mainPage) {

		if (isPassArray == null) {
			return toHtmlTable(header, values, mainPage);
		} else {
			StringBuffer sb = new StringBuffer();

			if (mainPage) {
				sb.append("!DOCTYPE HTML");
			} else {
				sb.append("<!DOCTYPE HTML>");
			}

			sb.append("<table border='1'>");

			if (header != null) {
				sb.append("<tr>");
				for (int i = 0; i < header.length; i++) {
					sb.append("<td  align='center'><b>");
					sb.append(header[i]);
					sb.append("</b></td>");
				}

				sb.append("</tr>");
			}

			for (int i = 0; i < values.length; i++) {

				if (values[i] != null) {

					sb.append("<tr>");

					String[] afterSplit = values[i].split(SEPERATOR);

					for (int j = 0; j < afterSplit.length; j++) {
						sb.append("<td  align='center'>");

						String colorEnder = "";

						if (isPassArray[i] != null) {
							if (isPassArray[i]) {
								sb.append("<font color='#5EFB6E'>");
							} else {
								sb.append("<font color='red'>");
							}
							colorEnder = "</font>";
						}

						sb.append(afterSplit[j]);
						sb.append(colorEnder);
						sb.append("</td>");
					}

					sb.append("</tr>");
				}
			}

			sb.append("</table>");

			return sb.toString();
		}
	}

	public static String toHtmlTableWithColor(String[] header, String[] values,
			Boolean[] isPassArray) {
		return toHtmlTableWithColor(header, values, isPassArray, false);

	}

	public static String toHtmlTableWithColorPerCell(String[] header,
			String[][] values, boolean[][] isPass) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table border='1'>");

		if (header != null) {

			sb.append("<tr>");

			for (int i = 0; i < header.length; i++) {

				sb.append("<td align='center'><b>");

				sb.append(header[i]);

				sb.append("</b></td>");

			}

			sb.append("</tr>");

		}

		for (int i = 0; i < values.length; i++) {

			if (values[i][0] != null) {
				sb.append("<tr>");
				for (int t = 0; t < values[1].length; t++) {

					sb.append("<td align='center'><b>");
					if (!isPass[i][t]) {
						sb.append("<font color='red'>");
					} else {
						sb.append("<font color='green'>");

					}
					sb.append(values[i][t]);
					sb.append("</font>");
					sb.append("</b></td>");

				}

				sb.append("</tr>");
			}

		}
		return sb.toString();
	}
}
