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
 * An input field that comes from a 1D array.
 * 
 * Note: this input field will not be persisted to an EG file.
 * This is because it could point to a lengthy array, that really
 * has no meaning inside of an EG file.  
 *
 */
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
