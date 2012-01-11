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

import org.encog.Encog;

/**
 * Several useful math functions for Encog.
 */
public final class EncogMath {

	/**
	 * Convert degrees to radians.
	 * 
	 * @param deg
	 *            Degrees.
	 * @return Radians.
	 */
	public static double deg2rad(final double deg) {
		return deg * (Math.PI / MathConst.DEG_SEMICIRCLE);
	}

	/**
	 * Determine if one double equals another, within the default percision.
	 * 
	 * @param d1
	 *            The first number.
	 * @param d2
	 *            The second number.
	 * @return True if the two doubles are equal.
	 */
	public static boolean doubleEquals(final double d1, final double d2) {
		return Math.abs(d1 - d2) < Encog.DEFAULT_DOUBLE_EQUAL;
	}

	/**
	 * sqrt(a^2 + b^2) without under/overflow.
	 * 
	 * @param a
	 *            First param.
	 * @param b
	 *            Second param.
	 * @return The result.
	 */
	public static double hypot(final double a, final double b) {
		double r;
		if (Math.abs(a) > Math.abs(b)) {
			r = b / a;
			r = Math.abs(a) * Math.sqrt(1 + r * r);
		} else if (b != 0) {
			r = a / b;
			r = Math.abs(b) * Math.sqrt(1 + r * r);
		} else {
			r = 0.0;
		}
		return r;
	}

	/**
	 * Get the index to the greatest number in a double array.
	 * 
	 * @param array
	 *            The array to search.
	 * @return The index of the greatest value, or -1 if empty.
	 */
	public static int maxIndex(final double[] array) {
		int result = -1;

		for (int i = 0; i < array.length; i++) {
			if ((result == -1) || (array[result] < array[i])) {
				result = i;
			}
		}

		return result;
	}

	/**
	 * Get the index to the smallest number in a double array.
	 * 
	 * @param array
	 *            The array to search.
	 * @return The index of the smallest value, or -1 if empty.
	 */
	public static int minIndex(final double[] array) {
		int result = -1;

		for (int i = 0; i < array.length; i++) {
			if ((result == -1) || (array[result] > array[i])) {
				result = i;
			}
		}

		return result;
	}

	/**
	 * Convert radians to degrees.
	 * 
	 * @param rad
	 *            Radians
	 * @return Degrees.
	 */
	public static double rad2deg(final double rad) {
		return rad * (MathConst.DEG_SEMICIRCLE / Math.PI);
	}
	
	/**
	 * Calculate x!.
	 * @param x The number to calculate for.
	 * @return The factorial of x.
	 */
	public static double factorial(int x)
    {
        double result = 1.0;

        for (int i = 1; i <= x; i++)
        {
            result *= (double)i;
        }

        return result;
    }

	/**
	 * Private constructor.
	 */
	private EncogMath() {

	}

	public static double square(double d) {
		return d*d;
	}
}
