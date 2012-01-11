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

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.world.Action;
import org.encog.ml.world.ActionProbability;
import org.encog.ml.world.State;
import org.encog.ml.world.World;
import org.encog.ml.world.WorldAgent;
import org.encog.ml.world.grid.GridState;


public abstract class BasicWorld implements World {
	private final List<State> states = new ArrayList<State>();
	private final List<Action> actions = new ArrayList<Action>();
	private ActionProbability probability;
	private List<WorldAgent> agents = new ArrayList<WorldAgent>();
	private List<State> goals = new ArrayList<State>();
	
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

	/**
	 * @return the probability
	 */
	public ActionProbability getProbability() {
		return probability;
	}

	/**
	 * @param probability the probability to set
	 */
	public void setProbability(ActionProbability probability) {
		this.probability = probability;
	}
	
	public static void removeRewardBelow(List<GridState> states, double d) {
		int i = 0;
		while(i<states.size()) {
			if( states.get(i).getReward()<d ) {
				states.remove(i);
			} else {
				i++;
			}
		}			
	}
	
	@Override
	public List<WorldAgent> getAgents() {
		return this.agents;
	}
	
	@Override
	public void addAgent(WorldAgent agent) {
		this.agents.add(agent);
		agent.setWorld(this);			
	}

	@Override
	public void removeAgent(WorldAgent agent) {
		this.agents.remove(agent);
		agent.setWorld(null);
	}
	
	@Override
	public void addGoal(State s) {
		this.goals.add(s);
	}

	@Override
	public void removeGoal(State s) {
		this.goals.remove(s);
		
	}

	@Override
	public List<State> getGoals() {
		return this.goals;
	}
	
	@Override
	public void addState(State state) {
		this.states.add(state);
	}

	@Override
	public List<State> getStates() {
		return this.states;
	}
	
	@Override
	public boolean isGoalState(State s) {
		for(State state: this.getGoals()) {
			if( s==state) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void runToGoal(WorldAgent a) {
		boolean done = false;
		while(!done) {
			tick();
			if( isGoalState(a.getCurrentState()) ) {
				done = true;
			}
		}
	}
	
	@Override
	public void tick() {
		for (WorldAgent agent : getAgents()) {
			agent.tick();
		}
	}
	
	@Override
	public void setAllRewards(double d) {
		for(State state: this.states) {
			state.setReward(d);
		}
	}	
	
	public void createAbsorbingState(State s, double r) {
		addGoal(s);
		s.setReward(r);
		s.setAllPolicyValues(r);
	}
}
