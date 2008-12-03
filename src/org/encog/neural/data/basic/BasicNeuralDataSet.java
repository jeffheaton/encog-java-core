/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.neural.data.basic;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.BasicNeuralDataSetPersistor;

/**
 * Basic implementation of the NeuralDataSet class.  This class simply
 * stores the neural data in an ArrayList.  This class is memory based, 
 * so large enough datasets could cause memory issues.  Many other dataset
 * types extend this class.
 * @author jheaton
 */
public class BasicNeuralDataSet implements NeuralDataSet, EncogPersistedObject {
	
	/**
	 * An iterator to be used with the BasicNeuralDataSet.  This iterator does
	 * not support removes.
	 * @author jheaton
	 */
	public class BasicNeuralIterator implements Iterator<NeuralDataPair> {

		/**
		 * The index that the iterator is currently at.
		 */
		private int currentIndex = 0;

		/**
		 * Is there more data for the iterator to read?
		 * @return Returns true if there is more data to read.
		 */
		public boolean hasNext() {
			return this.currentIndex < BasicNeuralDataSet.this.data.size();
		}

		/** 
		 * Read the next item.
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
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * The data held by this object.
	 */
	private List<NeuralDataPair> data = new ArrayList<NeuralDataPair>();

	/**
	 * The iterators that are currently open to this object.
	 */
	private final List<BasicNeuralIterator> iterators = 
		new ArrayList<BasicNeuralIterator>();
	
	private String description;
	private String name;

	/**
	 * Default constructor.
	 */
	public BasicNeuralDataSet() {
	}

	/**
	 * Construct a data set from an input and idea array.
	 * @param input The input into the neural network for training.
	 * @param ideal The ideal output for training.
	 */
	public BasicNeuralDataSet(final double[][] input, final double[][] ideal) {
		if( ideal!=null )
		{
			for (int i = 0; i < input.length; i++) {
				final BasicNeuralData inputData = new BasicNeuralData(input[i]);
				final BasicNeuralData idealData = new BasicNeuralData(ideal[i]);
				this.add(inputData, idealData);
			}
		}
		else
		{
			for (int i = 0; i < input.length; i++) {
				final BasicNeuralData inputData = new BasicNeuralData(input[i]);
				this.add(inputData);
			}
		}
	}

	/**
	 * Add input to the training set with no expected output.  This is
	 * used for unsupervised training.
	 * @param data The input to be added to the training set.
	 */
	public void add(final NeuralData data) {
		this.data.add(new BasicNeuralDataPair(data));
	}

	/**
	 * Add input and expected output.  This is used for supervised training.
	 * @param inputData The input data to train on.
	 * @param idealData The ideal data to use for training.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		if (!this.iterators.isEmpty()) {
			throw new ConcurrentModificationException();
		}
		final NeuralDataPair pair = new BasicNeuralDataPair(inputData,
				idealData);
		this.data.add(pair);
	}

	/**
	 * Add a neural data pair to the list.
	 * @param inputData A NeuralDataPair object that contains both 
	 * input and ideal data.
	 */
	public void add(final NeuralDataPair inputData) {
		this.data.add(inputData);
	}

	/**
	 * Close this data set.
	 */
	public void close() {
		// nothing to close

	}

	/**
	 * Get the data held by this container.
	 * @return the data
	 */
	public List<NeuralDataPair> getData() {
		return this.data;
	}

	/**
	 * Get the size of the ideal dataset.  This is obtained
	 * from the first item in the list.
	 * @return The size of the ideal data.
	 */
	public int getIdealSize() {
		if (this.data.isEmpty()) {
			return 0;
		}
		final NeuralDataPair first = this.data.get(0);
		if( first.getIdeal()==null )
			return 0;
		
		return first.getIdeal().size();
	}

	/**
	 * Get the size of the input dataset.  This is obtained
	 * from the first item in the list.
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
	 * Create an iterator for this collection.
	 * @return An iterator to access this collection.
	 */
	public Iterator<NeuralDataPair> iterator() {
		final BasicNeuralIterator result = new BasicNeuralIterator();
		this.iterators.add(result);
		return result;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(final List<NeuralDataPair> data) {
		this.data = data;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	

	public Persistor createPersistor() {
		return new BasicNeuralDataSetPersistor();
	}

}
