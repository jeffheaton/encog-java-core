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
	 * @param data The dataset to iterate over.
	 */
	public BufferedDataSetIterator(BufferedNeuralDataSet data)
	{
		this.data = data;
		this.current = 0;
	}
	
	/**
	 * @return True if there is are more records to read.
	 */
	@Override
	public boolean hasNext() {
		return this.current<data.getRecordCount();
	}

	/**
	 * Return the next record, or null if there are no more.
	 */
	@Override
	public NeuralDataPair next() {
		
		if(!hasNext() )
			return null;
		
		NeuralDataPair pair = BasicNeuralDataPair.createPair(this.data.getInputSize(), this.data.getIdealSize());
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
