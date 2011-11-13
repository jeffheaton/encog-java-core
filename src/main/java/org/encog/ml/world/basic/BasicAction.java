package org.encog.ml.world.basic;

import org.encog.ml.world.Action;

public class BasicAction implements Action {
	private final String label;

	public BasicAction(String label) {
		super();
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicAction: label=");
		result.append(this.label);
		result.append("]");
		return result.toString();
	}
	
	
}
