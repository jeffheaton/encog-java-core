package org.encog.ml.world.grid;

import org.encog.ml.world.State;
import org.encog.ml.world.WorldAgent;

public class GridAgent implements WorldAgent {
	
	private State currentState;

	/**
	 * @return the currentState
	 */
	public State getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
	
	
	
	
}
