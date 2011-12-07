package org.encog.ml.bayesian.parse;

import org.encog.Encog;

public class ParsedChoice {
	
	final private String label;
	final double min;
	final double max;
	
	public ParsedChoice(String label, double min, double max) {
		super();
		this.label = label;
		this.min = min;
		this.max = max;
	}
	
	public ParsedChoice(String label, int index) {
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
