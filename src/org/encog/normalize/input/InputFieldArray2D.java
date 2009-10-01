package org.encog.normalize.input;

public class InputFieldArray2D extends BasicInputField implements HasFixedLength {
	private double[][] array;
	private int index2;
	
	public InputFieldArray2D(double[][] array, int index2)
	{
		this.array = array;
		this.index2 = index2;
	}
	
	public double getValue(int i) {
		return this.array[i][index2];
	}
	
	public int length() {
		return this.array.length;
	}

}
