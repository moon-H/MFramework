
package com.cssweb.framework.utils;

public class HexConverter {

	private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F'};

	/** Convert input HexStrings to Byte[]. */
	public static byte[] hexStringToBytes(String hexString) {
		if ("".equals(hexString))
			return null;
		int stringLen = hexString.length();
		byte[] temp = new byte[stringLen];
		int resultLength = 0;
		int nibble = 0;
		byte nextByte = 0;
		for (int i = 0; i < stringLen; i++) {
			char c = hexString.charAt(i);
			byte b = (byte) Character.digit(c, 16);
			if (b == -1) {
				if (!Character.isWhitespace(c))
					throw new IllegalArgumentException("Not HexConverter character: " + c);
				continue;
			}
			if (nibble == 0) {
				nextByte = (byte) (b << 4);
				nibble = 1;
			} else {
				nextByte |= b;
				temp[resultLength++] = nextByte;
				nibble = 0;
			}
		}// for
		if (nibble != 0) {
			throw new IllegalArgumentException("odd number of characters.");
		} else {
			byte[] result = new byte[resultLength];
			System.arraycopy(temp, 0, result, 0, resultLength);
			return result;
		}
	}

	/** Convert input HexConverter to Byte. */
	public static byte hexStringToByte(String s) throws Exception {
		if (s == null)
			throw new RuntimeException("input String is null");

		int strLength = s.length();
		if (strLength == 1)
			s = "0" + s;
		else if (strLength > 2)
			s = s.substring(0, 2);

		byte[] result = new byte[1];
		result = hexStringToBytes(s);
		return result[0];
	}

	/** Convert input Byte to HexConverter. */
	public static String byteToHexString(byte b) {
		StringBuilder result = new StringBuilder();
		result.append(digits[b >> 4 & 0xf]).append(digits[b & 0xf]);
		return result.toString();
	}

	/** Convert input Byte[] to HexConverter from offset as length. */
	public static String bytesToHexString(byte d[], int offset, int len, boolean spaced) {
		StringBuilder result = new StringBuilder();
		int to = offset + len;
		for (int i = offset; i < to; i++) {
			result.append(byteToHexString(d[i]));
			if (spaced)
				result.append(" ");
		}
		return result.toString();
	}

	/** Convert input Byte[] to HexConverter. */
	public static String bytesToHexString(byte[] d) {
		if (d == null)
			return null;
		return bytesToHexString(d, 0, d.length, false);
	}

	/** Convert input Byte[] to HexConverter. */
	public static String bytesToHexString(byte[] d, boolean spaced) {
		if (d == null)
			return null;
		return bytesToHexString(d, 0, d.length, spaced);
	}

	public static byte trimByte(byte paramByte) {
		return (byte) (paramByte & 0xFF);
	}
	private static final byte[] HEX_CHAR_TABLE = {(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
			(byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D',
			(byte) 'E', (byte) 'F'};

	/**
	 * convert a byte arrary to hex string
	 * 
	 * @param raw
	 *            byte arrary
	 * @param len
	 *            lenght of the arrary.
	 * @return hex string.
	 */
	public static String getHexString(byte[] raw, int len) {
		byte[] hex = new byte[2 * len];
		int index = 0;
		int pos = 0;

		for (byte b : raw) {
			if (pos >= len)
				break;

			pos++;
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}

		return new String(hex);
	}
}
