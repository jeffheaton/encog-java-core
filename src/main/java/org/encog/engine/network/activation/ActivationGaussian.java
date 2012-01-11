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

import org.encog.mathutil.BoundMath;

/**
 * An activation function based on the Gaussian function. The output range is
 * between 0 and 1. This activation function is used mainly for the HyperNeat
 * implementation.
 * 
 * A derivative is provided, so this activation function can be used with
 * propagation training.  However, its primary intended purpose is for
 * HyperNeat.  The derivative was obtained with the R statistical package.
 * 
 * If you are looking to implement a RBF-based neural network, see the 
 * RBFNetwork class.
 * 
 * The idea for this activation function was developed by  Ken Stanley, of  
 * the University of Texas at Austin.
 * http://www.cs.ucf.edu/~kstanley/
 */
public class ActivationGaussian implements ActivationFunction {

	/**
	 * The offset to the parameter that holds the width.
	 */
	public static final int PARAM_GAUSSIAN_CENTER = 0;

	/**
	 * The offset to the parameter that holds the width.
	 */
	public static final int PARAM_GAUSSIAN_WIDTH = 1;

	/**
	 * The parameters.
	 */
	private double[] params;

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	/**
	 * Create a gaussian activation function.
	 * 
	 * @param center
	 *            The center of the curve.
	 * @param width
	 *            The width of the curve.
	 */
	public ActivationGaussian(final double center,
			final double width) {
		this.params = new double[2];
		this.params[ActivationGaussian.PARAM_GAUSSIAN_CENTER] = center;
		this.params[ActivationGaussian.PARAM_GAUSSIAN_WIDTH] = width;
	}

	public ActivationGaussian() {
		this.params = new double[2];
		this.params[0] = 0.1;
		this.params[0] = 0.1;
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationGaussian(this.getCenter(), 
				this.getWidth());
	}

	/**
	 * @return The width of the function.
	 */
	public final double getWidth() {
		return this.getParams()[ActivationGaussian.PARAM_GAUSSIAN_WIDTH];
	}

	/**
	 * @return The center of the function.
	 */
	public final double getCenter() {
		return this.getParams()[ActivationGaussian.PARAM_GAUSSIAN_CENTER];
	}

	/**
	 * @return Return true, gaussian has a derivative.
	 */
	public final boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {

		for (int i = start; i < start + size; i++) {

			double d = (x[i] - params[0]) * Math.sqrt(params[1]) * 4.0;
			x[i] = BoundMath.exp(-(d * d));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return -(Math.exp(-(Math.pow(
				((b - params[0]) * Math.sqrt(params[1]) * 4), 2))) * (2 * (Math
				.sqrt(params[1]) * 4 * ((b - params[0]) * Math.sqrt(params[1]) * 4))));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getParamNames() {
		final String[] result = { "center", "width" };
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getParams() {
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParam(final int index, final double value) {
		this.params[index] = value;

	}
}
