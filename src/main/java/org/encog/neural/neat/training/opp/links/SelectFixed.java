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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;

/**
 * Select a fixed number of link genes. If the genome does not have enough links
 * to select the specified count, then all genes will be returned.
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
public class SelectFixed implements SelectLinks {

	/**
	 * The number of links to choose.
	 */
	private final int linkCount;

	/**
	 * The trainer.
	 */
	private EvolutionaryAlgorithm trainer;

	/**
	 * Construct a fixed count link selector.
	 * @param theLinkCount The number of links to select.
	 */
	public SelectFixed(final int theLinkCount) {
		this.linkCount = theLinkCount;
	}

	/**
	 * @return the trainer
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
	public List<NEATLinkGene> selectLinks(final Random rnd,
			final NEATGenome genome) {
		final List<NEATLinkGene> result = new ArrayList<NEATLinkGene>();
		final int cnt = Math.min(this.linkCount, genome.getLinksChromosome()
				.size());

		while (result.size() < cnt) {
			final int idx = rnd.nextInt(genome.getLinksChromosome().size());
			final NEATLinkGene link = genome.getLinksChromosome().get(idx);
			if (!result.contains(link)) {
				result.add(link);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":linkCount=");
		result.append(this.linkCount);
		result.append("]");
		return result.toString();
	}
}
