/*
 * Encog(tm) Core v2.5 - Java Version
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
package org.encog.engine.concurrency.calc;

import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.kernels.KernelNetworkCalc;

/**
 * Holds an OpenCL device to perform calculation on.
 */
public class CalcOpenCLDevice {

	/**
	 * The device.
	 */
	private final EncogCLDevice device;
	
	/**
	 * The owner object.
	 */
	private final ConcurrentCalculate calc;
	
	/**
	 * The Kernel to use for calculation.
	 */
	private final KernelNetworkCalc kernelCalc;
	
	/**
	 * Is this OpenCL device busy?
	 */
	private boolean busy;

	/**
	 * Construct a device to use.
	 * @param device The underlying device.
	 * @param calc The owner.
	 */
	public CalcOpenCLDevice(final EncogCLDevice device,
			final ConcurrentCalculate calc) {
		super();
		this.device = device;
		this.calc = calc;
		this.kernelCalc = new KernelNetworkCalc(this.device);
	}

	/**
	 * Calculate the error for the neural network using the training set. If
	 * OpenCL is available, and enabled, the OpenCL device will be used to
	 * attempt to calculate the error.
	 * 
	 * @return The error.
	 */
	public CalculationResult calculateError() {
		if (this.busy) {
			return new CalculationResult(false, false);
		}

		try {
			this.busy = true;
			final CalculationResult result = new CalculationResult(true, true);
			
			this.kernelCalc.calculate(0, (int)this.calc.getTrainingData().getRecordCount());
			result.setError(this.kernelCalc.getError());
			
			return result;
		} finally {
			this.busy = false;
		}
	}

	/**
	 * @return The calculation object that this belongs to.
	 */
	public ConcurrentCalculate getCalc() {
		return this.calc;
	}

	/**
	 * @return The underlying device.
	 */
	public EncogCLDevice getDevice() {
		return this.device;
	}

	/**
	 * Set the network that we will be using.
	 * 
	 * @param network
	 *            The network to use.
	 */
	public void setNetwork(final FlatNetwork network) {
		this.kernelCalc.setFlat(network);
	}

	/**
	 * Set the training data that will be used.
	 * 
	 * @param training
	 *            The training data that will be used.
	 */
	public void setTraining(final EngineIndexableSet training) {
		this.kernelCalc.setTraining(training);
	}

}
