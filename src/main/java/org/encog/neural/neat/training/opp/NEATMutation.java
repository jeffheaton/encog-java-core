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

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

/**
 * This class represents a NEAT mutation. NEAT supports several different types
 * of mutations. This class provides common utility needed by any sort of a NEAT
 * mutation. This class is abstract and cannot be used by itself.
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
public abstract class NEATMutation implements EvolutionaryOperator {

	/**
	 * The trainer that owns this class.
	 */
	private EvolutionaryAlgorithm owner;

	/**
	 * Choose a random neuron.
	 * 
	 * @param target
	 *            The target genome. Should the input and bias neurons be
	 *            included.
	 * @param choosingFrom
	 *            True if we are chosing from all neurons, false if we exclude
	 *            the input and bias.
	 * @return The random neuron.
	 */
	public NEATNeuronGene chooseRandomNeuron(final NEATGenome target,
			final boolean choosingFrom) {
		int start;

		if (choosingFrom) {
			start = 0;
		} else {
			start = target.getInputCount() + 1;
		}

		// if this network will not "cycle" then output neurons cannot be source
		// neurons
		if (!choosingFrom) {
			final int ac = ((NEATPopulation) target.getPopulation())
					.getActivationCycles();
			if (ac == 1) {
				start += target.getOutputCount();
			}
		}

		final int end = target.getNeuronsChromosome().size() - 1;

		// no neurons to pick!
		if (start > end) {
			return null;
		}

		final int neuronPos = RangeRandomizer.randomInt(start, end);
		final NEATNeuronGene neuronGene = target.getNeuronsChromosome().get(
				neuronPos);
		return neuronGene;

	}

	/**
	 * Create a link between two neuron id's. Create or find any necessary
	 * innovation records.
	 * 
	 * @param target
	 *            The target genome.
	 * @param neuron1ID
	 *            The id of the source neuron.
	 * @param neuron2ID
	 *            The id of the target neuron.
	 * @param weight
	 *            The weight of this new link.
	 */
	public void createLink(final NEATGenome target, final long neuron1ID,
			final long neuron2ID, final double weight) {

		// first, does this link exist? (and if so, hopefully disabled,
		// otherwise we have a problem)
		for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
			if ((linkGene.getFromNeuronID() == neuron1ID)
					&& (linkGene.getToNeuronID() == neuron2ID)) {
				// bring the link back, at the new weight
				linkGene.setEnabled(true);
				linkGene.setWeight(weight);
				return;
			}
		}

		// check to see if this innovation has already been tried
		final NEATInnovation innovation = ((NEATPopulation) target
				.getPopulation()).getInnovations().findInnovation(neuron1ID,
				neuron2ID);

		// now create this link
		final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID, neuron2ID,
				true, innovation.getInnovationID(), weight);
		target.getLinksChromosome().add(linkGene);
	}

	/**
	 * Get the specified neuron's index.
	 * 
	 * @param neuronID
	 *            The neuron id to check for.
	 * @return The index.
	 */
	public int getElementPos(final NEATGenome target, final long neuronID) {

		for (int i = 0; i < target.getNeuronsChromosome().size(); i++) {
			final NEATNeuronGene neuronGene = target.getNeuronsChromosome()
					.get(i);
			if (neuronGene.getId() == neuronID) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @return the owner
	 */
	public EvolutionaryAlgorithm getOwner() {
		return this.owner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
	}

	/**
	 * Determine if this is a duplicate link.
	 * 
	 * @param fromNeuronID
	 *            The from neuron id.
	 * @param toNeuronID
	 *            The to neuron id.
	 * @return True if this is a duplicate link.
	 */
	public boolean isDuplicateLink(final NEATGenome target,
			final long fromNeuronID, final long toNeuronID) {
		for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
			if ((linkGene.isEnabled())
					&& (linkGene.getFromNeuronID() == fromNeuronID)
					&& (linkGene.getToNeuronID() == toNeuronID)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if a neuron is still needed. If all links to/from a neuron
	 * have been removed, then the neuron is no longer needed.
	 * 
	 * @param target
	 *            The target genome.
	 * @param neuronID
	 *            The neuron id to check for.
	 * @return Returns true, if the neuron is still needed.
	 */
	public boolean isNeuronNeeded(final NEATGenome target, final long neuronID) {

		// do not remove bias or input neurons or output
		for (final NEATNeuronGene gene : target.getNeuronsChromosome()) {
			if (gene.getId() == neuronID) {
				final NEATNeuronGene neuron = gene;
				if ((neuron.getNeuronType() == NEATNeuronType.Input)
						|| (neuron.getNeuronType() == NEATNeuronType.Bias)
						|| (neuron.getNeuronType() == NEATNeuronType.Output)) {
					return true;
				}
			}
		}

		// Now check to see if the neuron is used in any links
		for (final NEATLinkGene gene : target.getLinksChromosome()) {
			final NEATLinkGene linkGene = gene;
			if (linkGene.getFromNeuronID() == neuronID) {
				return true;
			}
			if (linkGene.getToNeuronID() == neuronID) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Obtain the NEATGenome that we will mutate. NEAT mutates the genomes in
	 * place. So the parent and child genome must be the same literal object.
	 * Throw an exception, if this is not the case.
	 * 
	 * @param parents
	 *            The parents.
	 * @param parentIndex
	 *            The parent index.
	 * @param offspring
	 *            The offspring.
	 * @param offspringIndex
	 *            The offspring index.
	 * @return The genome that we will mutate.
	 */
	public NEATGenome obtainGenome(final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		offspring[offspringIndex] = this.getOwner().getPopulation()
				.getGenomeFactory().factor(parents[0]);
		return (NEATGenome) offspring[offspringIndex];
	}

	/**
	 * @return Returns 1, as NEAT mutations only produce one child.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * @return Returns 1, as mutations typically are asexual and only require a
	 *         single parent.
	 */
	@Override
	public int parentsNeeded() {
		return 1;
	}

	/**
	 * Remove the specified neuron.
	 * 
	 * @param target
	 *            The target genome.
	 * @param neuronID
	 *            The neuron to remove.
	 */
	public void removeNeuron(final NEATGenome target, final long neuronID) {
		for (final NEATNeuronGene gene : target.getNeuronsChromosome()) {
			if (gene.getId() == neuronID) {
				target.getNeuronsChromosome().remove(gene);
				return;
			}
		}
	}

}
