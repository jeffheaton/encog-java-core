package org.encog.ml.genetic.sort;

import java.util.Comparator;

import org.encog.ml.genetic.genome.Genome;

public class MinimizeEffectiveScoreComp implements Comparator<Genome> {

	@Override
	public int compare(Genome p1, Genome p2) {
		return Double.compare(p1.getAdjustedScore(), p2.getAdjustedScore());
	}

}
