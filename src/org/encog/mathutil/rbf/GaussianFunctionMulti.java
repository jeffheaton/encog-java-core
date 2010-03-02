/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.mathutil.rbf;


/**
 * Multi-dimensional gaussian function. Do not use this to implement a 1d
 * function, simply use GaussianFunction for that.
 * 
 */
public class GaussianFunctionMulti implements RadialBasisFunctionMulti {

	/**
	 * The center of the RBF.
	 */
	private final double[] center;

	/**
	 * The peak of the RBF.
	 */
	private final double peak;

	/**
	 * The width of the RBF.
	 */
	private final double[] width;

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
	public GaussianFunctionMulti(final double peak, final double[] center,
			final double[] width) {
		this.center = center;
		this.peak = peak;
		this.width = width;
	}

	/**
	 * Construct a Gaussian function with the specified number of dimensions.
	 * The peak, center and widths are all the same.
	 * 
	 * @param dimensions The number of dimensions.
	 * @param peak The peak used for all dimensions.
	 * @param center The center used for all dimensions.
	 * @param width The widths used for all dimensions.
	 */
	public GaussianFunctionMulti(final int dimensions, final double peak,
			final double center, final double width) {
		this.peak = peak;
		this.center = new double[dimensions];
		this.width = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			this.center[i] = center;
			this.width[i] = width;
		}
	}

	/**
	 * Calculate thre result from the function.
	 * @param x The parameters for the function, one for each dimension.
	 * @return The result of the function.
	 */
	public double calculate(final double[] x) {
		double value = 0;

		for (int i = 0; i < this.center.length; i++) {
			value += Math.pow(x[i] - this.center[i], 2)
					/ (2.0 * this.width[i] * this.width[i]);
		}
		return this.peak * Math.exp(-value);
	}

	/**
	 * Get the center for the specified dimension.
	 * @param dimension The dimension.
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

	/**
	 * Get the width for one dimension.
	 * @param dimension The dimension.
	 * @return The width.
	 */
	public double getWidth(final int dimension) {
		return this.width[dimension];
	}

	/**
	 * Set the width for all dimensions.
	 * @param w The width.
	 */
	public void setWidth(final double w) {
		for (int i = 0; i < this.width.length; i++) {
			this.width[i] = w;
		}

	}

}
