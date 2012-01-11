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
package org.encog.util.normalize.target;

import org.encog.util.normalize.DataNormalization;

/**
 * Output the normalized data to a 2D array.
 */
public class NormalizationStorageArray2D implements NormalizationStorage {

	/**
	 * The array to output to.
	 */
	private double[][] array;
	
	/**
	 * The current data.
	 */	
	private int currentIndex;

	/**
	 * Construct an object to store to a 2D array.
	 * @param array The array to store to.
	 */
	public NormalizationStorageArray2D(final double[][] array) {
		this.array = array;
		this.currentIndex = 0;
	}
	
	public NormalizationStorageArray2D()
	{
		
	}

	/**
	 * Not needed for this storage type.
	 */
	public void close() {

	}

	/**
	 * Not needed for this storage type.
	 */
	public void open(DataNormalization norm) {

	}

	/**
	 * Write an array.
	 * 
	 * @param data
	 *            The data to write.
	 * @param inputCount
	 *            How much of the data is input.
	 */
	public void write(final double[] data, final int inputCount) {
		for (int i = 0; i < data.length; i++) {
			this.array[this.currentIndex][i] = data[i];
		}
		this.currentIndex++;
	}

	public double[][] getArray() {
		return this.array;
	}

}
