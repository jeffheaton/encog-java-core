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
package org.encog.mathutil.matrices;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.mathutil.matrices.decomposition.LUDecomposition;
import org.encog.mathutil.matrices.decomposition.QRDecomposition;
import org.encog.mathutil.randomize.RangeRandomizer;

/**
 * This class implements a mathematical matrix. Matrix math is very important to
 * neural network processing. Many of the neural network classes make use of the
 * matrix classes in this package.
 */
public class Matrix implements Cloneable, Serializable {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -7977897210426471675L;

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
	public final void add(final int row, final int col, final double value) {
		validate(row, col);
		final double newValue = this.matrix[row][col] + value;
		set(row, col, newValue);
	}

	/**
	 * Add the specified matrix to this matrix. This will modify the matrix to
	 * hold the result of the addition.
	 * 
	 * @param theMatrix
	 *            The matrix to add.
	 */
	public final void add(final Matrix theMatrix) {
		final double[][] source = theMatrix.getData();

		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] += source[row][col];
			}
		}
	}

	/**
	 * Set all rows and columns to zero.
	 */
	public final void clear() {
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
	public final Matrix clone() {
		return new Matrix(this.matrix);
	}

	/**
	 * Compare to matrixes with the specified level of precision.
	 * 
	 * @param theMatrix
	 *            The other matrix to compare to.
	 * @param precision
	 *            How much precision to use.
	 * @return True if the two matrixes are equal.
	 */
	public final boolean equals(final Matrix theMatrix, final int precision) {

		if (precision < 0) {
			throw new MatrixError("Precision can't be a negative number.");
		}

		final double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			throw new MatrixError("Precision of " + precision
					+ " decimal places is not supported.");
		}

		final int actualPrecision = (int) Math.pow(Encog.DEFAULT_PRECISION,
				precision);

		final double[][] data = theMatrix.getData();

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
	 * Check to see if this matrix equals another, using default precision.
	 * 
	 * @param other
	 *            The other matrix to compare.
	 * @return True if the two matrixes are equal.
	 */
	@Override
	public final boolean equals(final Object other) {
		if (other instanceof Matrix) {
			return equals((Matrix) other, Encog.DEFAULT_PRECISION);
		} else {
			return false;
		}
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
	public final int fromPackedArray(final double[] array, final int index) {
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
	public final double get(final int row, final int col) {
		validate(row, col);
		return this.matrix[row][col];
	}

	/**
	 * @return A COPY of this matrix as a 2d array.
	 */
	public final double[][] getArrayCopy() {
		final double[][] result = new double[getRows()][getCols()];
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				result[i][j] = this.matrix[i][j];
			}
		}
		return result;
	}

	/**
	 * Read one entire column from the matrix as a sub-matrix.
	 * 
	 * @param col
	 *            The column to read.
	 * @return The column as a sub-matrix.
	 */
	public final Matrix getCol(final int col) {
		if (col > getCols()) {
			throw new MatrixError("Can't get column #" + col
					+ " because it does not exist.");
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
	public final int getCols() {
		return this.matrix[0].length;
	}

	/**
	 * @return Get the 2D matrix array.
	 */
	public final double[][] getData() {
		return this.matrix;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index.
	 * @param i1
	 *            Final row index.
	 * @param j0
	 *            Initial column index.
	 * @param j1
	 *            Final column index.
	 * @return The specified submatrix.
	 */
	public final Matrix getMatrix(final int i0, final int i1, final int j0,
			final int j1) {

		final Matrix result = new Matrix(i1 - i0 + 1, j1 - j0 + 1);
		final double[][] b = result.getData();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					b[i - i0][j - j0] = this.matrix[i][j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index.
	 * @param i1
	 *            Final row index.
	 * @param c
	 *            Array of column indices.
	 * @return The specified submatrix.
	 */
	public final Matrix getMatrix(final int i0, final int i1, final int[] c) {
		final Matrix result = new Matrix(i1 - i0 + 1, c.length);
		final double[][] b = result.getData();
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					b[i - i0][j] = this.matrix[i][c[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return The specified submatrix.
	 */
	public final Matrix getMatrix(final int[] r, final int j0, final int j1) {
		final Matrix result = new Matrix(r.length, j1 - j0 + 1);
		final double[][] b = result.getData();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					b[i][j - j0] = this.matrix[r[i]][j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return The specified submatrix.
	 */
	public final Matrix getMatrix(final int[] r, final int[] c) {
		final Matrix result = new Matrix(r.length, c.length);
		final double[][] b = result.getData();
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					b[i][j] = this.matrix[r[i]][c[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
		return result;
	}

	/**
	 * Get the specified row as a sub-matrix.
	 * 
	 * @param row
	 *            The row to get.
	 * @return A matrix.
	 */
	public final Matrix getRow(final int row) {
		if (row > getRows()) {
			throw new MatrixError("Can't get row #" + row
					+ " because it does not exist.");
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
	public final int getRows() {
		return this.matrix.length;
	}

	/**
	 * Compute a hash code for this matrix.
	 * 
	 * @return The hash code.
	 */
	@Override
	public final int hashCode() {
		long result = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result += this.matrix[r][c];
			}
		}
		return (int) (result % Integer.MAX_VALUE);
	}

	/**
	 * @return The matrix inverted.
	 */
	public final Matrix inverse() {
		return solve(MatrixMath.identity(getRows()));
	}

	/**
	 * Determine if the matrix is a vector. A vector is has either a single
	 * number of rows or columns.
	 * 
	 * @return True if this matrix is a vector.
	 */
	public final boolean isVector() {
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
	public final boolean isZero() {
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
	public final void multiply(final double value) {

		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] *= value;
			}
		}
	}

	/**
	 * Multiply every row by the specified vector.
	 * 
	 * @param vector
	 *            The vector to multiply by.
	 * @param result
	 *            The result to hold the values.
	 */
	public final void multiply(final double[] vector, final double[] result) {
		for (int i = 0; i < getRows(); i++) {
			result[i] = 0;
			for (int j = 0; j < getCols(); j++) {
				result[i] += this.matrix[i][j] * vector[j];
			}
		}
	}

	/**
	 * Randomize the matrix.
	 * @param min Minimum random value.
	 * @param max Maximum random value.
	 */
	public final void randomize(final double min, final double max) {
		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] = RangeRandomizer.randomize(min, max);
			}
		}

	}

	/**
	 * Set every value in the matrix to the specified value.
	 * 
	 * @param value
	 *            The value to set the matrix to.
	 */
	public final void set(final double value) {
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
	public final void set(final int row, final int col, final double value) {
		validate(row, col);
		this.matrix[row][col] = value;
	}

	/**
	 * Set this matrix's values to that of another matrix.
	 * 
	 * @param theMatrix
	 *            The other matrix.
	 */
	public final void set(final Matrix theMatrix) {
		final double[][] source = theMatrix.getData();

		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getCols(); col++) {
				this.matrix[row][col] = source[row][col];
			}
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param x
	 *            A(i0:i1,j0:j1)
	 * 
	 */
	public final void setMatrix(final int i0, final int i1, final int j0,
			final int j1, final Matrix x) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = j0; j <= j1; j++) {
					this.matrix[i][j] = x.get(i - i0, j - j0);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param x
	 *            The submatrix.
	 */

	public final void setMatrix(final int i0, final int i1, final int[] c,
			final Matrix x) {
		try {
			for (int i = i0; i <= i1; i++) {
				for (int j = 0; j < c.length; j++) {
					this.matrix[i][c[j]] = x.get(i - i0, j);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param x
	 *            A(r(:),j0:j1)
	 */

	public final void setMatrix(final int[] r, final int j0, final int j1,
			final Matrix x) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = j0; j <= j1; j++) {
					this.matrix[r[i]][j] = x.get(i, j - j0);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param x
	 *            The matrix to set.
	 */
	public final void setMatrix(final int[] r, final int[] c, final Matrix x) {
		try {
			for (int i = 0; i < r.length; i++) {
				for (int j = 0; j < c.length; j++) {
					this.matrix[r[i]][c[j]] = x.get(i, j);
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
	}

	/**
	 * Get the size of the array. This is the number of elements it would take
	 * to store the matrix as a packed array.
	 * 
	 * @return The size of the matrix.
	 */
	public final int size() {
		return this.matrix[0].length * this.matrix.length;
	}

	/**
	 * Solve A*X = B.
	 * 
	 * @param b
	 *            right hand side.
	 * @return Solution if A is square, least squares solution otherwise.
	 */
	public final Matrix solve(final Matrix b) {
		if (getRows() == getCols()) {
			return (new LUDecomposition(this)).solve(b);
		} else {
			return (new QRDecomposition(this)).solve(b);
		}
	}

	/**
	 * Sum all of the values in the matrix.
	 * 
	 * @return The sum of the matrix.
	 */
	public final double sum() {
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
	public final double[] toPackedArray() {
		final double[] result = new double[getRows() * getCols()];

		int index = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result[index++] = this.matrix[r][c];
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
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
			throw new MatrixError(str);
		}

		if ((col >= getCols()) || (col < 0)) {
			final String str = "The col:" + col + " is out of range:"
					+ getCols();
			throw new MatrixError(str);
		}
	}
	
	public boolean isSquare() {
		return getRows()==getCols();
	}
}
