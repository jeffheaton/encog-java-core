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
public class GaussianFunction extends BasicRBF {

	/**
	 * Create centered at zero, width 0, and peak 0.
	 */
	public GaussianFunction(int dimensions)
	{
		this.setCenters(new double[dimensions]);
		this.setPeak(1.0);
		this.setWidth(1.0);		
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
		this.setCenters( center );
		this.setPeak( peak );
		this.setWidth( width );
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
		this.setCenters(new double[1]);
		this.getCenters()[0] = center;
		this.setPeak(peak);
		this.setWidth(width);
	}


	/**
	 * Calculate the result from the function.
	 * 
	 * @param x
	 *            The parameters for the function, one for each dimension.
	 * @return The result of the function.
	 */
	public double calculate(final double[] x) {
		double value = 0;
		double[] center = getCenters();
		double width = getWidth();

		for (int i = 0; i < center.length; i++) {
			value += Math.pow(x[i] - center[i], 2)
					/ (2.0 * width * width);
		}
		return this.getPeak() * Math.exp(-value);
	}


}
