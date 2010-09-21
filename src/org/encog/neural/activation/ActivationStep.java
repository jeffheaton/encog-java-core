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
 * The step activation function is a very simple activation function. It is the
 * activation function that was used by the original perceptron. Using the
 * default parameters it will return 1 if the input is 0 or greater. Otherwise
 * it will return 1.
 * 
 * The center, low and high properties allow you to define how this activation
 * function works. If the input is equal to center or higher the high property
 * value will be returned, otherwise the low property will be returned. This
 * activation function does not have a derivative, and can not be used with
 * propagation training, or any other training that requires a derivative.
 * 
 */
public class ActivationStep extends BasicActivationFunction {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3416782010146745754L;

	/**
	 * Construct a step activation function.
	 * @param low The low of the function.
	 * @param center The center of the function.
	 * @param high The high of the function.
	 */
	public ActivationStep(
			final double low, 
			final double center, 
			final double high) {
		this.params = new double[3];
		this.params[ActivationFunctions.PARAM_STEP_CENTER] = center;
		this.params[ActivationFunctions.PARAM_STEP_LOW] = low;
		this.params[ActivationFunctions.PARAM_STEP_HIGH] = high;
	}

	/**
	 * Create a basic step activation with low=0, center=0, high=1.
	 */
	public ActivationStep() {
		this(0.0, 0.0, 1.0);
	}

	/**
	 * @return The center.
	 */
	public double getCenter() {
		return this.params[ActivationFunctions.PARAM_STEP_CENTER];
	}

	/**
	 * @return The low value.
	 */
	public double getLow() {
		return this.params[ActivationFunctions.PARAM_STEP_LOW];
	}

	/**
	 * @return The high value.
	 */
	public double getHigh() {
		return this.params[ActivationFunctions.PARAM_STEP_HIGH];
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		ActivationStep result = new ActivationStep(getLow(), getCenter(),
				getHigh());
		return result;
	}

	/**
	 * @return Returns false, this activation function has no derivative.
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
		return ActivationFunctions.ACTIVATION_STEP;
	}

	/**
	 * Set the center of this function.
	 * 
	 * @param d
	 *            The center of this function.
	 */
	public void setCenter(final double d) {
		this.setParam(ActivationFunctions.PARAM_STEP_CENTER, d);
	}

	/**
	 * Set the high of this function.
	 * 
	 * @param d
	 *            The high of this function.
	 */
	public void setHigh(final double d) {
		this.setParam(ActivationFunctions.PARAM_STEP_HIGH, d);
	}

	/**
	 * Set the low of this function.
	 * 
	 * @param d
	 *            The low of this function.
	 */
	public void setLow(final double d) {
		this.setParam(ActivationFunctions.PARAM_STEP_LOW, d);
	}

}
