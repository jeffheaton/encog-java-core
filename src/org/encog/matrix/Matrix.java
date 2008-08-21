/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
  * See the copyright.txt in the distribution for a full listing of 
  * individual contributors.
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
  */
package org.encog.matrix;

import java.io.Serializable;

import org.encog.neural.persist.EncogPersistedObject;


/**
 * Matrix: This class implements a mathematical matrix.  Matrix
 * math is very important to neural network processing.  Many
 * of the classes developed in this book will make use of the
 * matrix classes in this package.
 */
public class Matrix implements Cloneable, Serializable,EncogPersistedObject {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -7977897210426471675L;

	public static Matrix createColumnMatrix(final double input[]) {
		final double d[][] = new double[input.length][1];
		for (int row = 0; row < d.length; row++) {
			d[row][0] = input[row];
		}
		return new Matrix(d);
	}

	public static Matrix createRowMatrix(final double input[]) {
		final double d[][] = new double[1][input.length];
		System.arraycopy(input, 0, d[0], 0, input.length);
		return new Matrix(d);
	}

	double matrix[][];

	public Matrix(final boolean sourceMatrix[][]) {
		this.matrix = new double[sourceMatrix.length][sourceMatrix[0].length];
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				if (sourceMatrix[r][c]) {
					this.set(r, c, 1);
				} else {
					this.set(r, c, -1);
				}
			}
		}
	}

	public Matrix(final double sourceMatrix[][]) {
		this.matrix = new double[sourceMatrix.length][sourceMatrix[0].length];
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				this.set(r, c, sourceMatrix[r][c]);
			}
		}
	}

	public Matrix(final int rows, final int cols) {
		this.matrix = new double[rows][cols];
	}

	public void add(final int row, final int col, final double value) {
		validate(row, col);
		final double newValue = get(row, col) + value;
		set(row, col, newValue);
	}

	public void clear() {
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				set(r, c, 0);
			}
		}
	}

	@Override
	public Matrix clone() {
		return new Matrix(this.matrix);
	}

	public boolean equals(final Matrix matrix) {
		return equals(matrix, 10);
	}

	public boolean equals(final Matrix matrix, int precision) {

		if (precision < 0) {
			throw new MatrixError("Precision can't be a negative number.");
		}

		final double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			throw new MatrixError("Precision of " + precision
					+ " decimal places is not supported.");
		}

		precision = (int) Math.pow(10, precision);

		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				if ((long) (get(r, c) * precision) != (long) (matrix.get(r, c) * precision)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 
	 * @param array
	 * @param index
	 * @return The new index after this matrix has been read.
	 */
	public int fromPackedArray(final Double[] array, int index) {

		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				this.matrix[r][c] = array[index++];
			}
		}

		return index;
	}

	public double get(final int row, final int col) {
		validate(row, col);
		return this.matrix[row][col];
	}

	public Matrix getCol(final int col) {
		if (col > getCols()) {
			throw new MatrixError("Can't get column #" + col
					+ " because it does not exist.");
		}

		final double newMatrix[][] = new double[getRows()][1];

		for (int row = 0; row < getRows(); row++) {
			newMatrix[row][0] = this.matrix[row][col];
		}

		return new Matrix(newMatrix);
	}

	public int getCols() {
		return this.matrix[0].length;
	}

	public Matrix getRow(final int row) {
		if (row > getRows()) {
			throw new MatrixError("Can't get row #" + row
					+ " because it does not exist.");
		}

		final double newMatrix[][] = new double[1][getCols()];

		for (int col = 0; col < getCols(); col++) {
			newMatrix[0][col] = this.matrix[row][col];
		}

		return new Matrix(newMatrix);
	}

	public int getRows() {
		return this.matrix.length;
	}

	public boolean isVector() {
		if (getRows() == 1) {
			return true;
		} else {
			return getCols() == 1;
		}
	}

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

	public void ramdomize(final double min, final double max) {
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				this.matrix[r][c] = (Math.random() * (max - min)) + min;
			}
		}
	}

	public void set(final int row, final int col, final double value) {
		validate(row, col);
		if (Double.isInfinite(value) || Double.isNaN(value)) {
			throw new MatrixError("Trying to assign invalud number to matrix: "
					+ value);
		}
		this.matrix[row][col] = value;
	}

	public int size() {
		return this.matrix[0].length * this.matrix.length;
	}

	public double sum() {
		double result = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result += this.matrix[r][c];
			}
		}
		return result;
	}

	public Double[] toPackedArray() {
		final Double result[] = new Double[getRows() * getCols()];

		int index = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getCols(); c++) {
				result[index++] = this.matrix[r][c];
			}
		}

		return result;
	}

	private void validate(final int row, final int col) {
		if ((row >= getRows()) || (row < 0)) {
			throw new MatrixError("The row:" + row + " is out of range:"
					+ getRows());
		}

		if ((col >= getCols()) || (col < 0)) {
			throw new MatrixError("The col:" + col + " is out of range:"
					+ getCols());
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
