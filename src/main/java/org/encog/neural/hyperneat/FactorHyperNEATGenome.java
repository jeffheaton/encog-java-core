package org.encog.neural.hyperneat;

import java.util.List;

import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class FactorHyperNEATGenome implements NEATGenomeFactory {
	@Override
	public NEATGenome factor(NEATGenome other) {
		return new HyperNEATGenome((HyperNEATGenome)other);
	}

	@Override
	public NEATGenome factor(long genomeID, List<NEATNeuronGene> neurons,
			List<NEATLinkGene> links, int inputCount, int outputCount) {
		return new HyperNEATGenome(genomeID, neurons, links, inputCount, outputCount);
	}

	@Override
	public NEATGenome factor(NEATPopulation pop, long id, int inputCount,
			int outputCount) {
		return new HyperNEATGenome(pop,id,inputCount,outputCount);
	}

	@Override
	public NEATGenome factor() {
		return new HyperNEATGenome();
	}
}
