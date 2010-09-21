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
 * An activation function based on the gaussian function.
 * 
 * @author jheaton
 * 
 */
public class ActivationGaussian extends BasicActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	/**
	 * Create a gaussian activation function.
	 * 
	 * @param center
	 *            The center of the curve.
	 * @param peak
	 *            The peak of the curve.
	 * @param width
	 *            The width of the curve.
	 */
	public ActivationGaussian(final double center, final double peak,
			final double width) {
		this.params = new double[3];
		this.params[ActivationFunctions.PARAM_GAUSSIAN_CENTER] = center;
		this.params[ActivationFunctions.PARAM_GAUSSIAN_PEAK] = peak;
		this.params[ActivationFunctions.PARAM_GAUSSIAN_WIDTH] = width;
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return new ActivationGaussian(this.getCenter(), this.getPeak(), this
				.getWidth());
	}

	/**
	 * @return The width of the function.
	 */
	private double getWidth() {
		return this.getParams()[ActivationFunctions.PARAM_GAUSSIAN_WIDTH];
	}

	/**
	 * @return The center of the function.
	 */
	private double getCenter() {
		return this.getParams()[ActivationFunctions.PARAM_GAUSSIAN_CENTER];
	}

	/**
	 * @return The peak of the function.
	 */
	private double getPeak() {
		return this.getParams()[ActivationFunctions.PARAM_GAUSSIAN_PEAK];
	}

	/**
	 * @return Return true, gaussian has a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		// TODO Auto-generated method stub
		return ActivationFunctions.ACTIVATION_GAUSSIAN;
	}

}
