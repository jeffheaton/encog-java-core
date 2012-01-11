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
package org.encog.engine.network.activation;

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
public class ActivationStep implements ActivationFunction {

	/**
	 * The step center parameter.
	 */
	public static final int PARAM_STEP_CENTER = 0;

	/**
	 * The step low parameter.
	 */
	public static final int PARAM_STEP_LOW = 1;

	/**
	 * The step high parameter.
	 */
	public static final int PARAM_STEP_HIGH = 2;

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3416782010146745754L;

	/**
	 * The parameters.
	 */
	private final double[] params;

	/**
	 * Create a basic step activation with low=0, center=0, high=1.
	 */
	public ActivationStep() {
		this(0.0, 0.0, 1.0);
	}

	/**
	 * Construct a step activation function.
	 * 
	 * @param low
	 *            The low of the function.
	 * @param center
	 *            The center of the function.
	 * @param high
	 *            The high of the function.
	 */
	public ActivationStep(final double low, final double center,
			final double high) {
		this.params = new double[3];
		this.params[ActivationStep.PARAM_STEP_CENTER] = center;
		this.params[ActivationStep.PARAM_STEP_LOW] = low;
		this.params[ActivationStep.PARAM_STEP_HIGH] = high;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			if (x[i] >= this.params[ActivationStep.PARAM_STEP_CENTER]) {
				x[i] = this.params[ActivationStep.PARAM_STEP_HIGH];
			} else {
				x[i] = this.params[ActivationStep.PARAM_STEP_LOW];
			}
		}
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public final ActivationFunction clone() {
		final ActivationStep result = new ActivationStep(getLow(), getCenter(),
				getHigh());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return 1.0;
	}

	/**
	 * @return The center.
	 */
	public final double getCenter() {
		return this.params[ActivationStep.PARAM_STEP_CENTER];
	}

	/**
	 * @return The high value.
	 */
	public final double getHigh() {
		return this.params[ActivationStep.PARAM_STEP_HIGH];
	}

	/**
	 * @return The low value.
	 */
	public final double getLow() {
		return this.params[ActivationStep.PARAM_STEP_LOW];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getParamNames() {
		final String[] result = { "center", "low", "high" };
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getParams() {
		return this.params;
	}

	/**
	 * @return Returns true, this activation function has a derivative.
	 */
	@Override
	public final boolean hasDerivative() {
		return true;
	}

	/**
	 * Set the center of this function.
	 * 
	 * @param d
	 *            The center of this function.
	 */
	public final void setCenter(final double d) {
		setParam(ActivationStep.PARAM_STEP_CENTER, d);
	}

	/**
	 * Set the high of this function.
	 * 
	 * @param d
	 *            The high of this function.
	 */
	public final void setHigh(final double d) {
		setParam(ActivationStep.PARAM_STEP_HIGH, d);
	}

	/**
	 * Set the low of this function.
	 * 
	 * @param d
	 *            The low of this function.
	 */
	public final void setLow(final double d) {
		setParam(ActivationStep.PARAM_STEP_LOW, d);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParam(final int index, final double value) {
		this.params[index] = value;
	}
}
