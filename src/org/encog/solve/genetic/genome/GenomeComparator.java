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

import org.encog.neural.networks.training.neat.NEATGenome;

/**
 * Used to comapre two genomes.
 */
public class GenomeComparator implements Comparator<Genome> {
	private final CalculateGenomeScore calculateScore;

	public GenomeComparator(final CalculateGenomeScore calculateScore) {
		this.calculateScore = calculateScore;
	}

	public double applyBonus(final double value, final double bonus) {
		final double amount = value * bonus;
		if (calculateScore.shouldMinimize()) {
			return value - amount;
		} else {
			return value + amount;
		}
	}

	public double applyPenalty(final double value, final double bonus) {
		final double amount = value * bonus;
		if (calculateScore.shouldMinimize()) {
			return value - amount;
		} else {
			return value + amount;
		}
	}

	public double bestScore(final double d1, final double d2) {
		if (calculateScore.shouldMinimize()) {
			return Math.min(d1, d2);
		} else {
			return Math.max(d1, d2);
		}
	}

	public int compare(final Genome genome1, final Genome genome2) {
		return Double.compare(genome1.getScore(), genome2.getScore());
	}

	public int compare(final NEATGenome g1, final NEATGenome g2) {
		if (calculateScore.shouldMinimize()) {
			return Double.compare(g1.getScore(), g2.getScore());
		} else {
			return Double.compare(g2.getScore(), g1.getScore());
		}
	}

	public CalculateGenomeScore getCalculateScore() {
		return calculateScore;
	}

	public boolean isBetterThan(final double d1, final double d2) {
		if (calculateScore.shouldMinimize()) {
			return d1 < d2;
		} else {
			return d2 > d1;
		}
	}

}
