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
 * The hyperbolic tangent activation function takes the curved shape of the
 * hyperbolic tangent. This activation function produces both positive and
 * negative output. Use this activation function if both negative and positive
 * output is desired.
 * 
 */
public class ActivationTANH implements ActivationFunction {

	/**
	 * The offset to the parameter that holds the tanh slope.
	 */
	public static final int PARAM_TANH_SLOPE = 0;

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 9121998892720207643L;

	/**
	 * The parameters.
	 */
	private double[] params;

	/**
	 * Construct a basic HTAN activation function, with a slope of 1.
	 */
	public ActivationTANH() {
		this.params = new double[1];
		this.params[ActivationTANH.PARAM_TANH_SLOPE] = 1;
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public ActivationFunction clone() {
		return new ActivationTANH();
	}

	/**
	 * @return Return true, TANH has a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @return Get the slope of the activation function.
	 */
	public double getSlope() {
		return this.params[ActivationTANH.PARAM_TANH_SLOPE];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			final double z = BoundMath.exp(-params[0] * x[i]);
			x[i] = (1.0 - z) / (1.0 + z);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double x) {
		return (params[0] * (1.0 - x * x));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParamNames() {
		final String[] result = { "slope" };
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] getParams() {
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
			return "(slope * (1.0f - x * x))";
		} else {
			if (allSlopeOne) {
				return "tanh(x)";
			} else {
				return "tanh(x)";
			}
		}
	}
}
