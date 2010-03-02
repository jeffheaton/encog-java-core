/*
 * Encog(tm) Core v2.4
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

package org.encog.mathutil;

/**
 * Used to produce an array of activations to classify data into groups. This
 * class is provided the number of groups, as well as the range that the
 * activations should fall into.
 */
public class Equilateral {

	/**
	 * The matrix of values that was generated.
	 */
	private final double[][] matrix;

	/**
	 * Construct an equilateral matrix.
	 * 
	 * @param count
	 *            The number of sets, these will be the rows in the matrix.
	 * @param high
	 *            The high value for the outputs.
	 * @param low
	 *            The low value for the outputs.
	 */
	public Equilateral(final int count, final double high, final double low) {
		this.matrix = equilat(count, high, low);
	}

	/**
	 * Decode a set of activations and see which set it has the lowest Euclidean
	 * distance from.
	 * 
	 * @param activations
	 *            The output from the neural network.
	 * @return The set that these activations were closest too.
	 */
	public int decode(final double[] activations) {
		double minValue = Double.POSITIVE_INFINITY;
		int minSet = -1;

		for (int i = 0; i < this.matrix.length; i++) {
			final double dist = getDistance(activations, i);
			if (dist < minValue) {
				minValue = dist;
				minSet = i;
			}
		}
		return minSet;
	}

	/**
	 * Get the activations for the specified set.
	 * 
	 * @param set
	 *            The set to determine the activations for.
	 * @return The activations for the specified sets.
	 */
	public double[] encode(final int set) {
		return this.matrix[set];
	}

	/**
	 * Called internally to generate the matrix.
	 * 
	 * @param n
	 *            The number of sets to generate for.
	 * @param high
	 *            The high end of the range of values to generate.
	 * @param low
	 *            The low end of the range of values to generate.
	 * @return One row for each set, the columns are the activations for that
	 *         set.
	 */
	private double[][] equilat(final int n, 
			final double high, final double low) {
		double r, f;
		final double[][] result = new double[n][n - 1];

		result[0][0] = -1;
		result[1][0] = 1.0;

		for (int k = 2; k < n; k++) {
			// scale the matrix so far
			r = k;
			f = Math.sqrt(r * r - 1.0) / r;
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < k - 1; j++) {
					result[i][j] *= f;
				}
			}

			r = -1.0 / r;
			for (int i = 0; i < k; i++) {
				result[i][k - 1] = r;
			}

			for (int i = 0; i < k - 1; i++) {
				result[k][i] = 0.0;
			}
			result[k][k - 1] = 1.0;
		}

		// scale it
		for (int row = 0; row < result.length; row++) {
			for (int col = 0; col < result[0].length; col++) {
				final double min = -1;
				final double max = 1;
				result[row][col] = ((result[row][col] - min) / (max - min))
						* (high - low) + low;
			}
		}

		return result;
	}

	/**
	 * Get the Euclidean distance between the specified data and the set number.
	 * @param data The data to check.
	 * @param set The set to check.
	 * @return The distance.
	 */
	public double getDistance(final double[] data, final int set) {
		double result = 0;
		for (int i = 0; i < data.length; i++) {
			result += Math.pow(data[i] - this.matrix[set][i], 2);
		}
		return Math.sqrt(result);
	}
}
