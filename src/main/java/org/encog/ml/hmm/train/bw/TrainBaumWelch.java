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
import java.util.EnumSet;
import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.ForwardBackwardCalculator;

/**
 * Baum Welch Learning allows a HMM to be constructed from a series of sequence
 * observations. This implementation of Baum Welch does not scale and is
 * susceptible to underflows in long sequences of data.
 * 
 * Baum Welch requires a starting point. You should create a HMM that has a
 * reasonable guess as to the observation and transition probabilities. If you
 * can make no such guess, you should consider using KMeans training.
 * 
 * L. E. Baum, T. Petrie, G. Soules, and N. Weiss,
 * "A maximization technique occurring in the statistical analysis of probabilistic functions of Markov chains"
 * , Ann. Math. Statist., vol. 41, no. 1, pp. 164-171, 1970.
 * 
 * Hidden Markov Models and the Baum-Welch Algorithm, IEEE Information Theory
 * Society Newsletter, Dec. 2003.
 */
public class TrainBaumWelch extends BaseBaumWelch {
	public TrainBaumWelch(final HiddenMarkovModel hmm,
			final MLSequenceSet training) {
		super(hmm, training);
	}

	@Override
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

	@Override
	public double[][][] estimateXi(final MLDataSet sequence,
			final ForwardBackwardCalculator fbc, final HiddenMarkovModel hmm) {
		if (sequence.size() <= 1) {
			throw new IllegalArgumentException(
					"Must have more than one observation");
		}

		final double xi[][][] = new double[sequence.size() - 1][hmm
				.getStateCount()][hmm.getStateCount()];
		final double probability = fbc.probability();

		final Iterator<MLDataPair> seqIterator = sequence.iterator();
		seqIterator.next();

		for (int t = 0; t < (sequence.size() - 1); t++) {
			final MLDataPair o = seqIterator.next();

			for (int i = 0; i < hmm.getStateCount(); i++) {
				for (int j = 0; j < hmm.getStateCount(); j++) {
					xi[t][i][j] = (fbc.alphaElement(t, i)
							* hmm.getTransitionProbability(i, j)
							* hmm.getStateDistribution(j).probability(o) * fbc
								.betaElement(t + 1, j)) / probability;
				}
			}
		}

		return xi;
	}

	@Override
	public ForwardBackwardCalculator generateForwardBackwardCalculator(
			final MLDataSet sequence, final HiddenMarkovModel hmm) {
		return new ForwardBackwardCalculator(sequence, hmm,
				EnumSet.allOf(ForwardBackwardCalculator.Computation.class));
	}

}
