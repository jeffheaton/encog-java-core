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
package org.encog.util;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.ContainsFlat;

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
	public static void validateNetworkForTraining(final ContainsFlat network,
			final MLDataSet training) {

		int inputCount = network.getFlat().getInputCount();
		int outputCount = network.getFlat().getOutputCount();		

		if (inputCount != training.getInputSize()) {
			throw new NeuralNetworkError("The input layer size of "
					+ inputCount
					+ " must match the training input size of "
					+ training.getInputSize() + ".");
		}

		if ((training.getIdealSize() > 0)
				&& (outputCount != training.getIdealSize())) {
			throw new NeuralNetworkError("The output layer size of "
					+ outputCount
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
