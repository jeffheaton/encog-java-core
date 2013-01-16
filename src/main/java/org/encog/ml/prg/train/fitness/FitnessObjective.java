package org.encog.ml.prg.train.fitness;

import java.io.Serializable;

import org.encog.ml.CalculateScore;

public class FitnessObjective implements Serializable {
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
