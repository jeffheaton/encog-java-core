package org.encog.ml.markov.train;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.markov.chain.MarkovChain;
import org.encog.ml.markov.chain.MarkovState;

public class FindTransitionProbabilities {
	private MarkovChain chain;
	private MarkovState initial;
	private MarkovState lastState;
	private Map<MarkovState,StateTransition> transition = new HashMap<MarkovState,StateTransition>();
	private int k;
	
	public FindTransitionProbabilities(MarkovChain theChain) {
		this(theChain,0);
	}
	
	public FindTransitionProbabilities(MarkovChain theChain, int theK) {
		this.chain = theChain;
		this.k  = theK;
	}
	
	public void clear() {
		this.transition.clear();
		this.initial = null;
		this.lastState = null;
	}
	
	public void add(String label) {
		add(this.chain.requireState(label));
	}
	
	public void add(MarkovState state) {
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
		this.chain.setInitialState(this.initial, 1.0);
		
		for( StateTransition st : this.transition.values() ) {
			int count = st.getCount();
			for( MarkovState nextState : st.getNextStates().keySet() ) {
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

	public void fromSingleLetterString(String str) {
		clear();
		for(int i=0;i<str.length();i++) {
			String labelName = ""+str.charAt(i);
			add(labelName);
		}
		finishTraining();
	}
}
