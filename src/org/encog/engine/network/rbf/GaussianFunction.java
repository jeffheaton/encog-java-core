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

package org.encog.engine.network.rbf;

import java.io.Serializable;

import org.encog.engine.util.BoundMath;

/**
 * Implements a radial function based on the gaussian function.
 * 
 * @author jheaton
 * 
 */
public class GaussianFunction implements RadialBasisFunction, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 548203092442332198L;

	/**
	 * The center of the RBF.
	 */
	private final double center;

	/**
	 * The peak of the RBF.
	 */
	private final double peak;

	/**
	 * The width of the RBF.
	 */
	private double width;

	/**
	 * Construct a Gaussian RBF with the specified center, peak and width.
	 * 
	 * @param center
	 *            The center.
	 * @param peak
	 *            The peak.
	 * @param width
	 *            The width.
	 */
	public GaussianFunction(final double center, final double peak,
			final double width) {
		this.center = center;
		this.peak = peak;
		this.width = width;
	}

	/**
	 * Calculate the value of the Gaussian function for the specified value.
	 * 
	 * @param x
	 *            The value to calculate the Gaussian function for.
	 * @return The return value for the Gaussian function.
	 */
	public double calculate(final double x) {
		return this.peak
				* BoundMath.exp(-Math.pow(x - this.center, 2)
						/ (2.0 * this.width * this.width));
	}

	/**
	 * Calculate the value of the derivative of the Gaussian function for the
	 * specified value.
	 * 
	 * @param x
	 *            The value to calculate the derivative Gaussian function for.
	 * @return The return value for the derivative of the Gaussian function.
	 */
	public double calculateDerivative(final double x) {
		return Math.exp(-0.5 * this.width * this.width * x * x) * this.peak
				* this.width * this.width
				* (this.width * this.width * x * x - 1);
	}

	/**
	 * @return The center of the RBF.
	 */
	public double getCenter() {
		return this.center;
	}

	/**
	 * @return The peak of the RBF.
	 */
	public double getPeak() {
		return this.peak;
	}

	/**
	 * @return The width of the RBF.
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * Set the width of the function.
	 * 
	 * @param radius
	 *            The width.
	 */
	public void setWidth(final double radius) {
		this.width = radius;
	}

}
