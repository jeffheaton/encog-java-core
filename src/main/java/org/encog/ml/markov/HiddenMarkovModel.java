package org.encog.ml.markov;

import java.util.ArrayList;

import org.encog.mathutil.probability.vars.RandomVariable;
import org.encog.mathutil.probability.vars.VariableList;
import org.encog.ml.bayesian.BayesianEvent;

public class HiddenMarkovModel extends MarkovChain {
	
	VariableList observations = new VariableList();
	
	public RandomVariable addObservation(String label) {
		RandomVariable result = new RandomVariable(label);
		this.observations.add(result);
		return result;		
	}
	
	public void setObservationProbability(RandomVariable observation, RandomVariable s, double d) {
	}

	public void defineProbabilityFromTransitional() {
		
		for(RandomVariable state: this.getStates().contents()) {
			double prob = calculateProbability(state, 1);
			//state.getTable().addLine(prob, true);
		}		
	}
}
