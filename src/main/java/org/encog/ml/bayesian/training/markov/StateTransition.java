package org.encog.ml.bayesian.training.markov;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.bayesian.markov.MarkovState;

public class StateTransition {
	private int count;
	private final MarkovState state;
	private final Map<MarkovState,Integer> nextStates = new HashMap<MarkovState,Integer>();
	
	public StateTransition(MarkovState theState) {
		this.state = theState;
	}
	
	public void update(MarkovState nextState) {
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
	public MarkovState getState() {
		return state;
	}

	/**
	 * @return the prior
	 */
	public Map<MarkovState, Integer> getNextStates() {
		return this.nextStates;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[StateTransition: state=");
		result.append(this.state.toString());
		result.append(", count=");
		result.append(count);
		result.append(", nextState=");
		for(MarkovState t : this.nextStates.keySet()) {
			int i = this.nextStates.get(t);
			result.append(t.toString());
			result.append("=");
			result.append(i);
		}
		result.append("]");
		return result.toString();
	}

	public void createNextState(MarkovState s2) {
		this.nextStates.put(s2, 0);
	}
}
