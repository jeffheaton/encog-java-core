package org.encog.neural.networks.training;

import org.encog.neural.networks.BasicNetwork;

/**
 * Used by simulated annealing and genetic algorithms to calculate the score
 * for a neural network.  This allows networks to be ranked.  We may be seeking
 * a high or a low score, depending on the value the shouldMinimize
 * method returns.
 */
public interface CalculateScore {
	
	/**
	 * Calculate this network's score.
	 * @param network The network.
	 * @return The score.
	 */
	double calculateScore(BasicNetwork network);
	
	/**
	 * @return True if the goal is to minimize the score.
	 */
	boolean shouldMinimize();
}
