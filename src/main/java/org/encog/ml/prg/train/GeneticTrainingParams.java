package org.encog.ml.prg.train;

public class GeneticTrainingParams {
	
	private double constMin = -10;
	
	private double constMax = 10;
	
	private int populationSize = 1000;
	
	private int stackSize = 50;
	
	/**
	 * The starting complexity pentalty.
	 */
	private double complexityPenalty = .2;
	
	/**
	 * The starting complexity pentalty.
	 */
	private double complexityFullPenalty = 2.0;
	
	/**
	 * The complexity level at which a penalty begins to be applied.
	 */
	private int complexityPenaltyThreshold = 10;
	
	/**
	 * The complexity level at which a full (100%) penalty is applied.
	 */
	private int complexityPentaltyFullThreshold = 50;
	
	private int maxGeneratedDepth = 5;
	
	private int maxIndividualSize = 128;

	/**
	 * @return the complexityPenalty
	 */
	public double getComplexityPenalty() {
		return complexityPenalty;
	}

	/**
	 * @param complexityPenalty the complexityPenalty to set
	 */
	public void setComplexityPenalty(double complexityPenalty) {
		this.complexityPenalty = complexityPenalty;
	}

	/**
	 * @return the complexityPenaltyThreshold
	 */
	public int getComplexityPenaltyThreshold() {
		return complexityPenaltyThreshold;
	}

	/**
	 * @param complexityPenaltyThreshold the complexityPenaltyThreshold to set
	 */
	public void setComplexityPenaltyThreshold(int complexityPenaltyThreshold) {
		this.complexityPenaltyThreshold = complexityPenaltyThreshold;
	}

	/**
	 * @return the complexityPentaltyFullThreshold
	 */
	public int getComplexityPentaltyFullThreshold() {
		return complexityPentaltyFullThreshold;
	}

	/**
	 * @param complexityPentaltyFullThreshold the complexityPentaltyFullThreshold to set
	 */
	public void setComplexityPentaltyFullThreshold(
			int complexityPentaltyFullThreshold) {
		this.complexityPentaltyFullThreshold = complexityPentaltyFullThreshold;
	}

	/**
	 * @return the complexityFullPenalty
	 */
	public double getComplexityFullPenalty() {
		return complexityFullPenalty;
	}

	/**
	 * @param complexityFullPenalty the complexityFullPenalty to set
	 */
	public void setComplexityFullPenalty(double complexityFullPenalty) {
		this.complexityFullPenalty = complexityFullPenalty;
	}
	
	/**
	 * @return the maxGeneratedDepth
	 */
	public int getMaxGeneratedDepth() {
		return maxGeneratedDepth;
	}

	/**
	 * @param maxGeneratedDepth the maxGeneratedDepth to set
	 */
	public void setMaxGeneratedDepth(int maxGeneratedDepth) {
		this.maxGeneratedDepth = maxGeneratedDepth;
	}

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

	
	
}
