/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
