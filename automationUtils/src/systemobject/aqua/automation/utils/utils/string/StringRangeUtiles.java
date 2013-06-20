package systemobject.aqua.automation.utils.utils.string;

import java.util.Vector;

/**
 * Generic Class, can be used outside JSystem Project Getting range from the
 * following shapes: 1, 2, 3, 4 or 1, 2, 4 - 10 , 40 and getting an int array of
 * the whole range.
 * 
 * @author Uri.Koaz
 */
public class StringRangeUtiles {

	public static int[] getRange(String range) {
		Vector<Integer> v = new Vector<Integer>();
		int[] result = null;

		try {
			int tempVal = Integer.parseInt(range);
			if (tempVal != 0) {
				v.add(tempVal);
			}
		} catch (Exception e) {
			String[] temp = range.split(",");
			for (int i = 0; i < temp.length; i++) {
				try {
					int tempVal = Integer.parseInt(temp[i].trim());
					v.add(tempVal);
				} catch (Exception ex) {
					String[] secondSplit = temp[i].trim().split("-");

					for (int j = Integer.parseInt(secondSplit[0].trim()); j <= Integer
							.parseInt(secondSplit[1].trim()); j++) {

						v.add(j);
					}
				}
			}
		}

		if (v.size() != 0) {
			result = new int[v.size()];
			for (int i = 0; i < v.size(); i++) {
				result[i] = (Integer) v.get(i);
			}
		}

		if (result == null) {
			result = new int[0];
		}
		return result;
	}

	public static void main(String[] args) {
		int[] res = StringRangeUtiles.getRange("0");

		if (res != null) {
			for (int i = 0; i < res.length; i++) {
				System.out.println(res[i]);
			}
		}

	}
}
