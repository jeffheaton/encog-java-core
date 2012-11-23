package org.encog.ml.prg.train;

public class GeneticTrainingParams {
	
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

	
	
	
}
