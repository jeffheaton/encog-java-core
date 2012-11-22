package org.encog.ml.prg.train.fitness;

import org.encog.neural.networks.training.CalculateScore;

public class FitnessObjective {
	private final double weight;
	private final CalculateScore score;
	
	public FitnessObjective(double weight, CalculateScore score) {
		super();
		this.weight = weight;
		this.score = score;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @return the score
	 */
	public CalculateScore getScore() {
		return score;
	}
	
	
	
}
