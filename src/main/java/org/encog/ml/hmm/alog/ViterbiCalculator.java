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

/**
 * The Viterbi algorithm is used to find the most likely sequence of hidden
 * states (called the Viterbi path) that results in a sequence of observed
 * events. Used for the Markov information sources, and more generally, hidden
 * Markov models (HMM).
 * 
 * Viterbi AJ (April 1967).
 * "Error bounds for convolutional codes and an asymptotically optimum decoding algorithm"
 * . IEEE Transactions on Information Theory 13 (2): 260-269.
 * doi:10.1109/TIT.1967.1054010.
 */
public class ViterbiCalculator {
	private final double[][] delta;
	private final int[][] psy;
	private final int[] stateSequence;
	private double lnProbability;

	public ViterbiCalculator(final MLDataSet oseq, final HiddenMarkovModel hmm) {
		if (oseq.size() < 1) {
			throw new IllegalArgumentException("Must not have empty sequence");
		}

		this.delta = new double[oseq.size()][hmm.getStateCount()];
		this.psy = new int[oseq.size()][hmm.getStateCount()];
		this.stateSequence = new int[oseq.size()];

		for (int i = 0; i < hmm.getStateCount(); i++) {
			this.delta[0][i] = -Math.log(hmm.getPi(i))
					- Math.log(hmm.getStateDistribution(i).probability(
							oseq.get(0)));
			this.psy[0][i] = 0;
		}

		final Iterator<MLDataPair> oseqIterator = oseq.iterator();
		if (oseqIterator.hasNext()) {
			oseqIterator.next();
		}

		int t = 1;
		while (oseqIterator.hasNext()) {
			final MLDataPair observation = oseqIterator.next();

			for (int i = 0; i < hmm.getStateCount(); i++) {
				computeStep(hmm, observation, t, i);
			}

			t++;
		}

		this.lnProbability = Double.MAX_VALUE;
		for (int i = 0; i < hmm.getStateCount(); i++) {
			final double thisProbability = this.delta[oseq.size() - 1][i];

			if (this.lnProbability > thisProbability) {
				this.lnProbability = thisProbability;
				this.stateSequence[oseq.size() - 1] = i;
			}
		}
		this.lnProbability = -this.lnProbability;

		for (int t2 = oseq.size() - 2; t2 >= 0; t2--) {
			this.stateSequence[t2] = this.psy[t2 + 1][this.stateSequence[t2 + 1]];
		}
	}

	private void computeStep(final HiddenMarkovModel hmm, final MLDataPair o,
			final int t, final int j) {
		double minDelta = Double.MAX_VALUE;
		int min_psy = 0;

		for (int i = 0; i < hmm.getStateCount(); i++) {
			final double thisDelta = this.delta[t - 1][i]
					- Math.log(hmm.getTransitionProbability(i, j));

			if (minDelta > thisDelta) {
				minDelta = thisDelta;
				min_psy = i;
			}
		}

		this.delta[t][j] = minDelta
				- Math.log(hmm.getStateDistribution(j).probability(o));
		this.psy[t][j] = min_psy;
	}

	public double lnProbability() {
		return this.lnProbability;
	}

	public int[] stateSequence() {
		return this.stateSequence.clone();
	}
}
