package org.encog.ml.ea.sort;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;

public class MinimizeAdjustedScoreComp extends AbstractGenomeComparator implements Serializable {

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
