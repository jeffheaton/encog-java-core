/**
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.normalize.input;

/**
 * A Normalization input field.  This field defines data that needs to be 
 * normalized.  There are many different types of normalization field that can
 * be used for many different purposes.
 * 
 * To assist in normalization each input file tracks the min and max values for
 * that field.
 */
public interface InputField {
	
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
	 * Called for input field types that require an index to get the current value.
	 * This is used by the InputFieldArray1D and InputFieldArray2D classes.
	 * @param i The index to read.
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
