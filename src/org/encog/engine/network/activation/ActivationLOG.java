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
 * An activation function based on the logarithm function.
 * 
 * This type of activation function can be useful to prevent saturation. A
 * hidden node of a neural network is said to be saturated on a given set of
 * inputs when its output is approximately 1 or -1 "most of the time". If this
 * phenomena occurs during training then the learning of the network can be
 * slowed significantly since the error surface is very at in this instance.
 * 
 * @author jheaton
 * 
 */
public class ActivationLOG implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 7134233791725797522L;

	/**
	 * The parameters.
	 */
	private double[] params;

	/**
	 * Construct the activation function.
	 */
	public ActivationLOG() {
		this.params = new double[0];
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationLOG();
	}

	/**
	 * @return Return true, log has a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activationFunction(final double[] x) {

		for (int i = 0; i < x.length; i++) {
			if (x[i] >= 0) {
				x[i] = BoundMath.log(1 + x[i]);
			} else {
				x[i] = -BoundMath.log(1 - x[i]);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double x) {
		if (x >= 0) {
			return 1 / (1 + x);
		} else {
			return 1 / (1 - x);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParamNames() {
		final String[] result = {};
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

}
