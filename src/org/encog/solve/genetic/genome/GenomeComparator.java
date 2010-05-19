/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.solve.genetic.genome;

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
	 * @param calculateScore
	 *            The score calculation object to use.
	 */
	public GenomeComparator(final CalculateGenomeScore calculateScore) {
		this.calculateScore = calculateScore;
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
	public double applyBonus(final double value, final double bonus) {
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
	public double applyPenalty(final double value, final double bonus) {
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
	public double bestScore(final double d1, final double d2) {
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
	public int compare(final Genome genome1, final Genome genome2) {
		return Double.compare(genome1.getScore(), genome2.getScore());
	}

	/**
	 * @return The score calculation object.
	 */
	public CalculateGenomeScore getCalculateScore() {
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
	public boolean isBetterThan(final double d1, final double d2) {
		if (this.calculateScore.shouldMinimize()) {
			return d1 < d2;
		} else {
			return d2 > d1;
		}
	}

}
