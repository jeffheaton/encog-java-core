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
 * An activation function that only allows a specified number, usually one, of
 * the out-bound connection to win. These connections will share in the sum of
 * the output, whereas the other neurons will receive zero.
 * 
 * This activation function can be useful for "winner take all" layers.
 * 
 */
public class ActivationCompetitive extends BasicActivationFunction {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 5396927873082336888L;

	/**
	 * Create a competitive activation function with one winner allowed.
	 */
	public ActivationCompetitive() {
		this(1);
	}

	/**
	 * Create a competitive activation function with the specified maximum
	 * number of winners.
	 * 
	 * @param winners
	 *            The maximum number of winners that this function supports.
	 */
	public ActivationCompetitive(final int winners) {
		this.params = new double[1];
		this.params[ActivationFunctions.PARAM_COMPETITIVE_MAX_WINNERS] = winners;
	}

	/**
	 * @return A cloned copy of this object.
	 */
	@Override
	public Object clone() {
		return new ActivationCompetitive(
				(int) this.params[ActivationFunctions.PARAM_COMPETITIVE_MAX_WINNERS]);
	}

	/**
	 * Implements the activation function. The array is modified according to
	 * the activation function being used. See the class description for more
	 * specific information on this type of activation function.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 * @return The derivative.
	 */
	public double derivativeFunction(final double d) {
		throw new NeuralNetworkError(
				"Can't use the competitive activation function "
						+ "where a derivative is required.");

	}

	/**
	 * @return The maximum number of winners this function supports.
	 */
	public int getMaxWinners() {
		return (int) this.params[ActivationFunctions.PARAM_COMPETITIVE_MAX_WINNERS];
	}

	/**
	 * @return False, indication that no derivative is available for this
	 *         function.
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
		return ActivationFunctions.ACTIVATION_COMPETITIVE;
	}

}
