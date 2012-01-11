package org.encog.ml.hmm.alog;

import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.hmm.HiddenMarkovModel;

public class ViterbiCalculator
{	
	private double[][] delta; 
	private int[][] psy;
	private int[] stateSequence;
	private double lnProbability;
	
	public 
	ViterbiCalculator(MLDataSet oseq, HiddenMarkovModel hmm)
	{
		if (oseq.size()<1)
			throw new IllegalArgumentException("Must not have empty sequence");
		
		delta = new double[oseq.size()][hmm.getStateCount()];
		psy = new int[oseq.size()][hmm.getStateCount()];
		stateSequence = new int[oseq.size()];
		
		for (int i = 0; i < hmm.getStateCount(); i++) {
			delta[0][i] = -Math.log(hmm.getPi(i)) - 
			Math.log(hmm.getStateDistribution(i).probability(oseq.get(0)));
			psy[0][i] = 0;
		}
		
		Iterator<MLDataPair> oseqIterator = oseq.iterator();
		if (oseqIterator.hasNext())
			oseqIterator.next();
		
		int t = 1;
		while (oseqIterator.hasNext()) {
			MLDataPair observation = oseqIterator.next();
			
			for (int i = 0; i < hmm.getStateCount(); i++)
				computeStep(hmm, observation, t, i);
			
			t++;
		}
		
		lnProbability = Double.MAX_VALUE;
		for (int i = 0; i < hmm.getStateCount(); i++) {
			double thisProbability = delta[oseq.size()-1][i];
			
			if (lnProbability > thisProbability) {
				lnProbability = thisProbability;
				stateSequence[oseq.size() - 1] = i;
			}
		}
		lnProbability = -lnProbability;
		
		for (int t2 = oseq.size() - 2; t2 >= 0; t2--)
			stateSequence[t2] = psy[t2+1][stateSequence[t2+1]];
	}
	
	private void
	computeStep(HiddenMarkovModel hmm, MLDataPair o, int t, int j) 
	{
		double minDelta = Double.MAX_VALUE;
		int min_psy = 0;
		
		for (int i = 0; i < hmm.getStateCount(); i++) {
			double thisDelta = delta[t-1][i] - Math.log(hmm.getTransitionProbability(i, j));
			
			if (minDelta > thisDelta) {
				minDelta = thisDelta;
				min_psy = i;
			}
		}
		
		delta[t][j] = minDelta - Math.log(hmm.getStateDistribution(j).probability(o));
		psy[t][j] = min_psy;
	}
	
	
	public double lnProbability()
	{
		return lnProbability;
	}
	
	
	public int[] stateSequence() 
	{
		return stateSequence.clone();
	}
}
