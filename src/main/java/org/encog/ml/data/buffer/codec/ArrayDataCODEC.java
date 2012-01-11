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

import org.encog.util.EngineArray;

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
	 * @param theInput The input array.
	 * @param theIdeal The ideal array.
	 */
	public ArrayDataCODEC(final double[][] theInput, 
			final double[][] theIdeal) {
		this.input = theInput;
		this.ideal = theIdeal;
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
	public final boolean read(final double[] theInput, 
			final double[] theIdeal, final double[] significance) {
		if (index >= this.input.length) {
			return false;
		} else {
			EngineArray.arrayCopy(this.input[index], theInput);
			EngineArray.arrayCopy(this.ideal[index], theIdeal);
			significance[0] = 1.0;
			index++;
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(final double[] theInput, 
			final double[] theIdeal,final double significance) {
		EngineArray.arrayCopy(theInput, this.input[index]);
		EngineArray.arrayCopy(theIdeal, this.ideal[index]);
		index++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareWrite(final int recordCount, 
			final int theInputSize, final int theIdealSize) {
		this.input = new double[recordCount][theInputSize];
		this.ideal = new double[recordCount][theIdealSize];
		this.inputSize = theInputSize;
		this.idealSize = theIdealSize;
		this.index = 0;
	}

	/**
	 * @return The input array.
	 */
	public final double[][] getInput() {
		return input;
	}
	
	/**
	 * @return The ideal array.
	 */
	public final double[][] getIdeal() {
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
