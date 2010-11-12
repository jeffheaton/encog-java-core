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

import java.util.HashMap;
import java.util.Map;

import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.EncogCLQueue;
import org.encog.engine.opencl.exceptions.OpenCLError;
import org.encog.engine.opencl.exceptions.OutOfOpenCLResources;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;
import org.encog.engine.util.ResourceLoader;
import org.jocl.CLException;
import org.jocl.cl_mem;

/**
 * An OpenCL kernel that is designed to calculate the output of a neural
 * network.
 */
public class KernelNetworkCalc extends EncogKernel {

	/**
	 * The input count.
	 */
	public static final int PARRAY_INPUT_COUNT = 0;

	/**
	 * The output count.
	 */
	public static final int PARRAY_OUTPUT_COUNT = 1;

	/**
	 * The layer count.
	 */
	public static final int PARRAY_LAYER_COUNT = 2;

	/**
	 * Are we learning? 0=no, 1 =yes.
	 */
	public static final int PARRAY_LEARN = 3;

	/**
	 * What is the starting index to train at.
	 */
	public static final int PARRAY_START = 4;

	/**
	 * Items to train per call.
	 */
	public static final int PARRAY_ITEMS_PER = 5;

	/**
	 * Items to train per call.
	 */
	public static final int PARRAY_ITERATIONS = 6;

	/**
	 * A buffer to communicate weights to the kernel.
	 */
	private cl_mem weightInArrayBuffer;

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
	 * The weight and bias array for the network.
	 */
	private float[] weightInArray;

	/**
	 * An array to hold the input to the neural network.
	 */
	private float[] inputArray;

	/**
	 * An array to hold the ideal values expected from the network.
	 */
	private float[] idealArray;

	/**
	 * The input buffer.
	 */
	private cl_mem inputBuffer;

	/**
	 * The layer output buffer.
	 */
	private cl_mem layerOutputBuffer;

	/**
	 * The ideal buffer.
	 */
	private cl_mem idealBuffer;

	/**
	 * The layer output.
	 */
	private float[] layerOutput;

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
	 * The network to train.
	 */
	private FlatNetwork flat;

	/**
	 * The training errors for this workload.
	 */
	private float[] errors;

	/**
	 * The training data to use.
	 */
	private EngineIndexableSet training;

	/**
	 * The device to train with.
	 */
	private final EncogCLDevice device;

	/**
	 * The length of the training data.
	 */
	private int trainingLength;

	/**
	 * Construct a kernel to train the network.
	 * 
	 * @param device
	 *            The OpenCL device to use.
	 * @param flat
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param tempDataSize
	 *            How much temp data.
	 */
	public KernelNetworkCalc(final EncogCLDevice device) {
		super(device, "org/encog/engine/resources/KernelNetCalc.txt",
				"NetworkCalc");

		this.device = device;
		this.paramArray = new int[10];
		this.paramBuffer = createArrayReadOnly(this.paramArray);

	}

	/**
	 * Calculate one iteration over the specified range.
	 * 
	 * @param start
	 *            The starting position to calculate for.
	 * @param size
	 *            The ending position to calculate for.
	 * @param iterations
	 *            The number of iterations to execute.
	 * @param learn
	 *            True, if we should learn.
	 */
	public void calculate(final int start, final int size) {
		prepareKernel();

		this.paramArray[KernelNetworkCalc.PARRAY_START] = start;
		this.paramArray[KernelNetworkCalc.PARRAY_ITEMS_PER] = size;
		this.setGlobalWork(size);
		this.setLocalWork(64);

		EngineArray.arrayCopy(this.flat.getWeights(), this.weightInArray);

		setArg(0, this.paramBuffer);
		setArg(1, this.errorBuffer);
		setArg(2, this.layerIndexBuffer);
		setArg(3, this.layerCountBuffer);
		setArg(4, this.layerFeedCountBuffer);
		setArg(5, this.weightIndexBuffer);
		setArg(6, this.inputBuffer);
		setArg(7, this.idealBuffer);
		setArg(8, this.weightInArrayBuffer);
		setArg(9, this.layerOutputBuffer);

		try {
			final EncogCLQueue queue = this.device.getQueue();

			this.paramArray[4] = start;

			queue.array2Buffer(this.weightInArray, this.weightInArrayBuffer);
			queue.array2Buffer(this.paramArray, this.paramBuffer);

			// Execute the kernel
			queue.execute(this);
			queue.waitFinish();

			// Read the results
			queue.buffer2Array(this.errorBuffer, this.errors);
			queue.buffer2Array(this.layerOutputBuffer, this.layerOutput );

		} catch (final CLException e) {
			if (e.getMessage().equals("CL_OUT_OF_RESOURCES")) {
				throw new OutOfOpenCLResources(e);
			} else {
				throw new OpenCLError(e);
			}
		} catch (final Exception e) {
			throw new OpenCLError(e);
		}
	}

	/**
	 * Compile the kernel.
	 * 
	 * @param options
	 *            The options.
	 * @param profile
	 *            The OpenCL training profile.
	 * @param network
	 *            The network to compile for.
	 */
	public void compile(final FlatNetwork network) {

		final ActivationFunction activation = network.getActivationFunctions()[0];
		final StringBuilder source = new StringBuilder();

		source.append("#define ACTIVATION(x,slope)");
		source.append(activation.getOpenCLExpression(false));
		source.append("\r\n");

		source.append(ResourceLoader.loadString(getSourceName()));
		setCLSource(source.toString());
		
		final Map<String, String> options = new HashMap<String, String>();
		options.put("NEURON_COUNT", "" + network.getNeuronCount());
		options.put("WEIGHT_COUNT", "" + network.getWeights().length);

		compile(options);
	}

