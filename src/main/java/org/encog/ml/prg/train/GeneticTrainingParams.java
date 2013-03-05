package org.encog.ml.prg.train;

import java.io.Serializable;

public class GeneticTrainingParams implements Serializable {

	private double constMin = -10;

	private double constMax = 10;

	private int stackSize = 50;

	private boolean ignoreExceptions;

	private int maxIndividualSize = 128;

	public GeneticTrainingParams() {

	}

	/**
	 * @return the constMax
	 */
	public double getConstMax() {
		return this.constMax;
	}

	/**
	 * @return the constMin
	 */
	public double getConstMin() {
		return this.constMin;
	}

	/**
	 * @return the maxIndividualSize
	 */
	public int getMaxIndividualSize() {
		return this.maxIndividualSize;
	}

	/**
	 * @return the stackSize
	 */
	public int getStackSize() {
		return this.stackSize;
	}

	/**
	 * @return the ignoreExceptions
	 */
	public boolean isIgnoreExceptions() {
		return this.ignoreExceptions;
	}

	/**
	 * @param constMax
	 *            the constMax to set
	 */
	public void setConstMax(final double constMax) {
		this.constMax = constMax;
	}

	/**
	 * @param constMin
	 *            the constMin to set
	 */
	public void setConstMin(final double constMin) {
		this.constMin = constMin;
	}

	/**
	 * @param ignoreExceptions
	 *            the ignoreExceptions to set
	 */
	public void setIgnoreExceptions(final boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}

	/**
	 * @param maxIndividualSize
	 *            the maxIndividualSize to set
	 */
	public void setMaxIndividualSize(final int maxIndividualSize) {
		this.maxIndividualSize = maxIndividualSize;
	}

	/**
	 * @param stackSize
	 *            the stackSize to set
	 */
	public void setStackSize(final int stackSize) {
		this.stackSize = stackSize;
	}

}
