package org.encog.neural.networks.training.strategy.end;

import org.encog.neural.networks.training.Strategy;

public interface EndTrainingStrategy extends Strategy {
	/**
	 * @return True if training should stop.
	 */
	boolean shouldStop();

}
