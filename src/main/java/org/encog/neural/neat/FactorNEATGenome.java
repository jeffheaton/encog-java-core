package org.encog.neural.neat;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class FactorNEATGenome implements NEATGenomeFactory, Serializable {

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
	public NEATGenome factor(Random rnd, NEATPopulation pop, long id, int inputCount,
			int outputCount, double connectionDensity) {
		return new NEATGenome(rnd, pop,id,inputCount,outputCount, connectionDensity);
	}

	@Override
	public NEATGenome factor() {
		return new NEATGenome();
	}

}
