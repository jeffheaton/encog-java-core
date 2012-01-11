/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.engine.network.activation;

/**
 * A ramp activation function. This function has a high and low threshold. If
 * the high threshold is exceeded a fixed value is returned. Likewise, if the
 * low value is exceeded another fixed value is returned.
 * 
 */
public class ActivationRamp implements ActivationFunction {

	/**
	 * The ramp high threshold parameter.
	 */
	public static final int PARAM_RAMP_HIGH_THRESHOLD = 0;

	/**
	 * The ramp low threshold parameter.
	 */
	public static final int PARAM_RAMP_LOW_THRESHOLD = 1;

	/**
	 * The ramp high parameter.
	 */
	public static final int PARAM_RAMP_HIGH = 2;

	/**
	 * The ramp low parameter.
	 */
	public static final int PARAM_RAMP_LOW = 3;

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 6336245112244386279L;

	/**
	 * The parameters.
	 */
	private final double[] params;

	/**
	 * Default constructor.
	 */
	public ActivationRamp() {
		this(1, 0, 1, 0);
	}

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
		this.params[ActivationRamp.PARAM_RAMP_HIGH_THRESHOLD] = thresholdHigh;
		this.params[ActivationRamp.PARAM_RAMP_LOW_THRESHOLD] = thresholdLow;
		this.params[ActivationRamp.PARAM_RAMP_HIGH] = high;
		this.params[ActivationRamp.PARAM_RAMP_LOW] = low;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		final double slope = (this.params[ActivationRamp.PARAM_RAMP_HIGH_THRESHOLD] - this.params[ActivationRamp.PARAM_RAMP_LOW_THRESHOLD])
				/ (this.params[ActivationRamp.PARAM_RAMP_HIGH] - this.params[ActivationRamp.PARAM_RAMP_LOW]);

		for (int i = start; i < start + size; i++) {
			if (x[i] < this.params[ActivationRamp.PARAM_RAMP_LOW_THRESHOLD]) {
				x[i] = this.params[ActivationRamp.PARAM_RAMP_LOW];
			} else if (x[i] > this.params[ActivationRamp.PARAM_RAMP_HIGH_THRESHOLD]) {
				x[i] = this.params[ActivationRamp.PARAM_RAMP_HIGH];
			} else {
				x[i] = (slope * x[i]);
			}
		}

	}

	/**
	 * Clone the object.
	 * 
	 * @return The cloned object.
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationRamp(
				this.params[ActivationRamp.PARAM_RAMP_HIGH_THRESHOLD],
				this.params[ActivationRamp.PARAM_RAMP_LOW_THRESHOLD],
				this.params[ActivationRamp.PARAM_RAMP_HIGH],
				this.params[ActivationRamp.PARAM_RAMP_LOW]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return 1.0;
	}

	/**
	 * @return the high
	 */
	public final double getHigh() {
		return this.params[ActivationRamp.PARAM_RAMP_HIGH];
	}

	/**
	 * @return the low
	 */
	public final double getLow() {
		return this.params[ActivationRamp.PARAM_RAMP_LOW];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getParamNames() {
		final String[] result = { "thresholdHigh", "thresholdLow", "high",
				"low" };
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getParams() {
		return this.params;
	}

	/**
	 * @return the thresholdHigh
	 */
	public final double getThresholdHigh() {
		return this.params[ActivationRamp.PARAM_RAMP_HIGH_THRESHOLD];
	}

	/**
	 * @return the thresholdLow
	 */
	public final double getThresholdLow() {
		return this.params[ActivationRamp.PARAM_RAMP_LOW_THRESHOLD];
	}

	/**
	 * @return True, as this function does have a derivative.
	 */
	@Override
	public final boolean hasDerivative() {
		return true;
	}

	/**
	 * Set the high value.
	 * 
	 * @param d
	 *            The high value.
	 */
	public final void setHigh(final double d) {
		setParam(ActivationRamp.PARAM_RAMP_HIGH, d);

	}

	/**
	 * Set the low value.
	 * 
	 * @param d
	 *            The low value.
	 */
	public final void setLow(final double d) {
		setParam(ActivationRamp.PARAM_RAMP_LOW, d);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParam(final int index, final double value) {
		this.params[index] = value;
	}

	/**
	 * Set the threshold high.
	 * 
	 * @param d
	 *            The threshold high.
	 */
	public final void setThresholdHigh(final double d) {
		setParam(ActivationRamp.PARAM_RAMP_HIGH_THRESHOLD, d);
	}

	/**
	 * Set the threshold low.
	 * 
	 * @param d
	 *            The threshold low.
	 */
	public final void setThresholdLow(final double d) {
		setParam(ActivationRamp.PARAM_RAMP_LOW_THRESHOLD, d);
	}

}
