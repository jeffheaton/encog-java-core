package org.encog.app.analyst.script.normalize;

import org.encog.app.quant.normalize.NormalizationDesired;

public class NormalizedField {
	private String name;
	private NormalizationDesired action;
	private double normalizedHigh;
	private double normalizedLow;
	
	public NormalizedField(String name, NormalizationDesired action,
			double normalizedHigh, double normalizedLow) {
		super();
		this.name = name;
		this.action = action;
		this.normalizedHigh = normalizedHigh;
		this.normalizedLow = normalizedLow;
	}
	
	public NormalizedField(String name, NormalizationDesired action) {
		super();
		this.name = name;
		this.action = action;
		this.normalizedHigh = 0;
		this.normalizedLow = 0;
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
	 * @return the action
	 */
	public NormalizationDesired getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(NormalizationDesired action) {
		this.action = action;
	}

	/**
	 * @return the normalizedHigh
	 */
	public double getNormalizedHigh() {
		return normalizedHigh;
	}

	/**
	 * @param normalizedHigh the normalizedHigh to set
	 */
	public void setNormalizedHigh(double normalizedHigh) {
		this.normalizedHigh = normalizedHigh;
	}

	/**
	 * @return the normalizedLow
	 */
	public double getNormalizedLow() {
		return normalizedLow;
	}

	/**
	 * @param normalizedLow the normalizedLow to set
	 */
	public void setNormalizedLow(double normalizedLow) {
		this.normalizedLow = normalizedLow;
	}
	
	
	
	
	
}
