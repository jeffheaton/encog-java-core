package org.encog.ml.hmm;

import java.io.Serializable;
import java.util.Iterator;

import org.encog.ml.MLStateSequence;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.hmm.alog.ForwardBackwardCalculator;
import org.encog.ml.hmm.alog.ForwardBackwardScaledCalculator;
import org.encog.ml.hmm.alog.ViterbiCalculator;
import org.encog.ml.hmm.distributions.ContinousDistribution;
import org.encog.ml.hmm.distributions.DiscreteDistribution;
import org.encog.ml.hmm.distributions.StateDistribution;

public class HiddenMarkovModel
implements MLStateSequence, Serializable, Cloneable
{		
	private double pi[];
	private double transitionProbability[][];
	private StateDistribution[] stateDistributions;
	private int items;
	
	public HiddenMarkovModel(int theStates, int theItems)
	{
		this.items = theItems;
		pi = new double[theStates];
		transitionProbability = new double[theStates][theStates];
		stateDistributions = new StateDistribution[theStates];
		
		for (int i = 0; i < theStates; i++) {
			pi[i] = 1. / ((double) theStates);
			stateDistributions[i] = new DiscreteDistribution(items);
			
			for (int j = 0; j < theStates; j++)
				transitionProbability[i][j] = 1.0 / ((double) theStates);
		}
	}
	
	public boolean isContinuous() {
		return this.items==-1;
	}
	
	public boolean isDiscrete() {
		return !isContinuous();
	}
	
	public HiddenMarkovModel(int nbStates)
	{
		this.items = -1;
		pi = new double[nbStates];
		transitionProbability = new double[nbStates][nbStates];
		stateDistributions = new StateDistribution[nbStates];
		
		for (int i = 0; i < nbStates; i++) {
			pi[i] = 1. / ((double) nbStates);
			
			if( this.isContinuous() ) {
				stateDistributions[i] = new ContinousDistribution(this.getStateCount());
			} else {
				stateDistributions[i] = new DiscreteDistribution(this.getStateCount());
			}
			
			for (int j = 0; j < nbStates; j++)
				transitionProbability[i][j] = 1. / ((double) nbStates);
		}
	}
	
	public int getStateCount()
	{
		return pi.length;
	}
	
	public double getPi(int stateNb)
	{
		return pi[stateNb];
	}
	
	public void setPi(int stateNb, double value)
	{
		pi[stateNb] = value;
	}
	
	public StateDistribution getStateDistribution(int i)
	{
		return stateDistributions[i];
	}
	
	public void setStateDistribution(int stateNb, StateDistribution opdf)
	{
		stateDistributions[stateNb] = opdf;
	}
	
	public double getTransitionProbability(int i, int j)
	{
		return transitionProbability[i][j];
	}
	
	public void setTransitionProbability(int i, int j, double value)
	{
		transitionProbability[i][j] = value;
	}
	
	public int[] getStatesForSequence(MLDataSet seq)
	{
		return (new ViterbiCalculator(seq, this)).stateSequence();
	}
	
	public double probability(MLDataSet seq)
	{
		return (new ForwardBackwardCalculator(seq, this)).probability();
	}
	
	public double lnProbability(MLDataSet seq)
	{
		return (new ForwardBackwardScaledCalculator(seq, this)).lnProbability();
	}
	
	public double probability(MLDataSet seq, int[] states)
	{
		if (seq.size() != states.length || seq.size()<1)
			throw new IllegalArgumentException();
		
		double probability = getPi(states[0]);
		
		Iterator<MLDataPair> oseqIterator = seq.iterator();
		
		for (int i = 0; i < states.length-1; i++)
			probability *= 
				getStateDistribution(states[i]).probability(oseqIterator.next()) *
				getTransitionProbability(states[i], states[i+1]);
		
		return probability * getStateDistribution(states[states.length-1]).
		probability(seq.get(states.length-1));
	}
	
	public HiddenMarkovModel cloneStructure() {
		HiddenMarkovModel hmm;
		
		if (isDiscrete()) {
			hmm = new HiddenMarkovModel(getStateCount(), this.items);
		} else {
			hmm = new HiddenMarkovModel(getStateCount());
		}
		
		return hmm;
	}

	
	public HiddenMarkovModel clone()
	throws CloneNotSupportedException
	{
		HiddenMarkovModel hmm = cloneStructure();
		
		hmm.pi = pi.clone();
		hmm.transitionProbability = transitionProbability.clone();
		
		for (int i = 0; i < transitionProbability.length; i++)
			hmm.transitionProbability[i] = transitionProbability[i].clone();
		
		for (int i = 0; i < hmm.stateDistributions.length; i++)
			hmm.stateDistributions[i] = stateDistributions[i].clone();
		
		return hmm;
	}
	
	public StateDistribution createNewDistribution() {
		if( isContinuous() ) {
			return new ContinousDistribution(this.items);
		} else {
			return new DiscreteDistribution(this.items);
		}
	}	
}
