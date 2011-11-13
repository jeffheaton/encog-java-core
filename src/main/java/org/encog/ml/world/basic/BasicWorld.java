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
	
	
}
