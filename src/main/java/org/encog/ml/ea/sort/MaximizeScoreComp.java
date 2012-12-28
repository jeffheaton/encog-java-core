package org.encog.ml.ea.sort;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;

public class MaximizeScoreComp extends AbstractGenomeComparator implements Serializable {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p2.getScore(), p1.getScore());
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
