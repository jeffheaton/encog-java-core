package org.encog.neural.neat;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class FactorNEATGenome implements NEATGenomeFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public NEATGenome factor() {
		return new NEATGenome();
	}

	@Override
	public NEATGenome factor(
			final List<NEATNeuronGene> neurons, final List<NEATLinkGene> links,
			final int inputCount, final int outputCount) {
		return new NEATGenome(neurons, links, inputCount, outputCount);
	}

	@Override
	public NEATGenome factor(final NEATGenome other) {
		return new NEATGenome(other);
	}

	@Override
	public NEATGenome factor(final Random rnd, final NEATPopulation pop,
			final int inputCount, final int outputCount,
			final double connectionDensity) {
		return new NEATGenome(rnd, pop, inputCount, outputCount,
				connectionDensity);
	}

}
