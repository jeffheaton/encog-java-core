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

package org.encog.engine.network.activation;

import org.encog.engine.util.BoundMath;

/**
 * The sigmoid activation function takes on a sigmoidal shape. Only positive
 * numbers are generated. Do not use this activation function if negative number
 * output is desired.
 */
public class ActivationSigmoid implements ActivationFunction {

	/**
	 * The offset to the parameter that holds the sigmoid slope.
	 */
	public static final int PARAM_SIGMOID_SLOPE = 0;

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 5622349801036468572L;

	/**
	 * The parameters.
	 */
	private double[] params;

	/**
	 * Construct a basic sigmoid function, with a slope of 1.
	 */
	public ActivationSigmoid() {
		this.params = new double[1];
		this.params[ActivationSigmoid.PARAM_SIGMOID_SLOPE] = 1;
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public ActivationFunction clone() {
		return new ActivationSigmoid();
	}

	/**
	 * @return Get the slope of the activation function.
	 */
	public double getSlope() {
		return this.params[ActivationSigmoid.PARAM_SIGMOID_SLOPE];
	}

	/**
	 * @return True, sigmoid has a derivative.
	 */
	@Override
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			x[i] = 1.0 / (1.0 + BoundMath.exp(-params[0] * x[i]));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double x) {
		return params[0] * x * (1.0 - x);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParamNames() {
		final String[] results = { "slope" };
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] getParams() {
		// TODO Auto-generated method stub
		return this.params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParam(final int index, final double value) {
		this.params[index] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOpenCLExpression(final boolean derivative,
			final boolean allSlopeOne) {
		if (derivative) {
			return "(slope * x * (1.0f - x))";
		} else {
			return "(1.0f / (1.0f + exp(-slope * x)))";
		}
	}
}
