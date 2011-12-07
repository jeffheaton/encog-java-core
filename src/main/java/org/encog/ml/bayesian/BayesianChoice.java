package org.encog.ml.bayesian;

import java.io.Serializable;

import org.encog.Encog;

public class BayesianChoice implements Serializable {
	
	final private String label;
	final double min;
	final double max;
	
	public BayesianChoice(String label, double min, double max) {
		super();
		this.label = label;
		this.min = min;
		this.max = max;
	}
	
	public BayesianChoice(String label, int index) {
		super();
		this.label = label;
		this.min = index;
		this.max = index;
	}

	public String getLabel() {
		return label;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
	
	public boolean isIndex() {
		return Math.abs(this.min-this.max)<Encog.DEFAULT_DOUBLE_EQUAL;
	}
	
	public String toString() {
		return this.label;
	}
		
}
