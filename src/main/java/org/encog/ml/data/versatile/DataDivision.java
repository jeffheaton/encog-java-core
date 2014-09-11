package org.encog.ml.data.versatile;

import org.encog.ml.data.MLDataSet;

public class DataDivision {
	private int count;
	private final double percent;
	private MatrixMLDataSet dataset;
	private int[] mask;
	
	public DataDivision(double thePercent) {
		this.percent = thePercent;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the dataset
	 */
	public MatrixMLDataSet getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(MatrixMLDataSet dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return the percent
	 */
	public double getPercent() {
		return percent;
	}

	public void allocateMask(int theSize) {
		this.mask = new int[theSize];
	}

	/**
	 * @return the mask
	 */
	public int[] getMask() {
		return mask;
	}

}
