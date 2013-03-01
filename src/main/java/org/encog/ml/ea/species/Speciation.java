package org.encog.ml.ea.species;

import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public interface Speciation {

	void init(EvolutionaryAlgorithm theOwner);
	void performSpeciation(List<Genome> genomeList );
	boolean isIterationBased();
}
