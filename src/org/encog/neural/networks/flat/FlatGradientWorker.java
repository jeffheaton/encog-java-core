package org.encog.neural.networks.flat;

import org.encog.util.concurrency.EncogTask;

/**
 * An interface used to define gradient workers for flat networks.
 *
 */
public interface FlatGradientWorker extends EncogTask {

    /**
     * @return The weights for this worker.
     */
    double[] getWeights();

    /**
     * @return The elapsed time for the last iteration of this worker.
     */
    long getElapsedTime();
	
}
