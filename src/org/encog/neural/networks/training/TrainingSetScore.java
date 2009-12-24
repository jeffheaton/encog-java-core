package org.encog.neural.networks.training;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * Calculate a score based on a training set. This class allows simulated
 * annealing or genetic algorithms just as you would any other training set
 * based training method.
 */
public class TrainingSetScore implements CalculateScore {

	/**
	 * The training set.
	 */
	private final NeuralDataSet training;

	/**
	 * Construct a training set score calculation.
	 * 
	 * @param training
	 *            The training data to use.
	 */
	public TrainingSetScore(final NeuralDataSet training) {
		this.training = training;
	}

	/**
	 * Calculate the score for the network.
	 * @param network The network to calculate for.
	 * @return The score.
	 */
	public double calculateScore(final BasicNetwork network) {
		return network.calculateError(this.training);
	}

	/**
	 * A training set based score should always seek to lower the error,
	 * as a result, this method always returns true.
	 * @return Returns true.
	 */
	public boolean shouldMinimize() {
		return true;
	}

}
