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
import org.encog.engine.EncogEngineError;
import org.encog.engine.network.flat.FlatNetwork;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_mem;

/**
 * An OpenCL kernel that is designed to calculate gradients and help train a
 * neural network.
 */
public class KernelNetworkTrain extends EncogKernel {

	/**
	 * A buffer to hold the weight and bias matrix.
	 */
	private cl_mem weightArrayBuffer;

	/**
	 * A buffer to hold the layer index.
	 */
	private cl_mem layerIndexBuffer;

	/**
	 * A buffer to hold the layer counts.
	 */
	private cl_mem layerCountBuffer;

	/**
	 * A buffer to hold the layer feed counts.
	 */
	private cl_mem layerFeedCountBuffer;

	/**
	 * A buffer to hold the weight indexes.
	 */
	private cl_mem weightIndexBuffer;

	/**
	 * A buffer to hold the activations for each of the layers.
	 */
	private cl_mem activationTypeBuffer;

	/**
	 * A buffer to hold the slope for the activation of each of the layers.
	 */
	private cl_mem slopeBuffer;

	/**
	 * The weight and bias array for the network.
	 */
	private float[] weightArray;

	/**
	 * The size of all layer deltas.
	 */
	private int layerDeltaSize;

	/**
	 * The slopes.
	 */
	private float[] slopeArray;

	/**
	 * Construct the kernel for the specified context.
	 * 
	 * @param context
	 *            The context to calculate for.
	 */
	public KernelNetworkTrain(final cl_context context) {
		super(context, "org/encog/engine/resources/KernelNetTrain.txt",
				"NetworkTrain");
	}

	/**
	 * Calculate the gradients for one workload.
	 * 
	 * @param workload
	 *            The workload to calculate for.
	 */
	public void calculate(final TrainingWorkload workload) {
		prepareKernel();

		final FlatNetwork flat = workload.getNetwork();

		for (int i = 0; i < flat.getWeights().length; i++) {
			this.weightArray[i] = (float) flat.getWeights()[i];
		}

		CL.clSetKernelArg(getKernel(), 0, Sizeof.cl_mem, Pointer.to(workload
				.getParamBuffer()));
		CL.clSetKernelArg(getKernel(), 1, Sizeof.cl_mem, Pointer.to(workload
				.getErrorBuffer()));
		CL.clSetKernelArg(getKernel(), 2, Sizeof.cl_mem, Pointer
				.to(this.layerIndexBuffer));
		CL.clSetKernelArg(getKernel(), 3, Sizeof.cl_mem, Pointer
				.to(this.layerCountBuffer));
		CL.clSetKernelArg(getKernel(), 4, Sizeof.cl_mem, Pointer
				.to(this.layerFeedCountBuffer));
		CL.clSetKernelArg(getKernel(), 5, Sizeof.cl_mem, Pointer
				.to(this.weightIndexBuffer));
		CL.clSetKernelArg(getKernel(), 6, Sizeof.cl_mem, Pointer.to(workload
				.getInputBuffer()));
		CL.clSetKernelArg(getKernel(), 7, Sizeof.cl_mem, Pointer.to(workload
				.getIdealBuffer()));
		CL.clSetKernelArg(getKernel(), 8, Sizeof.cl_mem, Pointer
				.to(this.weightArrayBuffer));
		CL.clSetKernelArg(getKernel(), 9, Sizeof.cl_mem, Pointer.to(workload
				.getGradientBuffer()));
		CL.clSetKernelArg(getKernel(), 10, Sizeof.cl_mem, Pointer
				.to(this.activationTypeBuffer));
		CL.clSetKernelArg(getKernel(), 11, Sizeof.cl_mem, Pointer
				.to(this.slopeBuffer));

		try {
			// Calculate the work-item dimensions
			int localWork = Math.max(EncogEngine.getInstance().getCL()
					.getCLWorkloadSize(), 1);
			final int globalWork = workload.getMaxUnits();

			// don't create more than we have work for
			localWork = Math.min(localWork, workload.getMaxUnits());

			// Set the work-item dimensions
			final long[] globalWorkSize = new long[] { globalWork };
			final long[] localWorkSize = new long[] { localWork };

			CL.clEnqueueWriteBuffer(workload.getDevice().getCommands(),
					this.weightArrayBuffer, CL.CL_TRUE, 0, Sizeof.cl_float
							* this.weightArray.length, Pointer
							.to(this.weightArray), 0, null, null);

			// Execute the kernel
			CL.clEnqueueNDRangeKernel(workload.getDevice().getCommands(),
					getKernel(), 1, null, globalWorkSize, localWorkSize, 0,
					null, null);
			CL.clFinish(workload.getDevice().getCommands());

			CL.clEnqueueReadBuffer(workload.getDevice().getCommands(), workload
					.getErrorBuffer(), CL.CL_TRUE, 0, workload.getMaxUnits()
					* Sizeof.cl_float, Pointer.to(workload.getErrors()), 0,
					null, null);

			CL.clEnqueueReadBuffer(workload.getDevice().getCommands(), workload
					.getGradientBuffer(), CL.CL_TRUE, 0,
					this.weightArray.length * workload.getMaxUnits()
							* Sizeof.cl_float, Pointer.to(workload
							.getGradients()), 0, null, null);

			// commands.Finish();
		} catch (final Exception e) {
			throw new EncogEngineError(e);
		}
	}

	/**
	 * Init the kernal for new training.
	 * 
	 * @param flat
	 *            The network to be trained.
	 */
	public void init(final FlatNetwork flat) {

		this.weightArray = new float[flat.getWeights().length];
		this.slopeArray = new float[flat.getParams().length];

		this.layerDeltaSize = 0;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			this.layerDeltaSize += flat.getLayerCounts()[i];
		}

		for (int i = 0; i < this.slopeArray.length; i++) {
			this.slopeArray[i] = (float) flat.getParams()[i];
		}

		this.layerIndexBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerIndex().length, Pointer.to(flat
						.getLayerIndex()), null);

		this.layerCountBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerCounts().length, Pointer.to(flat
						.getLayerCounts()), null);

		this.layerFeedCountBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerFeedCounts().length, Pointer.to(flat
						.getLayerFeedCounts()), null);

		this.weightArrayBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
						* this.weightArray.length,
				Pointer.to(this.weightArray), null);

		this.weightIndexBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getWeightIndex().length, Pointer.to(flat
						.getWeightIndex()), null);

		this.activationTypeBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getActivationType().length, Pointer.to(flat
						.getActivationType()), null);

		this.slopeBuffer = CL.clCreateBuffer(getContext(), CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
				* this.slopeArray.length, Pointer.to(this.slopeArray), null);
	}
}
