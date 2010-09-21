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

package org.encog.neural.networks.training;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * Calculate a score based on a training set. This class allows simulated
 * annealing or genetic algorithms just as you would any other training set
 * based training method.
 */
public class TrainingSetScore implements CalculateScore {

	/**
	 * The training set.
	 */
	private final NeuralDataSet training;

	/**
	 * Construct a training set score calculation.
	 * 
	 * @param training
	 *            The training data to use.
	 */
	public TrainingSetScore(final NeuralDataSet training) {
		this.training = training;
	}

	/**
	 * Calculate the score for the network.
	 * @param network The network to calculate for.
	 * @return The score.
	 */
	public double calculateScore(final BasicNetwork network) {
		return network.calculateError(this.training);
	}

	/**
	 * A training set based score should always seek to lower the error,
	 * as a result, this method always returns true.
	 * @return Returns true.
	 */
	public boolean shouldMinimize() {
		return true;
	}

}
