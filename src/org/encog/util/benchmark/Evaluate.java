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
package org.encog.util.benchmark;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Used to evaluate the training time for a network.
 * 
 * @author jheaton
 * 
 */
public final class Evaluate {

	/**
	 * Mili-seconds in a second.
	 */
	public static final double MILIS = 1000;

	/**
	 * How many times to try.
	 */
	public static final int TRYS = 10;

	/**
	 * Evaluate how long it takes to calculate the error for the network. This
	 * causes each of the training pairs to be run through the network. The
	 * network is evaluated 10 times and the lowest time is reported.
	 * 
	 * @param network
	 *            The network to evaluate with.
	 * @param training
	 *            The training data to use.
	 * @return The lowest number of seconds that each of the ten attempts took.
	 */
	public static double evaluateNetwork(final BasicNetwork network,
			final NeuralDataSet training) {
		// train the neural network
		long result = Long.MAX_VALUE;

		for (int i = 1; i < Evaluate.TRYS; i++) {
			final long start = System.currentTimeMillis();
			network.calculateError(training);
			final long time = System.currentTimeMillis() - start;
			if (time < result) {
				result = time;
			}
		}

		return result / Evaluate.MILIS;
	}

	/**
	 * Evaluate how long it takes to calculate the error for the network. This
	 * causes each of the training pairs to be run through the network. The
	 * network is evaluated 10 times and the lowest time is reported.
	 * 
	 * @param network
	 *            The network to evaluate with.
	 * @param training
	 *            The training data to use.
	 * @return The lowest number of seconds that each of the ten attempts took.
	 */
	public static double evaluateTrain(final BasicNetwork network,
			final NeuralDataSet training) {
		// train the neural network
		final Train train = new ResilientPropagation(network, training);
		long result = Long.MAX_VALUE;

		for (int i = 1; i < Evaluate.TRYS; i++) {
			final long start = System.currentTimeMillis();
			train.iteration();
			final long time = System.currentTimeMillis() - start;
			if (time < result) {
				result = time;
			}
		}
		return result / Evaluate.MILIS;
	}

	/**
	 * Private constructor.
	 */
	private Evaluate() {
	}
}
