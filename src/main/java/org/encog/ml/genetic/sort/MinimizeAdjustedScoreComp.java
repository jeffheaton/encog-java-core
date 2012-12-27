package org.encog.ml.genetic.sort;

import org.encog.ml.genetic.genome.Genome;

public class MinimizeAdjustedScoreComp extends AbstractGenomeComparator {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p1.getAdjustedScore(), p2.getAdjustedScore());
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

	@Override
	public boolean isBetterThan(Genome prg, Genome betterThan) {
		return isBetterThan(prg.getAdjustedScore(),betterThan.getAdjustedScore());
	}
}
