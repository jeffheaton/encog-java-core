package org.encog.util.csv;

import java.util.StringTokenizer;

public class NumberList {
	/**
	 * Convert an array of doubles to a comma separated list.
	 * 
	 * @param result
	 *            This string will have the values appended to it.
	 * @param data
	 *            The array of doubles to use.
	 */
	public static void toList(CSVFormat format, final StringBuilder result, 
			final double[] data) {
		result.setLength(0);
		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				result.append(format.getSeparator());
			}
			result.append(format.format(data[i],20));
		}
	}
	
	/**
	 * Get an array of double's from a string of comma separated text.
	 * 
	 * @param str
	 *            The string that contains a list of numbers.
	 * @return An array of doubles parsed from the string.
	 */
	public static double[] fromList(CSVFormat format, final String str) {
		// first count the numbers
		int count = 0;
		final StringTokenizer tok = new StringTokenizer(str, ""+format.getSeparator());
		while (tok.hasMoreTokens()) {
			tok.nextToken();
			count++;
		}

		// now allocate an object to hold that many numbers
		final double[] result = new double[count];

		// and finally parse the numbers
		int index = 0;
		final StringTokenizer tok2 = new StringTokenizer(str, ""+format.getSeparator());
		while (tok2.hasMoreTokens()) {
				final String num = tok2.nextToken();
				final double value = format.parse(num);
				result[index++] = value;
		}

		return result;
	}
}
