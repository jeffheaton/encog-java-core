package org.encog.ml.ea.score;

import java.io.Serializable;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

public class EmptyScoreFunction implements CalculateScore, Serializable {

	@Override
	public double calculateScore(final MLMethod phenotype) {
		return 0;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
