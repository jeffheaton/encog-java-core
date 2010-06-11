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

package org.encog.normalize.input;

import org.encog.persist.annotations.EGUnsupported;

/**
 * An input field that comes from a 1D array.
 * 
 * Note: this input field will not be persisted to an EG file.
 * This is because it could point to a lengthy array, that really
 * has no meaning inside of an EG file.  
 *
 */
@EGUnsupported
public class InputFieldArray1D extends BasicInputField implements
		HasFixedLength {
	
	/**
	 * A reference to the array.
	 */
	private final double[] array;

	/**
	 * Construct the 1D array field.
	 * @param usedForNetworkInput True if this field is used for the actual
	 * input to the neural network.  See getUsedForNetworkInput for more info.
	 * @param array The array to use.
	 */
	public InputFieldArray1D(final boolean usedForNetworkInput,
			final double[] array) {
		this.array = array;
		setUsedForNetworkInput(usedForNetworkInput);
	}

	/**
	 * Get the value from the specified index.
	 * @param i The index to retrieve.
	 * @return The value at the specified index.
	 */
	public double getValue(final int i) {
		return this.array[i];
	}

	/**
	 * @return The length of the array.
	 */
	public int length() {
		return this.array.length;
	}

}
