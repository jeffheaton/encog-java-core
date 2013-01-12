package org.encog.neural.neat;

import java.util.List;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class FactorNEATGenome implements NEATGenomeFactory {

	@Override
	public NEATGenome factor(NEATGenome other) {
		return new NEATGenome(other);
	}

	@Override
	public NEATGenome factor(long genomeID, List<NEATNeuronGene> neurons,
			List<NEATLinkGene> links, int inputCount, int outputCount) {
		return new NEATGenome(genomeID, neurons, links, inputCount, outputCount);
	}

	@Override
	public NEATGenome factor(NEATPopulation pop, long id, int inputCount,
			int outputCount) {
		return new NEATGenome(pop,id,inputCount,outputCount);
	}

	@Override
	public NEATGenome factor() {
		return new NEATGenome();
	}

}
