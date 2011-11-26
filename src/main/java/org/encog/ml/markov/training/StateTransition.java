package org.encog.ml.markov.training;

import java.util.HashMap;
import java.util.Map;

import org.encog.mathutil.probability.vars.RandomVariable;
import org.encog.ml.bayesian.BayesianEvent;

public class StateTransition {
	private int count;
	private final RandomVariable state;
	private final Map<RandomVariable,Integer> nextStates = new HashMap<RandomVariable,Integer>();
	
	public StateTransition(RandomVariable theState) {
		this.state = theState;
	}
	
	public void update(RandomVariable nextState) {
		this.count++;
		if( nextStates.containsKey(nextState) ) {
			nextStates.put(nextState, nextStates.get(nextState)+1);
		} else {
			nextStates.put(nextState, 1);
		}
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the state
	 */
	public RandomVariable getState() {
		return state;
	}

	/**
	 * @return the prior
	 */
	public Map<RandomVariable, Integer> getNextStates() {
		return this.nextStates;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[StateTransition: state=");
		result.append(this.state.toString());
		result.append(", count=");
		result.append(count);
		result.append(", nextState=");
		for(RandomVariable t : this.nextStates.keySet()) {
			int i = this.nextStates.get(t);
			result.append(t.toString());
			result.append("=");
			result.append(i);
		}
		result.append("]");
		return result.toString();
	}

	public void createNextState(RandomVariable s2) {
		this.nextStates.put(s2, 0);
	}
}
