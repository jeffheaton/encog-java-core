package org.encog.ca.universe.basic;

import java.io.Serializable;

import org.encog.ca.CellularAutomataError;
import org.encog.ca.universe.ContinuousCell;
import org.encog.ca.universe.UniverseCell;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.util.EngineArray;
import org.encog.util.Format;

public class BasicContinuousCell implements ContinuousCell, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double[] data;
	private double min;
	private double max;
	
	public BasicContinuousCell(int size, double theMin, double theMax) {
		this.data = new double[size];		
		this.max = theMax;
		this.min = theMin;
	}

	@Override
	public void randomize() {
		for(int i=0;i<this.data.length;i++) {
			this.data[i] = RangeRandomizer.randomize(min, max);
		}
		
	}

	@Override
	public void copy(UniverseCell sourceCell) {
		if( !(sourceCell instanceof BasicContinuousCell ) ) {
			throw new CellularAutomataError("Can only copy another BasicContinuousCell.");
		}
		
		for(int i=0;i<this.data.length;i++) {
			this.data[i] = sourceCell.get(i);
		}		
	}

	@Override
	public double getAvg() {
		return EngineArray.mean(this.data);
	}

	@Override
	public double get(int i) {
		return this.data[i];
	}

	@Override
	public void set(int i, double d) {
		this.data[i] = d;		
	}

	@Override
	public int size() {
		return this.data.length;
	}

	@Override
	public void add(UniverseCell otherCell) {
		for(int i=0;i<this.data.length;i++) {
			this.data[i]+=otherCell.get(i);
		}
	}

	@Override
	public void multiply(UniverseCell otherCell) {
		for(int i=0;i<this.data.length;i++) {
			this.data[i]*=otherCell.get(i);
		}
	}

	@Override
	public void set(int idx, double[] d) {
		for(int i=0;i<this.data.length;i++) {
			this.data[i]=d[idx+i];
		}
	}
	
	@Override
	public void clamp(double low, double high) {
		for(int i=0;i<this.data.length;i++) {
			if (this.data[i] < low)
				this.data[i] = low;
			if (this.data[i] > high)
				this.data[i] = high;
		}
		
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("'[");
		result.append(this.getClass().getSimpleName());
		result.append(":");
		for(int i=0;i<this.size();i++) {
			if( i>0 ) {
				result.append(',');
			}
			result.append(i);
			result.append("=");
			result.append(Format.formatDouble(this.data[i], 4));
		}
		result.append("]");
		return result.toString();
	}
}
