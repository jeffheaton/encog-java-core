package org.encog.ml.genetic.sort;

import org.encog.ml.genetic.genome.Genome;

public class MaximizeScoreComp extends AbstractGenomeComparator {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p2.getScore(), p1.getScore());
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}
	
	

}
