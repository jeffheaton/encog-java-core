package org.encog.ml.ea.species;

import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class SingleSpeciation implements Speciation {

	private EvolutionaryAlgorithm owner;
	
	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
	}

	@Override
	public void performSpeciation(List<Genome> genomeList) {
		
	}

	@Override
	public boolean isIterationBased() {
		return false;
	}

}
