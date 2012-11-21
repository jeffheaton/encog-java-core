package org.encog.ml.prg.train;

import org.encog.ml.MLRegression;
import org.encog.ml.prg.EncogProgram;
import org.encog.neural.networks.training.CalculateScore;

public class ComplexityBasedScore implements CalculateScore {
	
	private CalculateScore innerScore;
	private double complexityPenalty;
	
	public ComplexityBasedScore(CalculateScore theInnerScore, double theComplexityPenalty) {
		this.innerScore = theInnerScore;
		this.complexityPenalty = theComplexityPenalty;
	}

	@Override
	public double calculateScore(MLRegression method) {
		EncogProgram prg = (EncogProgram)method;
		double result = this.innerScore.calculateScore(prg);
		result+= (prg.size() * this.complexityPenalty);
		return result;
	}

	@Override
	public boolean shouldMinimize() {
		return this.innerScore.shouldMinimize();
	}

	
	
}
