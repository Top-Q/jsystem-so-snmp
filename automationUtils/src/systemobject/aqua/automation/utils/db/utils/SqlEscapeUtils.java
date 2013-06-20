package systemobject.aqua.automation.utils.db.utils;

/**
 * @author Itzhak.Hovav
 */
public class SqlEscapeUtils {

	public static String escapeMySqlString(String str) {
		StringBuilder sb = new StringBuilder();
		int c, cPrev = -1;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (c == 0x00 || c == 0x08 || c == 0x09 || c == 0x0A || c == 0x0D
					|| c == 0x1A || c == 0x22
					// || c == 0x25
					|| c == 0x27 || c == 0x5C
					// || c == 0x5F
					|| (c <= 0xFF && !isAlpha(c))) {
				if (cPrev != 0x5C) {
					if (c != 0x5C || i == (str.length() - 1)
							|| str.charAt(i + 1) != 0x5C) {
						sb.append((char) 0x5C);
					}
				}
			}
			sb.append((char) c);
			cPrev = c;
		}
		return sb.toString();
	}

	private static boolean isAlpha(int c) {
		return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
				|| (c >= '0' && c <= '9') || (c == 0x20) || (c == 0x25)
				|| (c == 0x2E) || (c == 0x28) || (c == 0x2B) || (c == 0x24)
				|| (c == 0x2A) || (c == 0x29) || (c == 0x2D) || (c == 0x2F)
				|| (c == 0x5F) || (c == 0x2C) || (c == 0x3D));
	}

}
