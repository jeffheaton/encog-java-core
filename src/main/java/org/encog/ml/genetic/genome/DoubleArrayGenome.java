package org.encog.ml.genetic.genome;

import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.util.EngineArray;

public class DoubleArrayGenome extends BasicGenome implements ArrayGenome {
	
	private double[] data;
	
	public DoubleArrayGenome(int size) {
		this.data = new double[size];
	}
	
	public DoubleArrayGenome(DoubleArrayGenome other) {
		this.data = other.getData().clone();
	}

	@Override
	public int size() {
		return this.data.length;
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
		setScore(source.getScore());
		setAdjustedScore(source.getAdjustedScore());
		
	}

	@Override
	public void swap(int iswap1, int iswap2) {
		double temp = this.data[iswap1];
		this.data[iswap1] = this.data[iswap2];
		this.data[iswap2] = temp;
		
	}

}
