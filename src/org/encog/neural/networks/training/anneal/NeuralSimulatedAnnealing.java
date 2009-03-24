/*
 * Encog Artificial Intelligence Framework v1.x
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

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.Train;
import org.encog.solve.anneal.SimulatedAnnealing;

/**
 * NeuralSimulatedAnnealing: This class implements a simulated annealing
 * training algorithm for feed forward neural networks. It is based on the
 * generic SimulatedAnnealing class. It is used in the same manner as any other
 * training class that implements the Train interface.
 */
public class NeuralSimulatedAnnealing extends BasicTraining {
	/**
	 * The neural network that is to be trained.
	 */
	private BasicNetwork network;


	
	/**
	 * The cutoff for random data.
	 */
	public static final double CUT = 0.5;
	
	class SimulatedAnnealingHelper extends SimulatedAnnealing<Double>
	{
		/**
		 * Determine the error of the current weights and thresholds.
		 * @return The error.
		 */
		@Override
		public double determineError() {
			return network.calculateError(getTraining());
		}

		/**
		 * Get the network as an array of doubles.
		 * 
		 * @return The network as an array of doubles.
		 */
		@Override
		public Double[] getArray() {
			return NetworkCODEC.networkToArray(network);
		}

		/**
		 * @return A copy of the annealing array.
		 */
		@Override
		public Double[] getArrayCopy() {
			return getArray();
		}
		
		/**
		 * Convert an array of doubles to the current best network.
		 * @param array An array.
		 */
		@Override
		public void putArray(final Double[] array) {
			NetworkCODEC.arrayToNetwork(array, network);
		}

		/**
		 * Randomize the weights and thresholds. This function does most of the work
		 * of the class. Each call to this class will randomize the data according
		 * to the current temperature. The higher the temperature the more
		 * randomness.
		 */
		@Override
		public void randomize() {
			final Double[] array = NetworkCODEC.networkToArray(network);

			for (int i = 0; i < array.length; i++) {
				double add = NeuralSimulatedAnnealing.CUT - Math.random();
				add /= getStartTemperature();
				add *= this.getTemperature();
				array[i] = array[i] + add;
			}

			NetworkCODEC.arrayToNetwork(array, network);
		}
		
	}
	
	private SimulatedAnnealingHelper anneal;

	/**
	 * Construct a simulated annleaing trainer for a feedforward neural network.
	 * 
	 * @param network
	 *            The neural network to be trained.
	 * @param training
	 *            The training set.
	 * @param startTemp
	 *            The starting temperature.
	 * @param stopTemp
	 *            The ending temperature.
	 * @param cycles
	 *            The number of cycles in a training iteration.
	 */
	public NeuralSimulatedAnnealing(final BasicNetwork network,
			final NeuralDataSet training, final double startTemp,
			final double stopTemp, final int cycles) {
		this.network = network;
		setTraining(training);
		this.anneal = new SimulatedAnnealingHelper();
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


	@Override
	public void iteration() {
		anneal.iteration();
		setError(anneal.determineError());
	}
}