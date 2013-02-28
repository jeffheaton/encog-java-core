package org.encog.ml.genetic.genome;

import org.encog.ml.ea.genome.BasicGenome;
import org.encog.ml.ea.genome.Genome;
import org.encog.util.EngineArray;

public class IntegerArrayGenome extends BasicGenome implements ArrayGenome {
	
	private int[] data;
	
	public IntegerArrayGenome(int size) {
		this.data = new int[size];
	}
	
	public IntegerArrayGenome(IntegerArrayGenome other) {
		this.data = other.getData().clone();
	}

	@Override
	public int size() {
		return this.data.length;
	}

	@Override
	public void copy(ArrayGenome source, int sourceIndex, int targetIndex) {
		IntegerArrayGenome sourceInt = (IntegerArrayGenome)source;
		this.data[targetIndex] = sourceInt.data[sourceIndex];
		
	}
	
	public int[] getData() {
		return this.data;
	}

	@Override
	public void copy(Genome source) {
		IntegerArrayGenome sourceInt = (IntegerArrayGenome)source;
		EngineArray.arrayCopy(sourceInt.data,this.data);
		this.setScore(source.getScore());
		this.setAdjustedScore(source.getAdjustedScore());
		
	}

	@Override
	public void swap(int iswap1, int iswap2) {
		int temp = this.data[iswap1];
		this.data[iswap1] = this.data[iswap2];
		this.data[iswap2] = temp;
		
	}

}
