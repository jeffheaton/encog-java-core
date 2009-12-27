/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.util.math;

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
