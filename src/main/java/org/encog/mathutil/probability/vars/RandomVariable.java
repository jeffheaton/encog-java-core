package org.encog.mathutil.probability.vars;

import java.io.Serializable;

import org.encog.ml.bayesian.BayesianNetwork;

public class RandomVariable implements Serializable {
	
	private final String label;
	private final String[] choices;
	
	public RandomVariable(String theLabel, String[] theChoices) {
		this.label = theLabel;
		this.choices = theChoices;		
	}
	
	public RandomVariable(String theLabel) {
		this(theLabel,BayesianNetwork.CHOICES_TRUE_FALSE);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		return result.toString();
	}
	
	/**
	 * @return the choices
	 */
	public String[] getChoices() {
		return choices;
	}


	public boolean isBoolean() {
		return this.choices.length==2;
	}
}
