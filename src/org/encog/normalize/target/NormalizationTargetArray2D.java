package org.encog.normalize.target;

public class NormalizationTargetArray2D implements NormalizationTarget {

	private final double[][] array;
	private int currentIndex;

	public NormalizationTargetArray2D(final double[][] array) {
		this.array = array;
		this.currentIndex = 0;
	}

	public void close() {

	}

	public void open() {

	}

	public void write(final double[] data, final int inputCount) {
		for (int i = 0; i < data.length; i++) {
			this.array[this.currentIndex][i] = data[i];
		}
		this.currentIndex++;
	}

}
