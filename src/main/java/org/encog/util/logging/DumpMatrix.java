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
package org.encog.util.logging;

import java.text.NumberFormat;

import org.encog.mathutil.matrices.Matrix;

/**
 * A utility for writing matrixes to the log.
 * 
 * @author jheaton
 * 
 */
public final class DumpMatrix {

	/**
	 * Maximum precision.
	 */
	public static final int MAX_PRECIS = 3;

	/**
	 * Dump an array of numbers to a string.
	 * 
	 * @param d
	 *            The array to dump.
	 * @return The array as a string.
	 */
	public static String dumpArray(final double[] d) {
		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(DumpMatrix.MAX_PRECIS);
		format.setMaximumFractionDigits(DumpMatrix.MAX_PRECIS);

		final StringBuilder result = new StringBuilder();
		result.append("[");
		for (int i = 0; i < d.length; i++) {
			if (i != 0) {
				result.append(",");
			}
			result.append(format.format(d[i]));
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * Dump a matrix to a string.
	 * 
	 * @param matrix
	 *            The matrix.
	 * @return The matrix as a string.
	 */
	public static String dumpMatrix(final Matrix matrix) {
		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(DumpMatrix.MAX_PRECIS);
		format.setMaximumFractionDigits(DumpMatrix.MAX_PRECIS);

		final StringBuilder result = new StringBuilder();
		result.append("==");
		result.append(matrix.toString());
		result.append("==\n");
		for (int row = 0; row < matrix.getRows(); row++) {
			result.append("  [");
			for (int col = 0; col < matrix.getCols(); col++) {
				if (col != 0) {
					result.append(",");
				}
				result.append(format.format(matrix.get(row, col)));
			}
			result.append("]\n");
		}
		return result.toString();
	}

	/**
	 * Private constructor.
	 */
	private DumpMatrix() {

	}
}
