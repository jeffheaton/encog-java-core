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

package org.encog.neural.activation;

import org.encog.engine.network.flat.ActivationFunctions;

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
		this(1, 0, 1, 0);
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

	/**
	 * Set the threshold low.
	 * @param d The threshold low.
	 */
	public void setThresholdLow(final double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_LOW_THRESHOLD, d);
	}

	/**
	 * Set the threshold high.
	 * @param d The threshold high.
	 */
	public void setThresholdHigh(final double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_HIGH_THRESHOLD, d);
	}

	/**
	 * Set the low value.
	 * @param d The low value.
	 */
	public void setLow(final double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_LOW, d);
	}

	/**
	 * Set the high value.
	 * @param d The high value.
	 */
	public void setHigh(final double d) {
		this.setParam(ActivationFunctions.PARAM_RAMP_HIGH, d);

	}

}
