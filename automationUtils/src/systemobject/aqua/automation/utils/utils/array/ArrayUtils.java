package systemobject.aqua.automation.utils.utils.array;

import java.util.Arrays;
import java.util.HashMap;

import systemobject.aqua.automation.utils.utils.numeric.NumericUtils;

/**
 * Generic Class, can be used outside JSystem Project
 * 
 * @author Itzhak.Hovav
 */
public class ArrayUtils {

	public static int find(int[] frame, int offset, int[] toFind) {
		for (int i = offset; i < (frame.length - toFind.length); i++) {
			if (frame[i] == toFind[0]) {
				for (int j = 1; j < toFind.length; j++) {
					if (frame[i + j] != toFind[j]) {
						break;
					} else if (j == (toFind.length - 1)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	public static int[] addToArr(int[] arr, int offset, int[] toAdd) {
		int[] all = new int[arr.length + toAdd.length];
		int i = 0, j = 0;
		for (; j < offset; i++, j++) {
			all[i] = arr[j];
		}
		for (int k = 0; k < toAdd.length; k++, i++) {
			all[i] = toAdd[k];
		}
		for (; j < arr.length; i++, j++) {
			all[i] = arr[j];
		}
		return all;
	}

	public static int[] replaceInArr(int[] arr, int offset, int[] replacement) {
		for (int i = offset; i < offset + replacement.length; i++) {
			arr[i] = replacement[i - offset];
		}
		return arr;
	}

	public static int length(Object[]... arrays) {
		int size = 0;
		for (Object[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(byte[]... arrays) {
		int size = 0;
		for (byte[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(char[]... arrays) {
		int size = 0;
		for (char[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(short[]... arrays) {
		int size = 0;
		for (short[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(int[]... arrays) {
		int size = 0;
		for (int[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(long[]... arrays) {
		int size = 0;
		for (long[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(float[]... arrays) {
		int size = 0;
		for (float[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int length(double[]... arrays) {
		int size = 0;
		for (double[] arr : arrays) {
			size += (arr == null ? 0 : arr.length);
		}
		return size;
	}

	public static int[] concatArr(int[]... arrays) {
		int[] all = new int[length(arrays)];
		int i = 0;
		for (int[] arr : arrays) {
			if (arr != null && arr.length > 0) {
				for (int j = 0; j < arr.length && i < all.length; i++, j++) {
					all[i] = arr[j];
				}
			}
		}
		return all;
	}

	public static Object toObject(Object obj) {
		if (obj != null) {
			if (obj instanceof boolean[][]) {
				obj = NumericUtils.toBooleanObjectArray(((boolean[][]) obj));
			} else if (obj instanceof boolean[]) {
				obj = NumericUtils.toBooleanObjectArray(((boolean[]) obj));
			} else if (obj instanceof byte[][]) {
				obj = NumericUtils.toByteObjectArray(((byte[][]) obj));
			} else if (obj instanceof byte[]) {
				obj = NumericUtils.toByteObjectArray(((byte[]) obj));
			} else if (obj instanceof char[][]) {
				obj = NumericUtils.toCharacterObjectArray(((char[][]) obj));
			} else if (obj instanceof char[]) {
				obj = NumericUtils.toCharacterObjectArray(((char[]) obj));
			} else if (obj instanceof int[][]) {
				obj = NumericUtils.toIntegerObjectArray(((int[][]) obj));
			} else if (obj instanceof int[]) {
				obj = NumericUtils.toIntegerObjectArray(((int[]) obj));
			} else if (obj instanceof long[][]) {
				obj = NumericUtils.toLongObjectArray(((long[][]) obj));
			} else if (obj instanceof long[]) {
				obj = NumericUtils.toLongObjectArray(((long[]) obj));
			} else if (obj instanceof float[][]) {
				obj = NumericUtils.toFloatObjectArray(((float[][]) obj));
			} else if (obj instanceof float[]) {
				obj = NumericUtils.toFloatObjectArray(((float[]) obj));
			} else if (obj instanceof double[][]) {
				obj = NumericUtils.toDoubleObjectArray(((double[][]) obj));
			} else if (obj instanceof double[]) {
				obj = NumericUtils.toDoubleObjectArray(((double[]) obj));
			}
		}
		return obj;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int compare(Object a, Object b) {
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}
		int val = 0;
		if (a instanceof Object[]) {
			Object[] arr1 = (Object[]) a;
			Object[] arr2 = (Object[]) b;
			val = (arr1.length - arr2.length);
			int i = 0;
			while (val == 0 && i < arr1.length) {
				val = compare(arr1[i], arr2[i]);
				i++;
			}
		} else {
			if (a instanceof Comparable<?>) {
				return ((Comparable) a).compareTo(b);
			} else {
				return a.hashCode() - b.hashCode();
			}
		}
		return val;
	}

	public static Object[] sort(Object[] o) {
		if (o != null) {
			if (o instanceof Object[][]) {
				Object[][] arr = new Object[o.length][];

				for (int i = 0; i < arr.length; i++) {
					arr[i] = sort((Object[]) o[i]);
				}
				/**
				 * sort columns
				 */
				for (int i = arr.length - 1; i > 0; i--) {
					for (int j = 0; j < i; j++) {
						if (compare(arr[j], arr[j + 1]) > 0) {
							Object[] temp = arr[j];
							arr[j] = arr[j + 1];
							arr[j + 1] = temp;
						}
					}
				}
				o = arr;
			} else {
				Object[] arr = Arrays.copyOf(o, o.length);
				if (o instanceof Comparable<?>[]) {
					Arrays.sort(arr);
				} else {
					/**
					 * Sort by the HashCode of the object
					 */
					HashMap<Integer, Object> map = new HashMap<Integer, Object>();
					for (Object obj : arr) {
						map.put(obj.hashCode(), obj);
					}
					Integer[] keys = map.keySet().toArray(
							new Integer[map.size()]);
					Arrays.sort(keys);
					for (int i = 0; i < keys.length; i++) {
						arr[i] = map.get(keys[i]);
					}
				}
				o = arr;
			}
		}
		return o;
	}

	public static boolean compareArraysOfObjects(Object[] arr1, Object[] arr2) {
		boolean array = false;
		if (arr1 == arr2) {
			/**
			 * both arrays are null or same address
			 */
			return true;
		} else if (arr1 == null || arr2 == null) {
			/**
			 * one array is null and the other not
			 */
			return false;
		} else if (arr1.length != arr2.length) {
			/**
			 * arrays length not equal
			 */
			return false;
		} else if (arr1.length == 0) {
			/**
			 * empty arrays
			 */
			return true;
		} else if (arr1[0] instanceof Object[] && arr2[0] instanceof Object[]) {
			/**
			 * arrays of arrays
			 */
			array = true;
		} else if (arr1[0] instanceof Object[] || arr2[0] instanceof Object[]) {
			/**
			 * one array is an array of arrays and the other not
			 */
			return false;
		}
		Object[] result1 = null;
		Object[] result2 = null;
		if (!array) {
			result1 = new Object[arr1.length];
			result2 = new Object[arr2.length];
			System.arraycopy(arr1, 0, result1, 0, arr1.length);
			System.arraycopy(arr2, 0, result2, 0, arr2.length);
		} else {
			result1 = arr1;
			result2 = arr2;
		}
		result1 = sort(result1);
		result2 = sort(result2);
		for (int j = 0; j < result1.length; j++) {
			if (array) {
				return compareArraysOfObjects((Object[]) result1[j],
						(Object[]) result2[j]);
			} else if ((result1[j] != result2[j])
					&& !result1[j].equals(result2[j])) {
				return false;
			}
		}
		return true;
	}

}
