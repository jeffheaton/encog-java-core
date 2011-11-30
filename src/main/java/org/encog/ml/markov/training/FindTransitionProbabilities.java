package org.encog.ml.markov.training;

import java.util.HashMap;
import java.util.Map;

import org.encog.mathutil.probability.vars.RandomVariable;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.markov.MarkovChain;

public class FindTransitionProbabilities {
	private MarkovChain chain;
	private RandomVariable initial;
	private RandomVariable lastState;
	private Map<RandomVariable,StateTransition> transition = new HashMap<RandomVariable,StateTransition>();
	private int k;
	
	public FindTransitionProbabilities(MarkovChain theChain) {
		this(theChain,0);
	}
	
	public FindTransitionProbabilities(MarkovChain theChain, int theK) {
		this.chain = theChain;
		this.k  = theK;
		clear();
	}
	
	public void clear() {
		this.transition.clear();
		this.initial = null;
		this.lastState = null;
		// create initial transitions
		for(RandomVariable state: this.chain.getStates().contents()) {
			StateTransition st = new StateTransition(state);
			this.transition.put(state, st);
			for(RandomVariable s2: this.chain.getStates().contents()) {
				st.createNextState(s2);
			}
		}
	}
	
	public void add(String label) {
		add(this.chain.getStates().requireEvent(label));
	}
	
	public void add(RandomVariable state) {
		if( this.initial==null ) {
			this.initial = state;			
		} else {
			StateTransition st = null;
			if( !transition.containsKey(this.lastState) ) {
				st = new StateTransition(this.lastState);
			} else {
				st = transition.get(this.lastState);
			}			
			this.transition.put(this.lastState, st);
			st.update(state);
		}
		this.lastState = state;
	}
	
	public void finishTraining() {
		this.chain.clearProbability();
		
		for(int i=0;i<this.chain.getStates().size();i++) {
			RandomVariable state = this.chain.getStates().get(i);
			double num = (state==this.initial)?1:0;
			double den = 1;
			num+=k;
			den+=(k*this.chain.getStates().size());
			this.chain.setInitialState(state, num/den);
		}
		
		//this.chain.setInitialState(this.initial, 1.0);
		
		for( StateTransition st : this.transition.values() ) {
			int count = st.getCount();
			for( RandomVariable nextState : st.getNextStates().keySet() ) {
				int p = st.getNextStates().get(nextState);
				double num = p;
				double den = count;
				num+=k;
				den+=this.chain.getStates().size()*k;
				double d = num/den;
				this.chain.setStateProbability(st.getState(), nextState, d);
			}
		}
	}
	
	public void fromNewString(String str) {
		this.initial = null;
		this.lastState = null;
		for(int i=0;i<str.length();i++) {
			String labelName = ""+str.charAt(i);
			add(labelName);
		}
	}

	public void fromSingleLetterString(String str) {

		for(int i=0;i<str.length();i++) {
			String labelName = ""+str.charAt(i);
			add(labelName);
		}
		finishTraining();
	}
}
