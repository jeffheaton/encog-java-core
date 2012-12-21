package org.encog.ml.genetic.sort;

import java.util.Comparator;

import org.encog.ml.genetic.genome.Genome;

public class MaximizeEffectiveScoreComp implements Comparator<Genome> {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p2.getAdjustedScore(), p1.getAdjustedScore());
	}

}
