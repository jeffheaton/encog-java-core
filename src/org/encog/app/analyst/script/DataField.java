/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.app.analyst.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds stats on a data field for the Encog Analyst. This data is used to
 * normalize the field.
 * 
 */
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

	public DataField(String name) {
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
	 * @param name
	 *            the name to set
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
	 * @param min
	 *            the min to set
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
	 * @param max
	 *            the max to set
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
	 * @param mean
	 *            the mean to set
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
	 * @param standardDeviation
	 *            the standardDeviation to set
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
	 * @param isInteger
	 *            the isInteger to set
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
	 * @param isReal
	 *            the isReal to set
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
	 * @param isClass
	 *            the isClass to set
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
	 * @param isComplete
	 *            the isComplete to set
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

	public int getMinClassCount() {
		int min = Integer.MAX_VALUE;
		for( AnalystClassItem cls : this.classMembers) {
			min = Math.min(min, cls.getCount());
		}
		return min;
	}
}
