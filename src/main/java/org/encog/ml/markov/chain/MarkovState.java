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
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[MarkovState: label=");
		result.append(this.label);
		result.append("]");
		return result.toString();
	}
}
