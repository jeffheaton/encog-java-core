package org.encog.ml.ea.sort;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;

public class MaximizeScoreComp extends AbstractGenomeComparator implements
		Serializable {

	@Override
	public int compare(final Genome p1, final Genome p2) {
		return Double.compare(p2.getScore(), p1.getScore());
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
