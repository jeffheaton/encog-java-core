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

package org.encog.normalize.input;

import org.encog.normalize.NormalizationError;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReferenceable;

/**
 * Provides basic functionality, such as min/max and current value
 * for other input fields.
 */
@EGReferenceable
public class BasicInputField implements InputField {

	/**
	 * The maximum value encountered so far for this field.
	 */
	@EGAttribute
	private double min = Double.POSITIVE_INFINITY;
	
	/**
	 * The minimum value encountered so far for this field.
	 */
	@EGAttribute
	private double max = Double.NEGATIVE_INFINITY;
	
	/**
	 * The current value for this field, only used while normalizing.
	 */
	@EGIgnore
	private double currentValue;

	/**
	 * True if this field is used to actually generate the input for
	 * the neural network.
	 */
	@EGAttribute
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
