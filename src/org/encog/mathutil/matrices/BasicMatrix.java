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
	
	

	
}
