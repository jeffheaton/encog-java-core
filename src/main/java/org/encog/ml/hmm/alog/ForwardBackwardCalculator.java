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

/**
 * The forward-backward algorithm is an inference algorithm for hidden Markov
 * models which computes the posterior marginals of all hidden state variables
 * given a sequence of observations.
 * 
 * 
 */
public class ForwardBackwardCalculator {
	public static enum Computation {
		ALPHA, BETA
	};

	/**
	 * Alpha matrix.
	 */
	protected double[][] alpha = null;
	
	/**
	 * Beta matrix.
	 */
	protected double[][] beta = null;
	
	/**
	 * Probability.
	 */
	protected double probability;

	/**
	 * Construct an empty object.
	 */
	protected ForwardBackwardCalculator() {
	};

	/**
	 * Construct the forward/backward calculator.
	 * @param oseq The sequence to use.
	 * @param hmm THe hidden markov model to use.
	 */
	public ForwardBackwardCalculator(final MLDataSet oseq,
			final HiddenMarkovModel hmm) {
		this(oseq, hmm, EnumSet.of(Computation.ALPHA));
	}

	/**
	 * Construct the object. 
	 * @param oseq The sequence.
	 * @param hmm The hidden markov model to use.
	 * @param flags Flags, alpha or beta.
	 */
	public ForwardBackwardCalculator(final MLDataSet oseq,
			final HiddenMarkovModel hmm, final EnumSet<Computation> flags) {
		if (oseq.size() < 1) {
			throw new IllegalArgumentException("Empty sequence");
		}

		if (flags.contains(Computation.ALPHA)) {
			computeAlpha(hmm, oseq);
		}

		if (flags.contains(Computation.BETA)) {
			computeBeta(hmm, oseq);
		}

		computeProbability(oseq, hmm, flags);
	}

	/**
	 * Alpha element.
	 * @param t The row.
	 * @param i The column.
	 * @return The element.
	 */
	public double alphaElement(final int t, final int i) {
		if (this.alpha == null) {
			throw new UnsupportedOperationException("Alpha array has not "
					+ "been computed");
		}

		return this.alpha[t][i];
	}

	/**
	 * Beta element, best element.
	 * @param t From.
	 * @param i To.
	 * @return The element.
	 */
	public double betaElement(final int t, final int i) {
		if (this.beta == null) {
			throw new UnsupportedOperationException("Beta array has not "
					+ "been computed");
		}

		return this.beta[t][i];
	}

	/**
	 * Compute alpha.
	 * @param hmm The hidden markov model.
	 * @param oseq The sequence.
	 */
	protected void computeAlpha(final HiddenMarkovModel hmm,
			final MLDataSet oseq) {
		this.alpha = new double[oseq.size()][hmm.getStateCount()];

		for (int i = 0; i < hmm.getStateCount(); i++) {
			computeAlphaInit(hmm, oseq.get(0), i);
		}

		final Iterator<MLDataPair> seqIterator = oseq.iterator();
		if (seqIterator.hasNext()) {
			seqIterator.next();
		}

		for (int t = 1; t < oseq.size(); t++) {
			final MLDataPair observation = seqIterator.next();

			for (int i = 0; i < hmm.getStateCount(); i++) {
				computeAlphaStep(hmm, observation, t, i);
			}
		}
	}

	/**
	 * Compute the alpha init.
	 * @param hmm THe hidden markov model.
	 * @param o The element.
	 * @param i The state.
	 */
	protected void computeAlphaInit(final HiddenMarkovModel hmm,
			final MLDataPair o, final int i) {
		this.alpha[0][i] = hmm.getPi(i)
				* hmm.getStateDistribution(i).probability(o);
	}

	/**
	 * Compute the alpha step.
	 * @param hmm The hidden markov model.
	 * @param o The sequence element.
	 * @param t The alpha step.
	 * @param j Thr column.
	 */
	protected void computeAlphaStep(final HiddenMarkovModel hmm,
			final MLDataPair o, final int t, final int j) {
		double sum = 0.;

		for (int i = 0; i < hmm.getStateCount(); i++) {
			sum += this.alpha[t - 1][i] * hmm.getTransitionProbability(i, j);
		}

		this.alpha[t][j] = sum * hmm.getStateDistribution(j).probability(o);
	}

	/**
	 * Compute the beta step.
	 * @param hmm The hidden markov model.
	 * @param oseq The sequence.
	 */
	protected void computeBeta(final HiddenMarkovModel hmm, final MLDataSet oseq) {
		this.beta = new double[oseq.size()][hmm.getStateCount()];

		for (int i = 0; i < hmm.getStateCount(); i++) {
			this.beta[oseq.size() - 1][i] = 1.;
		}

		for (int t = oseq.size() - 2; t >= 0; t--) {
			for (int i = 0; i < hmm.getStateCount(); i++) {
				computeBetaStep(hmm, oseq.get(t + 1), t, i);
			}
		}
	}

	/**
	 * Compute the beta step.
	 * @param hmm The hidden markov model.
	 * @param o THe data par to compute.
	 * @param t THe matrix row.
	 * @param i THe matrix column.
	 */
	protected void computeBetaStep(final HiddenMarkovModel hmm,
			final MLDataPair o, final int t, final int i) {
		double sum = 0.;

		for (int j = 0; j < hmm.getStateCount(); j++) {
			sum += this.beta[t + 1][j] * hmm.getTransitionProbability(i, j)
					* hmm.getStateDistribution(j).probability(o);
		}

		this.beta[t][i] = sum;
	}

	/**
	 * Compute the probability.
	 * @param oseq The sequence.
	 * @param hmm THe hidden markov model.
	 * @param flags The flags.
	 */
	private void computeProbability(final MLDataSet oseq,
			final HiddenMarkovModel hmm, final EnumSet<Computation> flags) {
		this.probability = 0.;

		if (flags.contains(Computation.ALPHA)) {
			for (int i = 0; i < hmm.getStateCount(); i++) {
				this.probability += this.alpha[oseq.size() - 1][i];
			}
		} else {
			for (int i = 0; i < hmm.getStateCount(); i++) {
				this.probability += hmm.getPi(i)
						* hmm.getStateDistribution(i).probability(oseq.get(0))
						* this.beta[0][i];
			}
		}
	}

	/**
	 * @return The probability.
	 */
	public double probability() {
		return this.probability;
	}
}
