package org.encog.neural.neat.training.opp;

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;

public class NEATMutateRemoveLink extends NEATMutation{
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		
		NEATGenome target = this.obtainGenome(rnd, parents, parentIndex, offspring, offspringIndex);
		
		if (target.getLinksChromosome().size() < 5) {
			// don't remove from small genomes
			return;
		}

		// determine the target and remove
		int index = RangeRandomizer.randomInt(0, target.getLinksChromosome()
				.size() - 1);
		NEATLinkGene targetGene = (NEATLinkGene) target.getLinksChromosome()
				.get(index);
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
