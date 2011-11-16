package org.encog.ml.markov.chain;

public class MarkovState {
	private final String label;
	
	public MarkovState(String theLabel) {
		this.label = theLabel;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}	
}
