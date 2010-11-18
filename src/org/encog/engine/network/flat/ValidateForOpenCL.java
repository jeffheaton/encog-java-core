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
package org.encog.engine.network.flat;

import org.encog.engine.EncogEngineError;
import org.encog.engine.EngineMachineLearning;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.validate.BasicMachineLearningValidate;

/**
 * Validate the network to be sure it can run on OpenCL.
 * 
 */
public class ValidateForOpenCL extends BasicMachineLearningValidate {

	/**
	 * Determine if the network is valid for OpenCL.
	 * 
	 * @param network
	 *            The network to check.
	 * @return The string indicating the error that prevents OpenCL from using
	 *         the network, or null if the network is fine for OpenCL.
	 */
	@Override
	public String isValid(final EngineMachineLearning network) {

		if (!(network instanceof FlatNetwork)) {
			return "Only flat networks are valid to be used for OpenCL";
		}

		final FlatNetwork flat = (FlatNetwork) network;

		for (ActivationFunction activation : flat.getActivationFunctions()) {
			if (activation.getOpenCLExpression(true) == null) {
				return "Can't use OpenCL if activation function does not have an OpenCL expression.";
			}
		}

		if (flat.hasSameActivationFunction() == null) {
			return "Can't use OpenCL training on a neural network that uses multiple activation functions.";
		}

		boolean hasContext = false;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			if (flat.getContextTargetOffset()[i] != 0) {
				hasContext = true;
			}

			if (flat.getContextTargetSize()[i] != 0) {
				hasContext = true;
			}
		}

		if (hasContext) {
			return "Can't use OpenCL if context neurons are present.";
		}

		return null;
	}

}
