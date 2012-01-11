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
package org.encog.ml.hmm.train.bw;

import java.util.Arrays;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.ForwardBackwardCalculator;
import org.encog.ml.hmm.distributions.StateDistribution;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public abstract class BaseBaumWelch implements MLTrain
{	
	private int iterations;
	private HiddenMarkovModel method;
	private MLSequenceSet training;
	
	public BaseBaumWelch(HiddenMarkovModel hmm, MLSequenceSet training)
	{
		this.method = hmm;
		this.training = training;
	}
	
	public abstract ForwardBackwardCalculator
	generateForwardBackwardCalculator(MLDataSet sequence, HiddenMarkovModel hmm);
		
	public abstract double[][][]
	estimateXi(MLDataSet sequence, ForwardBackwardCalculator fbc,
			HiddenMarkovModel hmm);
	
	protected double[][]
	estimateGamma(double[][][] xi, ForwardBackwardCalculator fbc)
	{
		double[][] gamma = new double[xi.length + 1][xi[0].length];
		
		for (int t = 0; t < xi.length + 1; t++)
			Arrays.fill(gamma[t], 0.);
		
		for (int t = 0; t < xi.length; t++)
			for (int i = 0; i < xi[0].length; i++)
				for (int j = 0; j < xi[0].length; j++)
					gamma[t][i] += xi[t][i][j];
		
		for (int j = 0; j < xi[0].length; j++)
			for (int i = 0; i < xi[0].length; i++)
				gamma[xi.length][j] += xi[xi.length - 1][i][j];
		
		return gamma;
	}
	
	
	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}

	@Override
	public boolean isTrainingDone() {
		return false;
	}

	@Override
	public MLDataSet getTraining() {
		return this.training;
	}

	@Override
	public void iteration() {
		HiddenMarkovModel nhmm;
		try {
			nhmm = method.clone();
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
			
		double allGamma[][][] = new double[training.getSequenceCount()][][];		
		double aijNum[][] = new double[method.getStateCount()][method.getStateCount()];
		double aijDen[] = new double[method.getStateCount()];
		
		Arrays.fill(aijDen, 0.0);
		for (int i = 0; i < method.getStateCount(); i++)
			Arrays.fill(aijNum[i], 0.);
		
		int g = 0;
		for (MLDataSet obsSeq : training.getSequences()) {	    
			ForwardBackwardCalculator fbc = 
				generateForwardBackwardCalculator(obsSeq, method);
			
			double xi[][][] = estimateXi(obsSeq, fbc, method);
			double gamma[][] = allGamma[g++] = estimateGamma(xi, fbc);
			
			for (int i = 0; i < method.getStateCount(); i++)
				for (int t = 0; t < obsSeq.size() - 1; t++) {
					aijDen[i] += gamma[t][i];
					
					for (int j = 0; j < method.getStateCount(); j++)
						aijNum[i][j] += xi[t][i][j];
				}
		}
		
		for (int i = 0; i < method.getStateCount(); i++) {
			if (aijDen[i] == 0.0) 
				for (int j = 0; j < method.getStateCount(); j++)
					nhmm.setTransitionProbability(i, j, method.getTransitionProbability(i, j));
			else
				for (int j = 0; j < method.getStateCount(); j++)
					nhmm.setTransitionProbability(i, j, aijNum[i][j] / aijDen[i]);
		}
		
		/* compute pi */
		for (int i = 0; i < method.getStateCount(); i++)
			nhmm.setPi(i, 0.);
		
		for (int o = 0; o < training.getSequenceCount(); o++)
			for (int i = 0; i < method.getStateCount(); i++)
				nhmm.setPi(i,
						nhmm.getPi(i) + allGamma[o][0][i] / training.getSequenceCount());
		
		/* compute pdfs */
		for (int i = 0; i < method.getStateCount(); i++) {
			
			double[] weights = new double[training.size()];
			double sum = 0.;
			int j = 0;
			
			int o = 0;
			for (MLDataSet obsSeq : training.getSequences()) {
				for (int t = 0; t < obsSeq.size(); t++, j++)
					sum += weights[j] = allGamma[o][t][i];
				o++;
			}
			
			for (j--; j >= 0; j--)
				weights[j] /= sum;
			
			StateDistribution opdf = nhmm.getStateDistribution(i);
			opdf.fit(training, weights);
		}
		
		this.method = nhmm;
	}

	@Override
	public double getError() {
		return 0;
	}

	@Override
	public void finishTraining() {
	
	}

	@Override
	public void iteration(int count) {
		for(int i=0;i<count;i++) {
			iteration();
		}
	}

	@Override
	public int getIteration() {
		return this.iterations;
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		
	}

	@Override
	public void addStrategy(Strategy strategy) {
		
	}

	@Override
	public MLMethod getMethod() {
		return this.method;
	}

	@Override
	public List<Strategy> getStrategies() {
		return null;
	}

	@Override
	public void setError(double error) {
		
	}

	@Override
	public void setIteration(int iteration) {
		this.iterations = iteration;
	}
}
