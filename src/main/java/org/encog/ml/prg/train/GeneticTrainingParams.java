package org.encog.ml.prg.train;

import java.io.Serializable;

public class GeneticTrainingParams implements Serializable {
	
	private double constMin = -10;
	
	private double constMax = 10;
	
	private int populationSize = 1000;
	
	private int stackSize = 50;
	
	private boolean ignoreExceptions;
	
	private int maxIndividualSize = 128;


	/**
	 * @return the maxIndividualSize
	 */
	public int getMaxIndividualSize() {
		return maxIndividualSize;
	}

	/**
	 * @param maxIndividualSize the maxIndividualSize to set
	 */
	public void setMaxIndividualSize(int maxIndividualSize) {
		this.maxIndividualSize = maxIndividualSize;
	}

	public GeneticTrainingParams() {
		
	}
	
	/**
	 * @return the constMin
	 */
	public double getConstMin() {
		return constMin;
	}
	/**
	 * @param constMin the constMin to set
	 */
	public void setConstMin(double constMin) {
		this.constMin = constMin;
	}
	/**
	 * @return the constMax
	 */
	public double getConstMax() {
		return constMax;
	}
	/**
	 * @param constMax the constMax to set
	 */
	public void setConstMax(double constMax) {
		this.constMax = constMax;
	}

	/**
	 * @return the stackSize
	 */
	public int getStackSize() {
		return stackSize;
	}

	/**
	 * @param stackSize the stackSize to set
	 */
	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the ignoreExceptions
	 */
	public boolean isIgnoreExceptions() {
		return ignoreExceptions;
	}

	/**
	 * @param ignoreExceptions the ignoreExceptions to set
	 */
	public void setIgnoreExceptions(boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}

	
	
	
}
