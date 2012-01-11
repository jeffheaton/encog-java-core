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
package org.encog.ml.data.buffer.codec;

import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.EngineArray;

/**
 * A CODEC that works with the NeuralDataSet class.
 */
public class NeuralDataSetCODEC implements DataSetCODEC {

	/**
	 * The number of input elements.
	 */
	private int inputSize;
	
	/**
	 * The number of ideal elements.
	 */
	private int idealSize;
	
	/**
	 * The dataset.
	 */
	private MLDataSet dataset;
	
	/**
	 * The iterator used to read through the dataset.
	 */
	private Iterator<MLDataPair> iterator;

	/**
	 * Construct a CODEC.
	 * @param theDataset The dataset to use.
	 */
	public NeuralDataSetCODEC(final MLDataSet theDataset) {
		this.dataset = theDataset;
		this.inputSize = theDataset.getInputSize();
		this.idealSize = theDataset.getIdealSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		return inputSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		return idealSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean read(final double[] input, final double[] ideal, final double[] significance) {
		if (!iterator.hasNext()) {
			return false;
		} else {
			MLDataPair pair = iterator.next();
			EngineArray.arrayCopy(pair.getInputArray(), input);
			EngineArray.arrayCopy(pair.getIdealArray(), ideal);
			significance[0] = pair.getSignificance();
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(final double[] input, final double[] ideal, double significance) {
		MLDataPair pair = BasicMLDataPair.createPair(inputSize,
				idealSize);
		EngineArray.arrayCopy(input, pair.getIdealArray());
		EngineArray.arrayCopy(ideal, pair.getIdealArray());
		pair.setSignificance(significance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareWrite(final int recordCount, 
			final int theInputSize, final int theIdealSize) {
		this.inputSize = theInputSize;
		this.idealSize = theIdealSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareRead() {
		this.iterator = this.dataset.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {

	}

}
