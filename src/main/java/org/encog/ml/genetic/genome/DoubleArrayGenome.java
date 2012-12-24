package org.encog.ml.genetic.genome;

import org.encog.util.EngineArray;

public class DoubleArrayGenome extends BasicGenome implements ArrayGenome {
	
	private double[] data;
	
	public DoubleArrayGenome(int size) {
		this.data = new double[size];
	}
	
	@Override
	public int size() {
		return this.data.length;
	}

	@Override
	public void decode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copy(ArrayGenome source, int sourceIndex, int targetIndex) {
		DoubleArrayGenome sourceInt = (DoubleArrayGenome)source;
		this.data[targetIndex] = sourceInt.data[sourceIndex];
		
	}
	
	public double[] getData() {
		return this.data;
	}
	
	@Override
	public void copy(Genome source) {
		DoubleArrayGenome sourceDouble = (DoubleArrayGenome)source;
		EngineArray.arrayCopy(sourceDouble.data,this.data);
		
	}

	@Override
	public void swap(int iswap1, int iswap2) {
		double temp = this.data[iswap1];
		this.data[iswap1] = this.data[iswap2];
		this.data[iswap2] = temp;
		
	}

}
