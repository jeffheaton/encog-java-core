package org.encog.neural.networks.training.anneal;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * A simulated annealing implementation that trains from a training set.
 */
public class NeuralTrainingSetSimulatedAnnealing extends NeuralSimulatedAnnealing {

	public NeuralTrainingSetSimulatedAnnealing(BasicNetwork network,
			NeuralDataSet training, double startTemp, double stopTemp,
			int cycles) {
		super(network, startTemp, stopTemp, cycles);
		setTraining(training);
	}
	
	/**
	 * Determine the error of the current weights and thresholds.
	 * 
	 * @return The error.
	 */
	public double determineError() {
		return getNetwork().calculateError(getTraining());
	}


}
