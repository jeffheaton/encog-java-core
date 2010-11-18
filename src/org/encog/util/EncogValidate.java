/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.util;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

/**
 * Used to validate if training is valid.
 */
public final class EncogValidate {

	/**
	 * Validate a network for training.
	 * 
	 * @param network
	 *            The network to validate.
	 * @param training
	 *            The training set to validate.
	 */
	public static void validateNetworkForTraining(final BasicNetwork network,
			final NeuralDataSet training) {
		final Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);

		if (inputLayer == null) {
			throw new NeuralNetworkError(
					"This operation requires that the neural network have an input layer.");
		}

		if (outputLayer == null) {
			throw new NeuralNetworkError(
					"This operation requires that the neural network have an output layer.");
		}

		if (inputLayer.getNeuronCount() != training.getInputSize()) {
			throw new NeuralNetworkError("The input layer size of "
					+ inputLayer.getNeuronCount()
					+ " must match the training input size of "
					+ training.getInputSize() + ".");
		}

		if ((training.getIdealSize() > 0)
				&& (outputLayer.getNeuronCount() != training.getIdealSize())) {
			throw new NeuralNetworkError("The output layer size of "
					+ outputLayer.getNeuronCount()
					+ " must match the training input size of "
					+ training.getIdealSize() + ".");
		}
	}

	/**
	 * Private constructor.
	 */
	private EncogValidate() {

	}
}
