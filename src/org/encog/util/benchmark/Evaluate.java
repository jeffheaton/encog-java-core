/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.util.benchmark;

import org.encog.engine.network.train.prop.RPROPConst;
import org.encog.engine.opencl.EncogCLDevice;
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
		return evaluateTrain(null, input,hidden1,hidden2,output);
	}
	
	public static int evaluateTrain(EncogCLDevice device, int input, int hidden1, int hidden2,
			int output) {
		final BasicNetwork network = EncogUtility.simpleFeedForward(input,
				hidden1, hidden2, output, true);
		final NeuralDataSet training = RandomTrainingFactory.generate(1000,
				10000, input, output, -1, 1);
		return evaluateTrain(device, network, training);
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
	public static int evaluateTrain(final EncogCLDevice device,
			final BasicNetwork network, final NeuralDataSet training) {
		// train the neural network
		Train train;
		
		if( device==null ) {
			train = new ResilientPropagation(network, training);
		} else {
			train = new ResilientPropagation(
					network, 
					training, 
					device, 
					RPROPConst.DEFAULT_INITIAL_UPDATE, 
					RPROPConst.DEFAULT_MAX_STEP);
		}

		final long start = System.currentTimeMillis();
		final long stop = start + (10 * MILIS);

		int iterations = 0;
		while (System.currentTimeMillis() < stop) {
			iterations++;
			train.iteration();
		}

		return iterations;
	}

}
