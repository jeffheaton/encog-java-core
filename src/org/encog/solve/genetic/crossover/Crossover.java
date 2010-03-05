package org.encog.solve.genetic.crossover;

import org.encog.solve.genetic.genome.Chromosome;

public interface Crossover {
	public void mate(
			final Chromosome mother,
			final Chromosome father,
			final Chromosome offspring1,
			final Chromosome offspring2);
}
