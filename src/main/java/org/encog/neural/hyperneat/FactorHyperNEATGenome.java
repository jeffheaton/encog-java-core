package org.encog.neural.hyperneat;

import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class FactorHyperNEATGenome implements NEATGenomeFactory {
	@Override
	public NEATGenome factor() {
		return new HyperNEATGenome();
	}

	@Override
	public Genome factor(final Genome other) {
		return new HyperNEATGenome((HyperNEATGenome) other);
	}

	@Override
	public NEATGenome factor(final List<NEATNeuronGene> neurons,
			final List<NEATLinkGene> links, final int inputCount,
			final int outputCount) {
		return new HyperNEATGenome(neurons, links, inputCount, outputCount);
	}

	@Override
	public NEATGenome factor(final Random rnd, final NEATPopulation pop,
			final int inputCount, final int outputCount,
			final double connectionDensity) {
		return new HyperNEATGenome(rnd, pop, inputCount, outputCount,
				connectionDensity);
	}
}
