package org.encog.neural.networks.training;

/**
 * The batch size. Specify 1 for pure online training. Specify 0 for pure batch
 * training (complete training set in one batch). Otherwise specify the batch
 * size for batch training.
 */
public interface BatchSize {
	/**
	 * The batch size. Specify 1 for pure online training. Specify 0 for pure
	 * batch training (complete training set in one batch). Otherwise specify
	 * the batch size for batch training.
	 * 
	 * @return The batch size.
	 */
	int getBatchSize();

	/**
	 * Set the batch size. Specify 1 for pure online training. Specify 0 for
	 * pure batch training (complete training set in one batch). Otherwise
	 * specify the batch size for batch training.
	 * 
	 * @param theBatchSize The batch size.
	 */
	void setBatchSize(int theBatchSize);
}
