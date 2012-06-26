package org.encog.ca.universe.basic;

import org.encog.ca.CellularAutomataError;
import org.encog.ca.universe.DiscreteCell;
import org.encog.ca.universe.UniverseCell;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.util.EngineArray;

public class BasicDiscreteCell implements DiscreteCell {
	
	private int[] data;
	private int elementCount;
	
	public BasicDiscreteCell(int theSize, int theElementCount) {
		this.data = new int[theSize];
		this.elementCount = theElementCount;
	}
	
	public double get(int index) {
		return this.data[index];
	}

	@Override
	public void randomize() {
		for(int i=0;i<this.data.length;i++) {
			this.data[i] = RangeRandomizer.randomInt(0, elementCount);
		}
		
	}

	@Override
	public void copy(UniverseCell sourceCell) {
		if( !(sourceCell instanceof BasicDiscreteCell ) ) {
			throw new CellularAutomataError("Can only copy another BasicDiscreteCell.");
		}
		
		for(int i=0;i<this.data.length;i++) {
			this.data[i] = (int)sourceCell.get(i);
		}		
		
	}

	@Override
	public double getAvg() {
		return (int)EngineArray.mean(this.data);
	}	
	
	@Override
	public void set(int i, double d) {
		this.data[i] = (int)d;		
	}

	@Override
	public int size() {
		return this.data.length;
	}
}
