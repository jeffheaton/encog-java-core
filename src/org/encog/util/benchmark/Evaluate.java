/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.util.benchmark;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.simple.EncogUtility;

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
	public static final int MILIS = 1000;

	
	public static int evaluateTrain(int input, int hidden1, int hidden2,
			int output) {
		final BasicNetwork network = EncogUtility.simpleFeedForward(input,
				hidden1, hidden2, output, true);
		final NeuralDataSet training = RandomTrainingFactory.generate(1000,
				10000, input, output, -1, 1);
		return evaluateTrain(network, training);
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
	public static int evaluateTrain(final BasicNetwork network,
			final NeuralDataSet training) {
		// train the neural network
		final Train train = new ResilientPropagation(network, training);

			final long start = System.currentTimeMillis();
			final long stop = start + (10*MILIS);
			
			int iterations = 0;
			while( System.currentTimeMillis()<stop ) {
				iterations++;
				train.iteration();	
			}
			
		return iterations;
	}
	
	/**
	 * Private constructor.
	 */
	private Evaluate() {
	}
}
