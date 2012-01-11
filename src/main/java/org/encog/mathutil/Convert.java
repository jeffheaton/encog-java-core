/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.mathutil;

/**
 * This class is used to convert strings into numeric values. If the string
 * holds a non-numeric value, a zero is returned.
 * 
 * @author jheaton
 * 
 */
public final class Convert {

	/**
	 * Convert a string to a double. Just make the number a zero if the string
	 * is invalid.
	 * 
	 * @param str
	 *            The string.
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
	 * Convert a string to an int. Just make the number a zero if the string is
	 * invalid.
	 * 
	 * @param str
	 *            The string.
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

	/**
	 * Private constructor.
	 */
	private Convert() {

	}
}
