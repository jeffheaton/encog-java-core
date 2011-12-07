package org.encog.ml.bayesian;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

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
	
	public String toFullString() {
		StringBuilder result = new StringBuilder();
		result.append(this.label);
		if( !isIndex() ) {
			result.append(":");
			result.append(CSVFormat.EG_FORMAT.format(this.min, 4));
			result.append(" to ");
			result.append(CSVFormat.EG_FORMAT.format(this.max, 4));
		}
		return result.toString();
	}
		
}
