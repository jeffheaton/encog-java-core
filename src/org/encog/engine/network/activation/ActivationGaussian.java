/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
 * An activation function based on the gaussian function.
 * 
 * @author jheaton
 * 
 */
public class ActivationGaussian implements ActivationFunction {

	/**
	 * The offset to the parameter that holds the width.
	 */
	public static final int PARAM_GAUSSIAN_CENTER = 0;

	/**
	 * The offset to the parameter that holds the peak.
	 */
	public static final int PARAM_GAUSSIAN_PEAK = 1;

	/**
	 * The offset to the parameter that holds the width.
	 */
	public static final int PARAM_GAUSSIAN_WIDTH = 2;

	
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
	 * @param peak
	 *            The peak of the curve.
	 * @param width
	 *            The width of the curve.
	 */
	public ActivationGaussian(final double center, final double peak,
			final double width) {
		this.params = new double[3];
		this.params[ActivationGaussian.PARAM_GAUSSIAN_CENTER] = center;
		this.params[ActivationGaussian.PARAM_GAUSSIAN_PEAK] = peak;
		this.params[ActivationGaussian.PARAM_GAUSSIAN_WIDTH] = width;
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public ActivationFunction clone() {
		return new ActivationGaussian(this.getCenter(), this.getPeak(), this
				.getWidth());
	}

	/**
	 * @return The width of the function.
	 */
	private double getWidth() {
		return this.getParams()[ActivationGaussian.PARAM_GAUSSIAN_WIDTH];
	}

	/**
	 * @return The center of the function.
	 */
	private double getCenter() {
		return this.getParams()[ActivationGaussian.PARAM_GAUSSIAN_CENTER];
	}

	/**
	 * @return The peak of the function.
	 */
	private double getPeak() {
		return this.getParams()[ActivationGaussian.PARAM_GAUSSIAN_PEAK];
	}

	/**
	 * @return Return true, gaussian has a derivative.
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
		for (int i = start; i < start+size ; i++) {
			x[i] = params[ActivationGaussian.PARAM_GAUSSIAN_PEAK]
			     * BoundMath.exp(-Math.pow(x[i] 
                    - params[ActivationGaussian.PARAM_GAUSSIAN_CENTER],2)
                    / (2.0 * params[ActivationGaussian.PARAM_GAUSSIAN_WIDTH] * params[ActivationGaussian.PARAM_GAUSSIAN_WIDTH]));
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double x) {
		final double width = params[ActivationGaussian.PARAM_GAUSSIAN_WIDTH];
		final double peak = params[ActivationGaussian.PARAM_GAUSSIAN_PEAK];
		return Math.exp(-0.5 * width * width * x * x) * peak * width * width
				* (width * width * x * x - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParamNames() {
		final String[] result = { "center", "peak", "width" };
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] getParams() {
		return params;
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
