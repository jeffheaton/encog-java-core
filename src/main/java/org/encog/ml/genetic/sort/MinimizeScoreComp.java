package org.encog.ml.genetic.sort;

import org.encog.ml.genetic.genome.Genome;

public class MinimizeScoreComp extends AbstractGenomeComparator {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p1.getScore(), p2.getScore());
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}
	
	@Override
	public boolean isBetterThan(Genome prg, Genome betterThan) {
		return isBetterThan(prg.getScore(),betterThan.getScore());
	}

}
