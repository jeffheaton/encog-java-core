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

package org.encog.engine.opencl.kernels;

import org.encog.engine.EncogEngine;
import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.data.EngineData;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_mem;

/**
 * Holds one OpenCL workload. These workloads will be created for all OpenCL
 * devices that will be used.
 */
public class TrainingWorkload {

	/**
	 * The length of the training data.
	 */
	private final int trainingLength;

	/**
	 * The size of the input layer.
	 */
	private final int inputSize;

	/**
	 * The size of the output layer.
	 */
	private final int idealSize;

	/**
	 * An array to hold the input to the neural network.
	 */
	private final float[] inputArray;

	/**
	 * An array to hold the ideal values expected from the network.
	 */
	private final float[] idealArray;

	/**
	 * The input buffer.
	 */
	private final cl_mem inputBuffer;

	/**
	 * The ideal buffer.
	 */
	private final cl_mem idealBuffer;

	/**
	 * Holds parameters passed to the kernel.
	 */
	private final int[] paramArray;

	/**
	 * A buffer to hold the parameters.
	 */
	private final cl_mem paramBuffer;

	/**
	 * The device to use with this training data.
	 */
	private final EncogCLDevice device;

	/**
	 * The network to train.
	 */
	private final FlatNetwork flat;

	/**
	 * A buffer to hold the errors.
	 */
	private final cl_mem errorBuffer;

	/**
	 * A buffer to hold the gradients.
	 */
	private final cl_mem gradientBuffer;

	/**
	 * The training errors for this workload.
	 */
	private final float[] errors;

	/**
	 * The gradients for this workload.
	 */
	private final float[] gradients;

	/**
	 * The number of threads that OpenCL will use.
	 */
	private final int maxUnits;

	/**
	 * Construct an OpenCL training workload.
	 * 
	 * @param device
	 *            The device to use.
	 * @param flat
	 *            The network to use.
	 * @param training
	 *            The training data to use.
	 * @param high
	 *            The high index to train from.
	 * @param low
	 *            The low index to train from.
	 */
	public TrainingWorkload(final EncogCLDevice device, final FlatNetwork flat,
			final EngineIndexableSet training, final int high, final int low) {
		this.flat = flat;
		this.trainingLength = (high - low) + 1;
		this.inputSize = flat.getInputCount();
		this.idealSize = flat.getOutputCount();
		this.inputArray = new float[this.inputSize * this.trainingLength];
		this.idealArray = new float[this.idealSize * this.trainingLength];
		this.paramArray = new int[10];
		this.device = device;

		this.maxUnits = Math.min(this.trainingLength, EncogEngine.getInstance()
				.getCL().getCLThreads());
		final EngineData pair = BasicEngineData.createPair(
				flat.getInputCount(), flat.getOutputCount());

		int inputIndex = 0;
		int idealIndex = 0;

		for (int i = low; i <= high; i++) {
			training.getRecord(i, pair);
			for (int col = 0; col < flat.getInputCount(); col++) {
				this.inputArray[inputIndex++] = (float) pair.getInputArray()[col];
			}

			for (int col = 0; col < flat.getOutputCount(); col++) {
				this.idealArray[idealIndex++] = (float) pair.getIdealArray()[col];
			}
		}

		final cl_context context = this.device.getPlatform().getContext();

		final int errorSize = this.maxUnits;
		final int gradientSize = this.maxUnits * this.flat.getWeights().length;

		this.errors = new float[errorSize];
		this.gradients = new float[gradientSize];

		this.inputBuffer = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
				* this.inputArray.length, Pointer.to(this.inputArray), null);

		this.idealBuffer = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
				* this.idealArray.length, Pointer.to(this.idealArray), null);

		this.errorBuffer = CL.clCreateBuffer(context, CL.CL_MEM_WRITE_ONLY,
				Sizeof.cl_float * errorSize, null, null);

		this.gradientBuffer = CL.clCreateBuffer(context, CL.CL_MEM_WRITE_ONLY,
				Sizeof.cl_float * gradientSize, null, null);

		this.paramArray[0] = flat.getInputCount();
		this.paramArray[1] = flat.getOutputCount();
		this.paramArray[2] = flat.getLayerCounts().length;
		this.paramArray[6] = this.maxUnits - 1;// index of last item
		this.paramArray[7] = Math.max(this.trainingLength / this.maxUnits, 1);// size
		// each
		// item
		this.paramArray[8] = Math.max(this.trainingLength % this.maxUnits, 1);// size
		// of
		// last
		// item

		this.paramBuffer = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
				* this.paramArray.length, Pointer.to(this.paramArray), null);
	}

	/**
	 * @return The OpenCL device this workload is used with.
	 */
	public EncogCLDevice getDevice() {
		return this.device;
	}

	/**
	 * @return A buffer to hold the errors.
	 */
	public cl_mem getErrorBuffer() {
		return this.errorBuffer;
	}

	/**
	 * @return The errors.
	 */
	public float[] getErrors() {
		return this.errors;
	}

	/**
	 * @return A buffer to hold the gradients.
	 */
	public cl_mem getGradientBuffer() {
		return this.gradientBuffer;
	}

	/**
	 * @return The gradients.
	 */
	public float[] getGradients() {
		return this.gradients;
	}

	/**
	 * @return The ideal data buffer.
	 */
	public cl_mem getIdealBuffer() {
		return this.idealBuffer;
	}

	/**
	 * @return The input data buffer.
	 */
	public cl_mem getInputBuffer() {
		return this.inputBuffer;
	}

	/**
	 * @return The max number of units.
	 */
	public int getMaxUnits() {
		return this.maxUnits;
	}

	/**
	 * @return The network being trained.
	 */
	public FlatNetwork getNetwork() {
		return this.flat;
	}

	/**
	 * @return Several parameters sent to the OpenCL kernel.
	 */
	public int[] getParamArray() {
		return this.paramArray;
	}

	/**
	 * @return A buffer to hold the parameters.
	 */
	public cl_mem getParamBuffer() {
		return this.paramBuffer;
	}
}
