/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
import org.encog.ml.train.strategy.end.EndTrainingStrategy;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * Provides a MLTrain compatible class that can be used to train genomes.
 */
public class TrainEA extends BasicEA implements MLTrain {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The training strategies to use.
	 */
	private final List<Strategy> strategies = new ArrayList<Strategy>();

	/**
	 * Create a trainer for a score function.
	 * @param thePopulation The population.
	 * @param theScoreFunction The score function.
	 */
	public TrainEA(Population thePopulation, CalculateScore theScoreFunction) {
		super(thePopulation, theScoreFunction);
	}

	/**
	 * Create a trainer for training data.
	 * @param thePopulation The population.
	 * @param trainingData The training data.
	 */
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
	public boolean isTrainingDone() {
		for (Strategy strategy : this.strategies) {
			if (strategy instanceof EndTrainingStrategy) {
				EndTrainingStrategy end = (EndTrainingStrategy)strategy;
				if( end.shouldStop() ) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	/**
	 * Training strategies can be added to improve the training results. There
	 * are a number to choose from, and several can be used at once.
	 * 
	 * @param strategy
	 *            The strategy to add.
	 */
	public void addStrategy(final Strategy strategy) {
		strategy.init(this);
		this.strategies.add(strategy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * {@inheritDoc}
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
		return this.getPopulation();
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
	 * @return The strategies to use.
	 */
	public List<Strategy> getStrategies() {
		return this.strategies;
	}
	
	@Override
	public void iteration() {
		preIteration();
		super.iteration();
		postIteration();
	}
	
	/**
	 * Call the strategies after an iteration.
	 */
	public void postIteration() {
		for (final Strategy strategy : this.strategies) {
			strategy.postIteration();
		}
	}

	/**
	 * Call the strategies before an iteration.
	 */
	public void preIteration() {
		for (final Strategy strategy : this.strategies) {
			strategy.preIteration();
		}
	}
}
