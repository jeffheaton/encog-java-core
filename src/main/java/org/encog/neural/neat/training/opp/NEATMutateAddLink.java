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
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATNeuronGene;

/**
 * Mutates a NEAT genome by adding a link. To add a link, two random neurons are
 * chosen and a new random link is created between them. There are severals
 * rules. Bias and input neurons can never be the target of a link. We also do
 * not create double links. Two neurons cannot have more than one link going in
 * the same direction. A neuron can have a single link to itself.
 * 
 * If the network is only one cycle, then output neurons cannot be a target.
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
public class NEATMutateAddLink extends NEATMutation {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		int countTrysToAddLink = this.getOwner().getMaxTries();

		NEATGenome target = this.obtainGenome(parents, parentIndex, offspring,
				offspringIndex);

		// the link will be between these two neurons
		long neuron1ID = -1;
		long neuron2ID = -1;

		// try to add a link
		while ((countTrysToAddLink--) > 0) {
			final NEATNeuronGene neuron1 = chooseRandomNeuron(target, true);
			final NEATNeuronGene neuron2 = chooseRandomNeuron(target, false);

			if (neuron1 == null || neuron2 == null) {
				return;
			}

			// do not duplicate
			// do not go to a bias neuron
			// do not go from an output neuron
			// do not go to an input neuron
			if (!isDuplicateLink(target, neuron1.getId(), neuron2.getId())
					&& (neuron2.getNeuronType() != NEATNeuronType.Bias)
					&& (neuron2.getNeuronType() != NEATNeuronType.Input)) {

				if ( ((NEATPopulation)getOwner().getPopulation()).getActivationCycles() != 1
						|| neuron1.getNeuronType() != NEATNeuronType.Output) {
					neuron1ID = neuron1.getId();
					neuron2ID = neuron2.getId();
					break;
				}
			}
		}

		// did we fail to find a link
		if ((neuron1ID < 0) || (neuron2ID < 0)) {
			return;
		}

		double r = ((NEATPopulation) target.getPopulation()).getWeightRange();
		createLink(target, neuron1ID, neuron2ID,
				RangeRandomizer.randomize(rnd, -r, r));
		target.sortGenes();
	}

}
