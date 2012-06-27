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
package org.encog.ml.hmm.distributions;

import java.util.Arrays;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * A discrete distribution is a distribution with a finite set of states that it
 * can be in.
 * 
 */
public class DiscreteDistribution implements StateDistribution {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The probabilities of moving between states.
	 */
	private final double[][] probabilities;

	/**
	 * Construct a discrete distribution with the specified probabilities.
	 * @param theProbabilities The probabilities.
	 */
	public DiscreteDistribution(final double[][] theProbabilities) {

		if (theProbabilities.length == 0) {
			throw new IllegalArgumentException("Invalid empty array");
		}

		this.probabilities = new double[theProbabilities.length][];

		for (int i = 0; i < theProbabilities.length; i++) {

			if (theProbabilities[i].length == 0) {
				throw new IllegalArgumentException("Invalid empty array");
			}

			this.probabilities[i] = new double[theProbabilities[i].length];

			for (int j = 0; j < probabilities[i].length; j++) {
				if ((this.probabilities[i][j] = theProbabilities[i][j]) < 0.0) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/**
	 * Construct a discrete distribution.
	 * @param cx The count of each.
	 */
	public DiscreteDistribution(final int[] cx) {
		this.probabilities = new double[cx.length][];
		for (int i = 0; i < cx.length; i++) {
			int c = cx[i];
			this.probabilities[i] = new double[c];

			for (int j = 0; j < c; j++) {
				this.probabilities[i][j] = 1.0 / c;
			}
		}
	}

	/**
	 * @return A clone of the distribution.
	 */
	@Override
	public DiscreteDistribution clone() {
		try {
			return (DiscreteDistribution) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Fit this distribution to the specified data.
	 * @param co THe data to fit to.
	 */
	@Override
	public void fit(final MLDataSet co) {
		if (co.size() < 1) {
			throw new IllegalArgumentException("Empty observation set");
		}

		for (int i = 0; i < this.probabilities.length; i++) {

			for (int j = 0; j < this.probabilities[i].length; j++) {
				this.probabilities[i][j] = 0.0;
			}

			for (final MLDataPair o : co) {
				this.probabilities[i][(int) o.getInput().getData(i)]++;
			}

			for (int j = 0; j < this.probabilities[i].length; j++) {
				this.probabilities[i][j] /= co.size();
			}
		}
	}

	/**
	 * Fit this distribution to the specified data, with weights.
	 * @param co The data to fit to.
	 * @param weights The weights.
	 */
	@Override
	public void fit(final MLDataSet co, final double[] weights) {
		if ((co.size() < 1) || (co.size() != weights.length)) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < this.probabilities.length; i++) {
			Arrays.fill(this.probabilities[i], 0.0);

			int j = 0;
			for (final MLDataPair o : co) {
				this.probabilities[i][(int) o.getInput().getData(i)] += weights[j++];
			}
		}
	}

	/**
	 * Generate a random sequence.
	 * @return The next element.
	 */
	@Override
	public MLDataPair generate() {
		final MLData result = new BasicMLData(this.probabilities.length);

		for (int i = 0; i < this.probabilities.length; i++) {
			double rand = Math.random();

			result.setData(i, this.probabilities[i].length - 1);
			for (int j = 0; j < (this.probabilities[i].length - 1); j++) {
				if ((rand -= this.probabilities[i][j]) < 0.0) {
					result.setData(i, j);
					break;
				}
			}
		}

		return new BasicMLDataPair(result);
	}

	/**
	 * Determine the probability of the specified data pair.
	 * @param o THe data pair.
	 */
	@Override
	public double probability(final MLDataPair o) {

		double result = 1;

		for (int i = 0; i < this.probabilities.length; i++) {
			if (o.getInput().getData(i) > (this.probabilities[i].length - 1)) {
				throw new IllegalArgumentException("Wrong observation value");
			}
			result *= this.probabilities[i][(int) o.getInput().getData(i)];
		}

		return result;
	}

	/**
	 * @return The state probabilities.
	 */
	public double[][] getProbabilities() {
		return this.probabilities;
	}

}
