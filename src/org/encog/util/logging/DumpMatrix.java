/*
 * Encog(tm) Core v2.5 
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

package org.encog.util.logging;

import java.text.NumberFormat;

import org.encog.mathutil.matrices.Matrix2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static String dumpMatrix(final Matrix2D matrix) {
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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Private constructor.
	 */
	private DumpMatrix() {

	}
}
