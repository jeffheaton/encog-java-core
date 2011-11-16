package org.encog.ml.markov.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkovChain {

	private final List<MarkovState> states = new ArrayList<MarkovState>();
	private final Map<String, MarkovState> stateMap = new HashMap<String, MarkovState>();
	private double[][] stateProbability;
	private double[] initialState;

	/**
	 * @return the states
	 */
	public List<MarkovState> getStates() {
		return states;
	}

	public MarkovState addState(String label) {
		MarkovState state = new MarkovState(label);
		this.states.add(state);
		this.stateMap.put(label, state);
		return state;
	}

	public double getStateProbability(int starting, int ending) {
		return this.stateProbability[starting][ending];
	}

	public double getStateProbability(String startingLabel, String endingLabel) {
		MarkovState starting = this.stateMap.get(startingLabel);
		MarkovState ending = this.stateMap.get(endingLabel);
		int startingIndex = this.states.indexOf(starting);
		int endingIndex = this.states.indexOf(ending);
		return this.stateProbability[startingIndex][endingIndex];
	}

	public void finalizeStructure() {
		int s = states.size();
		this.stateProbability = new double[s][s];
		this.initialState = new double[s];
	}

	public void setStateProbability(MarkovState starting, MarkovState ending,
			double d) {
		int startingIndex = this.states.indexOf(starting);
		int endingIndex = this.states.indexOf(ending);
		this.stateProbability[startingIndex][endingIndex] = d;

	}

	public void setInitialState(MarkovState state, double d) {
		int index = this.states.indexOf(state);
		this.initialState[index] = d;
	}

	public double calculateProbability(MarkovState state, int t) {
		int index = this.states.indexOf(state);
		if (t == 0) {
			return this.initialState[index];
		} else {
			double sum = 0.0;
			for (int i = 0; i < this.states.size(); i++) {
				sum += this.stateProbability[i][index]
						* calculateProbability(this.states.get(i), t - 1);
			}
			return sum;
		}
	}
	
	public double calculateProbability(MarkovState state) {
		double sum = 0;
		return sum;
	}
}
