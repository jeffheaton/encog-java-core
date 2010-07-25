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

package org.encog.mathutil.matrices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can perform many different mathematical operations on matrixes.
 * The matrixes passed in will not be modified, rather a new matrix, with the
 * operation performed, will be returned.
 */
public final class MatrixMath {

	/**
	 * The logging object.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MatrixMath.class);

	/**
	 * Add two matrixes.
	 * 
	 * @param a
	 *            The first matrix to add.
	 * @param b
	 *            The second matrix to add.
	 * @return A new matrix of the two added.
	 */
	public static Matrix add(final Matrix a, final Matrix b) {
		if (a.getRows() != b.getRows()) {
			final String str = "To add the matrices they must have the same number of "
					+ "rows and columns.  Matrix a has "
					+ a.getRows()
					+ " rows and matrix b has " + b.getRows() + " rows.";

			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}

			throw new MatrixError(str);
		}

		if (a.getCols() != b.getCols()) {
			final String str = "To add the matrices they must have the same number "
					+ "of rows and columns.  Matrix a has "
					+ a.getCols()
					+ " cols and matrix b has " + b.getCols() + " cols.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] result = new double[a.getRows()][a.getCols()];

		if (a instanceof Matrix2D && b instanceof Matrix2D) {
			final double[][] aa = ((Matrix2D) a).getData();
			final double[][] bb = ((Matrix2D) b).getData();

			for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
				for (int resultCol = 0; resultCol < a.getCols(); resultCol++) {
					result[resultRow][resultCol] = aa[resultRow][resultCol]
							+ bb[resultRow][resultCol];
				}
			}
		} else {
			for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
				for (int resultCol = 0; resultCol < a.getCols(); resultCol++) {
					result[resultRow][resultCol] = a.get(resultRow, resultCol)
							+ b.get(resultRow, resultCol);
				}
			}

		}

		return new Matrix2D(result);
	}

	/**
	 * Copy from one matrix to another.
	 * 
	 * @param source
	 *            The source matrix for the copy.
	 * @param target
	 *            The target matrix for the copy.
	 */
	public static void copy(final Matrix source, final Matrix target) {

		if (source instanceof Matrix2D && target instanceof Matrix2D) {
			final double[][] s = ((Matrix2D) source).getData();
			final double[][] t = ((Matrix2D) target).getData();

			for (int row = 0; row < source.getRows(); row++) {
				for (int col = 0; col < source.getCols(); col++) {
					t[row][col] = s[row][col];
				}
			}
		} else {
			for (int row = 0; row < source.getRows(); row++) {
				for (int col = 0; col < source.getCols(); col++) {
					target.set(row, col, source.get(row, col));
				}
			}
		}

	}

	/**
	 * Delete one column from the matrix. Does not actually touch the source
	 * matrix, rather a new matrix with the column deleted is returned.
	 * 
	 * @param matrix
	 *            The matrix.
	 * @param deleted
	 *            The column to delete.
	 * @return A matrix with the column deleted.
	 */
	public static Matrix2D deleteCol(final Matrix matrix, final int deleted) {
		if (deleted >= matrix.getCols()) {
			final String str = "Can't delete column " + deleted
					+ " from matrix, it only has " + matrix.getCols()
					+ " columns.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}
		final double[][] newMatrix = new double[matrix.getRows()][matrix
				.getCols() - 1];

		for (int row = 0; row < matrix.getRows(); row++) {
			int targetCol = 0;

			for (int col = 0; col < matrix.getCols(); col++) {
				if (col != deleted) {
					newMatrix[row][targetCol] = matrix.get(row, col);
					targetCol++;
				}

			}

		}
		return new Matrix2D(newMatrix);
	}

	/**
	 * Delete a row from the matrix. Does not actually touch the matrix, rather
	 * returns a new matrix.
	 * 
	 * @param matrix
	 *            The matrix.
	 * @param deleted
	 *            Which row to delete.
	 * @return A new matrix with the specified row deleted.
	 */
	public static Matrix2D deleteRow(final Matrix matrix, final int deleted) {

		if (deleted >= matrix.getRows()) {
			final String str = "Can't delete row " + deleted
					+ " from matrix, it only has " + matrix.getRows()
					+ " rows.";

			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}
		final double[][] newMatrix = new double[matrix.getRows() - 1][matrix
				.getCols()];

		int targetRow = 0;
		for (int row = 0; row < matrix.getRows(); row++) {
			if (row != deleted) {
				for (int col = 0; col < matrix.getCols(); col++) {
					newMatrix[targetRow][col] = matrix.get(row, col);
				}
				targetRow++;
			}
		}
		return new Matrix2D(newMatrix);
	}

	/**
	 * Return a matrix with each cell divided by the specified value.
	 * 
	 * @param a
	 *            The matrix to divide.
	 * @param b
	 *            The value to divide by.
	 * @return A new matrix with the division performed.
	 */
	public static Matrix2D divide(final Matrix a, final double b) {
		final double[][] result = new double[a.getRows()][a.getCols()];

		for (int row = 0; row < a.getRows(); row++) {
			for (int col = 0; col < a.getCols(); col++) {
				result[row][col] = a.get(row, col) / b;
			}
		}
		return new Matrix2D(result);
	}

	/**
	 * Compute the dot product for the two matrixes. To compute the dot product,
	 * both
	 * 
	 * @param a
	 *            The first matrix.
	 * @param b
	 *            The second matrix.
	 * @return The dot product.
	 */
	public static double dotProduct(final Matrix a, final Matrix b) {
		if (!a.isVector() || !b.isVector()) {
			final String str = "To take the dot product, both matrices must be vectors.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);

		}

		final Double[] aArray = a.toPackedArray();
		final Double[] bArray = b.toPackedArray();

		if (aArray.length != bArray.length) {
			final String str = "To take the dot product, both matrices must be of "
					+ "the same length.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		double result = 0;
		final int length = aArray.length;

		for (int i = 0; i < length; i++) {
			result += aArray[i] * bArray[i];
		}

		return result;
	}

	/**
	 * Return an identity matrix of the specified size.
	 * 
	 * @param size
	 *            The number of rows and columns to create. An identity matrix
	 *            is always square.
	 * @return An identity matrix.
	 */
	public static Matrix2D identity(final int size) {
		if (size < 1) {
			final String str = "Identity matrix must be at least of "
					+ "size 1.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final Matrix2D result = new Matrix2D(size, size);
		final double[][] d = result.getData();

		for (int i = 0; i < size; i++) {
			d[i][i] = 1;
		}

		return result;
	}

	/**
	 * Return the result of multiplying every cell in the matrix by the
	 * specified value.
	 * 
	 * @param a
	 *            The first matrix.
	 * @param b
	 *            The second matrix.
	 * @return The result of the multiplication.
	 */
	public static Matrix2D multiply(final Matrix a, final double b) {

		final double[][] result = new double[a.getRows()][a.getCols()];

		if (a instanceof Matrix2D) {
			final double[][] d = ((Matrix2D) a).getData();

			for (int row = 0; row < a.getRows(); row++) {
				for (int col = 0; col < a.getCols(); col++) {
					result[row][col] = d[row][col] * b;
				}
			}
		} else {
			for (int row = 0; row < a.getRows(); row++) {
				for (int col = 0; col < a.getCols(); col++) {
					result[row][col] = a.get(row, col) * b;
				}
			}
		}
		return new Matrix2D(result);
	}

	/**
	 * Return the product of the first and second matrix.
	 * 
	 * @param a
	 *            The first matrix.
	 * @param b
	 *            The second matrix.
	 * @return The result of the multiplication.
	 */
	public static Matrix2D multiply(final Matrix a, final Matrix b) {

		if (b.getRows() != a.getCols()) {
			final String str = "To use ordinary matrix multiplication the number of "
					+ "columns on the first matrix must mat the number of "
					+ "rows on the second.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final Matrix2D result = new Matrix2D(a.getRows(), b.getCols());
		final double[][] c = result.getData();

		if (a instanceof Matrix2D && b instanceof Matrix2D) {
			final double[][] aData = ((Matrix2D) a).getData();
			final double[][] bData = ((Matrix2D) b).getData();

			final double[] bcolj = new double[a.getCols()];
			for (int j = 0; j < b.getCols(); j++) {
				for (int k = 0; k < a.getCols(); k++) {
					bcolj[k] = bData[k][j];
				}
				for (int i = 0; i < a.getRows(); i++) {
					final double[] arowi = aData[i];
					double s = 0;
					for (int k = 0; k < a.getCols(); k++) {
						s += arowi[k] * bcolj[k];
					}
					c[i][j] = s;
				}
			}
		} else {
			for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
				for (int resultCol = 0; resultCol < b.getCols(); resultCol++) {
					double value = 0;

					for (int i = 0; i < a.getCols(); i++) {

						value += a.get(resultRow, i) * b.get(i, resultCol);
					}
					result.set(resultRow, resultCol, value);
				}
			}
		}

		return result;
	}

	/**
	 * Return the results of subtracting one matrix from another.
	 * 
	 * @param a
	 *            The first matrix.
	 * @param b
	 *            The second matrix.
	 * @return The results of the subtraction.
	 */
	public static Matrix2D subtract(final Matrix a, final Matrix b) {
		if (a.getRows() != b.getRows()) {
			final String str = "To subtract the matrices they must have the same "
					+ "number of rows and columns.  Matrix a has "
					+ a.getRows()
					+ " rows and matrix b has "
					+ b.getRows()
					+ " rows.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		if (a.getCols() != b.getCols()) {
			final String str = "To subtract the matrices they must have the same "
					+ "number of rows and columns.  Matrix a has "
					+ a.getCols()
					+ " cols and matrix b has "
					+ b.getCols()
					+ " cols.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] result = new double[a.getRows()][a.getCols()];

		if (a instanceof Matrix2D && b instanceof Matrix2D) {
			final double[][] aa = ((Matrix2D) a).getData();
			final double[][] bb = ((Matrix2D) b).getData();

			for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
				for (int resultCol = 0; resultCol < a.getCols(); resultCol++) {
					result[resultRow][resultCol] = aa[resultRow][resultCol]
							- bb[resultRow][resultCol];
				}
			}
		} else {
			for (int resultRow = 0; resultRow < a.getRows(); resultRow++) {
				for (int resultCol = 0; resultCol < a.getCols(); resultCol++) {
					result[resultRow][resultCol] = a.get(resultRow, resultCol)
							- b.get(resultRow, resultCol);
				}
			}

		}

		return new Matrix2D(result);
	}

	/**
	 * Return the transposition of a matrix.
	 * 
	 * @param input
	 *            The matrix to transpose.
	 * @return The matrix transposed.
	 */
	public static Matrix2D transpose(final Matrix input) {
		final double[][] transposeMatrix = new double[input.getCols()][input
				.getRows()];

		if (input instanceof Matrix2D) {
			final double[][] d = ((Matrix2D) input).getData();

			for (int r = 0; r < input.getRows(); r++) {
				for (int c = 0; c < input.getCols(); c++) {
					transposeMatrix[c][r] = d[r][c];
				}
			}
		}

		return new Matrix2D(transposeMatrix);
	}

	/**
	 * Calculate the length of a vector.
	 * 
	 * @param input
	 *            The matrix to calculate the length of.
	 * 
	 * @return Vector length.
	 */
	public static double vectorLength(final Matrix input) {

		if (!input.isVector()) {
			final String str = "Can only take the vector length of a vector.";
			if (MatrixMath.LOGGER.isErrorEnabled()) {
				MatrixMath.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}
		final Double[] v = input.toPackedArray();
		double rtn = 0.0;
		for (final Double element : v) {
			rtn += Math.pow(element, 2);
		}
		return Math.sqrt(rtn);
	}

	/**
	 * A private constructor.
	 */
	private MatrixMath() {
	}

}
