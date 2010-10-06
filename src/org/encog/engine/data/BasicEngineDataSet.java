/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.engine.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data is stored in an ArrayList. This class is memory based, so large enough
 * datasets could cause memory issues. Many other dataset types extend this
 * class.
 * 
 * @author jheaton
 */
public class BasicEngineDataSet implements Serializable, EngineIndexableSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -2279722928570071183L;

	/**
	 * The data held by this object.
	 */
	private List<EngineData> data = new ArrayList<EngineData>();

	/**
	 * Default constructor.
	 */
	public BasicEngineDataSet() {
	}

	/**
	 * Construct a data set from an input and idea array.
	 * 
	 * @param input
	 *            The input into the neural network for training.
	 * @param ideal
	 *            The ideal output for training.
	 */
	public BasicEngineDataSet(final double[][] input, final double[][] ideal) {
		if (ideal != null) {
			for (int i = 0; i < input.length; i++) {
				final double[] inputData = input[i];
				final double[] idealData = ideal[i];
				this.add(inputData, idealData);
			}
		} else {
			for (final double[] element : input) {
				final double[] inputData = element;
				this.add(inputData);
			}
		}
	}

	/**
	 * Construct a data set from an already created list. Mostly used to
	 * duplicate this class.
	 * 
	 * @param data
	 *            The data to use.
	 */
	public BasicEngineDataSet(final List<EngineData> data) {
		this.data = data;
	}

	/**
	 * Add input to the training set with no expected output. This is used for
	 * unsupervised training.
	 * 
	 * @param data
	 *            The input to be added to the training set.
	 */
	public void add(final double[] data) {
		this.data.add(new BasicEngineData(data));
	}

	/**
	 * Add input and expected output. This is used for supervised training.
	 * 
	 * @param inputData
	 *            The input data to train on.
	 * @param idealData
	 *            The ideal data to use for training.
	 */
	public void add(final double[] inputData, final double[] idealData) {

		final EngineData pair = new BasicEngineData(inputData, idealData);
		this.data.add(pair);
	}

	/**
	 * Add a neural data pair to the list.
	 * 
	 * @param inputData
	 *            A NeuralDataPair object that contains both input and ideal
	 *            data.
	 */
	public void add(final EngineData inputData) {
		this.data.add(inputData);
	}

	/**
	 * Get the data held by this container.
	 * 
	 * @return the data
	 */
	public List<EngineData> getData() {
		return this.data;
	}

	/**
	 * Get the size of the ideal dataset. This is obtained from the first item
	 * in the list.
	 * 
	 * @return The size of the ideal data.
	 */
	public int getIdealSize() {
		if (this.data.isEmpty()) {
			return 0;
		}
		final EngineData first = this.data.get(0);
		if (first.getIdealArray() == null) {
			return 0;
		}

		return first.getIdealArray().length;
	}

	/**
	 * Get the size of the input dataset. This is obtained from the first item
	 * in the list.
	 * 
	 * @return The size of the input data.
	 */
	public int getInputSize() {
		if (this.data.isEmpty()) {
			return 0;
		}
		final EngineData first = this.data.get(0);
		return first.getInputArray().length;
	}

	/**
	 * Get a record by index into the specified pair.
	 * 
	 * @param index
	 *            The index to read.
	 * @param pair
	 *            The pair to hold the data.
	 */
	public void getRecord(final long index, final EngineData pair) {

		final EngineData source = this.data.get((int) index);
		pair.setInputArray(source.getInputArray());
		if (pair.getIdealArray() != null) {
			pair.setIdealArray(source.getIdealArray());
		}

	}

	/**
	 * @return The total number of records in the file.
	 */
	public long getRecordCount() {
		return this.data.size();
	}

	/**
	 * Determine if this neural data set is supervied. All of the pairs should
	 * be either supervised or not, so simply check the first pair. If the list
	 * is empty then assume unsupervised.
	 * 
	 * @return True if supervised.
	 */
	public boolean isSupervised() {
		if (this.data.size() == 0) {
			return false;
		}
		return this.data.get(0).isSupervised();
	}

	/**
	 * Create an additional data set. It will use the same list.
	 * 
	 * @return The additional data set.
	 */
	public EngineIndexableSet openAdditional() {
		return new BasicEngineDataSet(this.data);
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(final List<EngineData> data) {
		this.data = data;
	}
}
