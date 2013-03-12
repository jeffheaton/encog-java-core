package org.encog.ml.ea.train.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.population.Population;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class TrainEA extends BasicEA implements MLTrain {


	public TrainEA(Population thePopulation, CalculateScore theScoreFunction) {
		super(thePopulation, theScoreFunction);
		// TODO Auto-generated constructor stub
	}
	
	public TrainEA(Population thePopulation, MLDataSet trainingData) {
		super(thePopulation, new TrainingSetScore(trainingData));
	}

	/**
	 * Not used.
	 * 
	 * @param error
	 *            Not used.
	 */
	@Override
	public void setError(final double error) {
	}
	
	/**
	 * @return True if training can progress no further.
	 */
	@Override
	public boolean isTrainingDone() {
		return false;
	}
	
	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}
	
	/**
	 * Perform the specified number of training iterations. This is a basic
	 * implementation that just calls iteration the specified number of times.
	 * However, some training methods, particularly with the GPU, benefit
	 * greatly by calling with higher numbers than 1.
	 * 
	 * @param count
	 *            The number of training iterations.
	 */
	@Override
	public void iteration(final int count) {
		for (int i = 0; i < count; i++) {
			iteration();
		}
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(final TrainingContinuation state) {

	}
	
	/**
	 * Not supported, will throw an error.
	 * 
	 * @param strategy
	 *            Not used.
	 */
	@Override
	public void addStrategy(final Strategy strategy) {
		throw new TrainingError(
				"Strategies are not supported by this training method.");
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * Called when training is done.
	 */
	@Override
	public void finishTraining() {
		super.finishTraining();
		this.getPopulation().setBestGenome(this.getBestGenome());
	}
	
	/**
	 * @return A network created for the best genome.
	 */
	@Override
	public MLMethod getMethod() {
		if (this.getBestGenome() != null) {
			return this.getCODEC().decode(this.getBestGenome());
		} else {
			return null;
		}
	}
		
	/**
	 * Returns null, does not use a training set, rather uses a score function.
	 * 
	 * @return null, not used.
	 */
	@Override
	public MLDataSet getTraining() {
		return null;
	}
	
	/**
	 * Returns an empty list, strategies are not supported.
	 * 
	 * @return The strategies in use(none).
	 */
	@Override
	public List<Strategy> getStrategies() {
		return new ArrayList<Strategy>();
	}
}
