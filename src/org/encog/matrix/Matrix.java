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

package org.encog.matrix;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a mathematical matrix. Matrix math is very important to
 * neural network processing. Many of the neural network classes make use of the
 * matrix classes in this package.
 */
public class Matrix implements Cloneable, Serializable, EncogPersistedObject {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -7977897210426471675L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(Matrix.class);

	/**
	 * Turn an array of doubles into a column matrix.
	 * 
	 * @param input
	 *            A double array.
	 * @return A column matrix.
	 */
	public static Matrix createColumnMatrix(final double[] input) {
		final double[][] d = new double[input.length][1];
		for (int row = 0; row < d.length; row++) {
			d[row][0] = input[row];
		}
		return new Matrix(d);
	}

	/**
	 * Turn an array of doubles into a row matrix.
	 * 
	 * @param input
	 *            A double array.
	 * @return A row matrix.
	 */
	public static Matrix createRowMatrix(final double[] input) {
		final double[][] d = new double[1][input.length];
		System.arraycopy(input, 0, d[0], 0, input.length);
		return new Matrix(d);
	}

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * The description for this object.
	 */
	private String description;

	/**
	 * The matrix data.
	 */
	private final double[][] matrix;

	/**
	 * Construct a bipolar matrix from an array of booleans.
	 * 
	 * @param sourceMatrix
	 *            The booleans to create the matrix from.
	 */
	public Matrix(final boolean[][] sourceMatrix) {
		this.matrix = new double[sourceMatrix.length][sourceMatrix[0].length];
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				if (sourceMatrix[r][c]) {
					set(r, c, 1);
				} else {
					set(r, c, -1);
				}
			}
		}
	}

	/**
	 * Create a matrix from an array of doubles.
	 * 
	 * @param sourceMatrix
	 *            An array of doubles.
	 */
	public Matrix(final double[][] sourceMatrix) {
		this.matrix = new double[sourceMatrix.length][sourceMatrix[0].length];
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				set(r, c, sourceMatrix[r][c]);
			}
		}
	}

	/**
	 * Create a blank array with the specified number of rows and columns.
	 * 
	 * @param rows
	 *            How many rows in the matrix.
	 * @param cols
	 *            How many columns in the matrix.
	 */
	public Matrix(final int rows, final int cols) {
		this.matrix = new double[rows][cols];
	}

	/**
	 * Add a value to one cell in the matrix.
	 * 
	 * @param row
	 *            The row to add to.
	 * @param col
	 *            The column to add to.
	 * @param value
	 *            The value to add to the matrix.
	 */
	public void add(final int row, final int col, final double value) {
		validate(row, col);
		final double newValue = this.matrix[row][col] + value;
		set(row, col, newValue);
	}

	/**
	 * Add the specified matrix to this matrix. This will modify the matrix to
	 * hold the result of the addition.
	 * 
	 * @param matrix
	 *            The matrix to add.
	 */
	public void add(final Matrix matrix) {
		final double[][] source = matrix.getData();

		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] += source[row][col];
			}
		}
	}

	/**
	 * Set all rows and columns to zero.
	 */
	public void clear() {
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				this.matrix[r][c] = 0;
			}
		}
	}

	/**
	 * Create a copy of the matrix.
	 * 
	 * @return A colne of the matrix.
	 */
	@Override
	public Matrix clone() {
		return new Matrix(this.matrix);
	}

	/**
	 * Create a Persistor for this object.
	 * 
	 * @return The new persistor.
	 */
	public Persistor createPersistor() {
		return null;
	}

	/**
	 * Check to see if this matrix equals another, using default precision.
	 * 
	 * @param matrix
	 *            The other matrix to compare.
	 * @return True if the two matrixes are equal.
	 */
	public boolean equals(final Matrix matrix) {
		return equals(matrix, Encog.DEFAULT_PRECISION);
	}

	/**
	 * Compare to matrixes with the specified level of precision.
	 * 
	 * @param matrix
	 *            The other matrix to compare to.
	 * @param precision
	 *            How much precision to use.
	 * @return True if the two matrixes are equal.
	 */
	public boolean equals(final Matrix matrix, final int precision) {

		if (precision < 0) {
			final String str = "Precision can't be a negative number.";
			if (Matrix.LOGGER.isErrorEnabled()) {
				Matrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			final String str = "Precision of " + precision
					+ " decimal places is not supported.";
			if (Matrix.LOGGER.isErrorEnabled()) {
				Matrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final int actualPrecision = (int) Math.pow(Encog.DEFAULT_PRECISION,
				precision);

		final double[][] data = matrix.getData();

		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				if ((long) (this.matrix[r][c] * actualPrecision) 
						!= (long) (data[r][c] * actualPrecision)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Create a matrix from a packed array.
	 * 
	 * @param array
	 *            The packed array.
	 * @param index
	 *            Where to start in the packed array.
	 * @return The new index after this matrix has been read.
	 */
	public int fromPackedArray(final Double[] array, final int index) {
		int i = index;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				this.matrix[r][c] = array[i++];
			}
		}

		return i;
	}

	/**
	 * Read the specified cell in the matrix.
	 * 
	 * @param row
	 *            The row to read.
	 * @param col
	 *            The column to read.
	 * @return The value at the specified row and column.
	 */
	public double get(final int row, final int col) {
		validate(row, col);
		return this.matrix[row][col];
	}

	/**
	 * Read one entire column from the matrix as a sub-matrix.
	 * 
	 * @param col
	 *            The column to read.
	 * @return The column as a sub-matrix.
	 */
	public Matrix getCol(final int col) {
		if (col > getCols()) {
			final String str = "Can't get column #" + col
					+ " because it does not exist.";
			if (Matrix.LOGGER.isErrorEnabled()) {
				Matrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] newMatrix = new double[getRows()][1];

		for (int row = 0; row < getRows(); row++) {
			newMatrix[row][0] = this.matrix[row][col];
		}

		return new Matrix(newMatrix);
	}

	/**
	 * Get the columns in the matrix.
	 * 
	 * @return The number of columns in the matrix.
	 */
	public int getCols() {
		return this.matrix[0].length;
	}

	/**
	 * @return Get the 2D matrix array.
	 */
	public double[][] getData() {
		return this.matrix;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the specified row as a sub-matrix.
	 * 
	 * @param row
	 *            The row to get.
	 * @return A matrix.
	 */
	public Matrix getRow(final int row) {
		if (row > getRows()) {
			final String str = "Can't get row #" + row
					+ " because it does not exist.";
			if (Matrix.LOGGER.isErrorEnabled()) {
				Matrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] newMatrix = new double[1][getCols()];

		for (int col = 0; col < getCols(); col++) {
			newMatrix[0][col] = this.matrix[row][col];
		}

		return new Matrix(newMatrix);
	}

	/**
	 * Get the number of rows in the matrix.
	 * 
	 * @return The number of rows in the matrix.
	 */
	public int getRows() {
		return this.matrix.length;
	}

	/**
	 * Compute a hash code for this matrix.
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		long result = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result += this.matrix[r][c];
			}
		}
		return (int) (result % Integer.MAX_VALUE);
	}

	/**
	 * Determine if the matrix is a vector. A vector is has either a single
	 * number of rows or columns.
	 * 
	 * @return True if this matrix is a vector.
	 */
	public boolean isVector() {
		if (getRows() == 1) {
			return true;
		}
		return getCols() == 1;
	}

	/**
	 * Return true if every value in the matrix is zero.
	 * 
	 * @return True if the matrix is all zeros.
	 */
	public boolean isZero() {
		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				if (this.matrix[row][col] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Multiply every value in the matrix by the specified value.
	 * 
	 * @param value
	 *            The value to multiply the matrix by.
	 */
	public void multiply(final double value) {

		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] *= value;
			}
		}
	}

	/**
	 * Set every value in the matrix to the specified value.
	 * 
	 * @param value
	 *            The value to set the matrix to.
	 */
	public void set(final double value) {
		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] = value;
			}
		}

	}

	/**
	 * Set an individual cell in the matrix to the specified value.
	 * 
	 * @param row
	 *            The row to set.
	 * @param col
	 *            The column to set.
	 * @param value
	 *            The value to be set.
	 */
	public void set(final int row, final int col, final double value) {
		validate(row, col);
		this.matrix[row][col] = value;
	}

	/**
	 * Set this matrix's values to that of another matrix.
	 * 
	 * @param matrix
	 *            The other matrix.
	 */
	public void set(final Matrix matrix) {
		final double[][] source = matrix.getData();

		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] = source[row][col];
			}
		}
	}

	/**
	 * Set the description for this object.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the size of the array. This is the number of elements it would take
	 * to store the matrix as a packed array.
	 * 
	 * @return The size of the matrix.
	 */
	public int size() {
		return this.matrix[0].length * this.matrix.length;
	}

	/**
	 * Sum all of the values in the matrix.
	 * 
	 * @return The sum of the matrix.
	 */
	public double sum() {
		double result = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result += this.matrix[r][c];
			}
		}
		return result;
	}

	/**
	 * Convert the matrix into a packed array.
	 * 
	 * @return The matrix as a packed array.
	 */
	public Double[] toPackedArray() {
		final Double[] result = new Double[getRows() * getCols()];

		int index = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result[index++] = this.matrix[r][c];
			}
		}

		return result;
	}

	/**
	 * @return Convert the matrix to a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[Matrix: rows=");
		result.append(getRows());
		result.append(",cols=");
		result.append(getCols());
		result.append("]");
		return result.toString();
	}

	/**
	 * Validate that the specified row and column are within the required
	 * ranges. Otherwise throw a MatrixError exception.
	 * 
	 * @param row
	 *            The row to check.
	 * @param col
	 *            The column to check.
	 */
	private void validate(final int row, final int col) {
		if ((row >= getRows()) || (row < 0)) {
			final String str = "The row:" + row + " is out of range:"
					+ getRows();
			if (Matrix.LOGGER.isErrorEnabled()) {
				Matrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		if ((col >= getCols()) || (col < 0)) {
			final String str = "The col:" + col + " is out of range:"
					+ getCols();
			if (Matrix.LOGGER.isErrorEnabled()) {
				Matrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}
	}

}
