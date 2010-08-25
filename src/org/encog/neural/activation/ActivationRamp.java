/*
 * Encog(tm) Core v2.5 
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

import org.encog.engine.util.EngineArray;


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

	public static final int PARAM_HIGH_THRESHOLD = 0;
	public static final int PARAM_LOW_THRESHOLD = 1;
	public static final int PARAM_HIGH = 2;
	public static final int PARAM_LOW = 3;
	
	public static final String[] PARAM_NAMES = {
	"thresholdHigh","thresholdLow", "high", "low" };	


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
		
		this.params = new double[4];
		this.params[PARAM_HIGH_THRESHOLD] = thresholdHigh;
		this.params[PARAM_LOW_THRESHOLD] = thresholdLow;
		this.params[PARAM_HIGH] = high;
		this.params[PARAM_LOW] = low;		
	}

	/**
	 * Default constructor.
	 */
	public ActivationRamp() {
		this(1,0,1,0);
	}

	/**
	 * Calculate the ramp value.
	 * 
	 * @param d
	 *            The array of values to calculate for.
	 */
	public void activationFunction(final double[] d) {

		double slope = (this.params[PARAM_HIGH_THRESHOLD] - this.params[PARAM_LOW_THRESHOLD])/
			(this.params[PARAM_HIGH] - this.params[PARAM_LOW]);
		
		for (int i = 0; i < d.length; i++) {
			if (d[i] < this.params[PARAM_LOW_THRESHOLD]) {
				d[i] = this.params[PARAM_LOW];
			} else if (d[i] > this.params[PARAM_HIGH_THRESHOLD]) {
				d[i] = this.params[PARAM_HIGH];
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
		return new ActivationRamp( 
				this.params[PARAM_HIGH_THRESHOLD],
				this.params[PARAM_LOW_THRESHOLD], 
				this.params[PARAM_HIGH], 
				this.params[PARAM_LOW]);
	}

	/**
	 * Calculate the derivative of this function. This will always be 1, as it
	 * is a linear function.
	 * 
	 * @param d
	 *            The array of values to calculate over.
	 */
	public void derivativeFunction(final double[] d) {
		EngineArray.fill(d, 1.0);
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return this.params[PARAM_HIGH];
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return this.params[PARAM_LOW];
	}

	/**
	 * @return the thresholdHigh
	 */
	public double getThresholdHigh() {
		return this.params[PARAM_HIGH_THRESHOLD];
	}

	/**
	 * @return the thresholdLow
	 */
	public double getThresholdLow() {
		return this.params[PARAM_LOW_THRESHOLD];
	}

	/**
	 * @return True, as this function does have a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}

	
	/**
	 * @return The paramater names for this activation function.
	 */
	@Override
	public String[] getParamNames() {
		return PARAM_NAMES;
	}

}
