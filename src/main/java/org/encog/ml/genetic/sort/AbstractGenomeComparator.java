package org.encog.ml.genetic.sort;

public abstract class AbstractGenomeComparator implements GenomeComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double applyBonus(final double value, final double bonus) {
		final double amount = value * bonus;
		if ( shouldMinimize()) {
			return value - amount;
		} else {
			return value + amount;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double applyPenalty(final double value, final double bonus) {
		final double amount = value * bonus;
		if (!shouldMinimize()) {
			return value - amount;
		} else {
			return value + amount;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBetterThan(final double d1, final double d2) {
		if (shouldMinimize()) {
			return d1 < d2;
		} else {
			return d1 > d2;
		}
	}
	
	
}
