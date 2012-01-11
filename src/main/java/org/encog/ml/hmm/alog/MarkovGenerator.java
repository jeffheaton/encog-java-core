package org.encog.ml.hmm.alog;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.basic.BasicMLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;

public class MarkovGenerator
{	
	private final HiddenMarkovModel hmm;
	private int currentState;
		
	public MarkovGenerator(HiddenMarkovModel hmm)
	{	
		this.hmm = hmm;
		newSequence();
	}
	
	public MLDataPair observation()
	{	
		MLDataPair o = hmm.getStateDistribution(currentState).generate();
		double rand = Math.random();
		
		for (int j = 0; j < hmm.getStateCount()-1; j++)
			if ((rand -= hmm.getTransitionProbability(currentState, j)) < 0) {
				currentState = j;
				return o;
			}
		
		currentState = hmm.getStateCount() - 1;
		return o;
	}
	
	public MLDataSet observationSequence(int length)
	{	
		MLDataSet sequence = new BasicMLDataSet();		
		while (length-- > 0)
			sequence.add(observation());
		newSequence();
		
		return sequence;
	}
	
	
	public void newSequence()
	{	
		double rand = Math.random(), current = 0.0;
		
		for (int i = 0; i < hmm.getStateCount() - 1; i++) {
			current += hmm.getPi(i);
			
			if (current > rand) {
				currentState = i;
				return;
			}
		}
		
		currentState = hmm.getStateCount() - 1;
	}
	
	public int getCurrentState()
	{
		return currentState;
	}
	
	public MLSequenceSet generateSequences(int observationCount, int observationLength)
	{
		MLSequenceSet result = new BasicMLSequenceSet();
		
		for (int i = 0; i < observationCount; i++) {
			result.startNewSequence();
			result.add(observationSequence(observationLength));
		}

		return result;
	}
}
