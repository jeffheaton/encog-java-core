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
package org.encog.ml.genetic.genome;

import java.util.Comparator;

/**
 * Used to compare two genomes, a score object is used.
 */
public class GenomeComparator implements Comparator<Genome> {

	/**
	 * The method to calculate the score.
	 */
	private final CalculateGenomeScore calculateScore;

	/**
	 * Construct the genome comparator.
	 * 
	 * @param theCalculateScore
	 *            The score calculation object to use.
	 */
	public GenomeComparator(final CalculateGenomeScore theCalculateScore) {
		this.calculateScore = theCalculateScore;
	}

	/**
	 * Apply a bonus, this is a simple percent that is applied in the direction
	 * specified by the "should minimize" property of the score function.
	 * 
	 * @param value
	 *            The current value.
	 * @param bonus
	 *            The bonus.
	 * @return The resulting value.
	 */
	public final double applyBonus(final double value, final double bonus) {
		final double amount = value * bonus;
		if (this.calculateScore.shouldMinimize()) {
			return value - amount;
		} else {
			return value + amount;
		}
	}

	/**
	 * Apply a penalty, this is a simple percent that is applied in the
	 * direction specified by the "should minimize" property of the score
	 * function.
	 * 
	 * @param value
	 *            The current value.
	 * @param bonus
	 *            The penalty.
	 * @return The resulting value.
	 */
	public final double applyPenalty(final double value, final double bonus) {
		final double amount = value * bonus;
		if (this.calculateScore.shouldMinimize()) {
			return value - amount;
		} else {
			return value + amount;
		}
	}

	/**
	 * Determine the best score from two scores, uses the "should minimize"
	 * property of the score function.
	 * 
	 * @param d1
	 *            The first score.
	 * @param d2
	 *            The second score.
	 * @return The best score.
	 */
	public final double bestScore(final double d1, final double d2) {
		if (this.calculateScore.shouldMinimize()) {
			return Math.min(d1, d2);
		} else {
			return Math.max(d1, d2);
		}
	}

	/**
	 * Compare two genomes.
	 * 
	 * @param genome1
	 *            The first genome.
	 * @param genome2
	 *            The second genome.
	 * @return Zero if equal, or less than or greater than zero to indicate
	 *         order.
	 */
	@Override
	public final int compare(final Genome genome1, final Genome genome2) {
		return Double.compare(genome1.getScore(), genome2.getScore());
	}

	/**
	 * @return The score calculation object.
	 */
	public final CalculateGenomeScore getCalculateScore() {
		return this.calculateScore;
	}

	/**
	 * Determine if one score is better than the other.
	 * 
	 * @param d1
	 *            The first score to compare.
	 * @param d2
	 *            The second score to compare.
	 * @return True if d1 is better than d2.
	 */
	public final boolean isBetterThan(final double d1, final double d2) {
		if (this.calculateScore.shouldMinimize()) {
			return d1 < d2;
		} else {
			return d1 > d2;
		}
	}

}
