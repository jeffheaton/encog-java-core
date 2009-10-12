package org.encog.normalize.segregate;

public class SegregationRange {
	private final double low;
	private final double high;
	private final boolean include;

	public SegregationRange(final double low, final double high,
			final boolean include) {
		super();
		this.low = low;
		this.high = high;
		this.include = include;
	}

	public double getHigh() {
		return this.high;
	}

	public double getLow() {
		return this.low;
	}

	public boolean inRange(final double value) {
		return ((value >= this.low) && (value <= this.high));
	}

	public boolean isIncluded() {
		return this.include;
	}

}
