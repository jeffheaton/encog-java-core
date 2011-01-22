/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.engine;

import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.opencl.EncogCL;

/**
 * The Encog Engine.
 * 
 * The Engine forms the core of Encog's neural network calculation and training.
 * 
 */
public class EncogEngine {

	/**
	 * The default zero tolerance.
	 */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.000000000001;

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
		EncogCL cl = new EncogCL();
		this.cl = cl;
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
