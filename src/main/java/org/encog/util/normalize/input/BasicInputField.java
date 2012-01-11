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
package org.encog.util.normalize.input;

import org.encog.util.normalize.NormalizationError;

/**
 * Provides basic functionality, such as min/max and current value
 * for other input fields.
 */
public class BasicInputField implements InputField {

	/**
	 * The maximum value encountered so far for this field.
	 */
	private double min = Double.POSITIVE_INFINITY;
	
	/**
	 * The minimum value encountered so far for this field.
	 */
	private double max = Double.NEGATIVE_INFINITY;
	
	/**
	 * The current value for this field, only used while normalizing.
	 */
	private double currentValue;

	/**
	 * True if this field is used to actually generate the input for
	 * the neural network.
	 */
	private boolean usedForNetworkInput = true;

	/**
	 * Given the current value, apply to the min and max values.
	 * @param d THe current value.
	 */
	public void applyMinMax(final double d) {
		this.min = Math.min(this.min, d);
		this.max = Math.max(this.max, d);

	}

	/**
	 * @return The current value of the input field.  This is only valid, 
	 * while the normalization is being performed.
	 */
	public double getCurrentValue() {
		return this.currentValue;
	}

	/**
	 * @return The maximum value for all of the input data, this is calculated
	 * during the first pass of normalization.
	 */
	public double getMax() {
		return this.max;
	}

	/**
	 * @return The minimum value for all of the input data, this is calculated
	 * during the first pass of normalization.
	 */	
	public double getMin() {
		return this.min;
	}


	/**
	 * @return True, if this field is used for network input.  This is needed
	 * so that the buildForNetworkInput method of the normalization class knows
	 * how many input fields to expect.  For instance, fields used only to 
	 * segregate data are not used for the actual network input and may
	 * not be provided when the network is actually being queried.
	 */
	public boolean getUsedForNetworkInput() {
		return this.usedForNetworkInput;
	}

	/**
	 * Not supported for this sort of class, may be implemented in subclasses.
	 * Will throw an exception.
	 * @param i The index.  Not used.
	 * @return The value at the specified index.
	 */
	public double getValue(final int i) {
		throw new NormalizationError("Can't call getValue on "
				+ this.getClass().getSimpleName());
	}

	/**
	 * Set the current value of this field.  This value is only valid while
	 * the normalization is occurring.
	 * @param currentValue The current value of this field.
	 */
	public void setCurrentValue(final double currentValue) {
		this.currentValue = currentValue;
	}


	/**
	 * Set the current max value.
	 * @param max The maximum value encountered on this field so far.
	 */
	public void setMax(final double max) {
		this.max = max;
	}

	/**
	 * Set the current min value.
	 * @param min The minimum value encountered on this field so far.
	 */
	public void setMin(final double min) {
		this.min = min;
	}


	/**
	 * This is needed so that the buildForNetworkInput method of the
	 * normalization class knows how many input fields to expect. For instance,
	 * fields used only to segregate data are not used for the actual network
	 * input and may not be provided when the network is actually being queried.
	 * @param usedForNetworkInput True, if this field is used for network input.
	 */
	public void setUsedForNetworkInput(final boolean usedForNetworkInput) {
		this.usedForNetworkInput = usedForNetworkInput;
	}

}
