package org.encog.ml.ea.score.adjust;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.score.AdjustScore;

public class ComplexityAdjustedScore implements AdjustScore {

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
}
