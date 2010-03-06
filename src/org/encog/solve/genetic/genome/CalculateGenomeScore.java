package org.encog.solve.genetic.genome;

import org.encog.neural.networks.BasicNetwork;

public interface CalculateGenomeScore {
	/**
	 * Calculate this genome's score.
	 * @param network The network.
	 * @return The score.
	 */
	double calculateScore(Genome genome);
	
	/**
	 * @return True if the goal is to minimize the score.
	 */
	boolean shouldMinimize();
}
