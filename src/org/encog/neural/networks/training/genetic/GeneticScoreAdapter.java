/*
 * Encog(tm) Core v2.5 
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

package org.encog.neural.networks.training.genetic;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.solve.genetic.genome.CalculateGenomeScore;
import org.encog.solve.genetic.genome.Genome;

/**
 * This adapter allows a CalculateScore object to be used to calculate a
 * Genome's score, where a CalculateGenomeScore object would be called for.
 */
public class GeneticScoreAdapter implements CalculateGenomeScore {

	/**
	 * The calculate score object to use.
	 */
	private final CalculateScore calculateScore;

	/**
	 * Construct the adapter.
	 * @param calculateScore The CalculateScore object to use.
	 */
	public GeneticScoreAdapter(final CalculateScore calculateScore) {
		this.calculateScore = calculateScore;
	}

	/**
	 * Calculate the genome's score.
	 * @param genome The genome to calculate for.
	 * @return The calculated score.
	 */
	public double calculateScore(final Genome genome) {
		final BasicNetwork network = (BasicNetwork) genome.getOrganism();
		return this.calculateScore.calculateScore(network);
	}

	/**
	 * @return True, if the score should be minimized.
	 */
	public boolean shouldMinimize() {
		return this.calculateScore.shouldMinimize();
	}

}
