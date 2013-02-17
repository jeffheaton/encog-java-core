package org.encog.neural.neat.training.opp.links;

import java.util.Random;

import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

public class MutatePerturbLinkWeight implements MutateLinkWeight {
	
	private NEATTraining trainer;
	private double sigma;
	
	public MutatePerturbLinkWeight(double theSigma) {
		this.sigma = theSigma;
	}
	
	@Override
	public void init(NEATTraining theTrainer) {
		this.trainer = theTrainer;
	}
	
	
	
	/**
	 * @return the trainer
	 */
	@Override
	public NEATTraining getTrainer() {
		return trainer;
	}

	@Override
	public void mutateWeight(Random rnd, NEATLinkGene linkGene, double weightRange) {				
			double delta = rnd.nextGaussian() * this.sigma;
			double w = linkGene.getWeight() + delta;
			w = NEATPopulation.clampWeight(w, weightRange);
			linkGene.setWeight(w);
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":sigma=");
		result.append(this.sigma);
		result.append("]");
		return result.toString();
	}
}
