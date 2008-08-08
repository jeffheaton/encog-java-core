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
	private boolean input;
	private boolean predict;
	private Type type;
	
	public TemporalDataDescription(double low,double high,Type type, boolean input,boolean predict)
	{
		this.low = low;
		this.type = type;
		this.high = high;
		this.input = input;
		this.predict = predict;
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

	/**
	 * @return the input
	 */
	public boolean isInput() {
		return input;
	}

	/**
	 * @return the predict
	 */
	public boolean isPredict() {
		return predict;
	}
	
	
	
}
