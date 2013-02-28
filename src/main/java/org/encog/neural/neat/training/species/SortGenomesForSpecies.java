package org.encog.neural.neat.training.species;

import java.util.Comparator;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public class SortGenomesForSpecies implements Comparator<Genome> {

	private final NEATTraining train;

	public SortGenomesForSpecies(final NEATTraining theTrain) {
		this.train = theTrain;
	}

	@Override
	public int compare(Genome g1, Genome g2) {
		final int result = this.train.getSelectionComparator().compare(g1, g2);

		if (result != 0) {
			return result;
		}

		return g2.getBirthGeneration() - g1.getBirthGeneration();
	}

}
