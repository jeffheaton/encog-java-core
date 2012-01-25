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

import java.io.Serializable;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

/**
 * This class represents a "state distribution". This is the means by which the
 * probabilities between the states and observations are mapped. Currently two
 * are supported. Use ContinousDistribution to use a Gaussian-based continuous
 * distribution. Use DiscreteDistribution for a item-based distribution.
 * 
 */
public interface StateDistribution extends Cloneable, Serializable {

	/**
	 * @return A clone of this distribution.
	 */
	StateDistribution clone();

	/**
	 * Fit this distribution to the specified data set.
	 * 
	 * @param set
	 *            The data set to fit to.
	 */
	void fit(MLDataSet set);

	/**
	 * Fit this distribution to the specified data set, given the specified
	 * weights, per element.
	 * 
	 * @param set
	 *            The data set to fit to.
	 * @param weights
	 *            The weights.
	 */
	void fit(MLDataSet set, double[] weights);

	/**
	 * Generate a random data pair, based on the probabilities.
	 * 
	 * @return A random data pair.
	 */
	MLDataPair generate();

	/**
	 * Determine the probability of the specified data pair.
	 * @param o The pair to consider.
	 * @return The probability.
	 */
	double probability(MLDataPair o);
}
