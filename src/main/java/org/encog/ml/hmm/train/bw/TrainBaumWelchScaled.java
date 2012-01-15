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

import java.util.EnumSet;
import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.ForwardBackwardCalculator;
import org.encog.ml.hmm.alog.ForwardBackwardScaledCalculator;

/**
 * Baum Welch Learning allows a HMM to be constructed from a series of sequence
 * observations. This implementation of Baum Welch scales and is not as 
 * susceptible to underflows in long sequences of data as the regular Baum Welch
 * algorithm.
 * 
 * Baum Welch requires a starting point.  You should create a HMM that has a
 * reasonable guess as to the observation and transition probabilities.  If you 
 * can make no such guess, you should consider using KMeans training.
 * 
 * L. E. Baum, T. Petrie, G. Soules, and N. Weiss,
 * "A maximization technique occurring in the statistical analysis of probabilistic functions of Markov chains"
 * , Ann. Math. Statist., vol. 41, no. 1, pp. 164-171, 1970.
 * 
 * Hidden Markov Models and the Baum-Welch Algorithm, IEEE Information Theory
 * Society Newsletter, Dec. 2003.
 */
public class TrainBaumWelchScaled extends BaseBaumWelch {
	public TrainBaumWelchScaled(final HiddenMarkovModel hmm,
			final MLSequenceSet training) {
		super(hmm, training);
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

		final Iterator<MLDataPair> seqIterator = sequence.iterator();
		seqIterator.next();

		for (int t = 0; t < (sequence.size() - 1); t++) {
			final MLDataPair observation = seqIterator.next();

			for (int i = 0; i < hmm.getStateCount(); i++) {
				for (int j = 0; j < hmm.getStateCount(); j++) {
					xi[t][i][j] = fbc.alphaElement(t, i)
							* hmm.getTransitionProbability(i, j)
							* hmm.getStateDistribution(j).probability(
									observation) * fbc.betaElement(t + 1, j);
				}
			}
		}

		return xi;
	}

	@Override
	public ForwardBackwardCalculator generateForwardBackwardCalculator(
			final MLDataSet sequence, final HiddenMarkovModel hmm) {
		return new ForwardBackwardScaledCalculator(sequence, hmm,
				EnumSet.allOf(ForwardBackwardCalculator.Computation.class));
	}
}
