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
}
