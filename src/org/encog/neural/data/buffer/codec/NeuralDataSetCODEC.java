/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
