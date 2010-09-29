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

import org.encog.engine.network.flat.ActivationFunctions;

/**
 * The Linear layer is really not an activation function at all. The input is
 * simply passed on, unmodified, to the output. This activation function is
 * primarily theoretical and of little actual use. Usually an activation
 * function that scales between 0 and 1 or -1 and 1 should be used.
 */
public class ActivationLinear implements ActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -5356580554235104944L;

	/**
	 * The parameters.
	 */
	private double[] params;
	
	/**
	 * Construct a linear activation function, with a slope of 1.
	 */
	public ActivationLinear() {
		this.params = new double[1];
		this.params[ActivationFunctions.PARAM_LINEAR_SLOPE] = 1;
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationLinear();
	}

	/**
	 * @return Return true, linear has a 1 derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @return The slope of the activation function.
	 */
	public double getSlope() {
		return this.params[ActivationFunctions.PARAM_LINEAR_SLOPE];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activationFunction(final double[] d) {		
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double d) {
		return 1;
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
}
