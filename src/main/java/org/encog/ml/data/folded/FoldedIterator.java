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

import org.encog.ml.data.MLDataError;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * Used to iterate over a folded data set.
 */
public class FoldedIterator implements Iterator<MLDataPair> {

	/**
	 * The owner.
	 */
	private final FoldedDataSet owner;
	
	/**
	 * The current index.
	 */
	private int currentIndex = 0;

	/**
	 * Construct the folded iterator.
	 * @param theOwner The owning dataset.
	 */
	public FoldedIterator(final FoldedDataSet theOwner) {
		this.owner = theOwner;
		this.currentIndex = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hasNext() {
		return this.currentIndex < this.owner.getCurrentFoldSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLDataPair next() {
		if (hasNext()) {
			final MLDataPair pair = BasicMLDataPair.createPair(
					this.owner.getInputSize(), this.owner.getIdealSize());
			this.owner.getRecord(this.currentIndex++, pair);
			return pair;
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void remove() {
		throw new MLDataError("Remove is not supported.");
	}
}
