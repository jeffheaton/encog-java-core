package org.encog.app.analyst.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.script.AnalystScript;

public class AnalyzedField {
	
	private String name;
	private double min;
	private double max;
	private double mean;
	private double standardDeviation;
	private boolean isInteger;
	private boolean isReal;
	private boolean isClass;
	private boolean isComplete;
	private double total;
	private int instances;
	private double devTotal;
	private Map<String,Object> classSize = new HashMap<String,Object>();
	private AnalystScript script;
	
	public AnalyzedField(AnalystScript script, String name)
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
		this.instances  = 0;
		this.script = script;
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

	
	public void analyze1(String str) {
		
		if( str.trim().length()==0 ) {
			this.isComplete = false;
			return;
		}
		
		this.instances++;
		
		if (this.isInteger) {
			try {
				int i = Integer.parseInt(str);
				this.max = Math.max(i, this.max);
				this.min = Math.min(i, this.min);
				this.total+=i;
			} catch (NumberFormatException ex) {
				this.isInteger = false;
				if(!this.isReal ) {
				this.max = 0;
				this.min = 0;
				this.standardDeviation = 0;
				}
			}
		}
		
		if (this.isReal) {
			try {
				double d = Double.parseDouble(str);
				this.max = Math.max(d, this.max);
				this.min = Math.min(d, this.min);
				this.total+=d;
			} catch (NumberFormatException ex) {
				this.isReal = false;
				if(!this.isInteger) {
				this.max = 0;
				this.min = 0;
				this.standardDeviation = 0;
				}
			}
		}
		
		if( this.isClass ) {
			if( !this.classSize.containsKey(str) ) {
				this.classSize.put(str, null);	
			}
			if ( this.classSize.size()>script.getConfig().getMaxClassSize() )
				this.isClass = false;
		}
	}

	public void completePass1() {
		
		this.devTotal = 0;
		
		if( this.instances==0)
			this.mean = 0;
		else
			this.mean = this.total/this.instances;	
	}
	
	public void analyze2(String str) {
		if( str.trim().length()==0 ) {
			return;
		}
		
		if( this.isReal || this.isInteger ) {
			double d = Double.parseDouble(str);
			this.devTotal += Math.pow((d - this.mean),2);
		}
	}
	
	public void completePass2() {
		this.standardDeviation = Math.sqrt(this.devTotal/this.instances);
	}
	
	public List<String> getClassMembers()
	{
		List<String> result = new ArrayList<String>();
		result.addAll(this.classSize.keySet());
		Collections.sort(result);
		return result;
	}
}
