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

public class NEATMutateAddNode extends NEATMutation {
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		
		NEATGenome target = this.obtainGenome(rnd, parents, parentIndex, offspring, offspringIndex);
		int countTrysToFindOldLink = getOwner().getMaxTries();
		
		NEATPopulation pop = ((NEATPopulation)target.getPopulation());

		// the link to split
		NEATLinkGene splitLink = null;

		final int sizeBias = getOwner().getNEATPopulation().getInputCount() + 
				getOwner().getNEATPopulation().getOutputCount()
				+ 10;

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
			final NEATLinkGene link = (NEATLinkGene) target
					.getLinksChromosome().get(i);

			// get the from neuron
			final long fromNeuron = link.getFromNeuronID();

			if ((link.isEnabled())
					&& (((NEATNeuronGene) target.getNeuronsChromosome().get(
							getElementPos(target, fromNeuron))).getNeuronType() != NEATNeuronType.Bias)) {
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

		NEATInnovation innovation = getOwner().getInnovations().findInnovationSplit(from, to);
		
		// add the splitting neuron
		ActivationFunction af = this.getOwner().getNEATPopulation().getActivationFunctions().pick(new Random());
		
		target.getNeuronsChromosome().add(
				new NEATNeuronGene(NEATNeuronType.Hidden, af, innovation
						.getNeuronID(), innovation.getInnovationID()));

		// add the other two sides of the link
		createLink(target, from, innovation.getNeuronID(), splitLink.getWeight());
		createLink(target, innovation.getNeuronID(), to, pop.getWeightRange());
	}
	
	
}
