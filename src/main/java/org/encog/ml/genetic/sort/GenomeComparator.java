package org.encog.ml.genetic.sort;

import java.util.Comparator;

import org.encog.ml.genetic.genome.Genome;

public interface GenomeComparator extends Comparator<Genome> {
	boolean shouldMinimize();
	
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
	double applyBonus(final double value, final double bonus);

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
	double applyPenalty(final double value, final double bonus);

	/**
	 * Determine if one score is better than the other.
	 * 
	 * @param d1
	 *            The first score to compare.
	 * @param d2
	 *            The second score to compare.
	 * @return True if d1 is better than d2.
	 */
	boolean isBetterThan(double d1, double d2);

	boolean isBetterThan(Genome prg, Genome bestGenome);
	
}
