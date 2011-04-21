package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.util.ParamsHolder;

/**
 * A factory to create simulated annealing trainers.
 */
public class AnnealFactory {
	/**
	 * Create an annealing trainer.
	 * 
	 * @param method
	 *            The method to use.
	 * @param training
	 *            The training data to use.
	 * @param argsStr
	 *            The arguments to use.
	 * @return The newly created trainer.
	 */
	public final MLTrain create(final MLMethod method,
			final MLDataSet training, final String argsStr) {

		if (!(method instanceof BasicNetwork)) {
			throw new TrainingError(
					"Invalid method type, requires BasicNetwork");
		}

		final CalculateScore score = new TrainingSetScore(training);

		final Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		final ParamsHolder holder = new ParamsHolder(args);
		final double startTemp = holder.getDouble(
				MLTrainFactory.PROPERTY_TEMPERATURE_START, false, 10);
		final double stopTemp = holder.getDouble(
				MLTrainFactory.PROPERTY_TEMPERATURE_STOP, false, 2);

		final int cycles = holder.getInt(MLTrainFactory.CYCLES, false, 100);

		final MLTrain train = new NeuralSimulatedAnnealing(
				(BasicNetwork) method, score, startTemp, stopTemp, cycles);

		return train;
	}
}
