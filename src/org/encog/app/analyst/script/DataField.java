package org.encog.app.analyst.script;

import java.util.ArrayList;
import java.util.List;

public class DataField {

	private String name;
	private double min;
	private double max;
	private double mean;
	private double standardDeviation;
	private boolean isInteger;
	private boolean isReal;
	private boolean isClass;
	private boolean isComplete;
	private List<AnalystClassItem> classMembers = new ArrayList<AnalystClassItem>();

	public DataField(String name)
	{
		this.name = name;
		this.min = Double.MAX_VALUE;
		this.max = Double.MIN_VALUE;
		this.mean = Double.NaN;
		this.standardDeviation = Double.NaN;
		this.isInteger = true;
		this.isReal = true;
		this.isClass = true;
		this.isComplete = true;
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
	 * @return the min
	 */
	public double getMin() {
		return min;
	}
	/**
	 * @param min the min to set
	 */
	public void setMin(double min) {
		this.min = min;
	}
	/**
	 * @return the max
	 */
	public double getMax() {
		return max;
	}
	/**
	 * @param max the max to set
	 */
	public void setMax(double max) {
		this.max = max;
	}
	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}
	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}
	/**
	 * @return the standardDeviation
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}
	/**
	 * @param standardDeviation the standardDeviation to set
	 */
	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	/**
	 * @return the isInteger
	 */
	public boolean isInteger() {
		return isInteger;
	}
	/**
	 * @param isInteger the isInteger to set
	 */
	public void setInteger(boolean isInteger) {
		this.isInteger = isInteger;
	}
	/**
	 * @return the isReal
	 */
	public boolean isReal() {
		return isReal;
	}
	/**
	 * @param isReal the isReal to set
	 */
	public void setReal(boolean isReal) {
		this.isReal = isReal;
	}
	/**
	 * @return the isClass
	 */
	public boolean isClass() {
		return isClass;
	}
	/**
	 * @param isClass the isClass to set
	 */
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}

	/**
	 * @return the isComplete
	 */
	public boolean isComplete() {
		return isComplete;
	}

	/**
	 * @param isComplete the isComplete to set
	 */
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	/**
	 * @return the classMembers
	 */
	public List<AnalystClassItem> getClassMembers() {
		return classMembers;
	}
	
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", min=");
		result.append(this.min);
		result.append(", max=");
		result.append(this.max);

		result.append("]");
		return result.toString();
	}
	

}
