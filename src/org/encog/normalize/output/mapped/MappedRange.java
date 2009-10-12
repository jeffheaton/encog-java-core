package org.encog.normalize.output.mapped;

public class MappedRange {

	private final double low;
	private final double high;
	private final double value;

	public MappedRange(final double low, final double high, final double value) {
		super();
		this.low = low;
		this.high = high;
		this.value = value;
	}

	public double getHigh() {
		return this.high;
	}

	public double getLow() {
		return this.low;
	}

	public double getValue() {
		return this.value;
	}

	public boolean inRange(final double d) {
		if ((d >= this.low) && (d <= this.high)) {
			return true;
		}
		return false;
	}

}
