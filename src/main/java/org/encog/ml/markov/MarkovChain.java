package org.encog.ml.markov;

import org.encog.EncogError;
import org.encog.mathutil.probability.vars.RandomVariable;
import org.encog.mathutil.probability.vars.VariableList;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.util.EngineArray;
import org.encog.util.Format;

public class MarkovChain {

	private final VariableList states = new VariableList();
	private double[][] stateProbability;
	private double[] initialState;

	/**
	 * @return the states
	 */
	public VariableList getStates() {
		return states;
	}

	public RandomVariable addState(String label) {
		RandomVariable state = new RandomVariable(label); 
		this.states.add(state);
		return state;
	}

	public double getStateProbability(int starting, int ending) {
		return this.stateProbability[starting][ending];
	}

	public double getStateProbability(String startingLabel, String endingLabel) {
		RandomVariable starting = this.states.get(startingLabel);
		RandomVariable ending = this.states.get(endingLabel);
		int startingIndex = this.states.indexOf(starting);
		int endingIndex = this.states.indexOf(ending);
		return this.stateProbability[startingIndex][endingIndex];
	}

	public void finalizeStructure() {
		int s = states.size();
		this.stateProbability = new double[s][s];
		this.initialState = new double[s];
	}

	public void setStateProbability(RandomVariable starting, RandomVariable ending,
			double d) {
		int startingIndex = this.states.indexOf(starting);
		int endingIndex = this.states.indexOf(ending);
		this.stateProbability[startingIndex][endingIndex] = d;

	}

	public void setInitialState(RandomVariable state, double d) {
		int index = this.states.indexOf(state);
		this.initialState[index] = d;
	}

	public double calculateProbability(RandomVariable state, int t) {
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
	
	public double calculateProbability(RandomVariable state) {
		double sum = 0;
		return sum;
	}

	public int requireState(RandomVariable state) {
		int result = this.states.indexOf(state);
		if( result==-1 ) {
			throw new EncogError("State does not exist: " + state.toString());
		}
		return result;
	}

	public void clearProbability() {
		EngineArray.fill(this.initialState, 0);
		EngineArray.fill(this.stateProbability, 0);
	}

	public int getStateIndex(RandomVariable state) {
		return this.states.indexOf(state);
	}
	
	

	/**
	 * @return the initialState
	 */
	public double[] getInitialState() {
		return initialState;
	}

	public String dump() {
		StringBuilder result = new StringBuilder();
		int states = this.states.size();
	
		
		// handle initial
		int idx = 0;
		for(RandomVariable state: this.states.contents()) {
			result.append("P(");
			result.append(state.getLabel());
			result.append("0)=");
			result.append(this.initialState[idx++]);
			result.append("\n");
		}
		
		// handle transitional prob
		for(int stateIndex = 0; stateIndex<states; stateIndex++) {
			for(int priorStateIndex = 0; priorStateIndex<states; priorStateIndex++) {
				result.append("P(");
				result.append(this.states.get(stateIndex).getLabel());
				result.append("|");
				result.append(this.states.get(priorStateIndex).getLabel());
				result.append(") = ");
				result.append(Format.formatDouble(this.getStateProbability(priorStateIndex, stateIndex), 4));
				result.append("\n");
			}
		}
		
		return result.toString();
	}

	public double getInitialState(RandomVariable r) {
		int index = requireState(r);
		return this.initialState[index];
	}

	public double getStateProbability(RandomVariable baseState, RandomVariable givenState) {
		int fromIndex = this.requireState(givenState);
		int toIndex = this.requireState(baseState);
		return this.stateProbability[fromIndex][toIndex];
	}	
}
