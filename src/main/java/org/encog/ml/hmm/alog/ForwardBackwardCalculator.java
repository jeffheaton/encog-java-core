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

import java.util.EnumSet;
import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.hmm.HiddenMarkovModel;

public class ForwardBackwardCalculator
{	
	public static enum Computation { ALPHA, BETA };
	
	protected double[][] alpha = null;
	protected double[][] beta = null;
	protected double probability;
	
	
	protected ForwardBackwardCalculator()
	{
	};
	
	
	public
	ForwardBackwardCalculator(MLDataSet oseq,
			HiddenMarkovModel hmm, EnumSet<Computation> flags)
	{
		if (oseq.size()<1)
			throw new IllegalArgumentException("Empty sequence");
		
		if (flags.contains(Computation.ALPHA))
			computeAlpha(hmm, oseq);
		
		if (flags.contains(Computation.BETA))
			computeBeta(hmm, oseq);
		
		computeProbability(oseq, hmm, flags);
	}
	
	
	public 
	ForwardBackwardCalculator(MLDataSet oseq, HiddenMarkovModel hmm)
	{
		this(oseq, hmm, EnumSet.of(Computation.ALPHA));
	}
	
	
	protected void
	computeAlpha(HiddenMarkovModel hmm, MLDataSet oseq)
	{
		alpha = new double[oseq.size()][hmm.getStateCount()];
		
		for (int i = 0; i < hmm.getStateCount(); i++)
			computeAlphaInit(hmm, oseq.get(0), i);
		
		Iterator<MLDataPair> seqIterator = oseq.iterator();
		if (seqIterator.hasNext())
			seqIterator.next();
		
		for (int t = 1; t < oseq.size(); t++) {
			MLDataPair observation = seqIterator.next();
			
			for (int i = 0; i < hmm.getStateCount(); i++)
				computeAlphaStep(hmm, observation, t, i);
		}
	}
	
	
	protected void
	computeAlphaInit(HiddenMarkovModel hmm, MLDataPair o, int i)
	{
		alpha[0][i] = hmm.getPi(i) * hmm.getStateDistribution(i).probability(o);
	}
	
	
	protected void 
	computeAlphaStep(HiddenMarkovModel hmm, MLDataPair o, int t, int j)
	{
		double sum = 0.;
		
		for (int i = 0; i < hmm.getStateCount(); i++)
			sum += alpha[t-1][i] * hmm.getTransitionProbability(i, j);		

		alpha[t][j] = sum * hmm.getStateDistribution(j).probability(o);
	}
	
	
	protected void 
	computeBeta(HiddenMarkovModel hmm, MLDataSet oseq)
	{
		beta = new double[oseq.size()][hmm.getStateCount()];
		
		for (int i = 0; i < hmm.getStateCount(); i++)
			beta[oseq.size()-1][i] = 1.;
		
		for (int t = oseq.size()-2; t >= 0; t--)
			for (int i = 0; i < hmm.getStateCount(); i++)
				computeBetaStep(hmm, oseq.get(t+1), t, i);
	}
	
	
	protected void 
	computeBetaStep(HiddenMarkovModel hmm, MLDataPair o, int t, int i)
	{
		double sum = 0.;
		
		for (int j = 0; j < hmm.getStateCount(); j++)
			sum += beta[t+1][j] * hmm.getTransitionProbability(i, j) * 
			hmm.getStateDistribution(j).probability(o);
		
		beta[t][i] = sum;
	}
	
	
	public double alphaElement(int t, int i)
	{
		if (alpha == null)
			throw new UnsupportedOperationException("Alpha array has not " +
					"been computed");
		
		return alpha[t][i];
	}
	
	public double betaElement(int t, int i)
	{
		if (beta == null)
			throw new UnsupportedOperationException("Beta array has not " +
					"been computed");
		
		return beta[t][i];
	}
	
	
	private void 
	computeProbability(MLDataSet oseq, HiddenMarkovModel hmm, 
			EnumSet<Computation> flags)
	{
		probability = 0.;
		
		if (flags.contains(Computation.ALPHA))
			for (int i = 0; i < hmm.getStateCount(); i++) 
				probability += alpha[oseq.size()-1][i];
		else
			for (int i = 0; i < hmm.getStateCount(); i++)
				probability += 
					hmm.getPi(i) * 
					hmm.getStateDistribution(i).probability(oseq.get(0)) * beta[0][i];
	}
	
	public double probability()
	{
		return probability;
	}
}
