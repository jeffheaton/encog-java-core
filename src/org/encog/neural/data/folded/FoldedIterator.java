/*
 * Encog(tm) Core v2.6 - Java Version
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

import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;

/**
 * Used to iterate over a folded data set.
 */
public class FoldedIterator implements Iterator<NeuralDataPair> {

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
	 * @param owner The owning dataset.
	 */
	public FoldedIterator(final FoldedDataSet owner) {
		this.owner = owner;
		this.currentIndex = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return this.currentIndex < this.owner.getCurrentFoldSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NeuralDataPair next() {
		if (hasNext()) {
			final NeuralDataPair pair = BasicNeuralDataPair.createPair(
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
	public void remove() {
		throw new NeuralDataError("Remove is not supported.");
	}
}
