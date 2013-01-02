package org.encog.ml.ea.score;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;

public class EmptyScoreFunction implements CalculateGenomeScore, Serializable {

	@Override
	public double calculateScore(Genome genome) {
		return 0;
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
	}

}
