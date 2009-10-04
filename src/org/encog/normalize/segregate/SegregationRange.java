package org.encog.normalize.segregate;

public class SegregationRange {
	private final double low;
	private final double high;
	private final boolean include;
	
	public SegregationRange(double low, double high, boolean include) {
		super();
		this.low = low;
		this.high = high;
		this.include = include;
	}

	public double getLow() {
		return low;
	}

	public double getHigh() {
		return high;
	}

	public boolean isIncluded() {
		return include;
	}

	public boolean inRange(double value) {
		return( value>=low && value<=high );
	}
	
}
