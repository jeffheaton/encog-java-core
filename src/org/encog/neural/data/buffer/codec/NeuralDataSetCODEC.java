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

public class NeuralDataSetCODEC implements DataSetCODEC {

	private int inputSize;
	private int idealSize;
	private NeuralDataSet dataset;
	private Iterator<NeuralDataPair> iterator;
	
	public NeuralDataSetCODEC(NeuralDataSet dataset)
	{
		this.dataset = dataset;
		this.inputSize = dataset.getInputSize();
		this.idealSize = dataset.getIdealSize();
	}


	@Override
	public int getInputSize() {
		return inputSize;
	}
	
	@Override
	public int getIdealSize() {
		return idealSize;
	}

	@Override
	public boolean read(double[] input, double[] ideal) {
		if( !iterator.hasNext() )
			return false;
		else
		{
			NeuralDataPair pair = iterator.next();
			EngineArray.arrayCopy(pair.getInputArray(), input);
			EngineArray.arrayCopy(pair.getIdealArray(), ideal);
			return true;
		}
	}

	
	@Override
	public void write(double[] input, double[] ideal) {
		NeuralDataPair pair = BasicNeuralDataPair.createPair(inputSize, idealSize);
		EngineArray.arrayCopy(input,pair.getIdealArray());
		EngineArray.arrayCopy(ideal,pair.getIdealArray());
	}

	@Override
	public void prepareWrite(int recordCount, int inputSize, int idealSize) {
		this.inputSize = inputSize;
		this.idealSize = idealSize;
	}

	@Override
	public void prepareRead() {
		this.iterator = this.dataset.iterator();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	
	
}
