package org.encog.neural.networks.training.neat;

public class SplitDepth {

	private final double value;
	private final int depth;

	public SplitDepth(double value, int depth) {
		this.value = value;
		this.depth = depth;
	}

	public double getValue() {
		return value;
	}

	public int getDepth() {
		return depth;
	}
}
