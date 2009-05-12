package org.encog.util.math;

/**
 * This class is used to convert strings into numeric values.  If the
 * string holds a non-numeric value, a zero is returned.
 * @author jheaton
 *
 */
public final class Convert {
	
	/**
	 * Private constructor.
	 */
	private Convert() {
	
	}
	
	/**
	 * Convert a string to a double.  Just make the number a zero
	 * if the string is invalid.
	 * @param str The string.
	 * @return The string converted to numeric.
	 */
	public static double string2double(final String str) {
		double result = 0;
		try {
			if (str != null) {
				result = Double.parseDouble(str);
			}
		} catch (final NumberFormatException e) {
			result = 0;
		}
		return result;
	}

	/**
	 * Convert a string to an int.  Just make the number a zero
	 * if the string is invalid.
	 * @param str The string.
	 * @return The string converted to numeric.
	 */
	public static int string2int(final String str) {
		int result = 0;
		try {
			if (str != null) {
				result = Integer.parseInt(str);
			}
		} catch (final NumberFormatException e) {
			result = 0;
		}
		return result;
	}
}
