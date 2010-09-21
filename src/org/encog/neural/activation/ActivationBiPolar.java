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
import org.encog.neural.NeuralNetworkError;

/**
 * BiPolar activation function. This will scale the neural data into the bipolar
 * range. Greater than zero becomes 1, less than or equal to zero becomes -1.
 * 
 * @author jheaton
 * 
 */
public class ActivationBiPolar extends BasicActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	/**
	 * Construct the bipolar activation function.
	 */
	public ActivationBiPolar() {
		this.params = new double[0];
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationBiPolar();
	}

	/**
	 * Implements the activation function derivative. The array is modified
	 * according derivative of the activation function being used. See the class
	 * description for more specific information on this type of activation
	 * function. Propagation training requires the derivative. Some activation
	 * functions do not support a derivative and will throw an error.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 * @return The derivative.
	 */
	public double derivativeFunction(final double d) {
		throw new NeuralNetworkError(
				"Can't use the bipolar activation function "
						+ "where a derivative is required.");

	}

	/**
	 * @return Return false, bipolar has no derivative.
	 */
	public boolean hasDerivative() {
		return false;
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		// TODO Auto-generated method stub
		return ActivationFunctions.ACTIVATION_BIPOLAR;
	}
}
