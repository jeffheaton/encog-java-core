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
package org.encog.ml.data.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.util.EngineArray;
import org.encog.util.obj.ObjectCloner;

/**
 * Stores data in an ArrayList. This class is memory based, so large enough
 * datasets could cause memory issues. Many other dataset types extend this
 * class.
 * 
 * @author jheaton
 */
public class BasicMLDataSet implements Serializable, MLDataSet {

	/**
	 * An iterator to be used with the BasicMLDataSet. This iterator does not
	 * support removes.
	 * 
	 * @author jheaton
	 */
	public class BasicMLIterator implements Iterator<MLDataPair> {

		/**
		 * The index that the iterator is currently at.
		 */
		private int currentIndex = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean hasNext() {
			return this.currentIndex < BasicMLDataSet.this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MLDataPair next() {
			if (!hasNext()) {
				return null;
			}

			return BasicMLDataSet.this.data.get(this.currentIndex++);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void remove() {
			throw new EncogError("Called remove, unsupported operation.");
		}
	}

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -2279722928570071183L;

	/**
	 * The data held by this object.
	 */
	private List<MLDataPair> data = new ArrayList<MLDataPair>();

	/**
	 * Default constructor.
	 */
	public BasicMLDataSet() {
	}

	/**
	 * Construct a data set from an input and idea array.
	 * 
	 * @param input
	 *            The input into the machine learning method for training.
	 * @param ideal
	 *            The ideal output for training.
	 */
	public BasicMLDataSet(final double[][] input, final double[][] ideal) {
		if (ideal != null) {
			for (int i = 0; i < input.length; i++) {
				final BasicMLData inputData = new BasicMLData(input[i]);
				final BasicMLData idealData = new BasicMLData(ideal[i]);
				this.add(inputData, idealData);
			}
		} else {
			for (final double[] element : input) {
				final BasicMLData inputData = new BasicMLData(element);
				this.add(inputData);
			}
		}
	}

	/**
	 * Construct a data set from an already created list. Mostly used to
	 * duplicate this class.
	 * 
	 * @param theData
	 *            The data to use.
	 */
	public BasicMLDataSet(final List<MLDataPair> theData) {
		this.data = theData;
	}

	/**
	 * Copy whatever dataset type is specified into a memory dataset.
	 * 
	 * @param set
	 *            The dataset to copy.
	 */
	public BasicMLDataSet(final MLDataSet set) {
		final int inputCount = set.getInputSize();
		final int idealCount = set.getIdealSize();

		for (final MLDataPair pair : set) {

			BasicMLData input = null;
			BasicMLData ideal = null;

			if (inputCount > 0) {
				input = new BasicMLData(inputCount);
				EngineArray.arrayCopy(pair.getInputArray(), input.getData());
			}

			if (idealCount > 0) {
				ideal = new BasicMLData(idealCount);
				EngineArray.arrayCopy(pair.getIdealArray(), ideal.getData());
			}

			add(new BasicMLDataPair(input, ideal));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final MLData theData) {
		this.data.add(new BasicMLDataPair(theData));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final MLData inputData, final MLData idealData) {

		final MLDataPair pair = new BasicMLDataPair(inputData, idealData);
		this.data.add(pair);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final MLDataPair inputData) {
		this.data.add(inputData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object clone() {
		return ObjectCloner.deepCopy(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close() {
		// nothing to close
	}

	/**
	 * Get the data held by this container.
	 * 
	 * @return the data
	 */
	public final List<MLDataPair> getData() {
		return this.data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		if (this.data.isEmpty()) {
			return 0;
		}
		final MLDataPair first = this.data.get(0);
		if (first.getIdeal() == null) {
			return 0;
		}

		return first.getIdeal().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		if (this.data.isEmpty()) {
			return 0;
		}
		final MLDataPair first = this.data.get(0);
		return first.getInput().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void getRecord(final long index, final MLDataPair pair) {

		final MLDataPair source = this.data.get((int) index);
		pair.setInputArray(source.getInputArray());
		if (pair.getIdealArray() != null) {
			pair.setIdealArray(source.getIdealArray());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getRecordCount() {
		return this.data.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSupervised() {
		if (this.data.size() == 0) {
			return false;
		}
		return this.data.get(0).isSupervised();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<MLDataPair> iterator() {
		final BasicMLIterator result = new BasicMLIterator();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLDataSet openAdditional() {
		return new BasicMLDataSet(this.data);
	}

	/**
	 * @param theData
	 *            the data to set
	 */
	public final void setData(final List<MLDataPair> theData) {
		this.data = theData;
	}

	/**
	 * Concert the data set to a list.
	 * @param theSet The data set to convert.
	 * @return The list.
	 */
	public static List<MLDataPair> toList(MLDataSet theSet) {
		List<MLDataPair> list = new ArrayList<MLDataPair>();
		for(MLDataPair pair: theSet) {
			list.add(pair);
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return (int)getRecordCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLDataPair get(int index) {
		return this.data.get(index);
	}

}
