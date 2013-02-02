package org.encog.neural.neat.training.opp;

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;

public class NEATMutateWeights extends NEATMutation {
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		NEATGenome target = this.obtainGenome(rnd, parents, parentIndex, offspring, offspringIndex);
		
		double weightRange = ((NEATPopulation)target.getPopulation()).getWeightRange();
		
		for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
			if (Math.random() < getOwner().getMutateRate()) {
				if (Math.random() < getOwner().getProbNewMutate() ) {
					linkGene.setWeight(RangeRandomizer.randomize(-weightRange, weightRange));
				} else {
					double w = linkGene.getWeight()
							+ RangeRandomizer.randomize(-1, 1) * getOwner().getMaxPertubation();
					w = NEATPopulation.clampWeight(w,weightRange);
					linkGene.setWeight(w);
				}
			}
		}
	}
}
