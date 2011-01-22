/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.data.buffer.codec;

import java.util.Iterator;

import org.encog.engine.util.EngineArray;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataPair;

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
	private NeuralDataSet dataset;
	
	/**
	 * The iterator used to read through the dataset.
	 */
	private Iterator<NeuralDataPair> iterator;

	/**
	 * Construct a CODEC.
	 * @param dataset The dataset to use.
	 */
	public NeuralDataSetCODEC(final NeuralDataSet dataset) {
		this.dataset = dataset;
		this.inputSize = dataset.getInputSize();
		this.idealSize = dataset.getIdealSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputSize() {
		return inputSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIdealSize() {
		return idealSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean read(final double[] input, final double[] ideal) {
		if (!iterator.hasNext()) {
			return false;
		} else {
			NeuralDataPair pair = iterator.next();
			EngineArray.arrayCopy(pair.getInputArray(), input);
			EngineArray.arrayCopy(pair.getIdealArray(), ideal);
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final double[] input, final double[] ideal) {
		NeuralDataPair pair = BasicNeuralDataPair.createPair(inputSize,
				idealSize);
		EngineArray.arrayCopy(input, pair.getIdealArray());
		EngineArray.arrayCopy(ideal, pair.getIdealArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareWrite(final int recordCount, 
			final int inputSize, final int idealSize) {
		this.inputSize = inputSize;
		this.idealSize = idealSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareRead() {
		this.iterator = this.dataset.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
