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
