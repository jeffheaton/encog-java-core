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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataError;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.util.EngineArray;
import org.encog.util.obj.ObjectCloner;

/**
 * A basic implementation of the MLSequenceSet.
 */
public class BasicMLSequenceSet implements Serializable, MLSequenceSet {

	/**
	 * An iterator to be used with the BasicMLDataSet. This iterator does not
	 * support removes.
	 * 
	 * @author jheaton
	 */
	public class BasicMLSeqIterator implements Iterator<MLDataPair> {

		/**
		 * The index that the iterator is currently at.
		 */
		private int currentIndex = 0;
		
		/**
		 * The sequence index.
		 */
		private int currentSequenceIndex = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean hasNext() {
			
			if( this.currentSequenceIndex>=sequences.size() ) {
				return false;
			} 
			
			MLDataSet seq = sequences.get(this.currentSequenceIndex);
			
			if(this.currentIndex>=seq.getRecordCount()) {
				return false;
			}
			
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MLDataPair next() {
			if (!hasNext()) {
				return null;
			}
			
			MLDataSet target = sequences.get(this.currentSequenceIndex);
			
			MLDataPair result = ((BasicMLDataSet)target).getData().get(this.currentIndex);
			this.currentIndex++;
			if( this.currentIndex>=target.getRecordCount()) {
				this.currentIndex = 0;
				this.currentSequenceIndex++;
			}
			
			return result;
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
	private List<MLDataSet> sequences = new ArrayList<MLDataSet>();
	
	private MLDataSet currentSequence;

	/**
	 * Default constructor.
	 */
	public BasicMLSequenceSet() {
		this.currentSequence = new BasicMLDataSet();
		sequences.add(this.currentSequence);
	}
	
	public BasicMLSequenceSet(BasicMLSequenceSet other) {
		this.sequences = other.sequences;
		this.currentSequence = other.currentSequence;
	}

	/**
	 * Construct a data set from an input and ideal array.
	 * 
	 * @param input
	 *            The input into the machine learning method for training.
	 * @param ideal
	 *            The ideal output for training.
	 */
	public BasicMLSequenceSet(final double[][] input, final double[][] ideal) {
		this.currentSequence = new BasicMLDataSet(input,ideal);
		this.sequences.add(this.currentSequence);
	}

	/**
	 * Construct a data set from an already created list. Mostly used to
	 * duplicate this class.
	 * 
	 * @param theData
	 *            The data to use.
	 */
	public BasicMLSequenceSet(final List<MLDataPair> theData) {
		this.currentSequence = new BasicMLDataSet(theData);
		this.sequences.add(this.currentSequence);
	}

	/**
	 * Copy whatever dataset type is specified into a memory dataset.
	 * 
	 * @param set
	 *            The dataset to copy.
	 */
	public BasicMLSequenceSet(final MLDataSet set) {
		this.currentSequence = new BasicMLDataSet();
		this.sequences.add(this.currentSequence);
		
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

			this.currentSequence.add(new BasicMLDataPair(input, ideal));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final MLData theData) {
		this.currentSequence.add(theData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final MLData inputData, final MLData idealData) {

		final MLDataPair pair = new BasicMLDataPair(inputData, idealData);
		this.currentSequence.add(pair);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final MLDataPair inputData) {
		this.currentSequence.add(inputData);
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
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		if (this.sequences.get(0).getRecordCount()==0) {
			return 0;
		}
		return this.sequences.get(0).getIdealSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		if (this.sequences.get(0).getRecordCount()==0) {
			return 0;
		}
		return this.sequences.get(0).getIdealSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void getRecord(final long index, final MLDataPair pair) {
		long recordIndex = index;
		int sequenceIndex = 0;
		
		while( this.sequences.get(sequenceIndex).getRecordCount()<recordIndex) {
			recordIndex-=this.sequences.get(sequenceIndex).getRecordCount();
			sequenceIndex++;
			if( sequenceIndex>this.sequences.size() ) {
				throw new MLDataError("Record out of range: " + index);
			}
		}

		this.sequences.get(sequenceIndex).getRecord(recordIndex, pair);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getRecordCount() {
		long result = 0;
		for(MLDataSet ds: this.sequences) {
			result+=ds.getRecordCount();
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSupervised() {
		if (this.sequences.get(0).getRecordCount() == 0) {
			return false;
		}
		return this.sequences.get(0).isSupervised();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<MLDataPair> iterator() {
		final BasicMLSeqIterator result = new BasicMLSeqIterator();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLDataSet openAdditional() {
		return new BasicMLSequenceSet(this);
	}

	@Override
	public void startNewSequence() {
		if (this.currentSequence.getRecordCount() > 0) {
			this.currentSequence = new BasicMLDataSet();
			this.sequences.add(this.currentSequence);
		}
	}

	@Override
	public int getSequenceCount() {
		return this.sequences.size();
	}

	@Override
	public MLDataSet getSequence(int i) {
		return this.sequences.get(i);
	}

	@Override
	public Collection<MLDataSet> getSequences() {
		return this.sequences;
	}
	

	@Override
	public int size() {
		return (int)getRecordCount();
	}

	@Override
	public MLDataPair get(int index) {
		MLDataPair result = BasicMLDataPair.createPair(getInputSize(), getIdealSize());
		this.getRecord(index, result);
		return result;
	}

	@Override
	public void add(MLDataSet sequence) {
		for(MLDataPair pair: sequence) {
			add(pair);
		}
		
	}

}
