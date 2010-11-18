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
package org.encog.engine.network.activation;

import org.encog.engine.util.BoundMath;

/**
 * An activation function based on the sin function.
 * 
 * @author jheaton
 */
public class ActivationSIN implements ActivationFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5301501177778271284L;

	/**
	 * Construct the sin activation function.
	 */
	public ActivationSIN() {
		this.params = new double[0];
	}

	/**
	 * The parameters.
	 */
	private double[] params;

	/**
	 * @return The object cloned;
	 */
	@Override
	public ActivationFunction clone() {
		return new ActivationSIN();
	}

	/**
	 * @return Return true, sin has a derivative.
	 */
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
			x[i] = BoundMath.sin(x[i]);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double x) {
		return BoundMath.cos(x);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOpenCLExpression(final boolean derivative) {
		return null;
	}
}
