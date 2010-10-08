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
 * Multi-dimensional Mexican Hat, or Ricker wavelet, function.
 * 
 * It is usually only referred to as the "Mexican hat" in the Americas, due to
 * cultural association with the "sombrero". In technical nomenclature this function
 * is known as the Ricker wavelet, where it is frequently employed to model
 * seismic data.
 * 
 * http://en.wikipedia.org/wiki/Mexican_Hat_Function
 * 
 */
public class MexicanHatFunction implements RadialBasisFunction {

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
	public MexicanHatFunction(int dimensions)
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
	public MexicanHatFunction(final double peak, final double[] center,
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
	public MexicanHatFunction(final double center, final double peak,
			final double width) {
		this.center = new double[1];
		this.center[0] = center;
		this.peak = peak;
		this.width = width;
	}


	/**
	 * Calculate the result from the function.
	 * 
	 * @param x
	 *            The parameters for the function, one for each dimension.
	 * @return The result of the function.
	 */
	public double calculate(final double[] x) {
		
		// calculate the "norm", but don't take square root
		// don't square because we are just going to square it
		double norm = 0;
		for(int i=0;i<center.length;i++)
		{
			norm+=Math.pow(x[i]-center[i],2);
		}

		// calculate the value
		
		return this.peak * (1-norm)*Math.exp(-norm/2);
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
