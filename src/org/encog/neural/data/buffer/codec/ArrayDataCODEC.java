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
package org.encog.neural.data.buffer.codec;

import org.encog.engine.util.EngineArray;

/**
 * A CODEC used for arrays.
 * 
 */
public class ArrayDataCODEC implements DataSetCODEC {

	/**
	 * The current index.
	 */
	private int index;
	
	/**
	 * The number of input elements.
	 */
	private int inputSize;
	
	/**
	 * The number of ideal elements.
	 */
	private int idealSize;
	
	/**
	 * The input array.
	 */
	private double[][] input;
	
	/**
	 * The ideal array.
	 */
	private double[][] ideal;

	/**
	 * Construct an array CODEC.
	 * @param input The input array.
	 * @param ideal The ideal array.
	 */
	public ArrayDataCODEC(final double[][] input, final double[][] ideal) {
		this.input = input;
		this.ideal = ideal;
		this.inputSize = input[0].length;
		this.idealSize = ideal[0].length;
		this.index = 0;
	}

	/**
	 * Default constructor.
	 */
	public ArrayDataCODEC() {
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
		if (index >= this.input.length) {
			return false;
		} else {
			EngineArray.arrayCopy(this.input[index], input);
			EngineArray.arrayCopy(this.ideal[index], ideal);
			index++;
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final double[] input, final double[] ideal) {
		EngineArray.arrayCopy(input, this.input[index]);
		EngineArray.arrayCopy(ideal, this.ideal[index]);
		index++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareWrite(final int recordCount, 
			final int inputSize, final int idealSize) {
		this.input = new double[recordCount][inputSize];
		this.ideal = new double[recordCount][idealSize];
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.index = 0;
	}

	/**
	 * @return The input array.
	 */
	public double[][] getInput() {
		return input;
	}
	
	/**
	 * @return The ideal array.
	 */
	public double[][] getIdeal() {
		return ideal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareRead() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {

	}

}
