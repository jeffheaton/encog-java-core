/*
  * Encog Neural Network and Bot Library for Java v0.5
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.feedforward.train.anneal;

import org.encog.matrix.MatrixCODEC;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.feedforward.FeedforwardNetwork;
import org.encog.solve.anneal.SimulatedAnnealing;



/**
 * NeuralSimulatedAnnealing: This class implements a simulated 
 * annealing training algorithm for feed forward neural networks.
 * It is based on the generic SimulatedAnnealing class.  It is used
 * in the same manner as any other training class that implements
 * the Train interface.
 */
public class NeuralSimulatedAnnealing extends SimulatedAnnealing<Double> {
	/**
	 * The neural network that is to be trained.
	 */
	protected FeedforwardNetwork network;

	/**
	 * The training data.
	 */
	protected NeuralDataSet training;


	/**
	 * Construct a simulated annleaing trainer for a feedforward neural network.
	 * 
	 * @param network
	 *            The neural network to be trained.
	 * @param input
	 *            The input values for training.
	 * @param ideal
	 *            The ideal values for training.
	 * @param startTemp
	 *            The starting temperature.
	 * @param stopTemp
	 *            The ending temperature.
	 * @param cycles
	 *            The number of cycles in a training iteration.
	 */
	public NeuralSimulatedAnnealing(final FeedforwardNetwork network,
			final NeuralDataSet training,
			final double startTemp, final double stopTemp, final int cycles) {
		this.network = network;
		this.training = training;
		this.temperature = startTemp;
		setStartTemperature(startTemp);
		setStopTemperature(stopTemp);
		setCycles(cycles);
	}

	/**
	 * Determine the error of the current weights and thresholds.
	 * 
	 * @throws NeuralNetworkException
	 */
	@Override
	public double determineError() throws NeuralNetworkError {
		return this.network.calculateError(this.training);
	}

	/**
	 * Get the network as an array of doubles.
	 * @return The network as an array of doubles.
	 */
	@Override
	public Double[] getArray() {
		return MatrixCODEC.networkToArray(this.network);
	}

	/**
	 * Get the best network from the training.
	 * @return The best network.
	 */
	public FeedforwardNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Convert an array of doubles to the current best network.
	 */
	@Override
	public void putArray(final Double[] array) {
		MatrixCODEC.arrayToNetwork(array, this.network);
	}

	/**
	 * Randomize the weights and thresholds. This function does most of the work
	 * of the class. Each call to this class will randomize the data according
	 * to the current temperature. The higher the temperature the more
	 * randomness.
	 */
	@Override
	public void randomize() {
		final Double array[] = MatrixCODEC.networkToArray(this.network);

		for (int i = 0; i < array.length; i++) {
			double add = 0.5 - (Math.random());
			add /= getStartTemperature();
			add *= this.temperature;
			array[i] = array[i] + add;
		}

		MatrixCODEC.arrayToNetwork(array, this.network);
	}

	@Override
	public Double[] getArrayCopy() {
		return this.getArray();
	}

}