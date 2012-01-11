/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
