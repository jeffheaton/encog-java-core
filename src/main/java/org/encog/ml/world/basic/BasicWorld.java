package org.encog.ml.world.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.world.Action;
import org.encog.ml.world.World;


public abstract class BasicWorld implements World {
	
	private final List<Action> actions = new ArrayList<Action>();
	
	public List<Action> getActions() {
		return this.actions;
	}
	
	public void addAction(Action action) {
		this.actions.add(action);
	}
}
