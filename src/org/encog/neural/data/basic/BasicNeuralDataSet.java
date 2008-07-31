package org.encog.neural.data.basic;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;

public class BasicNeuralDataSet implements NeuralDataSet  {
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
			iterators.remove(this);			
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

	@Override
	public void add(NeuralData data) {
		this.data.add(new BasicNeuralDataPair(data));		
	}
}

