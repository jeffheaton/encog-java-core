package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;
import org.encog.util.ParamsHolder;

/**
 * A factory to create genetic algorithm trainers.
 */
public class GeneticFactory {
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
		final int populationSize = holder.getInt(
				MLTrainFactory.PROPERTY_POPULATION_SIZE, false, 5000);
		final double mutation = holder.getDouble(
				MLTrainFactory.PROPERTY_MUTATION, false, 0.1);
		final double mate = holder.getDouble(MLTrainFactory.PROPERTY_MATE,
				false, 0.25);

		final MLTrain train = new NeuralGeneticAlgorithm((BasicNetwork) method,
				new RangeRandomizer(-1, 1), score, populationSize, mutation,
				mate);

		return train;
	}
}
