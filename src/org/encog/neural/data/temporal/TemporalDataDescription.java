package org.encog.neural.data.temporal;

import org.encog.neural.activation.ActivationFunction;

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
	private int index;
	private ActivationFunction activationFunction;
	
	public TemporalDataDescription(ActivationFunction activationFunction,double low,double high,Type type, boolean input,boolean predict)
	{
		this.low = low;
		this.type = type;
		this.high = high;
		this.input = input;
		this.predict = predict;
		this.activationFunction = activationFunction;
	}
	
	public TemporalDataDescription(Type type, boolean input,boolean predict)
	{
		this(null,0,0,type,input,predict);
	}
	
	public TemporalDataDescription(ActivationFunction activationFunction,Type type, boolean input,boolean predict)
	{
		this(activationFunction,0,0,type,input,predict);
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

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the activationFunction
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}
	
	
	
}
