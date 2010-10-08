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

package org.encog.mathutil.rbf;

import org.encog.engine.network.rbf.RadialBasisFunction;

/**
 * Multi-dimensional gaussian function. Do not use this to implement a 1d
 * function, simply use GaussianFunction for that.
 * 
 */
public class GaussianFunction implements RadialBasisFunction {

	/**
	 * The center of the RBF.
	 */
	private final double[] center;

	/**
	 * The peak of the RBF.
	 */
	private double peak;

	/**
	 * The width of the RBF.
	 */
	private double width;

	/**
	 * Create centered at zero, width 0, and peak 0.
	 */
	public GaussianFunction(int dimensions)
	{
		this.center = new double[dimensions];
		this.peak = 1.0;
		this.width = 1.0;		
	}
	
	/**
	 * Construct a multi-dimension Gaussian function with the specified peak,
	 * centers and widths.
	 * 
	 * @param peak
	 *            The peak for all dimensions.
	 * @param center
	 *            The centers for each dimension.
	 * @param width
	 *            The widths for each dimension.
	 */
	public GaussianFunction(final double peak, final double[] center,
			final double width) {
		this.center = center;
		this.peak = peak;
		this.width = width;
	}
	
	/**
	 * Construct a single-dimension Gaussian function with the specified peak,
	 * centers and widths.
	 * 
	 * @param peak
	 *            The peak for all dimensions.
	 * @param center
	 *            The centers for each dimension.
	 * @param width
	 *            The widths for each dimension.
	 */
	public GaussianFunction(final double center, final double peak,
			final double width) {
		this.center = new double[1];
		this.center[0] = center;
		this.peak = peak;
		this.width = width;
	}


	/**
	 * Calculate thre result from the function.
	 * 
	 * @param x
	 *            The parameters for the function, one for each dimension.
	 * @return The result of the function.
	 */
	public double calculate(final double[] x) {
		double value = 0;

		for (int i = 0; i < this.center.length; i++) {
			value += Math.pow(x[i] - this.center[i], 2)
					/ (2.0 * this.width * this.width);
		}
		return this.peak * Math.exp(-value);
	}

	/**
	 * Get the center for the specified dimension.
	 * 
	 * @param dimension
	 *            The dimension.
	 * @return The center.
	 */
	public double getCenter(final int dimension) {
		return this.center[dimension];
	}

	/**
	 * @return The number of dimensions.
	 */
	public int getDimensions() {
		return this.center.length;
	}

	/**
	 * @return The peak.
	 */
	public double getPeak() {
		return this.peak;
	}


	public double getWidth() {
		return this.width;
	}

	/**
	 * Set the width for all dimensions.
	 * 
	 * @param w
	 *            The width.
	 */
	public void setWidth(final double w) {
		this.width = w;
	}

	/**
	 * @return The centers.
	 */
	@Override
	public double[] getCenters() {
		return this.center;
	}

}
