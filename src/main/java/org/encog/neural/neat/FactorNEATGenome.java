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
package org.encog.neural.neat;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

/**
 * This factory is used to create NEATGenomes.
 * 
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/
 * Encog's NEAT implementation was drawn from the following three Journal
 * Articles. For more complete BibTeX sources, see NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public class FactorNEATGenome implements NEATGenomeFactory, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenome factor() {
		return new NEATGenome();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor(final Genome other) {
		return new NEATGenome((NEATGenome) other);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenome factor(final List<NEATNeuronGene> neurons,
			final List<NEATLinkGene> links, final int inputCount,
			final int outputCount) {
		return new NEATGenome(neurons, links, inputCount, outputCount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenome factor(final Random rnd, final NEATPopulation pop,
			final int inputCount, final int outputCount,
			final double connectionDensity) {
		return new NEATGenome(rnd, pop, inputCount, outputCount,
				connectionDensity);
	}
}
