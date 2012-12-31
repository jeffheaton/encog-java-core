package org.encog.ml.ea.score.adjust;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.AdjustScore;

public class ComplexityAdjustedScore implements AdjustScore {

	/**
	 * The starting complexity penalty.
	 */
	private double complexityPenalty = .2;

	/**
	 * The starting complexity penalty.
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

	public ComplexityAdjustedScore(int theComplexityPenaltyThreshold,
			int theComplexityPentaltyFullThreshold,
			double theComplexityPenalty, double theComplexityFullPenalty) {
		this.complexityPenaltyThreshold = theComplexityPenaltyThreshold;
		this.complexityPentaltyFullThreshold = theComplexityPentaltyFullThreshold;
		this.complexityFullPenalty = theComplexityPenalty;
		this.complexityFullPenalty = theComplexityFullPenalty;
	}

	@Override
	public double calculateAdjustment(Genome genome) {
		double score = genome.getScore();
		double result = 0;

		if (genome.size() > this.complexityPenaltyThreshold) {
			int over = genome.size() - this.complexityPenaltyThreshold;
			int range = this.complexityPentaltyFullThreshold
					- this.complexityPenaltyThreshold;
			double complexityPenalty = ((this.complexityFullPenalty - this.complexityPenalty) / range)
					* over;
			result = (score * complexityPenalty);
		}

		return result;
	}

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
	
	
}
