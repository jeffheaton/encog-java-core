package org.encog.normalize.output.mapped;

public class MappedRange {

	private final double low;
	private final double high;
	private final double value;
	
	public MappedRange(double low, double high, double value) {
		super();
		this.low = low;
		this.high = high;
		this.value = value;
	}

	public double getLow() {
		return low;
	}

	public double getHigh() {
		return high;
	}

	public double getValue() {
		return value;
	}
	
	public boolean inRange(double d)
	{
		if( d>=this.low && d<=this.high) {
			return true;
		}
		return false;
	}
	
	
}
