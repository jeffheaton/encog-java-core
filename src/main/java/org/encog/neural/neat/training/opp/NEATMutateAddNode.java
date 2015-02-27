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

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

/**
 * Mutate a genome by adding a new node. To do this a random link is chosen. The
 * a neuron is created to split this link. This removes one link and adds two
 * new links. The weights on the new link are created to minimize changes to the
 * values produced by the neuron.
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
public class NEATMutateAddNode extends NEATMutation {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		final NEATGenome target = obtainGenome(parents, parentIndex, offspring,
				offspringIndex);
		int countTrysToFindOldLink = getOwner().getMaxTries();

		final NEATPopulation pop = ((NEATPopulation) target.getPopulation());

		// the link to split
		NEATLinkGene splitLink = null;

		final int sizeBias = ((NEATGenome)parents[0]).getInputCount()
				+ ((NEATGenome)parents[0]).getOutputCount() + 10;

		// if there are not at least
		int upperLimit;
		if (target.getLinksChromosome().size() < sizeBias) {
			upperLimit = target.getNumGenes() - 1
					- (int) Math.sqrt(target.getNumGenes());
		} else {
			upperLimit = target.getNumGenes() - 1;
		}

		while ((countTrysToFindOldLink--) > 0) {
			// choose a link, use the square root to prefer the older links
			final int i = RangeRandomizer.randomInt(0, upperLimit);
			final NEATLinkGene link = target.getLinksChromosome().get(i);

			// get the from neuron
			final long fromNeuron = link.getFromNeuronID();

			if ((link.isEnabled())
					&& (target.getNeuronsChromosome()
							.get(getElementPos(target, fromNeuron))
							.getNeuronType() != NEATNeuronType.Bias)) {
				splitLink = link;
				break;
			}
		}

		if (splitLink == null) {
			return;
		}

		splitLink.setEnabled(false);

		final long from = splitLink.getFromNeuronID();
		final long to = splitLink.getToNeuronID();

		final NEATInnovation innovation = ((NEATPopulation)getOwner().getPopulation()).getInnovations()
				.findInnovationSplit(from, to);

		// add the splitting neuron
		final ActivationFunction af = ((NEATPopulation)getOwner().getPopulation())
				.getActivationFunctions().pick(new Random());

		target.getNeuronsChromosome().add(
				new NEATNeuronGene(NEATNeuronType.Hidden, af, innovation
						.getNeuronID(), innovation.getInnovationID()));

		// add the other two sides of the link
		createLink(target, from, innovation.getNeuronID(),
				splitLink.getWeight());
		createLink(target, innovation.getNeuronID(), to, pop.getWeightRange());
		
		target.sortGenes();
	}

}
