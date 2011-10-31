package org.encog.ml.bayesian.parse;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;

public class ParsedEvent {
	private final String label;
	private String value;
	
	public ParsedEvent(String theLabel) {
		this.label = theLabel;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public double resolveValue(BayesianEvent actualEvent) {
		int result = 0;
		
		for(String choice: actualEvent.getChoices()) {
			if( this.value.equals(choice)) {
				return result;
			}
			result++;
		}
		
		throw new BayesianError("Can'f find choice " + this.value + " in the event " + this.label );
	}
	
	
}
