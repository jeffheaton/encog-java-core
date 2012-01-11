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
package org.encog.ml.hmm.train.kmeans;

import java.util.Collection;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.ViterbiCalculator;
import org.encog.ml.hmm.distributions.StateDistribution;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class TrainKMeans implements MLTrain
{	
	private Clusters clusters;
	private int states;
	private MLSequenceSet sequnces;
	private boolean done;
	private HiddenMarkovModel modelHMM;
	private int iteration;
	private HiddenMarkovModel method;
	private MLSequenceSet training;
	
	public TrainKMeans(HiddenMarkovModel method,
			MLSequenceSet sequences)
	{	
		this.method = method;
		this.modelHMM = method;
		this.sequnces = sequences;
		this.states = method.getStateCount();
		this.training = sequences;
		clusters = new Clusters(states, sequences);
		done = false;
	}
	
	private void learnPi(HiddenMarkovModel hmm)
	{	
		double[] pi = new double[states];
		
		for (int i = 0; i < states; i++)
			pi[i] = 0.;
		
		for (MLDataSet sequence : sequnces.getSequences())
			pi[clusters.cluster(sequence.get(0))]++;
		
		for (int i = 0; i < states; i++)
			hmm.setPi(i, pi[i] / sequnces.size());
	}
	
	
	private void learnTransition(HiddenMarkovModel hmm)
	{	
		for (int i = 0; i < hmm.getStateCount(); i++)
			for (int j = 0; j < hmm.getStateCount(); j++)
				hmm.setTransitionProbability(i, j, 0.);
		
		for (MLDataSet obsSeq : sequnces.getSequences()) {
			if (obsSeq.size() < 2)
				continue;
			
			int first_state;
			int second_state = clusters.cluster(obsSeq.get(0));
			for (int i = 1; i < obsSeq.size(); i++) {
				first_state = second_state;
				second_state =
					clusters.cluster(obsSeq.get(i));
				
				hmm.setTransitionProbability(first_state, second_state,
						hmm.getTransitionProbability(first_state, second_state)+1.);
			}
		}
		
		/* Normalize Aij array */
		for (int i = 0; i < hmm.getStateCount(); i++) {
			double sum = 0;
			
			for (int j = 0; j < hmm.getStateCount(); j++)
				sum += hmm.getTransitionProbability(i, j);
			
			if (sum == 0.)
				for (int j = 0; j < hmm.getStateCount(); j++) 
					hmm.setTransitionProbability(i, j, 1. / hmm.getStateCount());     
			else
				for (int j = 0; j < hmm.getStateCount(); j++)
					hmm.setTransitionProbability(i, j, hmm.getTransitionProbability(i, j) / sum);
		}
	}
	
	
	private void learnOpdf(HiddenMarkovModel hmm)
	{
		for (int i = 0; i < hmm.getStateCount(); i++) {
			Collection<MLDataPair> clusterObservations = clusters.cluster(i);
			
			if (clusterObservations.size()<1) {
				StateDistribution o = modelHMM.createNewDistribution();				
				hmm.setStateDistribution(i, o);
			}
			else {
				MLDataSet temp =  new BasicMLDataSet();
				for(MLDataPair pair: clusterObservations) {
					temp.add(pair);
				}
				hmm.getStateDistribution(i).fit(temp);
			}
		}
	}
	
	
	/* Return true if no modification */
	private boolean optimizeCluster(HiddenMarkovModel hmm)
	{	
		boolean modif = false;
		
		for (MLDataSet obsSeq : sequnces.getSequences()) {
			ViterbiCalculator vc = new ViterbiCalculator(obsSeq, hmm);
			int states[] = vc.stateSequence();
			
			for (int i = 0; i < states.length; i++) {
				MLDataPair o = obsSeq.get(i);
				
				if (clusters.cluster(o) != states[i]) {
					modif = true;
					clusters.remove(o, clusters.cluster(o));
					clusters.put(o, states[i]);
				}
			}
		}
		
		return !modif;
	}


	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}


	@Override
	public boolean isTrainingDone() {
		return done;
	}


	@Override
	public MLDataSet getTraining() {
		return this.training;
	}


	@Override
	public void iteration() {
		HiddenMarkovModel hmm = this.modelHMM.cloneStructure();
		
		learnPi(hmm);
		learnTransition(hmm);
		learnOpdf(hmm);
		
		done = optimizeCluster(hmm);
		
		this.method = hmm;
	}


	@Override
	public double getError() {
		return done?0:100;
	}


	@Override
	public void finishTraining() {
		
	}


	@Override
	public void iteration(int count) {
		// this.iteration = count;
		
	}


	@Override
	public int getIteration() {
		return iteration;
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
		this.iteration = iteration;
	}
}
