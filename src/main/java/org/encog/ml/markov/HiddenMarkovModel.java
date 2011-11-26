package org.encog.ml.markov;

import org.encog.mathutil.probability.vars.RandomVariable;
import org.encog.mathutil.probability.vars.VariableList;

public class HiddenMarkovModel extends MarkovChain {
	
	private VariableList observations = new VariableList();
	private double[][] observationProbabilities;
	
	public RandomVariable addObservation(String label) {
		RandomVariable result = new RandomVariable(label);
		this.observations.add(result);
		return result;		
	}
	
	@Override
	public void finalizeStructure() {
		super.finalizeStructure();
		int obsCount = this.observations.size();
		int stateCount = this.getStates().size();
		this.observationProbabilities = new double[stateCount][obsCount];
	}
	
	public void setObservationProbability(RandomVariable observation, RandomVariable s, double d) {
		int indexObservation = this.observations.indexOf(observation);
		int indexState = this.getStates().indexOf(s);
		this.observationProbabilities[indexState][indexObservation] = d;
	}
	
	public double calculateStateProbability(RandomVariable state) {
		int stateIndex = this.getStates().indexOf(state);
		double result = 0;
		for(RandomVariable s : getStates().contents() ) {
			int index = getStates().indexOf(s);
			result+=this.getStateProbability(index, stateIndex)*this.getInitialState(s);
		}
		return result;
	}

	public double query(RandomVariable state,
			RandomVariable obs) {
		// calculate indexes
		int stateIndex = this.getStates().indexOf(state);
		int obsIndex = this.observations.indexOf(obs);
		
		// calculate num 1
		double num1 = this.observationProbabilities[stateIndex][obsIndex];
		
		// calculate num 2		
		double num2 = calculateStateProbability(state);
			
		// calculate den1
		double den1 = 0;
		for(RandomVariable s : getStates().contents()) {
			int index = getStates().indexOf(s);
			double d = calculateStateProbability(s)*this.observationProbabilities[index][obsIndex]; 
			den1+=d;
		}
		
		return (num1*num2)/den1;
	}	
}
