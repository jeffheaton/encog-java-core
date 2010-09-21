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
 * Holds basic functionality that all activation functions will likely have use
 * of. Specifically it implements a name and description for the
 * EncogPersistedObject class.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicActivationFunction implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 672555213449163812L;

	/**
	 * The params for this activation function.
	 */
	protected double[] params;

	/**
	 * Construct a base activation function with no params.
	 */
	public BasicActivationFunction() {
		this.params = new double[0];
	}

	/**
	 * Implements the activation function. The array is modified according to
	 * the activation function being used. See the class description for more
	 * specific information on this type of activation function.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 */
	public void activationFunction(final double[] d) {

		ActivationFunctions.calculateActivation(this.getEngineID(), d,
				this.params, 0, d.length, 0);
	}

	/**
	 * Calculate the derivative of the activation. It is assumed that the value
	 * d, which is passed to this method, was the output from this activation.
	 * This prevents this method from having to recalculate the activation, just
	 * to recalculate the derivative.
	 * 
	 * Some activation functions do not have derivatives and will throw an
	 * error.
	 * 
	 * Linear functions will return one for their derivative.
	 * 
	 * @param d
	 *            The input array to the activation function.
	 * 
	 * @return The derivative.
	 */
	public double derivativeFunction(final double d) {
		return ActivationFunctions.calculateActivationDerivative(this
				.getEngineID(), d, this.params, 0);

	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public abstract Object clone();

	/**
	 * {@inheritDoc}
	 */
	public double[] getParams() {
		return this.params;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParam(final int index, final double value) {
		this.params[index] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getParamNames() {
		return ActivationFunctions.getParams(getEngineID());
	}

}
