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

import java.io.Serializable;

import org.encog.Encog;
import org.encog.mathutil.matrices.decomposition.LUDecomposition;
import org.encog.mathutil.matrices.decomposition.QRDecomposition;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a mathematical matrix. Matrix math is very important to
 * neural network processing. Many of the neural network classes make use of the
 * matrix classes in this package.
 */
public class Matrix2D extends BasicMatrix {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -7977897210426471675L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(Matrix2D.class);
	

	/**
	 * Turn an array of doubles into a column matrix.
	 * 
	 * @param input
	 *            A double array.
	 * @return A column matrix.
	 */
	public static Matrix2D createColumnMatrix(final double[] input) {
		final double[][] d = new double[input.length][1];
		for (int row = 0; row < d.length; row++) {
			d[row][0] = input[row];
		}
		return new Matrix2D(d);
	}

	/**
	 * Turn an array of doubles into a row matrix.
	 * 
	 * @param input
	 *            A double array.
	 * @return A row matrix.
	 */
	public static Matrix2D createRowMatrix(final double[] input) {
		final double[][] d = new double[1][input.length];
		System.arraycopy(input, 0, d[0], 0, input.length);
		return new Matrix2D(d);
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
	public Matrix2D(final boolean[][] sourceMatrix) {
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
	public Matrix2D(final double[][] sourceMatrix) {
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
	public Matrix2D(final int rows, final int cols) {
		this.matrix = new double[rows][cols];
	}


	/**
	 * Create a copy of the matrix.
	 * 
	 * @return A colne of the matrix.
	 */
	@Override
	public Matrix2D clone() {
		return new Matrix2D(this.matrix);
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
	public Matrix2D getMatrix(final int i0, final int i1, final int j0,
			final int j1) {

		final Matrix2D result = new Matrix2D(i1 - i0 + 1, j1 - j0 + 1);
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
	public Matrix2D getMatrix(final int i0, final int i1, final int[] c) {
		final Matrix2D result = new Matrix2D(i1 - i0 + 1, c.length);
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
	public Matrix2D getMatrix(final int[] r, final int j0, final int j1) {
		final Matrix2D result = new Matrix2D(r.length, j1 - j0 + 1);
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
	public Matrix2D getMatrix(final int[] r, final int[] c) {
		final Matrix2D result = new Matrix2D(r.length, c.length);
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
	public Matrix2D getRow(final int row) {
		if (row > getRows()) {
			final String str = "Can't get row #" + row
					+ " because it does not exist.";
			if (Matrix2D.LOGGER.isErrorEnabled()) {
				Matrix2D.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] newMatrix = new double[1][getCols()];

		for (int col = 0; col < getCols(); col++) {
			newMatrix[0][col] = this.matrix[row][col];
		}

		return new Matrix2D(newMatrix);
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
	 * @return The matrix inverted.
	 */
	public Matrix2D inverse() {
		return solve(MatrixMath.identity(getRows()));
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
	 * Multiply every row by the specified vector.
	 * 
	 * @param vector
	 *            The vector to multiply by.
	 * @param result
	 *            The result to hold the values.
	 */
	public void multiply(final double[] vector, final double[] result) {
		for (int i = 0; i < getRows(); i++) {
			result[i] = 0;
			for (int j = 0; j < getCols(); j++) {
				result[i] += this.matrix[i][j] * vector[j];
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
		
		if (matrix instanceof Matrix2D) {
			final double[][] source = ((Matrix2D)matrix).getData();

			for (int row = 0; row < getRows(); row++) {
				for (int col = 0; col < getCols(); col++) {
					this.matrix[row][col] = source[row][col];
				}
			}
		} else {
			for (int row = 0; row < getRows(); row++) {
				for (int col = 0; col < getCols(); col++) {
					this.matrix[row][col] = matrix.get(row, col);
				}
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
	public void setMatrix(final int i0, final int i1, final int j0,
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

	public void setMatrix(final int i0, final int i1, final int[] c,
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

	public void setMatrix(final int[] r, final int j0, final int j1,
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
	public void setMatrix(final int[] r, final int[] c, final Matrix x) {
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
	 * Solve A*X = B.
	 * 
	 * @param b
	 *            right hand side.
	 * @return Solution if A is square, least squares solution otherwise.
	 */
	public Matrix2D solve(final Matrix2D b) {
		if (getRows() == getCols()) {
			return (new LUDecomposition(this)).solve(b);
		} else {
			return (new QRDecomposition(this)).solve(b);
		}
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


}
