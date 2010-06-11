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
 * An input field that comes from a 2D array. The first dimension
 * of the array will be used to read each successive row.  The second
 * dimension is fixed, and specified in the constructor.  You would create
 * multiple InputFieldArray2D object to read each of the "columns" stored
 * at each row.
 * 
 * Note: this input field will not be persisted to an EG file.
 * This is because it could point to a lengthy array, that really
 * has no meaning inside of an EG file.  
 *
 */
@EGUnsupported
public class InputFieldArray2D extends BasicInputField implements
		HasFixedLength {
	
	/**
	 * The 2D array to use.
	 */
	private final double[][] array;
	
	/**
	 * The 2nd dimension index to read the field from.
	 */
	private final int index2;

	/**
	 * Construct a 2D array input field.
	 * @param usedForNetworkInput Is this field used for neural network input?
	 * @param array The array to use.
	 * @param index2 The secondary index to read the field from.
	 */
	public InputFieldArray2D(final boolean usedForNetworkInput,
			final double[][] array, final int index2) {
		this.array = array;
		this.index2 = index2;
		setUsedForNetworkInput(usedForNetworkInput);
	}

	/**
	 * Read a value from the specified index.
	 * @param i The index.
	 * @return The value read.
	 */
	@Override
	public double getValue(final int i) {
		return this.array[i][this.index2];
	}

	/**
	 * @return The number of rows in the array.
	 */
	public int length() {
		return this.array.length;
	}
}
