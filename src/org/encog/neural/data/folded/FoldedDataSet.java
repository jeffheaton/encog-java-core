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
package org.encog.neural.data.folded;

import java.util.Iterator;

import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.training.TrainingError;

/**
 * A folded data set allows you to "fold" the data into several equal(or nearly
 * equal) datasets. You then have the ability to select which fold the dataset
 * will process. This is very useful for crossvalidation.
 * 
 * This dataset works off of an underlying dataset. By default there are no
 * folds (fold size 1). Call the fold method to create more folds.
 * 
 */
public class FoldedDataSet implements Indexable {

	/**
	 * Error message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED = "Direct adds to the folded dataset are not supported.";

	/**
	 * The underlying dataset.
	 */
	private final Indexable underlying;

	/**
	 * The fold that we are currently on.
	 */
	private int currentFold;

	/**
	 * The total number of folds. Or 0 if the data has not been folded yet.
	 */
	private int numFolds;

	/**
	 * The size of all folds, except the last fold, the last fold may have a
	 * different number.
	 */
	private int foldSize;

	/**
	 * The size of the last fold.
	 */
	private int lastFoldSize;

	/**
	 * The offset to the current fold.
	 */
	private int currentFoldOffset;

	/**
	 * The size of the current fold.
	 */
	private int currentFoldSize;
	
	/**
	 * The owner object(from openAdditional)
	 */
	private FoldedDataSet owner;

	/**
	 * Create a folded dataset.
	 * 
	 * @param underlying
	 *            The underlying folded dataset.
	 */
	public FoldedDataSet(final Indexable underlying) {
		this.underlying = underlying;
		fold(1);
	}

	/**
	 * Not supported.
	 * 
	 * @param data1
	 *            Not used.
	 */
	@Override
	public void add(final NeuralData data1) {
		throw new TrainingError(FoldedDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Not supported.
	 * 
	 * @param inputData
	 *            Not used.
	 * @param idealData
	 *            Not used.
	 */
	@Override
	public void add(final NeuralData inputData, final NeuralData idealData) {
		throw new TrainingError(FoldedDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Not supported.
	 * 
	 * @param inputData
	 *            Not used.
	 */
	@Override
	public void add(final NeuralDataPair inputData) {
		throw new TrainingError(FoldedDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Close the dataset.
	 */
	@Override
	public void close() {
		this.underlying.close();
	}

	/**
	 * Fold the dataset. Must be done before the dataset is used.
	 * 
	 * @param numFolds
	 *            The number of folds.
	 */
	public void fold(final int numFolds) {
		this.numFolds = (int) Math.min(numFolds, this.underlying
				.getRecordCount());
		this.foldSize = (int) (this.underlying.getRecordCount() / this.numFolds);
		this.lastFoldSize = (int) (this.underlying.getRecordCount() - (this.foldSize * this.numFolds));
		setCurrentFold(0);
	}

	/**
	 * @return the currentFold
	 */
	public int getCurrentFold() {
		if( this.owner!=null )
			return owner.getCurrentFold();
		else
			return this.currentFold;
	}

	/**
	 * @return the currentFoldOffset
	 */
	public int getCurrentFoldOffset() {
		if( this.owner!=null )
			return owner.getCurrentFoldOffset();
		else
			return this.currentFoldOffset;
	}

	/**
	 * @return the currentFoldSize
	 */
	public int getCurrentFoldSize() {
		if( this.owner!=null )
			return this.owner.getCurrentFoldSize();
		else
			return this.currentFoldSize;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIdealSize() {
		return this.underlying.getIdealSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputSize() {
		return this.underlying.getInputSize();
	}

	/**
	 * @return the numFolds
	 */
	public int getNumFolds() {
		return this.numFolds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getRecord(final long index, final EngineData pair) {
		this.underlying.getRecord(this.getCurrentFoldOffset() + index, pair);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getRecordCount() {
		return getCurrentFoldSize();
	}

	/**
	 * @return The underlying dataset.
	 */
	public Indexable getUnderlying() {
		return this.underlying;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSupervised() {
		return this.underlying.isSupervised();
	}
	
	

	/**
	 * @return The owner.
	 */
	public FoldedDataSet getOwner() {
		return owner;
	}

	/**
	 * @param owner The owner.
	 */
	public void setOwner(FoldedDataSet owner) {
		this.owner = owner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<NeuralDataPair> iterator() {
		return new FoldedIterator(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EngineIndexableSet openAdditional() {
		final FoldedDataSet folded = new FoldedDataSet(
				(Indexable) this.underlying.openAdditional());
		folded.setOwner(this);
		return folded;
	}

	/**
	 * Set the current fold.
	 * @param currentFold
	 *            the currentFold to set
	 */
	public void setCurrentFold(final int currentFold) {
		
		if( this.owner!=null ) {
			throw new TrainingError("Can't set the fold on a non-top-level set.");
		}
		
		if (currentFold >= this.numFolds) {
			throw new TrainingError(
					"Can't set the current fold to be greater than the number of folds.");
		}
		this.currentFold = currentFold;
		this.currentFoldOffset = this.foldSize * this.currentFold;

		if (this.currentFold == (this.numFolds - 1)) {
			this.currentFoldSize = this.lastFoldSize;
		} else {
			this.currentFoldSize = this.foldSize;
		}
	}
}
