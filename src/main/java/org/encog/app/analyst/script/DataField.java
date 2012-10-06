/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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

	/**
	 * The name of the field.
	 */
	private String name;

	/**
	 * The minimum value of this field.
	 */
	private double min;

	/**
	 * The maximum value of this field.
	 */
	private double max;

	/**
	 * The mean value of this field.
	 */
	private double mean;

	/**
	 * The standard deviation of this field.
	 */
	private double standardDeviation;

	/**
	 * Is this field an integer?
	 */
	private boolean isInteger;

	/**
	 * Is this field a real?
	 */
	private boolean isReal;

	/**
	 * Is this field a class?
	 */
	private boolean isClass;

	/**
	 * Is this field complete.
	 */
	private boolean isComplete;
	
	/**
	 * The source for this field.  This is usually used by Ninjatrader ot MT.
	 */
	private String source = "";

	/**
	 * The class members.
	 */
	private final List<AnalystClassItem> classMembers 
		= new ArrayList<AnalystClassItem>();


	/**
	 * Construct the data field.
	 * 
	 * @param theName
	 *            The name of this field.
	 */
	public DataField(final String theName) {
		this.name = theName;
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
	 * @return the classMembers
	 */
	public List<AnalystClassItem> getClassMembers() {
		return this.classMembers;
	}

	/**
	 * @return the max
	 */
	public double getMax() {
		return this.max;
	}

	/**
	 * @return the mean
	 */
	public double getMean() {
		return this.mean;
	}

	/**
	 * @return the min
	 */
	public double getMin() {
		return this.min;
	}

	/**
	 * Determine the minimum class count. This is the count of the
	 * classification field that is the smallest.
	 * 
	 * @return The minimum class count.
	 */
	public int getMinClassCount() {
		int cmin = Integer.MAX_VALUE;
		for (final AnalystClassItem cls : this.classMembers) {
			cmin = Math.min(cmin, cls.getCount());
		}
		return cmin;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the standardDeviation
	 */
	public double getStandardDeviation() {
		return this.standardDeviation;
	}

	/**
	 * @return the isClass
	 */
	public boolean isClass() {
		return this.isClass;
	}

	/**
	 * @return the isComplete
	 */
	public boolean isComplete() {
		return this.isComplete;
	}

	/**
	 * @return the isInteger
	 */
	public boolean isInteger() {
		return this.isInteger;
	}

	/**
	 * @return the isReal
	 */
	public boolean isReal() {
		return this.isReal;
	}

	/**
	 * @param theClass
	 *            the isClass to set
	 */
	public void setClass(final boolean theClass) {
		this.isClass = theClass;
	}

	/**
	 * @param theComplete
	 *            the isComplete to set
	 */
	public void setComplete(final boolean theComplete) {
		this.isComplete = theComplete;
	}

	/**
	 * @param theInteger
	 *            the isInteger to set
	 */
	public void setInteger(final boolean theInteger) {
		this.isInteger = theInteger;
	}

	/**
	 * @param theMax
	 *            the max to set
	 */
	public void setMax(final double theMax) {
		this.max = theMax;
	}

	/**
	 * @param theMean
	 *            the mean to set
	 */
	public void setMean(final double theMean) {
		this.mean = theMean;
	}

	/**
	 * @param theMin
	 *            the theMin to set
	 */
	public void setMin(final double theMin) {
		this.min = theMin;
	}

	/**
	 * @param theName
	 *            the name to set
	 */
	public void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * @param theReal
	 *            the isReal to set
	 */
	public void setReal(final boolean theReal) {
		this.isReal = theReal;
	}

	/**
	 * @param theStandardDeviation
	 *            the standardDeviation to set
	 */
	public void setStandardDeviation(final double theStandardDeviation) {
		this.standardDeviation = theStandardDeviation;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	

}
