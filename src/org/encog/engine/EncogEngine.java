package org.encog.engine;

import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.opencl.EncogCL;


public class EncogEngine {

	
	public final static double DEFAULT_ZERO_TOLERANCE = 0.000001;
	
	/**
	 * The instance.
	 */
	private static EncogEngine instance;

	/**
	 * If Encog is not using GPU/CL processing this attribute will be null.
	 * Otherwise it holds the Encog CL object.
	 */
	private EncogCL cl;

	/**
	 * Get the instance to the singleton.
	 * 
	 * @return The instance.
	 */
	public static EncogEngine getInstance() {
		if (EncogEngine.instance == null) {
			EncogEngine.instance = new EncogEngine();
		}
		return EncogEngine.instance;
	}
	
	/**
	 * Enable OpenCL processing. OpenCL processing allows Encog to use GPU
	 * devices to speed calculations. Not all areas of Encog can use this,
	 * however, GPU's can currently accelerate the training of Feedforward
	 * neural networks.
	 * 
	 * To make use of the GPU you must have OpenCL drivers installed. For more
	 * information on getting OpenCL drivers, visit the following URL.
	 * 
	 * http://www.heatonresearch.com/encog/opencl
	 */
	public void initCL() {
		try {
			EncogCL cl = new EncogCL();
			this.cl = cl;
		} catch (Throwable e) {
			throw new EncogEngineError(e);
		}
	}

	/**
	 * Provides any shutdown that Encog may need. Currently this shuts down the
	 * thread pool.
	 */
	public void shutdown() {
		EngineConcurrency.getInstance().shutdown(10000);
	}

	/**
	 * @return If Encog is not using GPU/CL processing this attribute will be
	 *         null. Otherwise it holds the Encog CL object.
	 */
	public EncogCL getCL() {
		return this.cl;
	}

	
}
