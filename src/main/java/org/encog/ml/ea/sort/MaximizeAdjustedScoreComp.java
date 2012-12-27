package org.encog.ml.ea.sort;

import org.encog.ml.ea.genome.Genome;

public class MaximizeAdjustedScoreComp extends AbstractGenomeComparator {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p2.getAdjustedScore(), p1.getAdjustedScore());
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}

	@Override
	public boolean isBetterThan(Genome prg, Genome betterThan) {
		return isBetterThan(prg.getAdjustedScore(),betterThan.getAdjustedScore());
	}
}
