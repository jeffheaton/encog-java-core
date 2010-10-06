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

package org.encog.neural.networks.training.concurrent.performers;

import org.encog.Encog;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.NeuralNetworkError;

/**
 * This performer allows jobs to be performed by the CPU.
 * 
 */
public class ConcurrentTrainingPerformerOpenCL extends
		ConcurrentTrainingPerformerCPU {

	/**
	 * The OpenCL device to use.
	 */
	private final EncogCLDevice device;
	

	/**
	 * Construct an OpenCL device performer.
	 * @param device The device to use.
	 */
	public ConcurrentTrainingPerformerOpenCL(final EncogCLDevice device) {
		if (Encog.getInstance().getCL() == null) {
			throw new NeuralNetworkError(
					"Can't use an OpenCL performer, because OpenCL " 
					+ "is not enabled.");
		}

		if (Encog.getInstance().getCL() == null) {
			throw new NeuralNetworkError("Can't use a null OpenCL device.");
		}

		this.device = device;
	}

	/**
	 * @return the device
	 */
	public EncogCLDevice getDevice() {
		return this.device;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[OpenCL-Performer: ");
		result.append(device.toString());
		result.append("]");
		return result.toString();
	}
}
