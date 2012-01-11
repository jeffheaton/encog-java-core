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

/**
 * Train a Hidden Markov Model (HMM) with the KMeans algorithm. Makes use of
 * KMeans clustering to estimate the transitional and observational
 * probabilities for the HMM.
 * 
 * Unlike Baum Welch training, this method does not require a prior estimate of
 * the HMM model, it starts from scratch.
 * 
 * Faber, Clustering and the Continuous k-Means Algorithm, Los Alamos Science,
 * no. 22, 1994.
 */
public class TrainKMeans implements MLTrain {
	private final Clusters clusters;
	private final int states;
	private final MLSequenceSet sequnces;
	private boolean done;
	private final HiddenMarkovModel modelHMM;
	private int iteration;
	private HiddenMarkovModel method;
	private final MLSequenceSet training;

	public TrainKMeans(final HiddenMarkovModel method,
			final MLSequenceSet sequences) {
		this.method = method;
		this.modelHMM = method;
		this.sequnces = sequences;
		this.states = method.getStateCount();
		this.training = sequences;
		this.clusters = new Clusters(this.states, sequences);
		this.done = false;
	}

	@Override
	public void addStrategy(final Strategy strategy) {
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public void finishTraining() {

	}

	@Override
	public double getError() {
		return this.done ? 0 : 100;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}

	@Override
	public int getIteration() {
		return this.iteration;
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
	public MLDataSet getTraining() {
		return this.training;
	}

	@Override
	public boolean isTrainingDone() {
		return this.done;
	}

	@Override
	public void iteration() {
		final HiddenMarkovModel hmm = this.modelHMM.cloneStructure();

		learnPi(hmm);
		learnTransition(hmm);
		learnOpdf(hmm);

		this.done = optimizeCluster(hmm);

		this.method = hmm;
	}

	@Override
	public void iteration(final int count) {
		// this.iteration = count;

	}

	private void learnOpdf(final HiddenMarkovModel hmm) {
		for (int i = 0; i < hmm.getStateCount(); i++) {
			final Collection<MLDataPair> clusterObservations = this.clusters
					.cluster(i);

			if (clusterObservations.size() < 1) {
				final StateDistribution o = this.modelHMM
						.createNewDistribution();
				hmm.setStateDistribution(i, o);
			} else {
				final MLDataSet temp = new BasicMLDataSet();
				for (final MLDataPair pair : clusterObservations) {
					temp.add(pair);
				}
				hmm.getStateDistribution(i).fit(temp);
			}
		}
	}

	private void learnPi(final HiddenMarkovModel hmm) {
		final double[] pi = new double[this.states];

		for (int i = 0; i < this.states; i++) {
			pi[i] = 0.;
		}

		for (final MLDataSet sequence : this.sequnces.getSequences()) {
			pi[this.clusters.cluster(sequence.get(0))]++;
		}

		for (int i = 0; i < this.states; i++) {
			hmm.setPi(i, pi[i] / this.sequnces.size());
		}
	}

	private void learnTransition(final HiddenMarkovModel hmm) {
		for (int i = 0; i < hmm.getStateCount(); i++) {
			for (int j = 0; j < hmm.getStateCount(); j++) {
				hmm.setTransitionProbability(i, j, 0.);
			}
		}

		for (final MLDataSet obsSeq : this.sequnces.getSequences()) {
			if (obsSeq.size() < 2) {
				continue;
			}

			int first_state;
			int second_state = this.clusters.cluster(obsSeq.get(0));
			for (int i = 1; i < obsSeq.size(); i++) {
				first_state = second_state;
				second_state = this.clusters.cluster(obsSeq.get(i));

				hmm.setTransitionProbability(
						first_state,
						second_state,
						hmm.getTransitionProbability(first_state, second_state) + 1.);
			}
		}

		/* Normalize Aij array */
		for (int i = 0; i < hmm.getStateCount(); i++) {
			double sum = 0;

			for (int j = 0; j < hmm.getStateCount(); j++) {
				sum += hmm.getTransitionProbability(i, j);
			}

			if (sum == 0.) {
				for (int j = 0; j < hmm.getStateCount(); j++) {
					hmm.setTransitionProbability(i, j, 1. / hmm.getStateCount());
				}
			} else {
				for (int j = 0; j < hmm.getStateCount(); j++) {
					hmm.setTransitionProbability(i, j,
							hmm.getTransitionProbability(i, j) / sum);
				}
			}
		}
	}

	private boolean optimizeCluster(final HiddenMarkovModel hmm) {
		boolean modif = false;

		for (final MLDataSet obsSeq : this.sequnces.getSequences()) {
			final ViterbiCalculator vc = new ViterbiCalculator(obsSeq, hmm);
			final int states[] = vc.stateSequence();

			for (int i = 0; i < states.length; i++) {
				final MLDataPair o = obsSeq.get(i);

				if (this.clusters.cluster(o) != states[i]) {
					modif = true;
					this.clusters.remove(o, this.clusters.cluster(o));
					this.clusters.put(o, states[i]);
				}
			}
		}

		return !modif;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(final TrainingContinuation state) {
	}

	@Override
	public void setError(final double error) {

	}

	@Override
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}
}
