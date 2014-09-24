/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.hyperneat;

import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

/**
 * Create a Genome for use with HyperNEAT.
 */
public class FactorHyperNEATGenome implements NEATGenomeFactory {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenome factor() {
		return new HyperNEATGenome();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor(final Genome other) {
		return new HyperNEATGenome((HyperNEATGenome) other);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenome factor(final List<NEATNeuronGene> neurons,
			final List<NEATLinkGene> links, final int inputCount,
			final int outputCount) {
		return new HyperNEATGenome(neurons, links, inputCount, outputCount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenome factor(final Random rnd, final NEATPopulation pop,
			final int inputCount, final int outputCount,
			final double connectionDensity) {
		return new HyperNEATGenome(rnd, pop, inputCount, outputCount,
				connectionDensity);
	}
}
