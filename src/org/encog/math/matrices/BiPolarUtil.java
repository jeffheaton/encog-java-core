/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.math.matrices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains a number of utility methods used to work with bipolar
 * numbers. A bipolar number is another way to represent binary numbers. The
 * value of true is defined to be one, where as false is defined to be negative
 * one.
 */
public final class BiPolarUtil {

	/**
	 * Convert a boolean to a bipolar number.
	 * 
	 * @param b
	 *            A boolean value.
	 * @return A bipolar number.
	 */
	public static double bipolar2double(final boolean b) {
		if (b) {
			return 1;
		}
		return -1;
	}

	/**
	 * Convert a boolean array to a bipolar array.
	 * 
	 * @param b
	 *            A an array of boolean values.
	 * @return An array of bipolar values.
	 */
	public static double[] bipolar2double(final boolean[] b) {
		final double[] result = new double[b.length];

		for (int i = 0; i < b.length; i++) {
			result[i] = BiPolarUtil.bipolar2double(b[i]);
		}

		return result;
	}

	/**
	 * 
	 * @param b
	 *            An array of boolean values.
	 * @return An array of bipolar values.
	 */
	public static double[][] bipolar2double(final boolean[][] b) {
		final double[][] result = new double[b.length][b[0].length];

		for (int row = 0; row < b.length; row++) {
			for (int col = 0; col < b[0].length; col++) {
				result[row][col] = BiPolarUtil.bipolar2double(b[row][col]);
			}
		}

		return result;
	}

	/**
	 * Convert a bipolar value to a boolean.
	 * 
	 * @param d
	 *            A bipolar value.
	 * @return A boolean value.
	 */
	public static boolean double2bipolar(final double d) {
		if (d > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Convert a bipolar array to booleans.
	 * 
	 * @param d
	 *            A bipolar array.
	 * @return An array of booleans.
	 */
	public static boolean[] double2bipolar(final double[] d) {
		final boolean[] result = new boolean[d.length];

		for (int i = 0; i < d.length; i++) {
			result[i] = BiPolarUtil.double2bipolar(d[i]);
		}

		return result;
	}

	/**
	 * Convert a bipolar array to a boolean array.
	 * 
	 * @param d
	 *            A bipolar array.
	 * @return A boolean array.
	 */
	public static boolean[][] double2bipolar(final double[][] d) {
		final boolean[][] result = new boolean[d.length][d[0].length];

		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				result[row][col] = BiPolarUtil.double2bipolar(d[row][col]);
			}
		}

		return result;
	}

	/**
	 * Normalize a binary number. If the number is not zero then make it 1, if
	 * it is zero, leave it alone.
	 * 
	 * @param d
	 *            A number to normalize to binary.
	 * @return A binary digit.
	 */
	public static double normalizeBinary(final double d) {
		if (d > 0) {
			return 1;
		}
		return 0;
	}

	/**
	 * Convert bipolar to binary.
	 * 
	 * @param d
	 *            A bipolar number.
	 * @return A binary digit.
	 */
	public static double toBinary(final double d) {
		return (d + 1) / 2.0;
	}

	/**
	 * Convert binary to bipolar.
	 * 
	 * @param d
	 *            A binary number.
	 * @return A bipolar number.
	 */
	public static double toBiPolar(final double d) {
		return 2 * BiPolarUtil.normalizeBinary(d) - 1;
	}

	/**
	 * Convert to binary and normalize.
	 * 
	 * @param d
	 *            A number to convert to binary.
	 * @return A normalized binary number.
	 */
	public static double toNormalizedBinary(final double d) {
		return BiPolarUtil.normalizeBinary(BiPolarUtil.toBinary(d));
	}

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Private constructor.
	 */
	private BiPolarUtil() {

	}
}
