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

package org.encog.neural.activation;

import org.encog.engine.network.flat.ActivationFunctions;

/**
 * The sigmoid activation function takes on a sigmoidal shape. Only positive
 * numbers are generated. Do not use this activation function if negative number
 * output is desired.
 */
public class ActivationSigmoid extends BasicActivationFunction {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 5622349801036468572L;

	/**
	 * Construct a basic sigmoid function, with a slope of 1.
	 */
	public ActivationSigmoid() {
		this.params = new double[1];
		this.params[ActivationFunctions.PARAM_SIGMOID_SLOPE] = 1;
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public Object clone() {
		return new ActivationSigmoid();
	}

	/**
	 * @return Get the slope of the activation function.
	 */
	public double getSlope() {
		return this.params[ActivationFunctions.PARAM_SIGMOID_SLOPE];
	}

	/**
	 * @return True, sigmoid has a derivative.
	 */
	@Override
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		return ActivationFunctions.ACTIVATION_SIGMOID;
	}
}
