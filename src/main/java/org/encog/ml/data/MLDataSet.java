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
package org.encog.ml.data;

/**
 * An interface designed to abstract classes that store machine learning data.
 * This interface is designed to provide EngineDataSet objects. These can be
 * used to train machine learning methods using both supervised and unsupervised
 * training.
 * 
 * Some implementations of this interface are memory based. That is they store
 * the entire contents of the dataset in memory.
 * 
 * Other implementations of this interface are not memory based. These
 * implementations read in data as it is needed. This allows very large datasets
 * to be used. Typically the add methods are not supported on non-memory based
 * datasets.
 * 
 * @author jheaton
 */
public interface MLDataSet extends Iterable<MLDataPair> {

	/**
	 * @return The size of the input data.
	 */
	int getIdealSize();

	/**
	 * @return The size of the input data.
	 */
	int getInputSize();

	/**
	 * @return True if this is a supervised training set.
	 */
	boolean isSupervised();

	/**
	 * Determine the total number of records in the set.
	 * 
	 * @return The total number of records in the set.
	 */
	long getRecordCount();

	/**
	 * Read an individual record, specified by index, in random order.
	 * 
	 * @param index
	 *            The index to read.
	 * @param pair
	 *            The pair that the record will be copied into.
	 */
	void getRecord(long index, MLDataPair pair);

	/**
	 * Opens an additional instance of this dataset.
	 * 
	 * @return The new instance.
	 */
	MLDataSet openAdditional();

	/**
	 * Add a object to the dataset. This is used with unsupervised training, as
	 * no ideal output is provided. Note: not all implemenations support the add
	 * methods.
	 * 
	 * @param data1
	 *            The data item to be added.
	 */
	void add(MLData data1);

	/**
	 * Add a set of input and ideal data to the dataset. This is used with
	 * supervised training, as ideal output is provided. Note: not all
	 * implementations support the add methods.
	 * 
	 * @param inputData
	 *            Input data.
	 * @param idealData
	 *            Ideal data.
	 */
	void add(MLData inputData, MLData idealData);

	/**
	 * Add a an object to the dataset. This is used with unsupervised training,
	 * as no ideal output is provided. Note: not all implementations support the
	 * add methods.
	 * 
	 * @param inputData
	 *            A MLDataPair object that contains both input and ideal data.
	 */
	void add(MLDataPair inputData);

	/**
	 * Close this datasource and release any resources obtained by it, including
	 * any iterators created.
	 */
	void close();
	
	int size();
	
	MLDataPair get(int index);
}
