package org.encog.mathutil.probability.vars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.bayesian.BayesianError;

public class VariableList {
	private final List<RandomVariable> variables = new ArrayList<RandomVariable>();
	private final Map<String,RandomVariable> map = new HashMap<String,RandomVariable>();
	
	public void add(RandomVariable v) {
		this.map.put(v.getLabel(), v);
		this.variables.add(v);
	}
	
	public List<RandomVariable> contents() {
		return this.variables;
	}

	public RandomVariable get(String label) {
		return this.map.get(label);
	}

	public int indexOf(RandomVariable s) {
		return this.variables.indexOf(s);
	}

	public int size() {
		return this.variables.size();
	}

	public RandomVariable get(int i) {
		return this.variables.get(i);
	}
	
	public RandomVariable requireEvent(String label) {
		RandomVariable result = this.map.get(label);
		if( result==null ) {
			throw new BayesianError("The variable " + label + " is not defined.");
		}
		return result;
	}

}
