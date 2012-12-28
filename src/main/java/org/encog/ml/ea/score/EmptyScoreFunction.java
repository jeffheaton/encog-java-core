package org.encog.ml.ea.score;

import org.encog.ml.ea.genome.Genome;

public class EmptyScoreFunction implements CalculateGenomeScore {

	@Override
	public double calculateScore(Genome genome) {
		return 0;
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
