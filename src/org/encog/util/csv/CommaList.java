package org.encog.util.csv;

import java.util.StringTokenizer;

import org.encog.persist.PersistError;

public class CommaList {
	/**
	 * Convert an array of doubles to a comma separated list.
	 * 
	 * @param result
	 *            This string will have the values appended to it.
	 * @param data
	 *            The array of doubles to use.
	 */
	public static void toCommas(final StringBuilder result, 
			final double[] data) {
		result.setLength(0);
		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				result.append(',');
			}
			result.append(data[i]);
		}
	}
	
	/**
	 * Get an array of double's from a string of comma separated text.
	 * 
	 * @param str
	 *            The string that contains a list of numbers.
	 * @return An array of doubles parsed from the string.
	 */
	public static double[] fromCommas(final String str) {
		// first count the numbers
		int count = 0;
		final StringTokenizer tok = new StringTokenizer(str, ",");
		while (tok.hasMoreTokens()) {
			tok.nextToken();
			count++;
		}

		// now allocate an object to hold that many numbers
		final double[] result = new double[count];

		// and finally parse the numbers
		int index = 0;
		final StringTokenizer tok2 = new StringTokenizer(str, ",");
		while (tok2.hasMoreTokens()) {
			try {
				final String num = tok2.nextToken();
				final double value = Double.parseDouble(num);
				result[index++] = value;
			} catch (final NumberFormatException e) {
				throw new PersistError(e);
			}

		}

		return result;
	}
}
