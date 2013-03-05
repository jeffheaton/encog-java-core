package org.encog.ml.ea.sort;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;

public class MinimizeScoreComp extends AbstractGenomeComparator implements
		Serializable {

	@Override
	public int compare(final Genome p1, final Genome p2) {
		return Double.compare(p1.getScore(), p2.getScore());
	}

	@Override
	public boolean isBetterThan(final Genome prg, final Genome betterThan) {
		return isBetterThan(prg.getScore(), betterThan.getScore());
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

}
