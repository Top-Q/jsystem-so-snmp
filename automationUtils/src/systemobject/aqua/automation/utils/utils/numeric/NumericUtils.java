package systemobject.aqua.automation.utils.utils.numeric;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public abstract class NumericUtils {

	public static int toInt(boolean b) {
		return (b ? 1 : 0);
	}

	public static Boolean[][] toBooleanObjectArray(boolean[][] arr) {
		Boolean[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Boolean[0][0];
		} else {
			temp = new Boolean[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toBooleanObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Boolean[] toBooleanObjectArray(boolean[] arr) {
		if (arr == null) {
			return new Boolean[0];
		}
		Boolean[] temp = new Boolean[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static boolean[] toBooleanArray(Boolean[] arr) {
		if (arr == null) {
			return new boolean[0];
		}
		boolean[] temp = new boolean[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static Byte[][] toByteObjectArray(byte[][] arr) {
		Byte[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Byte[0][0];
		} else {
			temp = new Byte[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toByteObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Byte[] toByteObjectArray(byte[] arr) {
		if (arr == null) {
			return new Byte[0];
		}
		Byte[] temp = new Byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static Character[][] toCharacterObjectArray(char[][] arr) {
		Character[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Character[0][0];
		} else {
			temp = new Character[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toCharacterObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Character[] toCharacterObjectArray(char[] arr) {
		if (arr == null) {
			return new Character[0];
		}
		Character[] temp = new Character[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static Integer[][] toIntegerObjectArray(int[][] arr) {
		Integer[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Integer[0][0];
		} else {
			temp = new Integer[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toIntegerObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Integer[] toIntegerObjectArray(int[] arr) {
		if (arr == null) {
			return new Integer[0];
		}
		Integer[] temp = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static Long[][] toLongObjectArray(long[][] arr) {
		Long[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Long[0][0];
		} else {
			temp = new Long[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toLongObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Long[] toLongObjectArray(long[] arr) {
		if (arr == null) {
			return new Long[0];
		}
		Long[] temp = new Long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static Float[][] toFloatObjectArray(float[][] arr) {
		Float[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Float[0][0];
		} else {
			temp = new Float[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toFloatObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Float[] toFloatObjectArray(float[] arr) {
		if (arr == null) {
			return new Float[0];
		}
		Float[] temp = new Float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static Double[][] toDoubleObjectArray(double[][] arr) {
		Double[][] temp = null;
		if (arr == null || arr.length == 0) {
			temp = new Double[0][0];
		} else {
			temp = new Double[arr.length][];
			for (int i = 0; i < arr.length; i++) {
				temp[i] = toDoubleObjectArray(arr[i]);
			}
		}
		return temp;
	}

	public static Double[] toDoubleObjectArray(double[] arr) {
		if (arr == null) {
			return new Double[0];
		}
		Double[] temp = new Double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = arr[i];
		}
		return temp;
	}

	public static int[] toIntArray(boolean[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = toInt(arr[i]);
		}
		return temp;
	}

	public static int[] toIntArray(char[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (int) arr[i];
		}
		return temp;
	}

	public static int[] toIntArray(byte[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (new Byte(arr[i])).intValue();
		}
		return temp;
	}

	public static int[] toIntArray(long[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (new Long(arr[i])).intValue();
		}
		return temp;
	}

	public static int[] toIntArray(float[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (new Float(arr[i])).intValue();
		}
		return temp;
	}

	public static int[] toIntArray(double[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (new Double(arr[i])).intValue();
		}
		return temp;
	}

	public static int[] toIntArray(Boolean[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? toInt(arr[i]) : 0);
		}
		return temp;
	}

	public static int[] toIntArray(Byte[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].intValue() : 0);
		}
		return temp;
	}

	public static int[] toIntArray(Character[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? (int) arr[i].charValue() : 0);
		}
		return temp;
	}

	public static int[] toIntArray(Integer[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].intValue() : 0);
		}
		return temp;
	}

	public static int[] toIntArray(Long[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].intValue() : 0);
		}
		return temp;
	}

	public static int[] toIntArray(Float[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].intValue() : 0);
		}
		return temp;
	}

	public static int[] toIntArray(Double[] arr) {
		if (arr == null) {
			return new int[0];
		}
		int[] temp = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].intValue() : 0);
		}
		return temp;
	}

	public static byte[] toByteArray(Byte[] arr) {
		if (arr == null) {
			return new byte[0];
		}
		byte[] temp = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].byteValue() : 0);
		}
		return temp;
	}

	public static byte[] toByteArray(Integer[] arr) {
		if (arr == null) {
			return new byte[0];
		}
		byte[] temp = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			temp[i] = (arr[i] != null ? arr[i].byteValue() : 0);
		}
		return temp;
	}

	/**
	 * parses and returns a long value from a String in decimal value if the
	 * input is an illegal input the method will return null, if the input
	 * starts with "0x" it will be calculated as hex value
	 * 
	 * @param longString
	 *            String containing long value
	 * @param base
	 *            base for the number parsing
	 * @return Long object or null
	 */
	public static Long parseLong(String longString, int base) {
		if (longString == null || longString.length() == 0) {
			return null;
		}
		longString = longString.toLowerCase().trim();
		if (longString.startsWith("0x")) {
			base = 16;
			longString = longString.substring(2).trim();
		}
		for (int i = 0; i < longString.length(); i++) {
			if (longString.charAt(i) > '9' || longString.charAt(i) < '0') {
				if (base < 10 || longString.charAt(i) > ('a' + (base - 10))
						|| longString.charAt(i) < 'a') {
					return null;
				}
			}
		}
		return Long.parseLong(longString, base);
	}

	/**
	 * parses and returns a long value from a String in decimal value if the
	 * input is an illegal input the method will return null, if the input
	 * starts with "0x" it will be calculated as hex value
	 * 
	 * @param longString
	 *            String containing long value
	 * @return Long object or null
	 */
	public static Long parseLong(String longString) {
		return parseLong(longString, 10);
	}

	public static Integer parseInt(String intString) {
		Long l = NumericUtils.parseLong(intString);
		if (l == null) {
			return null;
		}
		return l.intValue();
	}

	public static long max(long[][] arr) {
		long temp, val = arr[0][0];
		for (int i = 0; i < arr.length; i++) {
			temp = max(arr[i]);
			if (temp > val) {
				val = temp;
			}
		}
		return val;
	}

	public static long max(long[] arr) {
		long val = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (val < arr[i]) {
				val = arr[i];
			}
		}
		return val;
	}

	public static long min(long[][] arr) {
		long temp;
		long val = arr[0][0];
		for (int i = 0; i < arr.length; i++) {
			temp = min(arr[i]);
			if (val > temp) {
				val = temp;
			}
		}
		return val;
	}

	public static long min(long[] arr) {
		long val = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (val > arr[i]) {
				val = arr[i];
			}
		}
		return val;
	}

	/**
	 * returns the average value of all the 2D array's content
	 * 
	 * @param arr
	 *            array[][] of long
	 * @return average value as long
	 */
	public static long avg(long[][] arr) {
		double val = 0;
		int counter = 0;
		for (int i = 0; arr != null && i < arr.length; i++) {
			for (int j = 0; arr[i] != null && j < arr[i].length; j++) {
				val += arr[i][j];
				counter++;
			}
		}
		return (long) (val / counter);
	}

	/**
	 * returns the average value of all the array's content
	 * 
	 * @param arr
	 *            array of long
	 * @return average value as long
	 */
	public static long avg(long[] arr) {
		double val = 0;
		if (arr == null) {
			return 0;
		}
		for (int i = 0; i < arr.length; i++) {
			val += arr[i];
		}
		return (long) (val / arr.length);
	}

	/**
	 * this method generates and returns an integer values array from the low
	 * bound to the high bound (included) with the given step
	 * 
	 * @param lowBound
	 *            start value (included)
	 * @param highBound
	 *            stop value (included)
	 * @param step
	 *            between one value to another
	 * @return array of ints contains the old ones if given and the new ones
	 */
	public static int[] generateIntRangeArray(int lowBound, int highBound,
			int step) {
		return generateIntRangeArray(null, lowBound, highBound, step);
	}

	/**
	 * this method generates and returns an integer values array from the low
	 * bound to the high bound (included) with the step of 1
	 * 
	 * @param lowBound
	 *            start value (included)
	 * @param highBound
	 *            stop value (included)
	 * @return array of ints contains the old ones if given and the new ones
	 */
	public static int[] generateIntRangeArray(int lowBound, int highBound) {
		return generateIntRangeArray(null, lowBound, highBound, 1);
	}

	/**
	 * this method generates and returns an long values array from the low bound
	 * to the high bound (included) with the step of 1
	 * 
	 * @param lowBound
	 *            start value (included)
	 * @param highBound
	 *            stop value (included)
	 * @return array of longs contains the old ones if given and the new ones
	 */
	public static long[] generateLongRangeArray(long lowBound, long highBound) {
		long[] arr = new long[(int) (highBound - lowBound) + 1];
		long val = lowBound;
		for (int i = 0; i < arr.length; i++) {
			arr[i] = val;
			val++;
		}
		return arr;
	}

	/**
	 * this method receives null or array of ints and adds/creates-array with
	 * new values requested
	 * 
	 * @param arr
	 *            array of ints contains list of values - the new values will be
	 *            added to it, "null" to create new array
	 * @param lowBound
	 *            start value (included)
	 * @param highBound
	 *            stop value (included)
	 * @param step
	 *            between one value to another
	 * @return array of ints contains the old ones if given and the new ones
	 */
	public static int[] generateIntRangeArray(int[] arr, int lowBound,
			int highBound, int step) {
		return generateIntRangeArray(arr, lowBound, highBound, step, null);
	}

	/**
	 * this method receives null or array of ints and adds/creates-array with
	 * new values requested
	 * 
	 * @param arr
	 *            array of ints contains list of values - the new values will be
	 *            added to it, "null" to create new array
	 * @param lowBound
	 *            start value (included)
	 * @param highBound
	 *            stop value (included)
	 * @param step
	 *            between one value to another
	 * @param excludeValues
	 *            array of values that will be excluded from the added values
	 * @return array of ints contains the old ones if given and the new ones
	 */
	public static int[] generateIntRangeArray(int[] arr, int lowBound,
			int highBound, int step, int[] excludeValues) {
		int size = (arr == null ? 0 : arr.length);
		int excluded = 0;
		size += (((highBound - lowBound) + step) / step);
		int i = 0, j = 0;
		int[] newArrTemp = new int[size];

		for (; arr != null && i < arr.length; i++) {
			newArrTemp[i] = arr[i];
		}

		for (j = lowBound; j <= highBound && i < newArrTemp.length; i++, j += step) {
			if (isValueInArray(j, excludeValues)) {
				excluded++;
				newArrTemp[i] = Integer.MIN_VALUE;
			} else {
				newArrTemp[i] = j;
			}
		}

		arr = new int[newArrTemp.length - excluded];

		for (i = 0, j = 0; i < newArrTemp.length && j < arr.length; i++, j++) {
			if (newArrTemp[i] != Integer.MIN_VALUE) {
				arr[j] = newArrTemp[i];
			} else {
				j--;
			}
		}

		return arr;
	}

	private static boolean isValueInArray(int value, int[] array) {
		if (array == null) {
			return false;
		}
		for (int i = 0; i < array.length; i++) {
			if (value == array[i]) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
