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
package org.encog.ml.data.buffer;

import java.util.Iterator;

import org.encog.ml.data.MLDataError;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * An iterator for the BufferedNeuralDataSet.
 */
public class BufferedDataSetIterator implements Iterator<MLDataPair> {

	/**
	 * The dataset being iterated over.
	 */
	private final BufferedMLDataSet data;

	/**
	 * The current record.
	 */
	private int current;

	/**
	 * Construct the iterator.
	 * 
	 * @param theData
	 *            The dataset to iterate over.
	 */
	public BufferedDataSetIterator(final BufferedMLDataSet theData) {
		this.data = theData;
		this.current = 0;
	}

	/**
	 * @return True if there is are more records to read.
	 */
	@Override
	public final boolean hasNext() {
		return this.current < this.data.getRecordCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLDataPair next() {

		if (!hasNext()) {
			return null;
		}

		final MLDataPair pair = BasicMLDataPair.createPair(
				this.data.getInputSize(), this.data.getIdealSize());
		this.data.getRecord(this.current++, pair);
		return pair;
	}

	/**
	 * Not supported.
	 */
	@Override
	public final void remove() {
		throw new MLDataError("Remove is not supported.");
	}

}
