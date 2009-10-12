package org.encog.normalize.input;

public class InputFieldArray2D extends BasicInputField implements
		HasFixedLength {
	private final double[][] array;
	private final int index2;

	public InputFieldArray2D(final double[][] array, final int index2) {
		this.array = array;
		this.index2 = index2;
	}

	@Override
	public double getValue(final int i) {
		return this.array[i][this.index2];
	}

	public int length() {
		return this.array.length;
	}

}
