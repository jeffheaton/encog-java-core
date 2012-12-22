package org.encog.ml.genetic.sort;

import org.encog.ml.genetic.genome.Genome;

public class MinimizeAdjustedScoreScoreComp extends AbstractGenomeComparator {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p1.getAdjustedScore(), p2.getAdjustedScore());
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
