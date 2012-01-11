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
