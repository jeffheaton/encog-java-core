package org.encog.util.datastruct;

import org.encog.util.EngineArray;

public class WindowInt {
	private int[] data;
	
	public WindowInt(int size) {
		this.data = new int[size];
	}
	
	public int size() {
		return this.data.length;
	}
	
	public void shift() {
		EngineArray.arrayCopy(this.data, 0, this.data, 1, size()-1);
	}
	
	public void add(int i) {
		shift();
		data[0] = i;
	}
	
	public int[] getData() {
		return this.data;
	}
}
