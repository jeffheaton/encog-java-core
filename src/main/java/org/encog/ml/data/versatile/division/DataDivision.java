package org.encog.ml.data.versatile.division;

import org.encog.ml.data.versatile.MatrixMLDataSet;

/**
 * A division of data inside of a versatile data set.
 */
public class DataDivision {
	
	/**
	 * The count of items in this partition.
	 */
	private int count;
	
	/**
	 * The percent of items in this partition.
	 */
	private final double percent;
	
	/**
	 * The dataset that we are dividing.
	 */
	private MatrixMLDataSet dataset;
	
	/**
	 * The mask of items we are to use.
	 */
	private int[] mask;
	
	/**
	 * Construct a division.
	 * @param thePercent The desired percentage in this division.
	 */
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

	/**
	 * Allocat space to hold the mask.
	 * @param theSize The mask size.
	 */
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
