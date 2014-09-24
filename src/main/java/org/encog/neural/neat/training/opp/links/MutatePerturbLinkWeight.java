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
package org.encog.neural.neat.training.opp.links;

import java.util.Random;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATLinkGene;

/**
 * Mutate weight links by perturbing their weights. This will be done by adding
 * a Gaussian random number with the specified sigma. The sigma specifies the
 * standard deviation of the random number. Because the random numbers are
 * clustered at zero, this can be either an increase or decrease.
 * 
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/ Encog's NEAT implementation was drawn from
 * the following three Journal Articles. For more complete BibTeX sources, see
 * NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public class MutatePerturbLinkWeight implements MutateLinkWeight {

	/**
	 * The trainer being used.
	 */
	private EvolutionaryAlgorithm trainer;

	/**
	 * The sigma (standard deviation) of the Gaussian random numbers.
	 */
	private final double sigma;

	/**
	 * Construct the perturbing mutator.
	 * 
	 * @param theSigma
	 *            The sigma (standard deviation) for all random numbers.
	 */
	public MutatePerturbLinkWeight(final double theSigma) {
		this.sigma = theSigma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EvolutionaryAlgorithm getTrainer() {
		return this.trainer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theTrainer) {
		this.trainer = theTrainer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mutateWeight(final Random rnd, final NEATLinkGene linkGene,
			final double weightRange) {
		final double delta = rnd.nextGaussian() * this.sigma;
		double w = linkGene.getWeight() + delta;
		w = NEATPopulation.clampWeight(w, weightRange);
		linkGene.setWeight(w);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":sigma=");
		result.append(this.sigma);
		result.append("]");
		return result.toString();
	}
}
