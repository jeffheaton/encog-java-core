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

/**
 * This class provides the base implementation for Baum-Welch learning for
 * HMM's. There are currently two implementations provided.
 * 
 * TrainBaumWelch - Regular Baum Welch Learning.
 * 
 * TrainBaumWelchScaled - Regular Baum Welch Learning, which can handle
 * underflows in long sequences.
 * 
 * L. E. Baum, T. Petrie, G. Soules, and N. Weiss,
 * "A maximization technique occurring in the statistical analysis of probabilistic functions of Markov chains"
 * , Ann. Math. Statist., vol. 41, no. 1, pp. 164-171, 1970.
 * 
 * Hidden Markov Models and the Baum-Welch Algorithm, IEEE Information Theory
 * Society Newsletter, Dec. 2003.
 * 
 */
public abstract class BaseBaumWelch implements MLTrain {
	private int iterations;
	private HiddenMarkovModel method;
	private final MLSequenceSet training;

	public BaseBaumWelch(final HiddenMarkovModel hmm,
			final MLSequenceSet training) {
		this.method = hmm;
		this.training = training;
	}

	@Override
	public void addStrategy(final Strategy strategy) {

	}

	@Override
	public boolean canContinue() {
		return false;
	}

	protected double[][] estimateGamma(final double[][][] xi,
			final ForwardBackwardCalculator fbc) {
		final double[][] gamma = new double[xi.length + 1][xi[0].length];

		for (int t = 0; t < (xi.length + 1); t++) {
			Arrays.fill(gamma[t], 0.);
		}

		for (int t = 0; t < xi.length; t++) {
			for (int i = 0; i < xi[0].length; i++) {
				for (int j = 0; j < xi[0].length; j++) {
					gamma[t][i] += xi[t][i][j];
				}
			}
		}

		for (int j = 0; j < xi[0].length; j++) {
			for (int i = 0; i < xi[0].length; i++) {
				gamma[xi.length][j] += xi[xi.length - 1][i][j];
			}
		}

		return gamma;
	}

	public abstract double[][][] estimateXi(MLDataSet sequence,
			ForwardBackwardCalculator fbc, HiddenMarkovModel hmm);

	@Override
	public void finishTraining() {

	}

	public abstract ForwardBackwardCalculator generateForwardBackwardCalculator(
			MLDataSet sequence, HiddenMarkovModel hmm);

	@Override
	public double getError() {
		return 0;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}

	@Override
	public int getIteration() {
		return this.iterations;
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
		return false;
	}

	@Override
	public void iteration() {
		HiddenMarkovModel nhmm;
		try {
			nhmm = this.method.clone();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError();
		}

		final double allGamma[][][] = new double[this.training
				.getSequenceCount()][][];
		final double aijNum[][] = new double[this.method.getStateCount()][this.method
				.getStateCount()];
		final double aijDen[] = new double[this.method.getStateCount()];

		Arrays.fill(aijDen, 0.0);
		for (int i = 0; i < this.method.getStateCount(); i++) {
			Arrays.fill(aijNum[i], 0.);
		}

		int g = 0;
		for (final MLDataSet obsSeq : this.training.getSequences()) {
			final ForwardBackwardCalculator fbc = generateForwardBackwardCalculator(
					obsSeq, this.method);

			final double xi[][][] = estimateXi(obsSeq, fbc, this.method);
			final double gamma[][] = allGamma[g++] = estimateGamma(xi, fbc);

			for (int i = 0; i < this.method.getStateCount(); i++) {
				for (int t = 0; t < (obsSeq.size() - 1); t++) {
					aijDen[i] += gamma[t][i];

					for (int j = 0; j < this.method.getStateCount(); j++) {
						aijNum[i][j] += xi[t][i][j];
					}
				}
			}
		}

		for (int i = 0; i < this.method.getStateCount(); i++) {
			if (aijDen[i] == 0.0) {
				for (int j = 0; j < this.method.getStateCount(); j++) {
					nhmm.setTransitionProbability(i, j,
							this.method.getTransitionProbability(i, j));
				}
			} else {
				for (int j = 0; j < this.method.getStateCount(); j++) {
					nhmm.setTransitionProbability(i, j, aijNum[i][j]
							/ aijDen[i]);
				}
			}
		}

		/* compute pi */
		for (int i = 0; i < this.method.getStateCount(); i++) {
			nhmm.setPi(i, 0.);
		}

		for (int o = 0; o < this.training.getSequenceCount(); o++) {
			for (int i = 0; i < this.method.getStateCount(); i++) {
				nhmm.setPi(
						i,
						nhmm.getPi(i)
								+ (allGamma[o][0][i] / this.training
										.getSequenceCount()));
			}
		}

		/* compute pdfs */
		for (int i = 0; i < this.method.getStateCount(); i++) {

			final double[] weights = new double[this.training.size()];
			double sum = 0.;
			int j = 0;

			int o = 0;
			for (final MLDataSet obsSeq : this.training.getSequences()) {
				for (int t = 0; t < obsSeq.size(); t++, j++) {
					sum += weights[j] = allGamma[o][t][i];
				}
				o++;
			}

			for (j--; j >= 0; j--) {
				weights[j] /= sum;
			}

			final StateDistribution opdf = nhmm.getStateDistribution(i);
			opdf.fit(this.training, weights);
		}

		this.method = nhmm;
	}

	@Override
	public void iteration(final int count) {
		for (int i = 0; i < count; i++) {
			iteration();
		}
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
		this.iterations = iteration;
	}
}
