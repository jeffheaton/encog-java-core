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
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.EncogCLQueue;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ResourceLoader;
import org.jocl.cl_mem;

/**
 * An OpenCL kernel that is designed to calculate gradients and help train a
 * neural network.
 */
public class KernelNetworkTrain extends EncogKernel {

	public static final int PARRAY_INPUT_COUNT = 0;
	public static final int PARRAY_OUTPUT_COUNT = 1;
	public static final int PARRAY_LAYER_COUNT = 2;
	public static final int PARRAY_LEARN = 3; 
	public static final int PARRAY_START = 4;
	public static final int PARRAY_ITEMS_PER = 5;
	
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

	/**
	 * The temp data in buffer. Temp data that is used while training.
	 */
	private cl_mem tempDataInBuffer;

	/**
	 * The temp data out buffer. Temp data that is used while training.
	 */
	private cl_mem tempDataOutBuffer;

	/**
	 * The weight and bias array for the network.
	 */
	private final float[] weightInArray;

	/**
	 * The weight output array.
	 */
	private final float[] weightOutArray;

	/**
	 * The temp data array.  Temp data that is used while training.
	 */
	private float[] tempDataArray;

	/**
	 * The size of all layer deltas.
	 */
	private int layerDeltaSize;

	/**
	 * The slopes.
	 */
	private final float[] slopeArray;

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
	private final int[] paramArray;

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

	/**
	 * The gradient input buffer.
	 */
	private cl_mem gradientInBuffer;

	/**
	 * The network to train.
	 */
	private final FlatNetwork flat;

	/**
	 * The training errors for this workload.
	 */
	private float[] errors;

	/**
	 * The gradients.
	 */
	private final float[] gradients;

	/**
	 * The training data to use.
	 */
	private final EngineIndexableSet training;

	/**
	 * The device to train with.
	 */
	private final EncogCLDevice device;

	/**
	 * The length of the training data.
	 */
	private final int trainingLength;

	/**
	 * Construct a kernel to train the network.
	 * @param device The OpenCL device to use.
	 * @param flat The network to train.
	 * @param training The training data.
	 * @param tempDataSize How much temp data.
	 */
	public KernelNetworkTrain(final EncogCLDevice device,
			final FlatNetwork flat, final EngineIndexableSet training,
			final int tempDataSize) {
		super(device, "org/encog/engine/resources/KernelNetTrain.txt",
				"NetworkTrain");

		this.training = training;
		this.trainingLength = (int) this.training.getRecordCount();
		this.device = device;
		this.flat = flat;
		this.weightInArray = new float[flat.getWeights().length];
		this.weightOutArray = new float[flat.getWeights().length];
		this.tempDataArray = new float[tempDataSize];
		this.slopeArray = new float[flat.getActivationFunctions().length];
		this.gradients = new float[flat.getWeights().length];

		this.layerDeltaSize = 0;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			this.layerDeltaSize += flat.getLayerCounts()[i];
		}

		int index = 0;
		for(int i=0;i<flat.getActivationFunctions().length;i++) {
			this.slopeArray[index++] = (float)flat.getActivationFunctions()[i].getParams()[0];
		}
		
		final int inputSize = flat.getInputCount();
		final int idealSize = flat.getOutputCount();

		this.inputArray = new float[inputSize * this.trainingLength];
		this.idealArray = new float[idealSize * this.trainingLength];
		this.paramArray = new int[10];

		final EngineData pair = BasicEngineData.createPair(
				flat.getInputCount(), flat.getOutputCount());

		int inputIndex = 0;
		int idealIndex = 0;

