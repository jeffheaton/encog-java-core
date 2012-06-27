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
package org.encog.neural.som.training.clustercopy;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.som.SOM;

/**
 * SOM cluster copy is a very simple trainer for SOM's. Using this trainer all of
 * the training data is copied to the SOM weights. This can provide a functional
 * SOM, or can be used as a starting point for training.
 * 
 * For now, this trainer will only work if you have equal or fewer training elements 
 * to the number of output neurons.  Eventually I hope to expand this by using 
 * KMeans clustering.
 * 
 */
public class SOMClusterCopyTraining extends BasicTraining {

	/**
	 * The SOM to train.
	 */
	private final SOM network;

	/**
	 * Is training done.
	 */
	private boolean done;

	/**
	 * Construct the object.
	 * @param network The network to train.
	 * @param training The training data.
	 */
	public SOMClusterCopyTraining(final SOM network, final MLDataSet training) {
		super(TrainingImplementationType.OnePass);
		this.network = network;
		if (this.network.getOutputCount() < training.getRecordCount()) {
			throw new NeuralNetworkError(
					"To use cluster copy training you must have at least as many output neurons as training elements.");
		}		
		setTraining(training);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * @return True if training can progress no further.
	 */
	public boolean isTrainingDone() {
		if (super.isTrainingDone())
			return true;
		else
			return done;
	}

	/**
	 * Copy the specified input pattern to the weight matrix. This causes an
	 * output neuron to learn this pattern "exactly". This is useful when a
	 * winner is to be forced.
	 *
	 * @param outputNeuron
	 *            The output neuron to set.
	 * @param input
	 *            The input pattern to copy.
	 */
	private void copyInputPattern(final int outputNeuron, final MLData input) {
		for (int inputNeuron = 0; inputNeuron < this.network.getInputCount(); inputNeuron++) {
			this.network.getWeights().set(outputNeuron, inputNeuron,
					input.getData(inputNeuron));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void iteration() {
		int outputNeuron = 0;
		for (final MLDataPair pair : getTraining()) {
			copyInputPattern(outputNeuron++, pair.getInput());
		}
		this.done = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {
	}

}
