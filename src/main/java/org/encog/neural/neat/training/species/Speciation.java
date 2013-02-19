package org.encog.neural.neat.training.species;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public interface Speciation {

	void init(EvolutionaryAlgorithm theOwner);
	void performSpeciation();
}
