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

package org.encog.neural.data.buffer;

import java.util.Iterator;

import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;

/**
 * An iterator for the BufferedNeuralDataSet.
 */
public class BufferedDataSetIterator implements Iterator<NeuralDataPair> {

	/**
	 * The dataset being iterated over.
	 */
	private BufferedNeuralDataSet data;

	/**
	 * The current record.
	 */
	private int current;

	/**
	 * Construct the iterator.
	 * 
	 * @param data
	 *            The dataset to iterate over.
	 */
	public BufferedDataSetIterator(final BufferedNeuralDataSet data) {
		this.data = data;
		this.current = 0;
	}

	/**
	 * @return True if there is are more records to read.
	 */
	@Override
	public boolean hasNext() {
		return this.current < data.getRecordCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NeuralDataPair next() {

		if (!hasNext())
			return null;

		NeuralDataPair pair = BasicNeuralDataPair.createPair(this.data
				.getInputSize(), this.data.getIdealSize());
		this.data.getRecord(this.current++, pair);
		return pair;
	}

	/**
	 * Not supported.
	 */
	@Override
	public void remove() {
		throw new NeuralDataError("Remove is not supported.");
	}

}
