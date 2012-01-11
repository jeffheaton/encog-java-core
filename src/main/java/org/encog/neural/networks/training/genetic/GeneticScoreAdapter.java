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
package org.encog.neural.networks.training.genetic;

import org.encog.ml.MLRegression;
import org.encog.ml.genetic.genome.CalculateGenomeScore;
import org.encog.ml.genetic.genome.Genome;
import org.encog.neural.networks.training.CalculateScore;

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
	public final double calculateScore(final Genome genome) {
		final MLRegression network = (MLRegression) genome.getOrganism();
		return this.calculateScore.calculateScore(network);
	}

	/**
	 * @return True, if the score should be minimized.
	 */
	public final boolean shouldMinimize() {
		return this.calculateScore.shouldMinimize();
	}

}
