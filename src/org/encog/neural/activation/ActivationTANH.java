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
 * The hyperbolic tangent activation function takes the curved shape of the
 * hyperbolic tangent. This activation function produces both positive and
 * negative output. Use this activation function if both negative and positive
 * output is desired.
 * 
 */
public class ActivationTANH extends BasicActivationFunction {

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 9121998892720207643L;

	/**
	 * Construct a basic HTAN activation function, with a slope of 1.
	 */
	public ActivationTANH() {
		this.params = new double[1];
		this.params[ActivationFunctions.PARAM_TANH_SLOPE] = 1;
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public Object clone() {
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
		return this.params[ActivationFunctions.PARAM_TANH_SLOPE];
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		// TODO Auto-generated method stub
		return ActivationFunctions.ACTIVATION_TANH;
	}

}
