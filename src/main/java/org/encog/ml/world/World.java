package org.encog.ml.world;

import java.util.List;

public interface World {
	List<WorldAgent> getAgents();
	List<Action> getActions();
	void addAction(Action action);
	void setPolicyValue(State state, Action action, double r);	
	double getPolicyValue(State state, Action action);
}
