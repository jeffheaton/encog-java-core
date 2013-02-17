package org.encog.neural.neat.training.opp.links;

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

public class MutateResetLinkWeight implements MutateLinkWeight {

	private NEATTraining trainer;
	
	@Override
	public void init(NEATTraining theTrainer) {
		this.trainer = theTrainer;
	}
	
	@Override
	public void mutateWeight(Random rnd, NEATLinkGene linkGene, double weightRange) {				
		linkGene.setWeight(RangeRandomizer.randomize(rnd, -weightRange, weightRange));
	}

	@Override
	public NEATTraining getTrainer() {
		return this.trainer;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append("]");
		return result.toString();
	}
}
