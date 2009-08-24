/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.training.anneal;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a simulated annealing training algorithm for 
 * neural networks. It is based on the generic SimulatedAnnealing class.
 * It is used in the same manner as any other training class that implements the
 * Train interface.  This class is abstract, to create your own version
 * of simulated annealing, you must provide an implementation of the
 * determineError method.  If you want to train with a training set, use
 * the NeuralTrainingSetSimulatedAnnealing class.
 */
public abstract class NeuralSimulatedAnnealing extends BasicTraining {

	/**
	 * The cutoff for random data.
	 */
	public static final double CUT = 0.5;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The neural network that is to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * This class actually performs the training.
	 */
	private final NeuralSimulatedAnnealingHelper anneal;

	/**
	 * Construct a simulated annleaing trainer for a feedforward neural network.
	 * 
	 * @param network
	 *            The neural network to be trained.
	 * @param startTemp
	 *            The starting temperature.
	 * @param stopTemp
	 *            The ending temperature.
	 * @param cycles
	 *            The number of cycles in a training iteration.
	 */
	public NeuralSimulatedAnnealing(final BasicNetwork network,
			final double startTemp,
			final double stopTemp, 
			final int cycles) {
		this.network = network;
		this.anneal = new NeuralSimulatedAnnealingHelper(this);
		this.anneal.setTemperature(startTemp);
		this.anneal.setStartTemperature(startTemp);
		this.anneal.setStopTemperature(stopTemp);
		this.anneal.setCycles(cycles);
	}

	/**
	 * Get the best network from the training.
	 * 
	 * @return The best network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Perform one iteration of simulated annealing.
	 */
	public void iteration() {
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Performing Simulated Annealing iteration.");
		}
		preIteration();
		this.anneal.iteration();
		setError(this.anneal.determineError());
		postIteration();
	}
	
	/**
	 * Determine the error of the current weights and thresholds.
	 * 
	 * @return The error.
	 */
	public abstract double determineError();

	/**
	 * Get the network as an array of doubles.
	 * 
	 * @return The network as an array of doubles.
	 */
	public Double[] getArray() {
		return NetworkCODEC
				.networkToArray(NeuralSimulatedAnnealing.this.network);
	}

	/**
	 * @return A copy of the annealing array.
	 */
	public Double[] getArrayCopy() {
		return getArray();
	}

	/**
	 * Convert an array of doubles to the current best network.
	 * 
	 * @param array
	 *            An array.
	 */
	public void putArray(final Double[] array) {
		NetworkCODEC.arrayToNetwork(array,
				NeuralSimulatedAnnealing.this.network);
	}

	/**
	 * Randomize the weights and thresholds. This function does most of the
	 * work of the class. Each call to this class will randomize the data
	 * according to the current temperature. The higher the temperature the
	 * more randomness.
	 */
	public void randomize() {
		final Double[] array = NetworkCODEC
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

}