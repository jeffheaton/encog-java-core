package org.encog.ml.ea.sort;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;

public class MaximizeAdjustedScoreComp extends AbstractGenomeComparator
		implements Serializable {

	@Override
	public int compare(final Genome p1, final Genome p2) {
		return Double.compare(p2.getAdjustedScore(), p1.getAdjustedScore());
	}

	@Override
	public boolean isBetterThan(final Genome prg, final Genome betterThan) {
		return isBetterThan(prg.getAdjustedScore(),
				betterThan.getAdjustedScore());
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}
}
