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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.hmm.HiddenMarkovModel;

public class ForwardBackwardScaledCalculator
extends ForwardBackwardCalculator
{
	private double[] ctFactors;
	private double lnProbability;
	
	public <O extends MLDataPair> 
	ForwardBackwardScaledCalculator(MLDataSet oseq,
			HiddenMarkovModel hmm, EnumSet<Computation> flags)
	{
		if (oseq.size()<1)
			throw new IllegalArgumentException();
		
		ctFactors = new double[oseq.size()];
		Arrays.fill(ctFactors, 0.);
		
		computeAlpha(hmm, oseq);
		
		if (flags.contains(Computation.BETA))
			computeBeta(hmm, oseq);
		
		computeProbability(oseq, hmm, flags);
	}
	
		public 
	ForwardBackwardScaledCalculator(MLDataSet oseq, HiddenMarkovModel hmm)
	{
		this(oseq, hmm, EnumSet.of(Computation.ALPHA));
	}
	
	
	protected void
	computeAlpha(HiddenMarkovModel hmm, MLDataSet oseq)
	{	
		alpha = new double[oseq.size()][hmm.getStateCount()];
		
		for (int i = 0; i < hmm.getStateCount(); i++)
			computeAlphaInit(hmm, oseq.get(0), i);
		scale(ctFactors, alpha, 0);
		
		Iterator<MLDataPair> seqIterator = oseq.iterator();
		if (seqIterator.hasNext())
			seqIterator.next();
		
		for (int t = 1; t < oseq.size(); t++) {
			MLDataPair observation = seqIterator.next();
			
			for (int i = 0; i < hmm.getStateCount(); i++)
				computeAlphaStep(hmm, observation, t, i);
			scale(ctFactors, alpha, t);
		}
	}
		
	protected void 
	computeBeta(HiddenMarkovModel hmm, MLDataSet oseq)
	{	
		beta = new double[oseq.size()][hmm.getStateCount()];
		
		for (int i = 0; i < hmm.getStateCount(); i++)
			beta[oseq.size()-1][i] = 1. / ctFactors[oseq.size()-1];
		
		for (int t = oseq.size() - 2; t >= 0; t--)
			for (int i = 0; i < hmm.getStateCount(); i++) {
				computeBetaStep(hmm, oseq.get(t+1), t, i);
				beta[t][i] /= ctFactors[t];
			}
	}
	
	
	private void scale(double[] ctFactors, double[][] array, int t)
	{
		double[] table = array[t];
		double sum = 0.;
		
		for (int i = 0; i < table.length; i++)
			sum += table[i];
		
		ctFactors[t] = sum;
		for (int i = 0; i < table.length; i++) 
			table[i] /= sum;
	}
	
	
	private void
	computeProbability(MLDataSet oseq, HiddenMarkovModel hmm, 
			EnumSet<Computation> flags)
	{	
		lnProbability = 0.;
		
		for (int t = 0; t < oseq.size(); t++)
			lnProbability += Math.log(ctFactors[t]);
		
		probability = Math.exp(lnProbability);
	}
	
	
	public double lnProbability()
	{
		return lnProbability;
	}
}
