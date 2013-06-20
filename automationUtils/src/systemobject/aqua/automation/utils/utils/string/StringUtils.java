package systemobject.aqua.automation.utils.utils.string;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import systemobject.aqua.automation.utils.utils.numeric.NumericUtils;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class StringUtils {

	public static int[] mibToIntArray(String mibOid) {
		String[] p = mibOid.split("\\.");
		int[] r = new int[p.length];
		for (int i = 0; i < p.length; i++) {
			r[i] = Integer.parseInt(p[i]);
		}

		return r;
	}

	/**
	 * Convert Text like "Moshe" to "4D 6F 73 68 65"
	 */
	public static String fromByteArrayToVopXmlStyleString(byte[] arr) {
		StringBuilder sb = new StringBuilder();
		if (arr != null && arr.length > 0) {
			for (byte b : arr) {
				sb.append(Byte.toString(b));
				sb.append("|");
			}
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String fromBytesToHexString(byte[] bytes) {
		String str = null;
		if (bytes != null) {
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				sb.append(' ');
				sb.append((Integer.toHexString(b | 0x100)).substring(1)
						.toUpperCase());
			}
			str = sb.toString();
			if (str.length() > 0) {
				str = str.substring(1);
			}
		}
		return str;
	}

	public static String fromBytesToTextString(byte[] bytes) {
		String str = null;
		if (bytes != null) {
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				sb.append((char) b);
			}
			str = sb.toString();
		}
		return str;
	}

	public static String fromBytesToAsciiDottedString(byte[] bytes) {
		String str = null;
		if (bytes != null) {
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				sb.append('.');
				sb.append((int) b);
			}
			str = sb.toString();
			if (str.length() > 0) {
				str = str.substring(1);
			}
		}
		return str;
	}

	/**
	 * Convert Text like "Moshe" to "4D 6F 73 68 65"
	 */
	public static String fromTextStringToHexString(String text) {
		text = fromTextStringToAsciiDottedString(text);
		if (text != null && text.length() > 1) {
			String[] arr = text.split("\\.");
			StringBuffer sb = new StringBuffer();
			for (String s : arr) {
				sb.append(' ');
				sb.append((Integer.toHexString(Integer.parseInt(s, 10) | 0x100))
						.substring(1).toUpperCase());
			}
			text = sb.toString();
			text = text.substring(1);
		}
		return text;
	}

	/**
	 * Convert Text like "Moshe" to "77.111.115.104.101"
	 */
	public static String fromTextStringToAsciiDottedString(String text) {
		StringBuffer sb = new StringBuffer();
		if (text != null && text.length() > 1) {
			for (byte b : text.getBytes()) {
				sb.append('.');
				sb.append((int) b);
			}
			text = sb.toString();
			text = text.substring(1);
		}
		return text;
	}

	/**
	 * Convert Hex String like "4D 6F 73 68 65" to "Moshe"
	 */
	public static String fromHexStringToTextString(String hexString) {
		if (hexString != null && hexString.length() > 1) {
			String[] arr = changeSeperator(hexString, ' ').split("\\ +");
			StringBuffer sb = new StringBuffer();
			for (String s : arr) {
				sb.append((char) Integer.parseInt(s, 16));
			}
			hexString = sb.toString();
		}
		return hexString;
	}

	/**
	 * Convert Hex String like "4D 6F 73 68 65" to int[]
	 */
	public static int[] fromHexStringToIntArray(String hexString) {
		int[] bytes = null;
		if (hexString != null && hexString.length() > 1) {
			String[] arr = changeSeperator(hexString, ' ').split("\\ +");
			bytes = new int[arr.length];
			for (int i = 0; i < arr.length; i++) {
				bytes[i] = Integer.parseInt(arr[i], 16);
			}
		}
		return bytes;
	}

	/**
	 * Convert Hex String like "4D 6F 73 68 65" to byte[]
	 */
	public static byte[] fromHexStringToByteArray(String hexString) {
		int[] arr = fromHexStringToIntArray(hexString);
		byte[] bytes = null;
		if (arr != null) {
			bytes = new byte[arr.length];
			for (int i = 0; i < arr.length; i++) {
				bytes[i] = (new Integer(arr[i])).byteValue();
			}
		}
		return bytes;
	}

	/**
	 * Convert Ascii Dotted String like "77.111.115.104.101" to "Moshe"
	 */
	public static String fromAsciiDottedStringToTextString(
			String asciiDottedString) {
		if (asciiDottedString != null && asciiDottedString.length() > 1) {
			String[] arr = changeSeperator(asciiDottedString, ' ')
					.split("\\ +");
			StringBuffer sb = new StringBuffer();
			for (String s : arr) {
				sb.append((char) Integer.parseInt(s, 10));
			}
			asciiDottedString = sb.toString();
		}
		return asciiDottedString;
	}

	/**
	 * Convert Ascii Dotted String like "77.111.115.104.101" to byte array
	 */
	public static byte[] fromAsciiDottedStringToByteArray(
			String asciiDottedString) {
		byte[] bytes = null;
		if (asciiDottedString != null && asciiDottedString.length() > 1) {
			String[] arr = changeSeperator(asciiDottedString, ' ')
					.split("\\ +");
			bytes = new byte[arr.length];
			for (int i = 0; i < arr.length; i++) {
				bytes[i] = (byte) Integer.parseInt(arr[i]);
			}
		}
		return bytes;
	}

	/**
	 * Convert Ascii Dotted String like "77.111.115.104.101" to "4D 6F 73 68 65"
	 */
	public static String fromAsciiDottedStringToHexString(
			String asciiDottedString) {
		if (asciiDottedString != null && asciiDottedString.length() > 1) {
			String[] arr = changeSeperator(asciiDottedString, ' ')
					.split("\\ +");
			StringBuffer sb = new StringBuffer();
			for (String s : arr) {
				sb.append(' ');
				sb.append(Integer.toHexString(Integer.parseInt(s, 10) | 0x100)
						.substring(1));
			}
			asciiDottedString = sb.toString();
			asciiDottedString = asciiDottedString.substring(1);
		}
		return asciiDottedString;
	}

	/**
	 * Convert Hex String like "4D 6F 73 68 65" to "77.111.115.104.101"
	 */
	public static String fromHexStringToAsciiDottedString(String hexString) {
		if (hexString != null && hexString.length() > 1) {
			String[] arr = changeSeperator(hexString, ' ').split("\\ +");
			StringBuffer sb = new StringBuffer();
			for (String s : arr) {
				sb.append('.');
				sb.append(Integer.parseInt(s, 16));
			}
			hexString = sb.toString();
			hexString = hexString.substring(1);
		}
		return hexString;
	}

	private static String changeSeperator(String str, char seperator) {
		if (str != null) {
			str = str.trim();
			str = (str.replace('\r', seperator).replace('\n', seperator)
					.replace('\f', seperator).replace('\b', seperator)
					.replace('\t', seperator).replace(' ', seperator)
					.replace('.', seperator).replace(':', seperator)
					.replace('_', seperator).replace('-', seperator)
					.replace(',', seperator).replace('/', seperator)
					.replace('\\', seperator).replace('+', seperator)
					.replace(';', seperator).replace('*', seperator)
					.replace('#', seperator).replace('[', seperator)
					.replace(']', seperator).replace('{', seperator)
					.replace('}', seperator).replace('(', seperator).replace(
					')', seperator)).trim();

			while (str.contains("" + seperator + seperator)) {
				str = str.replace("" + seperator + seperator, "" + seperator);
			}
		}
		return str;
	}

	public static String formatHexString(String str, int numOfbytes,
			boolean bytesSeparated, char seperator) {
		if (str != null) {
			StringBuffer sb = new StringBuffer();
			if (!bytesSeparated) {
				for (int i = 0; i < str.length() - 1; i += 2) {
					sb.append(' ');
					sb.append(str.subSequence(i, Math.min(i + 2, str.length())));
				}
				str = sb.toString().trim();
				sb = new StringBuffer();
			}
			int i = 0;
			if (str != null && str.length() > 0) {
				String[] arr = changeSeperator(str, ' ').split("\\ +");

				for (; (numOfbytes <= 0 || i < numOfbytes) && i < arr.length; i++) {
					sb.append(seperator);
					sb.append((Integer.toHexString(Integer.parseInt(arr[i], 16) | 0x100))
							.substring(1).toUpperCase());
				}
			} else {
				return null;
			}
			for (; i < numOfbytes; i++) {
				sb.append(seperator);
				sb.append("00");
			}
			return sb.toString().substring(1);
		}
		return "";
	}

	public static String intArray2HexString(int[] intVals) {
		if (intVals == null) {
			return "";
		}
		StringBuffer strVals = new StringBuffer();
		for (int i = 0; i < intVals.length; i++) {
			strVals.append(Integer.toHexString(intVals[i] | 0x100).substring(1));
			strVals.append(' ');
		}
		return strVals.toString();
	}

	public static String intArray2HexString(int[][] intVals) {
		if (intVals == null) {
			return "";
		}
		StringBuffer strVals = new StringBuffer();
		for (int i = 0; i < intVals.length; i++) {
			strVals.append(intArray2HexString(intVals[i]));
			strVals.append("\n");
		}
		return strVals.toString();

	}

	public static String toSingleHexString(int val, int numOfBytes) {
		return int2HexString(val, numOfBytes);
	}

	public static String toSingleHexString(int[] val) {
		StringBuffer sb = new StringBuffer();
		for (int j = 0; val != null && j < val.length;) {
			int value = val[j + 0];
			int length = (j + 4 < val.length ? 4 : val.length - j);
			for (int i = 1; i < 4 && (i + j) < length; i++) {
				value = value << 8;
				value = value | val[j + i];
			}
			return toSingleHexString(value, length);
		}
		return sb.toString();
	}

	public static String toHexString(int[] val) {
		String temp = toSingleHexString(val);
		String str = "";
		if (temp != null) {
			for (int i = 0; i < temp.length(); temp += 2) {
				str = str + ' ' + temp.substring(i, i + 2);
			}
			str = str.substring(1);
		}
		return str;
	}

	public static String toIntString(boolean b) {
		return Integer.toString(NumericUtils.toInt(b));
	}

	public static String int2HexString(int val, int numOfBytes) {
		return Integer.toString(val | (1 << (numOfBytes * 8)), 16).substring(1);
	}

	public static InputStream stringToStream(String s) {

		try {
			InputStream is = new ByteArrayInputStream(s.getBytes("UTF-8"));
			return is;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String cleanStringValueFromSpecialCharacters(String value,
			String[] specialCharactersList) {
		String tmp = value;
		for (int i = 0; i < specialCharactersList.length; i++) {
			tmp = tmp.replace(specialCharactersList[i], "");
		}
		return tmp;
	}

}
