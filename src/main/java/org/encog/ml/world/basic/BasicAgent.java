/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