		for (int i = 0; i < this.trainingLength; i++) {
			training.getRecord(i, pair);
			for (int col = 0; col < flat.getInputCount(); col++) {
				this.inputArray[inputIndex++] 
				               = (float) pair.getInputArray()[col];
			}

			for (int col = 0; col < flat.getOutputCount(); col++) {
				this.idealArray[idealIndex++] 
				                = (float) pair.getIdealArray()[col];
			}
		}

	}

	/**
	 * Calculate one iteration over the specified range.
	 * @param start The starting position to calculate for.
	 * @param size The ending position to calculate for.
	 * @param learn True, if we should learn.
	 */
	public void calculate(final int start, final int size, 
			final boolean learn) {
		prepareKernel();

		this.paramArray[PARRAY_LEARN] = learn ? 1 : 0; // should it learn
		this.paramArray[PARRAY_START] = start; // training offset
		this.paramArray[PARRAY_ITEMS_PER] = size;// index of last item

		EngineArray.arrayCopy(this.flat.getWeights(),this.weightInArray);

		setArg(0, this.paramBuffer);
		setArg(1, this.errorBuffer);
		setArg(2, this.layerIndexBuffer);
		setArg(3, this.layerCountBuffer);
		setArg(4, this.layerFeedCountBuffer);
		setArg(5, this.weightIndexBuffer);
		setArg(6, this.inputBuffer);
		setArg(7, this.idealBuffer);
		setArg(8, this.weightInArrayBuffer);
		setArg(9, this.weightOutArrayBuffer);
		setArg(10, this.gradientOutBuffer);
		setArg(11, this.activationTypeBuffer);
		setArg(12, this.slopeBuffer);
		setArg(13, this.tempDataInBuffer);
		setArg(14, this.tempDataOutBuffer);
		setArg(15, this.gradientInBuffer);

		try {
			final EncogCLQueue queue = this.device.getQueue();

			EngineArray.fill(this.gradients, 0);

			if (learn) {
				this.paramArray[3] = 1;
			} else {
				this.paramArray[3] = 0;
			}

			this.paramArray[4] = start;

			queue.array2Buffer(this.weightInArray, this.weightInArrayBuffer);
			queue.array2Buffer(this.tempDataArray, this.tempDataInBuffer);
			queue.array2Buffer(this.gradients, this.gradientInBuffer);
			queue.array2Buffer(this.paramArray, this.paramBuffer);

			// Execute the kernel
			queue.execute(this);
			queue.waitFinish();

			// Read the results
			queue.buffer2Array(this.errorBuffer, this.errors);
			queue.buffer2Array(this.weightOutArrayBuffer, this.weightOutArray);
			queue.buffer2Array(this.tempDataOutBuffer, this.tempDataArray);
			queue.buffer2Array(this.gradientOutBuffer, this.gradients);

		} catch (final Exception e) {
			throw new EncogEngineError(e);
		}
	}

	/**
	 * Compile the kernel.
	 * @param options The options.
	 * @param network The network to compile for.
	 */
	public void compile(final Map<String, String> options,
			final FlatNetwork network) {

		final ActivationFunction activation = network.getActivationFunctions()[0];
		final boolean allSlopeOne = !network.anySlopeNotOne();
		final StringBuilder source = new StringBuilder();

		source.append("#define ACTIVATION(x,slope)");
		source.append(activation.getOpenCLExpression(false, allSlopeOne));
		source.append("\r\n");
		
		source.append("#define DERIVATIVE(x,slope)");
		source.append(activation.getOpenCLExpression(true, allSlopeOne));
		source.append("\r\n");
		

		source.append(ResourceLoader.loadString(getSourceName()));
		setCLSource(source.toString());

		compile(options);
		assignWorkgroupSizes(this.trainingLength);
		
		// setup
		init();
	}
	
	public void assignWorkgroupSizes(int size)
	{
		// Calculate the work-item dimensions
		int threads = EncogEngine.getInstance().getCL().getGlobalWork();
		threads = Math.min(size, EncogEngine.getInstance()
				.getCL().getGlobalWork());
		setLocalWork(Math.min(getMaxWorkGroupSize(), threads));
		setGlobalWork(Math.min(threads, getLocalWork()));
	}

	/**
	 * @return the errors
	 */
	public float[] getErrors() {
		return this.errors;
	}

	/**
	 * @return the tempDataArray
	 */
	public float[] getTempDataArray() {
		return this.tempDataArray;
	}

	/**
	 * @return the weightOutArray
	 */
	public float[] getWeightOutArray() {
		return this.weightOutArray;
	}

	/**
	 * Setup the kernel.
	 */
	public void init() {
		final int errorSize = getGlobalWork();
		final int gradientSize = getGlobalWork()
				* this.flat.getWeights().length;

		this.errors = new float[errorSize];

		this.paramArray[0] = this.flat.getInputCount();
		this.paramArray[1] = this.flat.getOutputCount();
		this.paramArray[2] = this.flat.getLayerCounts().length;

		// create the buffers
		this.inputBuffer = createArrayReadOnly(this.inputArray);
		this.idealBuffer = createArrayReadOnly(this.idealArray);
		this.errorBuffer = createFloatArrayWriteOnly(errorSize);
		this.gradientOutBuffer = createFloatArrayWriteOnly(gradientSize);
		this.gradientInBuffer = createArrayReadOnly(this.gradients);
		this.paramBuffer = createArrayReadOnly(this.paramArray);
		this.layerIndexBuffer = createArrayReadOnly(this.flat.getLayerIndex());
		this.layerCountBuffer = createArrayReadOnly(this.flat.getLayerCounts());
		this.layerFeedCountBuffer = createArrayReadOnly(this.flat
				.getLayerFeedCounts());
		this.weightInArrayBuffer = createArrayReadOnly(this.weightInArray);
		this.weightOutArrayBuffer = createFloatArrayWriteOnly(this.weightInArray.length);
		this.weightIndexBuffer = createArrayReadOnly(this.flat.getWeightIndex());
		this.activationTypeBuffer = createArrayReadOnly(this.flat.getLayerCounts());
		this.slopeBuffer = createArrayReadOnly(this.slopeArray);
		this.tempDataInBuffer = createArrayReadOnly(this.tempDataArray);
		this.tempDataOutBuffer = createFloatArrayWriteOnly(this.tempDataArray.length);
	}

	/**
	 * Release the kernel and all buffers.
	 */
	@Override
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

	/**
	 * @param tempDataArray
	 *            the tempDataArray to set
	 */
	public void setTempDataArray(final float[] tempDataArray) {
		this.tempDataArray = tempDataArray;
	}
}
