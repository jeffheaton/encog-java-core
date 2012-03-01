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

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.hmm.HiddenMarkovModel;

/**
 * The forward-backward algorithm is an inference algorithm for hidden Markov
 * models which computes the posterior marginals of all hidden state variables
 * given a sequence of observations. This version makes use of scaling, and will
 * not generate underflows with long sequences.
 * 
 */
public class ForwardBackwardScaledCalculator extends ForwardBackwardCalculator {
	private final double[] ctFactors;
	private double lnProbability;

	public ForwardBackwardScaledCalculator(final MLDataSet oseq,
			final HiddenMarkovModel hmm) {
		this(oseq, hmm, EnumSet.of(Computation.ALPHA));
	}

	public ForwardBackwardScaledCalculator(
			final MLDataSet oseq, final HiddenMarkovModel hmm,
			final EnumSet<Computation> flags) {
		if (oseq.size() < 1) {
			throw new IllegalArgumentException();
		}

		this.ctFactors = new double[oseq.size()];
		Arrays.fill(this.ctFactors, 0.);

		computeAlpha(hmm, oseq);

		if (flags.contains(Computation.BETA)) {
			computeBeta(hmm, oseq);
		}

		computeProbability(oseq, hmm, flags);
	}

	@Override
	protected void computeAlpha(final HiddenMarkovModel hmm,
			final MLDataSet oseq) {
		this.alpha = new double[oseq.size()][hmm.getStateCount()];

		for (int i = 0; i < hmm.getStateCount(); i++) {
			computeAlphaInit(hmm, oseq.get(0), i);
		}
		scale(this.ctFactors, this.alpha, 0);

		final Iterator<MLDataPair> seqIterator = oseq.iterator();
		if (seqIterator.hasNext()) {
			seqIterator.next();
		}

		for (int t = 1; t < oseq.size(); t++) {
			final MLDataPair observation = seqIterator.next();

			for (int i = 0; i < hmm.getStateCount(); i++) {
				computeAlphaStep(hmm, observation, t, i);
			}
			scale(this.ctFactors, this.alpha, t);
		}
	}

	@Override
	protected void computeBeta(final HiddenMarkovModel hmm, final MLDataSet oseq) {
		this.beta = new double[oseq.size()][hmm.getStateCount()];

		for (int i = 0; i < hmm.getStateCount(); i++) {
			this.beta[oseq.size() - 1][i] = 1. / this.ctFactors[oseq.size() - 1];
		}

		for (int t = oseq.size() - 2; t >= 0; t--) {
			for (int i = 0; i < hmm.getStateCount(); i++) {
				computeBetaStep(hmm, oseq.get(t + 1), t, i);
				this.beta[t][i] /= this.ctFactors[t];
			}
		}
	}

	private void computeProbability(final MLDataSet oseq,
			final HiddenMarkovModel hmm, final EnumSet<Computation> flags) {
		this.lnProbability = 0.;

		for (int t = 0; t < oseq.size(); t++) {
			this.lnProbability += Math.log(this.ctFactors[t]);
		}

		this.probability = Math.exp(this.lnProbability);
	}

	public double lnProbability() {
		return this.lnProbability;
	}

	private void scale(final double[] ctFactors, final double[][] array,
			final int t) {
		final double[] table = array[t];
		double sum = 0.;

		for (final double element : table) {
			sum += element;
		}

		ctFactors[t] = sum;
		for (int i = 0; i < table.length; i++) {
			table[i] /= sum;
		}
	}
}
