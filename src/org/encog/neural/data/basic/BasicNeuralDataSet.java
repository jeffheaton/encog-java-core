/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
  * See the copyright.txt in the distribution for a full listing of 
  * individual contributors.
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
  */

package org.encog.neural.data.basic;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.persist.EncogPersistedObject;

public class BasicNeuralDataSet implements NeuralDataSet, EncogPersistedObject  {
	private List<NeuralDataPair> data = new ArrayList<NeuralDataPair>();
	private List<BasicNeuralIterator> iterators = new ArrayList<BasicNeuralIterator>(); 
	
	public class BasicNeuralIterator implements Iterator<NeuralDataPair> {
		
		private int currentIndex = 0;

		public boolean hasNext() {
			return currentIndex<data.size();
		}

		public NeuralDataPair next() {
			if( !hasNext() ) {
				return null;
			}
			
			return data.get(this.currentIndex++);
		}

		public void remove() {
			throw new UnsupportedOperationException();			
		}
	}	
	
	public BasicNeuralDataSet()
	{
	}
	
	public BasicNeuralDataSet(double input[][],double ideal[][])
	{
		for(int i=0;i<input.length;i++)
		{
			BasicNeuralData inputData = new BasicNeuralData(input[i]);
			BasicNeuralData idealData = new BasicNeuralData(ideal[i]);
			this.add(inputData,idealData);
		}
	}
	
	public void add(NeuralData inputData,NeuralData idealData)
	{
		if( !this.iterators.isEmpty() )
		{
			throw new ConcurrentModificationException();
		}
		NeuralDataPair pair = new BasicNeuralDataPair(inputData,idealData);
		this.data.add(pair);
	}

	public void add(NeuralDataPair inputData) {
		data.add(inputData);		
	}
	
	public Iterator<NeuralDataPair> iterator() {
		BasicNeuralIterator result = new BasicNeuralIterator();
		this.iterators.add(result);
		return result;
	}

	public int getIdealSize() {
		if(data.isEmpty())
			return 0;
		NeuralDataPair first = data.get(0);
		return first.getIdeal().size();
	}

	public int getInputSize() {
		if(data.isEmpty())
			return 0;
		NeuralDataPair first = data.get(0);
		return first.getInput().size();
	}

	public void add(NeuralData data) {
		this.data.add(new BasicNeuralDataPair(data));		
	}

	public String getName() {
		return "BasicNeuralDataSet";
	}

	/**
	 * @return the data
	 */
	public List<NeuralDataPair> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<NeuralDataPair> data) {
		this.data = data;
	}

	@Override
	public void close() {
		// nothing to close
		
	}
	
	
}

