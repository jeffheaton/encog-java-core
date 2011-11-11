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
	
	
	
}
