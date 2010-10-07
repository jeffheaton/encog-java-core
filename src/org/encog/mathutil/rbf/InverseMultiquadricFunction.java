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
import org.encog.engine.util.BoundMath;

/**
 * Implements an Inverse Multiquadric Function.
 *
 */
public class InverseMultiquadricFunction implements RadialBasisFunction {

	/**
	 * The center of the RBF.
	 */
	private double center;

	/**
	 * The peak of the RBF.
	 */
	private double peak;

	/**
	 * The width of the RBF.
	 */
	private double width;

	/**
	 * Construct an Inverse Multiquadric RBF with the specified center, peak
	 * and width.
	 * @param center The center.
	 * @param peak The peak.
	 * @param width The width.
	 */
	public InverseMultiquadricFunction(final double center, final double peak,
			final double width) {
		this.center = center;
		this.peak = peak;
		this.width = width;
	}

	/**
	 * {@inheritDoc}
	 */
	public double calculate(final double x) {
		return this.peak
				/ BoundMath.sqrt(BoundMath.pow(x - this.center, 2)
						+ (this.width * this.width));
	}

	/**
	 * {@inheritDoc}
	 */
	public double calculateDerivative(final double x) {
		return calculateFirstDerivative(x);
	}

	// / <summary>
	// / 
	// / </summary>
	// / <param name="x"></param>
	// / <returns></returns>
	
	/**
	 * Calculate the value of the second derivative of the Inverse
	 * Multiquadric function for the specified value.
	 * @param x The value to calculate the derivative Inverse
	 * Multiquadric function for.
	 * @return The return value for the derivative of the Inverse Multiquadric
	 * function.
	 */
	public double calculateFirstDerivative(final double x) {
		return -1
				* this.peak
				* (x - this.center)
				/ BoundMath.pow(BoundMath.pow(x - this.center, 2)
						+ (this.width * this.width), 1.5);
	}

	/**
	 * Calculate the value of the second derivative of the Inverse
	 * Multiquadric function for the specified value.
	 * @param x The value to calculate the derivative Inverse
	 * Multiquadric function for.
	 * @return The return value for the derivative of the Inverse
	 * Multiquadric function.
	 */
	public double calculateSecondDerivative(final double x) {
		return this.peak
				* (this.width * this.width + 2 * BoundMath.pow(x - this.center,
						2))
				/ BoundMath.pow(BoundMath.pow(x - this.center, 2) + this.width
						* this.width, 2.5);
	}

	/**
	 * {@inheritDoc}
	 */
	public double getCenter() {
		return this.center;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getPeak() {
		return this.peak;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCenter(final double center) {
		this.center = center;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPeak(final double peak) {
		this.peak = peak;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setWidth(final double width) {
		this.width = width;
	}

}
