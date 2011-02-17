package org.encog.app.analyst.script.classify;

import org.encog.app.quant.classify.ClassifyMethod;

public class ClassifyField {
	private String name;
	private ClassifyMethod method;
	private double high;
	private double low;
	
	
	public ClassifyField(String name, ClassifyMethod method, double high, double low) {
		super();
		this.name = name;
		this.method = method;
		this.high = high;
		this.low = low;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the method
	 */
	public ClassifyMethod getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(ClassifyMethod method) {
		this.method = method;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @param high the high to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @param low the low to set
	 */
	public void setLow(double low) {
		this.low = low;
	}	
}
