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
import org.encog.engine.util.Format;
import org.encog.engine.util.ResourceLoader;
import org.jocl.cl_mem;

/**
 * An OpenCL kernel that is designed to calculate gradients and help train a
 * neural network.
 */
public class KernelNetworkTrain extends BaseTrainKernel {

	/**
	 * The size of all layer deltas.
	 */
	private int layerDeltaSize;

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
		int trainingLength = (int) this.getTraining().getRecordCount();
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
		super(flat,training,device, "org/encog/engine/resources/KernelNetTrain.txt",
				"NetworkTrain");
		
		this.setWeightInArray( new float[flat.getWeights().length] );
		this.setWeightOutArray( new float[flat.getWeights().length] );
		this.setTempDataArray( new float[tempDataSize] );
		this.setSlopeArray( new float[flat.getParams().length] );

		this.layerDeltaSize = 0;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			this.layerDeltaSize += flat.getLayerCounts()[i];
		}

		for (int i = 0; i < this.getSlopeArray().length; i++) {
			this.getSlopeArray()[i] = (float) flat.getParams()[i];
		}

		int trainingLength = (int) training.getRecordCount();
		int inputSize = flat.getInputCount();
		int idealSize = flat.getOutputCount();
		
		this.setInputArray( new float[inputSize * trainingLength] );
		this.setIdealArray( new float[idealSize * trainingLength] );
		this.setParamArray( new int[10] );

		final EngineData pair = BasicEngineData.createPair(
				flat.getInputCount(), flat.getOutputCount());

		int inputIndex = 0;
		int idealIndex = 0;
		
		float[] inputArray = this.getInputArray();
		float[] idealArray = this.getIdealArray();

		for (int i = 0; i < trainingLength; i++) {
			training.getRecord(i, pair);
			for (int col = 0; col < flat.getInputCount(); col++) {
				inputArray[inputIndex++] = (float) pair.getInputArray()[col];
			}

			for (int col = 0; col < flat.getOutputCount(); col++) {
				idealArray[idealIndex++] = (float) pair.getIdealArray()[col];
			}
		}

	}
	
	public void init()
	{		
		int trainingLength = (int) this.getTraining().getRecordCount();

		final int errorSize = this.getGlobalWork();
		final int gradientSize = this.getGlobalWork() * this.getFlat().getWeights().length;

		this.setErrors(new float[errorSize]);

		this.getParamArray()[0] = this.getFlat().getInputCount();
		this.getParamArray()[1] = this.getFlat().getOutputCount();
		this.getParamArray()[2] = this.getFlat().getLayerCounts().length;
		this.getParamArray()[6] = this.getGlobalWork() - 1;// index of last item
		// size each item
		this.getParamArray()[7] = Math.max(trainingLength / this.getGlobalWork(), 1);
		// size of last item
		if( this.getGlobalWork()==1 )
			this.getParamArray()[8] = trainingLength;
		else
			this.getParamArray()[8] = Math.max(trainingLength % this.getGlobalWork(), 1);

		// create the buffers
		this.setInputBuffer( createArrayReadOnly(this.getInputArray()) );
		this.setIdealBuffer( createArrayReadOnly(this.getIdealArray()) );
		this.setErrorBuffer( createFloatArrayWriteOnly(errorSize) );
		this.setGradientBuffer( createFloatArrayWriteOnly(gradientSize) );		
		this.setParamBuffer( createArrayReadOnly(this.getParamArray()) );
		this.setLayerIndexBuffer( createArrayReadOnly(this.getFlat().getLayerIndex()) );
		this.setLayerCountBuffer( createArrayReadOnly(this.getFlat().getLayerCounts()) );
		this.setLayerFeedCountBuffer( createArrayReadOnly(this.getFlat().getLayerFeedCounts()) );
		this.setWeightInArrayBuffer( createArrayReadOnly(this.getWeightInArray()) );
		this.setWeightOutArrayBuffer( createFloatArrayWriteOnly(this.getWeightInArray().length) );
		this.setWeightIndexBuffer( createArrayReadOnly(this.getFlat().getWeightIndex()) );
		this.setActivationTypeBuffer( createArrayReadOnly(this.getFlat().getActivationType()) );
		this.setSlopeBuffer( createArrayReadOnly(this.getSlopeArray()) );
		this.setTempDataInBuffer( createArrayReadOnly(this.getTempDataArray()) );
		this.setTempDataOutBuffer( createFloatArrayWriteOnly(this.getTempDataArray().length) );
	}

	/**
	 * Calculate the gradients for one workload.
	 * 
	 * @param workload
	 *            The workload to calculate for.
	 */
	public void calculate() {
		prepareKernel();

		double[] weights = this.getFlat().getWeights();
		float[] weightInArray = this.getWeightInArray();
		for (int i = 0; i < this.getFlat().getWeights().length; i++) {
			weightInArray[i] = (float) weights[i];
		}

		setArgs();

		try {
			EncogCLQueue queue = this.getDevice().getQueue();
			
			queue.array2BufferFloat(this.getWeightInArray(),this.getWeightInArrayBuffer());
			queue.array2BufferFloat(this.getTempDataArray(),this.getTempDataInBuffer());

			// Execute the kernel
			queue.execute(this);
			queue.waitFinish();
			
			// Read the results
			queue.buffer2Float(this.getErrorBuffer(),this.getErrors());
			queue.buffer2Float(this.getWeightOutArrayBuffer(),this.getWeightOutArray());
			queue.buffer2Float(this.getTempDataOutBuffer(),this.getTempDataArray());
			
		} catch (final Exception e) {
			throw new EncogEngineError(e);
		}
	}

	public void release() {
		super.release();
		releaseBuffer(this.getActivationTypeBuffer());
		releaseBuffer(this.getErrorBuffer());
		releaseBuffer(this.getGradientBuffer());
		releaseBuffer(this.getIdealBuffer());
		releaseBuffer(this.getInputBuffer());
		releaseBuffer(this.getLayerCountBuffer());
		releaseBuffer(this.getLayerFeedCountBuffer());
		releaseBuffer(this.getLayerIndexBuffer());
		releaseBuffer(this.getParamBuffer());
		releaseBuffer(this.getSlopeBuffer());
		releaseBuffer(this.getTempDataInBuffer());
		releaseBuffer(this.getTempDataOutBuffer());
		releaseBuffer(this.getWeightInArrayBuffer());
		releaseBuffer(this.getWeightIndexBuffer());
		releaseBuffer(this.getWeightOutArrayBuffer());
	}
}
