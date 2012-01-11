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

import java.io.Serializable;

/**
 * A Normalization input field.  This field defines data that needs to be 
 * normalized.  There are many different types of normalization field that can
 * be used for many different purposes.
 * 
 * To assist in normalization each input file tracks the min and max values for
 * that field.
 */
public interface InputField extends Serializable {
	
	/**
	 * Update the min and max values for this field with the specified values.
	 * @param d The current value to use to update min and max.
	 */
	void applyMinMax(double d);

	/**
	 * @return The current value of the input field.  This is only valid, 
	 * while the normalization is being performed.
	 */
	double getCurrentValue();

	/**
	 * @return The maximum value for all of the input data, this is calculated
	 * during the first pass of normalization.
	 */
	double getMax();

	/**
	 * @return The minimum value for all of the input data, this is calculated
	 * during the first pass of normalization.
	 */
	double getMin();

	/**
	 * @return True, if this field is used for network input.  This is needed
	 * so that the buildForNetworkInput method of the normalization class knows
	 * how many input fields to expect.  For instance, fields used only to 
	 * segregate data are not used for the actual network input and may
	 * not be provided when the network is actually being queried.
	 */
	boolean getUsedForNetworkInput();

	/**
	 * Called for input field types that require an index to get the current
	 * value. This is used by the InputFieldArray1D and InputFieldArray2D
	 * classes.
	 * 
	 * @param i
	 *            The index to read.
	 * @return The value read.
	 */
	double getValue(int i);

	/**
	 * Set the current value of this field.  This value is only valid while
	 * the normalization is occurring.
	 * @param d The current value of this field.
	 */
	void setCurrentValue(double d);

	/**
	 * Set the current max value.
	 * @param max The maximum value encountered on this field so far.
	 */
	void setMax(double max);

	/**
	 * Set the current min value.
	 * @param min The minimum value encountered on this field so far.
	 */
	void setMin(double min);
}
