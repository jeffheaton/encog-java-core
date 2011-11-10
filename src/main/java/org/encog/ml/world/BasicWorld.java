package org.encog.ml.world;

import java.util.ArrayList;
import java.util.List;


public abstract class BasicWorld implements World {
	
	private final List<Action> actions = new ArrayList<Action>();
	
	public List<Action> getActions() {
		return this.actions;
	}
	
	public void addAction(Action action) {
		this.actions.add(action);
	}
}
