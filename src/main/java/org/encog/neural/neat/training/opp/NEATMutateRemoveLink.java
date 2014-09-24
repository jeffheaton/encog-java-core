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
package org.encog.neural.neat.training.opp;

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;

/**
 * Mutate a genome by removing a random link. Do not remove links from genomes
 * that have fewer than 5 links.
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
public class NEATMutateRemoveLink extends NEATMutation {

	/**
	 * Do not remove from genomes that have fewer than this number of links.
	 */
	public static final int MIN_LINK = 5;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {

		final NEATGenome target = obtainGenome(parents, parentIndex, offspring,
				offspringIndex);

		if (target.getLinksChromosome().size() < NEATMutateRemoveLink.MIN_LINK) {
			// don't remove from small genomes
			return;
		}

		// determine the target and remove
		final int index = RangeRandomizer.randomInt(0, target
				.getLinksChromosome().size() - 1);
		final NEATLinkGene targetGene = target.getLinksChromosome().get(index);
		target.getLinksChromosome().remove(index);

		// if this orphaned any nodes, then kill them too!
		if (!isNeuronNeeded(target, targetGene.getFromNeuronID())) {
			removeNeuron(target, targetGene.getFromNeuronID());
		}

		if (!isNeuronNeeded(target, targetGene.getToNeuronID())) {
			removeNeuron(target, targetGene.getToNeuronID());
		}
	}
}
