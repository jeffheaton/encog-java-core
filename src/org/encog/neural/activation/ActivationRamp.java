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

import org.encog.engine.network.flat.ActivationFunctions;
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
		this.params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD] = thresholdHigh;
		this.params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD] = thresholdLow;
		this.params[ActivationFunctions.PARAM_RAMP_HIGH] = high;
		this.params[ActivationFunctions.PARAM_RAMP_LOW] = low;		
	}

	/**
	 * Default constructor.
	 */
	public ActivationRamp() {
		this(1,0,1,0);
	}

	/**
	 * Clone the object.
	 * 
	 * @return The cloned object.
	 */
	@Override
	public Object clone() {
		return new ActivationRamp( 
				this.params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD],
				this.params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD], 
				this.params[ActivationFunctions.PARAM_RAMP_HIGH], 
				this.params[ActivationFunctions.PARAM_RAMP_LOW]);
	}

	/**
	 * Calculate the derivative of this function. This will always be 1, as it
	 * is a linear function.
	 * 
	 * @param d
	 *            The array of values to calculate over.
	 */
	public double derivativeFunction(final double d) {
		return 1;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return this.params[ActivationFunctions.PARAM_RAMP_HIGH];
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return this.params[ActivationFunctions.PARAM_RAMP_LOW];
	}

	/**
	 * @return the thresholdHigh
	 */
	public double getThresholdHigh() {
		return this.params[ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD];
	}

	/**
	 * @return the thresholdLow
	 */
	public double getThresholdLow() {
		return this.params[ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD];
	}

	/**
	 * @return True, as this function does have a derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}
	
	/**
	 * @return The Encog Engine ID for this activation type, or -1 if not
	 *         defined by the Encog engine.
	 */
	@Override
	public int getEngineID() {
		// TODO Auto-generated method stub
		return ActivationFunctions.ACTIVATION_RAMP;
	}

	public void setThresholdLow(double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD, d);
	}

	public void setThresholdHigh(double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD, d);
	}

	public void setLow(double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_LOW, d);
	}

	public void setHigh(double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_HIGH, d);
		
	}

}
