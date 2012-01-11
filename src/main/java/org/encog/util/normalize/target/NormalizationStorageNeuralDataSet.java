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

import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.normalize.DataNormalization;

/**
 * Store the normalized data to a neural data set.
 */
public class NormalizationStorageNeuralDataSet implements NormalizationStorage {

	/**
	 * The input count.
	 */
	private int inputCount;

	/**
	 * The ideal count.
	 */
	private int idealCount;

	/**
	 * The data set to add to.
	 */
	private MLDataSet dataset;

	
	public NormalizationStorageNeuralDataSet()
	{
		
	}
	
	/**
	 * Construct a new NeuralDataSet based on the parameters specified.
	 * 
	 * @param inputCount The input count.
 	 * @param idealCount The output count.
	 */
	public NormalizationStorageNeuralDataSet(final int inputCount,
			final int idealCount) {
		this.inputCount = inputCount;
		this.idealCount = idealCount;
		this.dataset = new BasicNeuralDataSet();
	}

	/**
	 * Construct a normalized neural storage class to hold data.
	 * 
	 * @param dataset
	 *            The data set to store to. This uses an existing data set.
	 */
	public NormalizationStorageNeuralDataSet(final MLDataSet dataset) {
		this.dataset = dataset;
		this.inputCount = this.dataset.getInputSize();
		this.idealCount = this.dataset.getIdealSize();
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

		if (this.idealCount == 0) {
			final BasicNeuralData inputData = new BasicNeuralData(data);
			this.dataset.add(inputData);
		} else {
			final BasicNeuralData inputData = new BasicNeuralData(
					this.inputCount);
			final BasicNeuralData idealData = new BasicNeuralData(
					this.idealCount);

			int index = 0;
			for (int i = 0; i < this.inputCount; i++) {
				inputData.setData(i, data[index++]);
			}

			for (int i = 0; i < this.idealCount; i++) {
				idealData.setData(i, data[index++]);
			}

			this.dataset.add(inputData, idealData);
		}

	}

	/**
	 * @return The dataset used.
	 */
	public MLDataSet getDataset() {
		return dataset;
	}
	
	

}
