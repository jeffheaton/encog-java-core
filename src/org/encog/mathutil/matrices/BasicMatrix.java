package org.encog.mathutil.matrices;

import org.encog.Encog;
import org.encog.persist.EncogCollection;
import org.encog.persist.Persistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicMatrix implements Matrix {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(BasicMatrix.class);
	
	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * The description for this object.
	 */
	private String description;

	
	public abstract Matrix clone();
	
	

	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
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
	 * Validate that the specified row and column are within the required
	 * ranges. Otherwise throw a MatrixError exception.
	 * 
	 * @param row
	 *            The row to check.
	 * @param col
	 *            The column to check.
	 */
	protected void validate(final int row, final int col) {
		if ((row >= getRows()) || (row < 0)) {
			final String str = "The row:" + row + " is out of range:"
					+ getRows();
			if (BasicMatrix.LOGGER.isErrorEnabled()) {
				BasicMatrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		if ((col >= getCols()) || (col < 0)) {
			final String str = "The col:" + col + " is out of range:"
					+ getCols();
			if (BasicMatrix.LOGGER.isErrorEnabled()) {
				BasicMatrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}
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
				result += get(r,c);
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
				result[index++] = get(r,c);
			}
		}

		return result;
	}
	
	/**
	 * Get the size of the array. This is the number of elements it would take
	 * to store the matrix as a packed array.
	 * 
	 * @return The size of the matrix.
	 */
	public int size() {
		return getRows()*getCols();
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
		final double newValue = get(row,col) + value;
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
		
		if (matrix instanceof Matrix2D) {

			for (int row = 0; row < getRows(); row++) {
				for (int col = 0; col < getCols(); col++) {
					add(row,col,matrix.get(row,col));
				}
			}
		}
		else {
			for (int row = 0; row < getRows(); row++) {
				for (int col = 0; col < getCols(); col++) {
					add(row,col, matrix.get(row, col));
				}
			}			
		}

	}

	/**
	 * Set all rows and columns to zero.
	 */
	public void clear() {
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				set(r,c,0);
			}
		}
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
			if (BasicMatrix.LOGGER.isErrorEnabled()) {
				BasicMatrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			final String str = "Precision of " + precision
					+ " decimal places is not supported.";
			if (BasicMatrix.LOGGER.isErrorEnabled()) {
				BasicMatrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final int actualPrecision = (int) Math.pow(Encog.DEFAULT_PRECISION,
				precision);

		if (matrix instanceof Matrix2D) {
			final double[][] data = ((Matrix2D)matrix).getData();

			for (int r = 0; r < getRows(); r++) {
				for (int c = 0; c < getCols(); c++) {
					if ((long) (get(r,c) * actualPrecision) != (long) (data[r][c] * actualPrecision)) {
						return false;
					}
				}
			}
		}
		else
		{
			for (int r = 0; r < getRows(); r++) {
				for (int c = 0; c < getCols(); c++) {
					if ((long) (get(r,c) * actualPrecision) != (long) (matrix.get(r,c) * actualPrecision)) {
						return false;
					}
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
	public boolean equals(final Object other) {
		if (other instanceof Matrix2D) {
			return equals((Matrix2D) other, Encog.DEFAULT_PRECISION);
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
	public int fromPackedArray(final Double[] array, final int index) {
		int i = index;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				set(r,c,array[i++]);
			}
		}

		return i;
	}

	
	/**
	 * @return A COPY of this matrix as a 2d array.
	 */
	public double[][] getArrayCopy() {
		final double[][] result = new double[getRows()][getCols()];
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				result[i][j] = get(i,j);
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
	public Matrix2D getCol(final int col) {
		if (col > getCols()) {
			final String str = "Can't get column #" + col
					+ " because it does not exist.";
			if (BasicMatrix.LOGGER.isErrorEnabled()) {
				BasicMatrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] newMatrix = new double[getRows()][1];

		for (int row = 0; row < getRows(); row++) {
			newMatrix[row][0] = get(row,col);
		}

		return new Matrix2D(newMatrix);
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
					b[i - i0][j - j0] = this.get(i,j);
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
					b[i - i0][j] = get(i,c[j]);
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
					b[i][j - j0] = get(r[i],j);
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
					b[i][j] = this.get(r[i],c[j]);
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
			if (BasicMatrix.LOGGER.isErrorEnabled()) {
				BasicMatrix.LOGGER.error(str);
			}
			throw new MatrixError(str);
		}

		final double[][] newMatrix = new double[1][getCols()];

		for (int col = 0; col < getCols(); col++) {
			newMatrix[0][col] = this.get(row,col);
		}

		return new Matrix2D(newMatrix);
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
				result += this.get(r,c);
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
				if ( get(row,col) != 0) {
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
				set(row,col,get(row,col)*value);
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
				result[i] += get(i,j) * vector[j];
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
				set(row,col,value);
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
		set(row,col,value);
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
					set(row,col,source[row][col]);
				}
			}
		} else {
			for (int row = 0; row < getRows(); row++) {
				for (int col = 0; col < getCols(); col++) {
					set(row,col, matrix.get(row, col));
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
					set(i,j,x.get(i - i0, j - j0));
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
					set(i,c[j],x.get(i - i0, j));
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
					set(r[i],j,x.get(i, j - j0));
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
					set(r[i],c[j],x.get(i, j));
				}
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new MatrixError("Submatrix indices");
		}
	}

	
}
