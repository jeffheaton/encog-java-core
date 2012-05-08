package org.encog.util.arrayutil;

import java.util.ArrayList;
import java.util.List;

public class WindowDouble {
	private int size;
	private List<double[]> data = new ArrayList<double[]>();
	
	public WindowDouble(int theSize)
	{
		this.size = theSize;
	}
	
	public void add(double[] a) {
		this.data.add(a);
		while( this.data.size()>=this.size) {
			this.data.remove(this.data.size()-1);
		}
	}
	
	public void clear() {
		this.data.clear();
	}

	public boolean isFull() {
		return this.data.size() == this.size;
	}
	
	public double calculateMax(int index, int starting) {
		double result = Double.NEGATIVE_INFINITY;
		
		for(int i=starting; i<this.data.size(); i++) {
			double[] a = this.data.get(i);
			result = Math.max(a[index], result);
		}
		
		return result;
	}
	
	public double calculateMin(int index, int starting) {
		double result = Double.POSITIVE_INFINITY;
		
		for(int i=starting; i<this.data.size(); i++) {
			double[] a = this.data.get(i);
			result = Math.min(a[index], result);
		}
		
		return result;
	}
}
