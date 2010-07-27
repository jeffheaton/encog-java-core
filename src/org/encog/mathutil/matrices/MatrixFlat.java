package org.encog.mathutil.matrices;

public class MatrixFlat extends BasicMatrix {

	private double[] array;
	private int rows;
	private int cols;
	private int offset;
	
	public MatrixFlat(int rows, int cols, double[] array, int offset)
	{
		this.rows = rows;
		this.cols = cols;
		this.array = array;
		this.offset = offset;
	}
	
	@Override
	public Matrix clone() {
		return new Matrix2D(this);
	}

	@Override
	public double get(int row, int col) {
		int location = this.offset+(row*this.cols)+col;
		return array[location];
	}

	@Override
	public int getCols() {	
		return cols;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public void set(int row, int col, double value) {
		int location = this.offset+(row*this.cols)+col;
		this.array[location] = value;		
	}

}
