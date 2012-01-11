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
package org.encog.ml.data.folded;

import java.util.Iterator;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
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
public class FoldedDataSet implements MLDataSet {

	/**
	 * Error message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED 
		= "Direct adds to the folded dataset are not supported.";

	/**
	 * The underlying dataset.
	 */
	private final MLDataSet underlying;

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
	 * The owner object(from openAdditional).
	 */
	private FoldedDataSet owner;

	/**
	 * Create a folded dataset.
	 * 
	 * @param theUnderlying
	 *            The underlying folded dataset.
	 */
	public FoldedDataSet(final MLDataSet theUnderlying) {
		this.underlying = theUnderlying;
		fold(1);
	}

	/**
	 * Not supported.
	 * 
	 * @param data1
	 *            Not used.
	 */
	@Override
	public final void add(final MLData data1) {
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
	public final void add(final MLData inputData, final MLData idealData) {
		throw new TrainingError(FoldedDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Not supported.
	 * 
	 * @param inputData
	 *            Not used.
	 */
	@Override
	public final void add(final MLDataPair inputData) {
		throw new TrainingError(FoldedDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Close the dataset.
	 */
	@Override
	public final void close() {
		this.underlying.close();
	}

	/**
	 * Fold the dataset. Must be done before the dataset is used.
	 * 
	 * @param theNumFolds
	 *            The number of folds.
	 */
	public final void fold(final int theNumFolds) {
		this.numFolds = (int) Math.min(theNumFolds,
				this.underlying.getRecordCount());
		this.foldSize = (int) (this.underlying.getRecordCount() 
					/ this.numFolds);
		this.lastFoldSize = (int) (this.underlying.getRecordCount() 
					- (this.foldSize * this.numFolds));
		setCurrentFold(0);
	}

	/**
	 * @return the currentFold
	 */
	public final int getCurrentFold() {
		if (this.owner != null) {
			return this.owner.getCurrentFold();
		} else {
			return this.currentFold;
		}
	}

	/**
	 * @return the currentFoldOffset
	 */
	public final int getCurrentFoldOffset() {
		if (this.owner != null) {
			return this.owner.getCurrentFoldOffset();
		} else {
			return this.currentFoldOffset;
		}
	}

	/**
	 * @return the currentFoldSize
	 */
	public final int getCurrentFoldSize() {
		if (this.owner != null) {
			return this.owner.getCurrentFoldSize();
		} else {
			return this.currentFoldSize;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		return this.underlying.getIdealSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		return this.underlying.getInputSize();
	}

	/**
	 * @return the numFolds
	 */
	public final int getNumFolds() {
		return this.numFolds;
	}

	/**
	 * @return The owner.
	 */
	public final FoldedDataSet getOwner() {
		return this.owner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void getRecord(final long index, final MLDataPair pair) {
		this.underlying.getRecord(getCurrentFoldOffset() + index, pair);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long getRecordCount() {
		return getCurrentFoldSize();
	}

	/**
	 * @return The underlying dataset.
	 */
	public final MLDataSet getUnderlying() {
		return this.underlying;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSupervised() {
		return this.underlying.isSupervised();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<MLDataPair> iterator() {
		return new FoldedIterator(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLDataSet openAdditional() {
		final FoldedDataSet folded = new FoldedDataSet(
				this.underlying.openAdditional());
		folded.setOwner(this);
		return folded;
	}

	/**
	 * Set the current fold.
	 * 
	 * @param theCurrentFold
	 *            the currentFold to set
	 */
	public final void setCurrentFold(final int theCurrentFold) {

		if (this.owner != null) {
			throw new TrainingError(
					"Can't set the fold on a non-top-level set.");
		}

		if (currentFold >= this.numFolds) {
			throw new TrainingError(
		"Can't set the current fold to be greater than " 
					+ "the number of folds.");
		}
		this.currentFold = theCurrentFold;
		this.currentFoldOffset = this.foldSize * this.currentFold;

		if (this.currentFold == (this.numFolds - 1)) {
			this.currentFoldSize = this.lastFoldSize;
		} else {
			this.currentFoldSize = this.foldSize;
		}
	}

	/**
	 * @param theOwner
	 *            The owner.
	 */
	public final void setOwner(final FoldedDataSet theOwner) {
		this.owner = theOwner;
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
}
