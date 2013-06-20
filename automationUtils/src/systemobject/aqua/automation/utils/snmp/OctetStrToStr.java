package systemobject.aqua.automation.utils.snmp;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Uri.Koaz
 */
public class OctetStrToStr {

	private final static String exceptionString = "wrong format of the string";

	private final static int firstPrintCharacter = 33;

	private final static int lastPrintCharacter = 126;

	private final static int DOT = '.';

	/**
	 * Convert octet string like -1.3.6.1.4.1.1286.1.3.4.3.4.1.6 to string that
	 * contain character values of the numbers
	 * 
	 * @param value
	 * @return converted String
	 */
	public static String convertToNum(String value) throws Exception {
		Exception excpt;
		char ch;
		boolean end = false;
		int beginIndex = 0, endIndex = 0, num;
		String toUser = new String("");
		String temp;

		/** ************************************* */

		endIndex = value.indexOf(DOT, beginIndex);
		if (endIndex <= 0) {
			excpt = new Exception(exceptionString);
			throw excpt;
		}
		while (!end) {
			// extract new character and add to resulting string
			temp = value.substring(beginIndex, endIndex);
			num = (char) Integer.parseInt(temp);
			if (num < firstPrintCharacter || num > lastPrintCharacter) {
				excpt = new Exception(exceptionString);
				throw excpt;
			} else {
				ch = (char) num;
				toUser += ch;
			}
			// if not end of the string
			if (endIndex < value.length()) {
				beginIndex = endIndex + 1;
				endIndex = value.indexOf(DOT, beginIndex);
				if (endIndex == -1)
					endIndex = value.length();
				if (beginIndex == endIndex) {
					excpt = new Exception(exceptionString);
					throw excpt;
				}
			} else {
				end = true;
			}
		}// end while

		return toUser;
	}

	// convert string to octet string like -1.3.6.1.4.1.1286.1.3.4.3.4.1.6
	public static String convertToStr(String value) throws Exception {
		Exception excpt;
		char ch;
		int num;
		String toUser = "";

		for (int i = 0; i < value.length(); i++) {
			ch = value.charAt(i);
			num = (int) ch;

			if (num < firstPrintCharacter || num > lastPrintCharacter) {
				excpt = new Exception(exceptionString);
				throw excpt;
			}
			toUser = String.format("%s%c%d", toUser, DOT, num);
			if (i == (value.length() - 1)) {
				toUser = toUser.substring(1);
			}
		}
		return toUser;
	}

	public static long setMask(int[] arr) {
		long mask = 0, temp = 1;

		for (int i = 0; i < arr.length; i++) {
			temp = temp << arr[i];
			mask = mask | temp;
			temp = 1;

		}
		return mask;
	}

	/**
	 * This method Convert string Octet string .
	 * 
	 * @param value
	 * @return Octet string as byte array
	 * @throws Exception
	 */

	static public byte[] StrToOctetStr(String value) throws Exception {

		int OctetStrLength = (value.length() + 1) / 3;
		byte[] arr = new byte[OctetStrLength];

		for (int i = 0, j = 0; j < OctetStrLength; i += 3, j++) {
			arr[j] = ((Integer) Integer.parseInt(value.substring(i, i + 2), 16))
					.byteValue();
		}

		return arr;

	}

	public static void main(String[] args) {

		System.out.println("Mask is " + setMask(new int[] { 0, 2, 4 }));
	}
}
