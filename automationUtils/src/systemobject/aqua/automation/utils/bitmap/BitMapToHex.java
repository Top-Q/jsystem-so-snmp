package systemobject.aqua.automation.utils.bitmap;import java.util.HashMap;/** * Generic Class, can be used outside JSystem Project This class simulates a * bitmap - array of bits. *  * @author Uri.Koaz */public class BitMapToHex {	private byte[] bitmap = null;	private HashMap<String, String> hexToBin = null;	private HashMap<String, String> binToHex = null;	public BitMapToHex(int bitmapSize) throws Exception {		if ((bitmapSize % 4) != 0) {			throw new Exception("size must divide in 4");		}		bitmap = new byte[bitmapSize];		hexToBin = new HashMap<String, String>();		hexToBin.put("0", "0000");		hexToBin.put("1", "0001");		hexToBin.put("2", "0010");		hexToBin.put("3", "0011");		hexToBin.put("4", "0100");		hexToBin.put("5", "0101");		hexToBin.put("6", "0110");		hexToBin.put("7", "0111");		hexToBin.put("8", "1000");		hexToBin.put("9", "1001");		hexToBin.put("A", "1010");		hexToBin.put("B", "1011");		hexToBin.put("C", "1100");		hexToBin.put("D", "1101");		hexToBin.put("E", "1110");		hexToBin.put("F", "1111");		binToHex = new HashMap<String, String>();		binToHex.put("0000", "0");		binToHex.put("0001", "1");		binToHex.put("0010", "2");		binToHex.put("0011", "3");		binToHex.put("0100", "4");		binToHex.put("0101", "5");		binToHex.put("0110", "6");		binToHex.put("0111", "7");		binToHex.put("1000", "8");		binToHex.put("1001", "9");		binToHex.put("1010", "A");		binToHex.put("1011", "B");		binToHex.put("1100", "C");		binToHex.put("1101", "D");		binToHex.put("1110", "E");		binToHex.put("1111", "F");	}	/**	 * set the value into the array. The index goes from 0.	 * 	 * @param index	 * @param value	 */	public void setValueToBitMap(int index, boolean value) {		if (value) {			bitmap[index] = 1;		} else {			bitmap[index] = 0;		}	}	/**	 * sets the value from the end of the array. for example, the last place in	 * the will be 1.	 * 	 * @param indexFromEnd	 * @param value	 */	public void setValueToBitMapReveres(int indexFromEnd, boolean value) {		if (value) {			bitmap[getBitMapSize() - indexFromEnd] = 1;		} else {			bitmap[getBitMapSize() - indexFromEnd] = 0;		}	}	private void setValuetoBitMap(int index, int value) {		bitmap[index] = (byte) value;	}	public int getBitMapSize() {		return bitmap.length;	}	public int get(int index) {		return bitmap[index];	}	/**	 * transform hex ( in String) to bitmap. example: 0A -> will be 00001010 The	 * Hex must be at capitol letters.	 * 	 * @param hexValue	 */	public void hexToBinary(String hexValue) {		StringBuffer buffer = new StringBuffer();		System.out.println(hexToBin.get("0"));		for (int i = 0; i < hexValue.length(); i++) {			Character c = hexValue.charAt(i);			buffer.append(hexToBin.get(c.toString()));		}		String binString = buffer.toString();		for (int i = 0; i < binString.length(); i++) {			Character c = binString.charAt(i);			setValuetoBitMap(i, Integer.parseInt(c.toString()));		}	}	/**	 * convert the bitmap into Stirng hex.	 * 	 * @return hex string	 */	public String binToHex() {		StringBuffer mainBuffer = new StringBuffer();		for (int i = 0; i < getBitMapSize(); i += 4) {			StringBuffer miniBuffer = new StringBuffer();			miniBuffer.append(get(i));			miniBuffer.append(get(i + 1));			miniBuffer.append(get(i + 2));			miniBuffer.append(get(i + 3));			mainBuffer.append(binToHex.get(miniBuffer.toString()));		}		return mainBuffer.toString();	}	public String binToHexWithSpaces() {		StringBuffer mainBuffer = new StringBuffer();		int digitCounter = 1;		for (int i = 0; i < getBitMapSize(); i += 4) {			StringBuffer miniBuffer = new StringBuffer();			miniBuffer.append(get(i));			miniBuffer.append(get(i + 1));			miniBuffer.append(get(i + 2));			miniBuffer.append(get(i + 3));			mainBuffer.append(binToHex.get(miniBuffer.toString()));			digitCounter++;			if (digitCounter % 2 != 0 && i != 0 && i != getBitMapSize() - 4) {				mainBuffer.append(" ");			}		}		return mainBuffer.toString();	}}