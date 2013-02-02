package org.encog.neural.neat.training.opp;

import java.util.Random;

import org.encog.EncogError;
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
import org.encog.neural.neat.training.NEATTraining;

public abstract class NEATMutation implements EvolutionaryOperator {
	
	private NEATTraining owner;

	/**
	 * @return the owner
	 */
	public NEATTraining getOwner() {
		return owner;
	}
	
	@Override
	public int offspringProduced() {
		return 1;
	}

	@Override
	public int parentsNeeded() {
		return 1;
	}
	
	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = (NEATTraining) theOwner;	
	}
	
	public NEATGenome obtainGenome(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		if (parents[parentIndex] != offspring[offspringIndex]) {
			throw new EncogError(
					"This mutation only works when the offspring and parents are the same.  That is, it only mutates itself.");
		}

		return (NEATGenome) parents[parentIndex];
	}
	
	/**
	 * Choose a random neuron.
	 * 
	 * @param includeInput
	 *            Should the input and bias neurons be included.
	 * @return The random neuron.
	 */
	public NEATNeuronGene chooseRandomNeuron(NEATGenome target,
			final boolean choosingFrom) {
		int start;

		if (choosingFrom) {
			start = 0;
		} else {
			start = owner.getInputCount() + 1;
		}

		// if this network will not "cycle" then output neurons cannot be source
		// neurons
		if (!choosingFrom) {
			int ac = ((NEATPopulation) target.getPopulation())
					.getActivationCycles();
			if (ac == 1) {
				start += target.getOutputCount();
			}
		}
		
		int end = target
				.getNeuronsChromosome().size() - 1;
		
		// no neurons to pick!
		if( start>end ) {
			return null;
		}

		final int neuronPos = RangeRandomizer.randomInt(start, end);
		final NEATNeuronGene neuronGene = (NEATNeuronGene) target
				.getNeuronsChromosome().get(neuronPos);
		return neuronGene;

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
	public boolean isDuplicateLink(NEATGenome target, final long fromNeuronID,
			final long toNeuronID) {
		for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
			if ((linkGene.getFromNeuronID() == fromNeuronID)
					&& (linkGene.getToNeuronID() == toNeuronID)) {
				return true;
			}
		}

		return false;
	}
	
	public void createLink(NEATGenome target, long neuron1ID, long neuron2ID, double weight) {

		// check to see if this innovation has already been tried
		NEATInnovation innovation = owner.getInnovations().findInnovation(neuron1ID, neuron2ID);
		
		// now create this link
		final NEATLinkGene linkGene = new NEATLinkGene(neuron1ID,
				neuron2ID, true, innovation.getInnovationID(),
				weight);
		target.getLinksChromosome().add(linkGene);
	}
	
	/**
	 * Get the specified neuron's index.
	 * 
	 * @param neuronID
	 *            The neuron id to check for.
	 * @return The index.
	 */
	public int getElementPos(NEATGenome target, final long neuronID) {

		for (int i = 0; i < target.getNeuronsChromosome().size(); i++) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) target
					.getNeuronsChromosome().get(i);
			if (neuronGene.getId() == neuronID) {
				return i;
			}
		}

		return -1;
	}
	
	public boolean isNeuronNeeded(NEATGenome target, long neuronID) {

		// do not remove bias or input neurons or output
		for (NEATNeuronGene gene : target.getNeuronsChromosome()) {
			if (gene.getId() == neuronID) {
				NEATNeuronGene neuron = (NEATNeuronGene) gene;
				if (neuron.getNeuronType() == NEATNeuronType.Input
						|| neuron.getNeuronType() == NEATNeuronType.Bias
						|| neuron.getNeuronType() == NEATNeuronType.Output) {
					return true;
				}
			}
		}

		for (NEATLinkGene gene : target.getLinksChromosome()) {
			NEATLinkGene linkGene = (NEATLinkGene) gene;
			if (linkGene.getFromNeuronID() == neuronID) {
				return true;
			}
			if (linkGene.getToNeuronID() == neuronID) {
				return true;
			}
		}

		return false;
	}

	public void removeNeuron(NEATGenome target, long neuronID) {
		for (NEATNeuronGene gene : target.getNeuronsChromosome()) {
			if (gene.getId() == neuronID) {
				target.getNeuronsChromosome().remove(gene);
				return;
			}
		}
	}
	
}
