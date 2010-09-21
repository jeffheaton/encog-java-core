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

package org.encog.engine.opencl.kernels;

import java.util.Map;

import org.encog.engine.EncogEngine;
import org.encog.engine.EncogEngineError;
import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.ResourceLoader;
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
	 * A buffer to communicate weights to the kernel.
	 */
	private cl_mem weightInArrayBuffer;

	/**
	 * A buffer to communicate weights from the kernel.
	 */
	private cl_mem weightOutArrayBuffer;

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

	private cl_mem tempDataInBuffer;

	private cl_mem tempDataOutBuffer;

	/**
	 * The weight and bias array for the network.
	 */
	private float[] weightInArray;

	private float[] weightOutArray;

	private float[] tempDataArray;

	/**
	 * The size of all layer deltas.
	 */
	private int layerDeltaSize;

	/**
	 * The slopes.
	 */
	private float[] slopeArray;

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
	 * A buffer to hold the errors.
	 */
	private final cl_mem errorBuffer;

	/**
	 * A buffer to hold the gradients.
	 */
	private final cl_mem gradientBuffer;

	/**
	 * The number of threads that OpenCL will use.
	 */
	private final int maxUnits;

	private final FlatNetwork flat;

	/**
	 * The training errors for this workload.
	 */
	private final float[] errors;

	/**
	 * The gradients for this workload.
	 */
	private final float[] gradients;

	public void compile(final Map<String, String> options, FlatNetwork network) {

		int activation = network.getUniformActivation();

		StringBuilder source = new StringBuilder();

		switch (activation) {
		case ActivationFunctions.ACTIVATION_TANH:
			if (network.anySlopeNotOne()) {
				source.append("#define ACTIVATION(x,slope) tanh(x)\r\n");
			} else {
				source.append("#define ACTIVATION(x,slope) tanh(x)\r\n");
			}
			source.append("#define DERIVATIVE(x,slope) (slope * (1.0f - x * x))\r\n");
			break;
		case ActivationFunctions.ACTIVATION_SIGMOID:
			source.append("#define ACTIVATION(x,slope) (1.0f / (1.0f + exp(-slope * x)))\r\n");
			source.append("#define DERIVATIVE(x,slope) (slope * x * (1.0f - x))\r\n");
			break;
		}

		source.append(ResourceLoader.loadString(getSourceName()));
		setCLSource(source.toString());

		compile(options);
	}

	public KernelNetworkTrain(final EncogCLDevice device,
			final FlatNetwork flat, EngineIndexableSet training,
			int tempDataSize) {
		super(device, "org/encog/engine/resources/KernelNetTrain.txt",
				"NetworkTrain");

		this.flat = flat;
		this.weightInArray = new float[flat.getWeights().length];
		this.weightOutArray = new float[flat.getWeights().length];
		this.tempDataArray = new float[tempDataSize];
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
						* flat.getLayerIndex().length,
				Pointer.to(flat.getLayerIndex()), null);

		this.layerCountBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerCounts().length,
				Pointer.to(flat.getLayerCounts()), null);

		this.layerFeedCountBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getLayerFeedCounts().length,
				Pointer.to(flat.getLayerFeedCounts()), null);

		this.weightInArrayBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
						* this.weightInArray.length,
				Pointer.to(this.weightInArray), null);

		this.weightOutArrayBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_WRITE_ONLY, Sizeof.cl_float
						* this.weightInArray.length, null, null);

		this.weightIndexBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getWeightIndex().length,
				Pointer.to(flat.getWeightIndex()), null);

		this.activationTypeBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
						* flat.getActivationType().length,
				Pointer.to(flat.getActivationType()), null);

		this.slopeBuffer = CL.clCreateBuffer(getContext(), CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
				* this.slopeArray.length, Pointer.to(this.slopeArray), null);

		this.tempDataInBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float
						* this.tempDataArray.length,
				Pointer.to(this.tempDataArray), null);

		this.tempDataOutBuffer = CL.clCreateBuffer(getContext(),
				CL.CL_MEM_WRITE_ONLY, Sizeof.cl_float
						* this.tempDataArray.length, null, null);

		int trainingLength = (int) training.getRecordCount();
		int inputSize = flat.getInputCount();
		int idealSize = flat.getOutputCount();
		this.inputArray = new float[inputSize * trainingLength];
		this.idealArray = new float[idealSize * trainingLength];
		this.paramArray = new int[10];

		this.maxUnits = Math.min(trainingLength, EncogEngine.getInstance()
				.getCL().getCLThreads());
		final EngineData pair = BasicEngineData.createPair(
				flat.getInputCount(), flat.getOutputCount());

		int inputIndex = 0;
		int idealIndex = 0;

		for (int i = 0; i < trainingLength; i++) {
			training.getRecord(i, pair);
			for (int col = 0; col < flat.getInputCount(); col++) {
				this.inputArray[inputIndex++] = (float) pair.getInputArray()[col];
			}

			for (int col = 0; col < flat.getOutputCount(); col++) {
				this.idealArray[idealIndex++] = (float) pair.getIdealArray()[col];
			}
		}

		final cl_context context = device.getPlatform().getContext();

		final int errorSize = this.maxUnits;
		final int gradientSize = this.maxUnits * flat.getWeights().length;

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
		// size each item
		this.paramArray[7] = Math.max(trainingLength / this.maxUnits, 1);
		// size of last item
		this.paramArray[8] = Math.max(trainingLength % this.maxUnits, 1);

		this.paramBuffer = CL.clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_int
				* this.paramArray.length, Pointer.to(this.paramArray), null);

	}

	/**
	 * Calculate the gradients for one workload.
	 * 
	 * @param workload
	 *            The workload to calculate for.
	 */
	public void calculate() {
		prepareKernel();

		for (int i = 0; i < this.flat.getWeights().length; i++) {
			this.weightInArray[i] = (float) flat.getWeights()[i];
		}

		CL.clSetKernelArg(getKernel(), 0, Sizeof.cl_mem,
				Pointer.to(this.paramBuffer));
		CL.clSetKernelArg(getKernel(), 1, Sizeof.cl_mem,
				Pointer.to(this.errorBuffer));
		CL.clSetKernelArg(getKernel(), 2, Sizeof.cl_mem,
				Pointer.to(this.layerIndexBuffer));
		CL.clSetKernelArg(getKernel(), 3, Sizeof.cl_mem,
				Pointer.to(this.layerCountBuffer));
		CL.clSetKernelArg(getKernel(), 4, Sizeof.cl_mem,
				Pointer.to(this.layerFeedCountBuffer));
		CL.clSetKernelArg(getKernel(), 5, Sizeof.cl_mem,
				Pointer.to(this.weightIndexBuffer));
		CL.clSetKernelArg(getKernel(), 6, Sizeof.cl_mem,
				Pointer.to(this.inputBuffer));
		CL.clSetKernelArg(getKernel(), 7, Sizeof.cl_mem,
				Pointer.to(this.idealBuffer));
		CL.clSetKernelArg(getKernel(), 8, Sizeof.cl_mem,
				Pointer.to(this.weightInArrayBuffer));
		CL.clSetKernelArg(getKernel(), 9, Sizeof.cl_mem,
				Pointer.to(this.weightOutArrayBuffer));
		CL.clSetKernelArg(getKernel(), 10, Sizeof.cl_mem,
				Pointer.to(this.gradientBuffer));
		CL.clSetKernelArg(getKernel(), 11, Sizeof.cl_mem,
				Pointer.to(this.activationTypeBuffer));
		CL.clSetKernelArg(getKernel(), 12, Sizeof.cl_mem,
				Pointer.to(this.slopeBuffer));
		CL.clSetKernelArg(getKernel(), 13, Sizeof.cl_mem,
				Pointer.to(this.tempDataInBuffer));
		CL.clSetKernelArg(getKernel(), 14, Sizeof.cl_mem,
				Pointer.to(this.tempDataOutBuffer));

		try {
			// Calculate the work-item dimensions
			int localWork = Math.max(EncogEngine.getInstance().getCL()
					.getCLWorkloadSize(), 1);
			final int globalWork = this.maxUnits;

			// don't create more than we have work for
			localWork = Math.min(localWork, this.maxUnits);

			// Set the work-item dimensions
			final long[] globalWorkSize = new long[] { globalWork };
			final long[] localWorkSize = new long[] { localWork };

			CL.clEnqueueWriteBuffer(getDevice().getCommands(),
					this.weightInArrayBuffer, CL.CL_TRUE, 0, Sizeof.cl_float
							* this.weightInArray.length,
					Pointer.to(this.weightInArray), 0, null, null);

			CL.clEnqueueWriteBuffer(getDevice().getCommands(),
					this.tempDataInBuffer, CL.CL_TRUE, 0, Sizeof.cl_float
							* this.tempDataArray.length,
					Pointer.to(this.tempDataArray), 0, null, null);

			// Execute the kernel
			CL.clEnqueueNDRangeKernel(getDevice().getCommands(), getKernel(),
					1, null, globalWorkSize, localWorkSize, 0, null, null);
			CL.clFinish(getDevice().getCommands());

			CL.clEnqueueReadBuffer(getDevice().getCommands(), this.errorBuffer,
					CL.CL_TRUE, 0, this.maxUnits * Sizeof.cl_float,
					Pointer.to(this.errors), 0, null, null);

			CL.clEnqueueReadBuffer(
					getDevice().getCommands(),
					this.gradientBuffer,
					CL.CL_TRUE,
					0,
					this.weightInArray.length * this.maxUnits * Sizeof.cl_float,
					Pointer.to(this.gradients), 0, null, null);

			CL.clEnqueueReadBuffer(getDevice().getCommands(),
					this.weightOutArrayBuffer, CL.CL_TRUE, 0,
					this.weightOutArray.length * Sizeof.cl_float,
					Pointer.to(this.weightOutArray), 0, null, null);

			CL.clEnqueueReadBuffer(getDevice().getCommands(),
					this.tempDataOutBuffer, CL.CL_TRUE, 0,
					this.tempDataArray.length * Sizeof.cl_float,
					Pointer.to(this.tempDataArray), 0, null, null);

			// commands.Finish();
		} catch (final Exception e) {
			throw new EncogEngineError(e);
		}
	}

	/**
	 * @return the weightOutArray
	 */
	public float[] getWeightOutArray() {
		return weightOutArray;
	}

	/**
	 * @param tempDataArray
	 *            the tempDataArray to set
	 */
	public void setTempDataArray(float[] tempDataArray) {
		this.tempDataArray = tempDataArray;
	}

	/**
	 * @return the tempDataArray
	 */
	public float[] getTempDataArray() {
		return tempDataArray;
	}

	/**
	 * @return the maxUnits
	 */
	public int getMaxUnits() {
		return maxUnits;
	}

	/**
	 * @return the errors
	 */
	public float[] getErrors() {
		return errors;
	}

	public void release() {
		super.release();
		CL.clReleaseMemObject(this.activationTypeBuffer);
		CL.clReleaseMemObject(this.errorBuffer);
		CL.clReleaseMemObject(this.gradientBuffer);
		CL.clReleaseMemObject(this.idealBuffer);
		CL.clReleaseMemObject(this.inputBuffer);
		CL.clReleaseMemObject(this.layerCountBuffer);
		CL.clReleaseMemObject(this.layerFeedCountBuffer);
		CL.clReleaseMemObject(this.layerIndexBuffer);
		CL.clReleaseMemObject(this.paramBuffer);
		CL.clReleaseMemObject(this.slopeBuffer);
		CL.clReleaseMemObject(this.tempDataInBuffer);
		CL.clReleaseMemObject(this.tempDataOutBuffer);
		CL.clReleaseMemObject(this.weightInArrayBuffer);
		CL.clReleaseMemObject(this.weightIndexBuffer);
		CL.clReleaseMemObject(this.weightOutArrayBuffer);
	}

}
