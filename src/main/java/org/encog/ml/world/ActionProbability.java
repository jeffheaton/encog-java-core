package org.encog.ml.world;

import java.util.Set;

public interface ActionProbability {
	double calculate(State targetState, State currentState, Action action);
	Set<SuccessorState> determineSuccessorStates(State state, Action action);
}
