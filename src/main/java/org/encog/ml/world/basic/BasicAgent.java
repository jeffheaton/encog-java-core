package org.encog.ml.world.basic;

import java.util.Set;

import org.encog.ml.world.Action;
import org.encog.ml.world.AgentPolicy;
import org.encog.ml.world.State;
import org.encog.ml.world.SuccessorState;
import org.encog.ml.world.World;
import org.encog.ml.world.WorldAgent;

public class BasicAgent implements WorldAgent {

	private State currentState;
	private AgentPolicy policy;
	private World world;
	private boolean first = true;
	
	@Override
	public State getCurrentState() {
		return this.currentState;
	}

	@Override
	public void setCurrentState(State s) {
		this.currentState = s;		
	}

	@Override
	public AgentPolicy getPolicy() {
		return this.policy;
	}

	@Override
	public void setAgentPolicy(AgentPolicy p) {
		this.policy = p;		
	}
	
	/**
	 * @return the world
	 */
	@Override
	public World getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	@Override
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void tick() {
		if( first ) {
			first = false;
			this.currentState.increaseVisited();
		}
		
		Action action = this.policy.determineNextAction(this);
		Set<SuccessorState> states = world.getProbability().determineSuccessorStates(currentState, action);
		double d = Math.random();
		double sum = 0;
		for(SuccessorState state: states) {
			sum+=state.getProbability();
			if( d<sum) {
				this.currentState = state.getState();
				if( state.getState()==null ) {
					System.out.println("danger");
				}
				state.getState().increaseVisited();
				return;
			}
		}		
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[BasicAgent: state=");
		result.append(this.currentState.toString());
		result.append("]");
		return result.toString();
	}

}
