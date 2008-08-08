package org.encog.neural.data.temporal;

public class TemporalDataDescription {
	
	enum Type
	{
		RAW,
		PERCENT_CHANGE,
		DELTA_CHANGE,
	}
		
	private double low;
	private double high;
	private Type type;
	
	public TemporalDataDescription(double low,double high,Type type)
	{
		this.low = low;
		this.type = type;
		this.high = high;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	
	
}
