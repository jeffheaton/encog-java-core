package org.encog.engine.network.train;

import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;

public interface TrainFlatNetwork {

	/**
	 * @return The error from the neural network.
	 */
	double getError();

	/**
	 * @return The trained neural network.
	 */
	FlatNetwork getNetwork();

	/**
	 * @return The target OpenCL device.
	 */
	EncogCLDevice getTargetDevice();

	/**
	 * @return The data we are training with.
	 */
	EngineDataSet getTraining();

	/**
	 * Perform one training iteration.
	 */
	void iteration();
	/**
	 * Set the number of threads to use.
	 * 
	 * @param numThreads
	 *            The number of threads to use.
	 */
	void setNumThreads(final int numThreads);
	/**
	 * Set the target device.
	 * @param targetDevice The target device.
	 */
	void setTargetDevice(final EncogCLDevice targetDevice);

}
