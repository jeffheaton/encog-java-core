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

package org.encog.neural.activation;

import org.encog.util.EncogArray;

/**
 * A ramp activation function. This function has a high and low threshold. If
 * the high threshold is exceeded a fixed value is returned. Likewise, if the
 * low value is exceeded another fixed value is returned.
 * 
 */
public class ActivationRamp extends BasicActivationFunction {

	/**
	 * The serial ID. 
	 */
	private static final long serialVersionUID = 6336245112244386279L;


	/**
	 * The high threshold.
	 */
	private double thresholdHigh = 1;

	/**
	 * The low threshold.
	 */
	private double thresholdLow = 0;

	/**
	 * The high value that will be used if the high threshold is exceeded.
	 */
	private double high = 1;

	/**
	 * The low value that will be used if the low threshold is exceeded.
	 */
	private double low = 0;

	/**
	 * Construct a ramp activation function.
	 * 
	 * @param thresholdHigh
	 *            The high threshold value.
	 * @param thresholdLow
	 *            The low threshold value.
	 * @param high
	 *            The high value, replaced if the high threshold is exceeded.
	 * @param low
	 *            The low value, replaced if the low threshold is exceeded.
	 */
	public ActivationRamp(final double thresholdHigh,
			final double thresholdLow, final double high, final double low) {
		this.thresholdHigh = thresholdHigh;
		this.thresholdLow = thresholdLow;
		this.high = high;
		this.low = low;
	}

	/**
	 * Default constructor.
	 */
	public ActivationRamp() {
	}

	/**
	 * Calculate the ramp value.
	 * 
	 * @param d
	 *            The array of values to calculate for.
	 */
	public void activationFunction(final double[] d) {

		double slope = (this.thresholdHigh - this.thresholdLow)/
			(this.high - this.low);
		
		for (int i = 0; i < d.length; i++) {
			if (d[i] < this.thresholdLow) {
				d[i] = this.low;
			} else if (d[i] > this.thresholdHigh) {
				d[i] = this.high;
			} else {
				d[i] = (slope * d[i]);
			}
		}

	}

	/**
	 * Clone the object.
	 * 
	 * @return The cloned object.
	 */
	@Override
	public Object clone() {
		return new ActivationRamp( this.thresholdHigh,
				this.thresholdLow, this.high, this.low);
	}

	/**
	 * Calculate the derivative of this function. This will always be 1, as it
	 * is a linear function.
	 * 
	 * @param d
	 *            The array of values to calculate over.
	 */
	public void derivativeFunction(final double[] d) {
		EncogArray.fill(d, 1.0);
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return the thresholdHigh
	 */
	public double getThresholdHigh() {
		return this.thresholdHigh;
	}

	/**
	 * @return the thresholdLow
	 */
	public double getThresholdLow() {
		return this.thresholdLow;
	}

	/**
	 * @return True, as this function does have a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * @param high
	 *            the high to set
	 */
	public void setHigh(final double high) {
		this.high = high;
	}

	/**
	 * @param low
	 *            the low to set
	 */
	public void setLow(final double low) {
		this.low = low;
	}

	/**
	 * @param thresholdHigh
	 *            the thresholdHigh to set
	 */
	public void setThresholdHigh(final double thresholdHigh) {
		this.thresholdHigh = thresholdHigh;
	}

	/**
	 * @param thresholdLow
	 *            the thresholdLow to set
	 */
	public void setThresholdLow(final double thresholdLow) {
		this.thresholdLow = thresholdLow;
	}

}