	/**
	 * @return the errors
	 */
	public float[] getErrors() {
		return this.errors;
	}

	/**
	 * Release the kernel and all buffers.
	 */
	@Override
	public void release() {
		super.release();

		if (this.errorBuffer != null) {
			releaseBuffer(this.errorBuffer);
			this.errorBuffer = null;
		}

		if (this.idealBuffer != null) {
			releaseBuffer(this.idealBuffer);
			this.idealBuffer = null;
		}

		if (this.inputBuffer != null) {
			releaseBuffer(this.inputBuffer);
			this.inputBuffer = null;
		}

		if (this.layerCountBuffer != null) {
			releaseBuffer(this.layerCountBuffer);
			this.layerCountBuffer = null;
		}

		if (this.layerFeedCountBuffer != null) {
			releaseBuffer(this.layerFeedCountBuffer);
			this.layerFeedCountBuffer = null;
		}

		if (this.layerIndexBuffer != null) {
			releaseBuffer(this.layerIndexBuffer);
			this.layerIndexBuffer = null;
		}

		if (this.paramBuffer != null) {
			releaseBuffer(this.paramBuffer);
			this.paramBuffer = null;
		}

		if (this.weightInArrayBuffer != null) {
			releaseBuffer(this.weightInArrayBuffer);
			this.weightInArrayBuffer = null;
		}

		if (this.weightIndexBuffer != null) {
			releaseBuffer(this.weightIndexBuffer);
			this.weightIndexBuffer = null;
		}
	}

	public FlatNetwork getFlat() {
		return flat;
	}

	public void setFlat(FlatNetwork flat) {
		this.flat = flat;

		this.weightInArray = new float[flat.getWeights().length];

		final int inputSize = flat.getInputCount();
		final int idealSize = flat.getOutputCount();

		this.paramArray[0] = this.flat.getInputCount();
		this.paramArray[1] = this.flat.getOutputCount();
		this.paramArray[2] = this.flat.getLayerCounts().length;

		if (this.layerCountBuffer != null) {
			releaseBuffer(this.layerCountBuffer);
			this.layerCountBuffer = null;
		}

		if (this.layerFeedCountBuffer != null) {
			releaseBuffer(this.layerFeedCountBuffer);
			this.layerFeedCountBuffer = null;
		}

		if (this.layerIndexBuffer != null) {
			releaseBuffer(this.layerIndexBuffer);
			this.layerIndexBuffer = null;
		}

		if (this.weightInArrayBuffer != null) {
			releaseBuffer(this.weightInArrayBuffer);
			this.weightInArrayBuffer = null;
		}

		if (this.weightIndexBuffer != null) {
			releaseBuffer(this.weightIndexBuffer);
			this.weightIndexBuffer = null;
		}

		this.layerIndexBuffer = createArrayReadOnly(this.flat.getLayerIndex());
		this.layerCountBuffer = createArrayReadOnly(this.flat.getLayerCounts());
		this.layerFeedCountBuffer = createArrayReadOnly(this.flat
				.getLayerFeedCounts());
		this.weightInArrayBuffer = createArrayReadOnly(this.weightInArray);
		this.weightIndexBuffer = createArrayReadOnly(this.flat.getWeightIndex());
		allocateCommon();
		compile(flat);
	}

	private void allocateCommon() {
		if (this.training != null && this.flat != null) {

			if (this.layerOutputBuffer != null) {
				releaseBuffer(this.layerOutputBuffer);
				this.layerOutputBuffer = null;
			}

			this.layerOutput = new float[this.flat.getLayerOutput().length*this.trainingLength];
			this.layerOutputBuffer = this
					.createFloatArrayWriteOnly(this.layerOutput.length);
		}
	}

	public EngineIndexableSet getTraining() {
		return training;
	}

	public void setTraining(EngineIndexableSet training) {
		this.training = training;
		this.trainingLength = (int) this.training.getRecordCount();

		final EngineData pair = BasicEngineData.createPair(
				flat.getInputCount(), flat.getOutputCount());

		this.inputArray = new float[training.getInputSize() * this.trainingLength];
		this.idealArray = new float[training.getIdealSize() * this.trainingLength];
		
		int inputIndex = 0;
		int idealIndex = 0;

		for (int i = 0; i < this.trainingLength; i++) {
			training.getRecord(i, pair);
			for (int col = 0; col < flat.getInputCount(); col++) {
				this.inputArray[inputIndex++] = (float) pair.getInputArray()[col];
			}

			for (int col = 0; col < flat.getOutputCount(); col++) {
				this.idealArray[idealIndex++] = (float) pair.getIdealArray()[col];
			}
		}

		final int errorSize = (int) training.getRecordCount();
		this.errors = new float[errorSize];

		if (this.errorBuffer != null) {
			releaseBuffer(this.errorBuffer);
			this.errorBuffer = null;
		}

		if (this.idealBuffer != null) {
			releaseBuffer(this.idealBuffer);
			this.idealBuffer = null;
		}

		if (this.inputBuffer != null) {
			releaseBuffer(this.inputBuffer);
			this.inputBuffer = null;
		}

		this.errorBuffer = createFloatArrayWriteOnly(errorSize);
		this.inputBuffer = createArrayReadOnly(this.inputArray);
		this.idealBuffer = createArrayReadOnly(this.idealArray);

		allocateCommon();
	}

	/**
	 * @return The error from the last evaluation.
	 */
	public double getError() {
		ErrorCalculation ec = new ErrorCalculation();
		double result = 0;
		for (int i = 0; i < this.errors.length; i++) {
			result += this.errors[i];
		}
		return result/(this.errors.length*this.flat.getOutputCount());
	}

}
