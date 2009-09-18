package org.encog.neural.networks.training.anneal;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * A simulated annealing implementation that trains from a training set.
 */
public class NeuralTrainingSetSimulatedAnnealing extends
		NeuralSimulatedAnnealing {

	/**
	 * Construct a simulated annealing object to train a network with
	 * a training set.
	 * @param network The network to train.
	 * @param training The training set to use.
	 * @param startTemp The starting temperature.
	 * @param stopTemp The ending temperature.
	 * @param cycles The number of cycles per iteration.
	 */
	public NeuralTrainingSetSimulatedAnnealing(
			final BasicNetwork network,
			final NeuralDataSet training, final double startTemp,
			final double stopTemp, final int cycles) {
		super(network, startTemp, stopTemp, cycles);
		setTraining(training);
	}

	/**
	 * Determine the error of the current weights and thresholds.
	 * 
	 * @return The error.
	 */
	@Override
	public double determineError() {
		return getNetwork().calculateError(getTraining());
	}

}
