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

import org.encog.neural.data.NeuralDataSet;

/**
 * An input field based on an Encog NeuralDataSet.
 *
 */
public class InputFieldMLDataSet extends BasicInputField {

	/**
	 * The data set.
	 */
	private final NeuralDataSet data;
	
	/**
	 * The input or ideal index.  This treats the input and ideal as one
	 * long array, concatenated together.
	 */
	private final int offset;

	/**
	 * Construct a input field based on a NeuralDataSet.
	 * @param usedForNetworkInput Is this field used for neural input.
	 * @param data The data set to use.
	 * @param offset The input or ideal index to use. This treats the input 
	 * and ideal as one long array, concatenated together.
	 */
	public InputFieldMLDataSet(final boolean usedForNetworkInput,
			final NeuralDataSet data, final int offset) {
		this.data = data;
		this.offset = offset;
		setUsedForNetworkInput(usedForNetworkInput);
	}

	/**
	 * @return The neural data set to read.
	 */
	public NeuralDataSet getNeuralDataSet() {
		return this.data;
	}

	/**
	 * @return The field to be accessed. This treats the input and 
	 * ideal as one long array, concatenated together.
	 */
	public int getOffset() {
		return this.offset;
	}

}
