package org.encog.ml.world.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.world.Action;
import org.encog.ml.world.State;
import org.encog.ml.world.World;


public abstract class BasicWorld implements World {
	
	private final List<Action> actions = new ArrayList<Action>();
	
	public List<Action> getActions() {
		return this.actions;
	}
	
	public void addAction(Action action) {
		this.actions.add(action);
	}
	
	private int getActionIndex(Action a) {
		return actions.indexOf(a);
	}
	
	private int requireActionIndex(Action a)   {
		int result = getActionIndex(a);
		if( result==-1 ) {
			throw new EncogError("No such action: " + a);
		}
		return result;
	}
	
	@Override
	public void setPolicyValue(State state, Action action, double r) {
		int index = requireActionIndex(action);
		state.getPolicyValue()[index] = r;
		
	}

	@Override
	public double getPolicyValue(State state, Action action) {
		int index = requireActionIndex(action);
		return state.getPolicyValue()[index];
	}
}
