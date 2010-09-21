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

package org.encog.neural.data.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.engine.data.EngineData;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicNeuralDataSetPersistor;
import org.encog.util.ObjectCloner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * neural data in an ArrayList. This class is memory based, so large enough
 * datasets could cause memory issues. Many other dataset types extend this
 * class.
 * 
 * @author jheaton
 */
public class BasicNeuralDataSet implements EncogPersistedObject, Serializable,
		Indexable {

	/**
	 * An iterator to be used with the BasicNeuralDataSet. This iterator does
	 * not support removes.
	 * 
	 * @author jheaton
	 */
	public class BasicNeuralIterator implements Iterator<NeuralDataPair> {

		/**
		 * The index that the iterator is currently at.
		 */
		private int currentIndex = 0;

		/**
		 * Is there more data for the iterator to read?
		 * 
		 * @return Returns true if there is more data to read.
		 */
		public boolean hasNext() {
			return this.currentIndex < BasicNeuralDataSet.this.data.size();
		}

		/**
		 * Read the next item.
		 * 
		 * @return The next item.
		 */
		public NeuralDataPair next() {
			if (!hasNext()) {
				return null;
			}

			return BasicNeuralDataSet.this.data.get(this.currentIndex++);
		}

		/**
		 * Removes are not supported.
		 */
		public void remove() {
			if (BasicNeuralDataSet.this.logger.isErrorEnabled()) {
				BasicNeuralDataSet.this.logger
						.error("Called remove, unsupported operation.");
			}
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -2279722928570071183L;

	/**
	 * The logging object.
	 */
	private final transient Logger logger = LoggerFactory.getLogger(this
			.getClass());
	
	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;

	/**
	 * The data held by this object.
	 */
	private List<NeuralDataPair> data = new ArrayList<NeuralDataPair>();

	/**
	 * The description for this object.
	 */
	private String description;

	/**
	 * The name for this object.
	 */
	private String name;

	/**
	 * Default constructor.
	 */
	public BasicNeuralDataSet() {
	}

	/**
	 * Construct a data set from an input and idea array.
	 * 
	 * @param input
	 *            The input into the neural network for training.
	 * @param ideal
	 *            The ideal output for training.
	 */
	public BasicNeuralDataSet(final double[][] input, final double[][] ideal) {
		if (ideal != null) {
			for (int i = 0; i < input.length; i++) {
				final BasicNeuralData inputData = new BasicNeuralData(input[i]);
				final BasicNeuralData idealData = new BasicNeuralData(ideal[i]);
				this.add(inputData, idealData);
			}
		} else {
			for (final double[] element : input) {
				final BasicNeuralData inputData = new BasicNeuralData(element);
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
	public BasicNeuralDataSet(final List<NeuralDataPair> data) {
		this.data = data;
	}

	/**
	 * Add input to the training set with no expected output. This is used for
	 * unsupervised training.
	 * 
	 * @param data
	 *            The input to be added to the training set.
	 */
	public void add(final NeuralData data) {
		this.data.add(new BasicNeuralDataPair(data));
	}

	/**
	 * Add input and expected output. This is used for supervised training.
	 * 
	 * @param inputData
	 *            The input data to train on.
	 * @param idealData
	 *            The ideal data to use for training.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {

		final NeuralDataPair pair = new BasicNeuralDataPair(inputData,
				idealData);
		this.data.add(pair);
	}

	/**
	 * Add a neural data pair to the list.
	 * 
	 * @param inputData
	 *            A NeuralDataPair object that contains both input and ideal
	 *            data.
	 */
	public void add(final NeuralDataPair inputData) {
		this.data.add(inputData);
	}

	/**
	 * @return A cloned copy of this object.
	 */
	@Override
	public Object clone() {
		return ObjectCloner.deepCopy(this);
	}

	/**
	 * Close this data set.
	 */
	public void close() {
		// nothing to close
	}

	/**
	 * Create a persistor for this object.
	 * 
	 * @return A persistor for this object.
	 */
	public Persistor createPersistor() {
		return new BasicNeuralDataSetPersistor();
	}

	/**
	 * Get the data held by this container.
	 * 
	 * @return the data
	 */
	public List<NeuralDataPair> getData() {
		return this.data;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
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
		final NeuralDataPair first = this.data.get(0);
		if (first.getIdeal() == null) {
			return 0;
		}

		return first.getIdeal().size();
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
		final NeuralDataPair first = this.data.get(0);
		return first.getInput().size();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
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

		final NeuralDataPair source = this.data.get((int) index);
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
	 * Create an iterator for this collection.
	 * 
	 * @return An iterator to access this collection.
	 */
	public Iterator<NeuralDataPair> iterator() {
		final BasicNeuralIterator result = new BasicNeuralIterator();
		return result;
	}

	/**
	 * Create an additional data set. It will use the same list.
	 * 
	 * @return The additional data set.
	 */
	public Indexable openAdditional() {
		return new BasicNeuralDataSet(this.data);
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(final List<NeuralDataPair> data) {
		this.data = data;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}
}
