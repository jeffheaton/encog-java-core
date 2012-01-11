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
package org.encog.mathutil.randomize;

import org.encog.EncogError;
import org.encog.util.EngineArray;

/**
 * Generate random choices unevenly.  This class is used to select random 
 * choices from a list, with a probability weight places on each item 
 * in the list.
 */
public class RandomChoice {

	/**
	 * The probabilities of each item in the list.
	 */
	final private double[] probabilities;

	/**
	 * Construct a list of probabilities.
	 * @param theProbabilities The probability of each item in the list.
	 */
	public RandomChoice(double[] theProbabilities) {
		this.probabilities = EngineArray.arrayCopy(theProbabilities);

		double total = 0;
		for (int i = 0; i < probabilities.length; i++) {
			total += probabilities[i];
		}

		if (total == 0.0) {
			double prob = 1.0 / probabilities.length;
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = prob;
			}
		} else {
			double total2 = 0;
			double factor = 1.0 / total;
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = probabilities[i] * factor;
				total2 += probabilities[i];
			}

			if (Math.abs(1.0 - total2) > 0.02) {
				double prob = 1.0 / probabilities.length;
				for (int i = 0; i < probabilities.length; i++) {
					probabilities[i] = prob;
				}
			}
		}
	}

	public static boolean generate(double p) {
		return Math.random() < p;
	}

	public int generate() {
		double r = Math.random();
		double sum = 0.0;

		for (int i = 0; i < probabilities.length; i++) {
			sum += probabilities[i];
			if (r < sum) {
				return i;
			}
		}

		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] != 0.0) {
				return i;
			}
		}

		throw new EncogError("Invalid probabilities.");
	}

	public int generate(int skip) {
		double totalProb = 1.0 - probabilities[skip];

		double throwValue = Math.random() * totalProb;
		double accumulator = 0.0;

		for (int i = 0; i < skip; i++) {
			accumulator += probabilities[i];
			if (accumulator > throwValue) {
				return i;
			}
		}

		for (int i = skip + 1; i < probabilities.length; i++) {
			accumulator += probabilities[i];
			if (accumulator > throwValue) {
				return i;
			}
		}

		for (int i = 0; i < skip; i++) {
			if (probabilities[i] != 0.0) {
				return i;
			}
		}
		for (int i = skip + 1; i < probabilities.length; i++) {
			if (probabilities[i] != 0.0) {
				return i;
			}
		}

		return -1;
	}
}
