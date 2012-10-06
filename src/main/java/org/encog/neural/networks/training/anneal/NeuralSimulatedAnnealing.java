/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.training.anneal;

import org.encog.ml.MLEncodable;
import org.encog.ml.MLRegression;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.logging.EncogLogging;

/**
 * This class implements a simulated annealing training algorithm for neural
 * networks. It is based on the generic SimulatedAnnealing class. It is used in
 * the same manner as any other training class that implements the Train
 * interface. There are essentially two ways you can make use of this class.
 * 
 * Either way, you will need a score object. The score object tells the
 * simulated annealing algorithm how well suited a neural network is.
 * 
 * If you would like to use simulated annealing with a training set you should
 * make use TrainingSetScore class. This score object uses a training set to
 * score your neural network.
 * 
 * If you would like to be more abstract, and not use a training set, you can
 * create your own implementation of the CalculateScore method. This class can
 * then score the networks any way that you like.
 * 
 */
public class NeuralSimulatedAnnealing extends BasicTraining {

	/**
	 * The cutoff for random data.
	 */
	public static final double CUT = 0.5;

	/**
	 * The neural network that is to be trained.
	 */
	private final MLEncodable network;

	/**
	 * This class actually performs the training.
	 */
	private final NeuralSimulatedAnnealingHelper anneal;

	/**
	 * Used to calculate the score.
	 */
	private final CalculateScore calculateScore;

	/**
	 * Construct a simulated annleaing trainer for a encodable MLMethod.
	 * 
	 * @param network
	 *            The neural network to be trained.
	 * @param calculateScore
	 *            Used to calculate the score for a MLMethod.
	 * @param startTemp
	 *            The starting temperature.
	 * @param stopTemp
	 *            The ending temperature.
	 * @param cycles
	 *            The number of cycles in a training iteration.
	 */
	public NeuralSimulatedAnnealing(final MLEncodable network,
			final CalculateScore calculateScore, final double startTemp,
			final double stopTemp, final int cycles) {
		super(TrainingImplementationType.Iterative);
		
		if( !(network instanceof MLRegression) ) {
			throw new TrainingError("Simulated annealing requires the MLMethod to support MLRegression.");
		}
		
		this.network = network;
		this.calculateScore = calculateScore;
		this.anneal = new NeuralSimulatedAnnealingHelper(this);
		this.anneal.setTemperature(startTemp);
		this.anneal.setStartTemperature(startTemp);
		this.anneal.setStopTemperature(stopTemp);
		this.anneal.setCycles(cycles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * Get the network as an array of doubles.
	 * 
	 * @return The network as an array of doubles.
	 */
	public double[] getArray() {
		return NetworkCODEC
				.networkToArray(NeuralSimulatedAnnealing.this.network);
	}

	/**
	 * @return A copy of the annealing array.
	 */
	public double[] getArrayCopy() {
		return getArray();
	}

	/**
	 * @return The object used to calculate the score.
	 */
	public CalculateScore getCalculateScore() {
		return this.calculateScore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLEncodable getMethod() {
		return this.network;
	}

	/**
	 * Perform one iteration of simulated annealing.
	 */
	@Override
	public void iteration() {
		EncogLogging.log(EncogLogging.LEVEL_INFO,
				"Performing Simulated Annealing iteration.");
		preIteration();
		this.anneal.iteration();
		setError(this.anneal.calculateScore());
		postIteration();
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * Convert an array of doubles to the current best network.
	 * 
	 * @param array
	 *            An array.
	 */
	public void putArray(final double[] array) {
		NetworkCODEC.arrayToNetwork(array,
				NeuralSimulatedAnnealing.this.network);
	}

	/**
	 * Randomize the weights and bias values. This function does most of the
	 * work of the class. Each call to this class will randomize the data
	 * according to the current temperature. The higher the temperature the more
	 * randomness.
	 */
	public void randomize() {
		final double[] array = NetworkCODEC
				.networkToArray(NeuralSimulatedAnnealing.this.network);

		for (int i = 0; i < array.length; i++) {
			double add = NeuralSimulatedAnnealing.CUT - Math.random();
			add /= this.anneal.getStartTemperature();
			add *= this.anneal.getTemperature();
			array[i] = array[i] + add;
		}

		NetworkCODEC.arrayToNetwork(array,
				NeuralSimulatedAnnealing.this.network);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

}
