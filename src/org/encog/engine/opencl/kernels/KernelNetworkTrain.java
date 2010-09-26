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
import org.encog.engine.opencl.EncogCLQueue;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.Format;
import org.encog.engine.util.ResourceLoader;
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
	private cl_mem inputBuffer;

	/**
	 * The ideal buffer.
	 */
	private cl_mem idealBuffer;

	/**
	 * Holds parameters passed to the kernel.
	 */
	private int[] paramArray;

	/**
	 * A buffer to hold the parameters.
	 */
	private cl_mem paramBuffer;

	/**
	 * A buffer to hold the errors.
	 */
	private cl_mem errorBuffer;

	/**
	 * A buffer to hold the gradients.
	 */
	private cl_mem gradientOutBuffer;
	
	private cl_mem gradientInBuffer;

	private final FlatNetwork flat;

	/**
	 * The training errors for this workload.
	 */
	private float[] errors;
	
	private float[] gradients;
	
	private EngineIndexableSet training;
	
	private final EncogCLDevice device;

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
		
		// Calculate the work-item dimensions
		int trainingLength = (int) training.getRecordCount();
		int threads = EncogEngine.getInstance().getCL().getCLThreads();
		threads = Math.min(trainingLength, EncogEngine.getInstance().getCL().getCLThreads());
		this.setLocalWork( Math.min(this.getMaxWorkGroupSize(), threads) );
		this.setGlobalWork( Math.min(threads,getLocalWork()) );
		
		// setup
		init();
	}

	public KernelNetworkTrain(final EncogCLDevice device,
			final FlatNetwork flat, EngineIndexableSet training,
			int tempDataSize) {
		super(device, "org/encog/engine/resources/KernelNetTrain.txt",
				"NetworkTrain");

		this.training = training;
		this.device = device;
		this.flat = flat;
		this.weightInArray = new float[flat.getWeights().length];
		this.weightOutArray = new float[flat.getWeights().length];
		this.tempDataArray = new float[tempDataSize];
		this.slopeArray = new float[flat.getParams().length];
		this.gradients = new float[flat.getWeights().length];

		this.layerDeltaSize = 0;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			this.layerDeltaSize += flat.getLayerCounts()[i];
		}

		for (int i = 0; i < this.slopeArray.length; i++) {
			this.slopeArray[i] = (float) flat.getParams()[i];
		}

		int trainingLength = (int) training.getRecordCount();
		int inputSize = flat.getInputCount();
		int idealSize = flat.getOutputCount();
		
		this.inputArray = new float[inputSize * trainingLength];
		this.idealArray = new float[idealSize * trainingLength];
		this.paramArray = new int[10];

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

	}
	
	public void init()
	{		
		int trainingLength = (int) training.getRecordCount();

		final int errorSize = this.getGlobalWork();
		final int gradientSize = this.getGlobalWork() * flat.getWeights().length;

		this.errors = new float[errorSize];

		this.paramArray[0] = flat.getInputCount();
		this.paramArray[1] = flat.getOutputCount();
		this.paramArray[2] = flat.getLayerCounts().length;
		this.paramArray[3] = 1; // should it learn
		this.paramArray[4] = 0; // training offset
		this.paramArray[6] = this.getGlobalWork() - 1;// index of last item
		// size each item
		this.paramArray[7] = Math.max(trainingLength / this.getGlobalWork(), 1);
		// size of last item
		if( this.getGlobalWork()==1 )
			this.paramArray[8] = trainingLength;
		else
			this.paramArray[8] = Math.max(trainingLength % this.getGlobalWork(), 1);

		// create the buffers
		this.setAllocatedMemory(0);
		this.inputBuffer = createArrayReadOnly(this.inputArray);
		this.idealBuffer = createArrayReadOnly(this.idealArray);
		this.errorBuffer = createFloatArrayWriteOnly(errorSize);
		this.gradientOutBuffer = createFloatArrayWriteOnly(gradientSize);
		this.gradientInBuffer = createArrayReadOnly(this.gradients);
		this.paramBuffer = createArrayReadOnly(this.paramArray);
		this.layerIndexBuffer = createArrayReadOnly(flat.getLayerIndex());
		this.layerCountBuffer = createArrayReadOnly(flat.getLayerCounts());
		this.layerFeedCountBuffer = createArrayReadOnly(flat.getLayerFeedCounts());
		this.weightInArrayBuffer = createArrayReadOnly(this.weightInArray);
		this.weightOutArrayBuffer = createFloatArrayWriteOnly(this.weightInArray.length);
		this.weightIndexBuffer = createArrayReadOnly(flat.getWeightIndex());
		this.activationTypeBuffer = createArrayReadOnly(flat.getActivationType());
		this.slopeBuffer = createArrayReadOnly(this.slopeArray);
		this.tempDataInBuffer = createArrayReadOnly(this.tempDataArray);
		this.tempDataOutBuffer = createFloatArrayWriteOnly(this.tempDataArray.length);
		System.out.println(Format.formatMemory(this.getAllocatedMemory()));

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

		setArg(0,this.paramBuffer);
		setArg(1,this.errorBuffer);
		setArg(2,this.layerIndexBuffer);
		setArg(3,this.layerCountBuffer);
		setArg(4,this.layerFeedCountBuffer);
		setArg(5,this.weightIndexBuffer);
		setArg(6,this.inputBuffer);
		setArg(7,this.idealBuffer);
		setArg(8,this.weightInArrayBuffer);
		setArg(9,this.weightOutArrayBuffer);
		setArg(10,this.gradientOutBuffer);
		setArg(11,this.activationTypeBuffer);
		setArg(12,this.slopeBuffer);
		setArg(13,this.tempDataInBuffer);
		setArg(14,this.tempDataOutBuffer);
		setArg(15,this.gradientInBuffer);

		try {
			EncogCLQueue queue = this.device.getQueue();
			
			EngineArray.fill(this.gradients, 0);
			
			queue.array2BufferFloat(this.weightInArray,this.weightInArrayBuffer);
			queue.array2BufferFloat(this.tempDataArray,this.tempDataInBuffer);
			queue.array2BufferFloat(this.gradients, this.gradientInBuffer);

			// Execute the kernel
			queue.execute(this);
			queue.waitFinish();
			
			// Read the results
			queue.buffer2Float(this.errorBuffer,this.errors);
			queue.buffer2Float(this.weightOutArrayBuffer,this.weightOutArray);
			queue.buffer2Float(this.tempDataOutBuffer,this.tempDataArray);
			queue.buffer2Float(this.gradientOutBuffer, this.gradients);
			
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
	 * @return the errors
	 */
	public float[] getErrors() {
		return errors;
	}

	public void release() {
		super.release();
		releaseBuffer(this.activationTypeBuffer);
		releaseBuffer(this.errorBuffer);
		releaseBuffer(this.gradientOutBuffer);
		releaseBuffer(this.gradientInBuffer);
		releaseBuffer(this.idealBuffer);
		releaseBuffer(this.inputBuffer);
		releaseBuffer(this.layerCountBuffer);
		releaseBuffer(this.layerFeedCountBuffer);
		releaseBuffer(this.layerIndexBuffer);
		releaseBuffer(this.paramBuffer);
		releaseBuffer(this.slopeBuffer);
		releaseBuffer(this.tempDataInBuffer);
		releaseBuffer(this.tempDataOutBuffer);
		releaseBuffer(this.weightInArrayBuffer);
		releaseBuffer(this.weightIndexBuffer);
		releaseBuffer(this.weightOutArrayBuffer);
	}
}
