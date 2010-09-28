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

package org.encog.engine.network.train.prop;

import java.util.HashMap;
import java.util.Map;

import org.encog.engine.EncogEngine;
import org.encog.engine.EncogEngineError;
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.flat.ValidateForOpenCL;
import org.encog.engine.network.train.TrainFlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.kernels.KernelNetworkTrain;
import org.encog.engine.util.ErrorCalculation;
import org.encog.engine.util.ErrorCalculationMode;

public class TrainFlatNetworkOpenCL implements TrainFlatNetwork {

	public static final int LEARN_RPROP = 0;
	public static final int LEARN_BPROP = 1;
	public static final int LEARN_MANHATTAN = 2;

	private double error;
	private EncogCLDevice targetDevice;

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The training data.
	 */
	private final EngineIndexableSet training;

	/**
	 * Training type.
	 */
	private int learningType;

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The momentum.
	 */
	private double momentum;

	/**
	 * The initial update.
	 */
	private double initialUpdate;

	/**
	 * The max step.
	 */
	private double maxStep;
	
	private KernelNetworkTrain kernel;

	/**
	 * Train a flat network multithreaded.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 */
	public TrainFlatNetworkOpenCL(final FlatNetwork network,
			final EngineDataSet training, final EncogCLDevice targetDevice) {

		(new ValidateForOpenCL()).validate(network);
		
		if (!(training instanceof EngineIndexableSet)) {
			throw new EncogEngineError(
					"Training data must be Indexable for this training type.");
		}

		if (EncogEngine.getInstance().getCL() == null) {
			throw new EncogEngineError(
					"You must enable OpenCL before using this training type.");

		}

		this.targetDevice = targetDevice;
		this.network = network;
		this.training = (EngineIndexableSet) training;				
	}

	public void learnRPROP() {
		learnRPROP(RPROPConst.DEFAULT_INITIAL_UPDATE,
				RPROPConst.DEFAULT_MAX_STEP);
	}

	public void learnRPROP(final double initialUpdate, final double maxStep) {
		this.learningType = TrainFlatNetworkOpenCL.LEARN_RPROP;
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;
		
		final Map<String, String> options = getOptions("LEARN_RPROP");

		this.kernel = new KernelNetworkTrain(targetDevice, network, this.training, this.network.getWeights().length * 2);
		
		kernel.compile(options,network);
		
		int weightLength = this.network.getWeights().length;

		for(int i=0;i<weightLength;i++)
		{
			kernel.getTempDataArray()[i] = 0;
			kernel.getTempDataArray()[i+weightLength] = (float)this.initialUpdate;
		}

	}
	
	private Map<String,String> getOptions(String learningType)
	{
		final Map<String, String> options = new HashMap<String, String>();
		options.put("NEURON_COUNT", "" + this.network.getNeuronCount());
		options.put("WEIGHT_COUNT", "" + this.network.getWeights().length);
		options.put(learningType, null);

		return options;
	}

	public void learnBPROP(double learningRate, double momentum) {
		this.learningType = TrainFlatNetworkOpenCL.LEARN_BPROP;
		this.momentum = momentum;
		this.learningRate = learningRate;
		
		this.learningType = TrainFlatNetworkOpenCL.LEARN_BPROP;
		
		final Map<String, String> options = getOptions("LEARN_BPROP");

		this.kernel = new KernelNetworkTrain(targetDevice, network, this.training, this.network.getWeights().length+2 );
		kernel.compile(options,network);
		kernel.getTempDataArray()[0]=(float)learningRate;
		kernel.getTempDataArray()[1]=(float)momentum;
	}

	public void learnManhattan(double learningRate) {
		this.learningType = TrainFlatNetworkOpenCL.LEARN_MANHATTAN;
		this.learningRate = learningRate;
		
		final Map<String, String> options = getOptions("LEARN_MANHATTAN");

		this.kernel = new KernelNetworkTrain(targetDevice, network, this.training, 1 );
		kernel.compile(options,network);
		kernel.getTempDataArray()[0]=(float)learningRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getError() {
		return error;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlatNetwork getNetwork() {
		return this.network;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumThreads() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public EncogCLDevice getTargetDevice() {
		return this.targetDevice;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EngineDataSet getTraining() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iteration() {

		if (this.learningType == -1) {
			throw new EncogEngineError(
					"Learning type has not been defined yet, you must first call one of the learnXXXX methods, such as learnRPROP.");
		}
		
		long networkLoad = this.network.getWeights().length*(this.training.getRecordCount());
		int workloadCount = (int)(networkLoad/(long)(EncogEngine.getInstance().getCL().getMaxTrainingSize()*1000l));
		int maxWorkloadSize = (int)this.training.getRecordCount()/workloadCount;
		int lastWorkloadSize = (int)this.training.getRecordCount()%workloadCount;
		
		if( workloadCount==0 )
			lastWorkloadSize = maxWorkloadSize;
		
		int currentIndex = 0;
		this.error=0;
		
		while(workloadCount>0) {
			this.callKernel(currentIndex, maxWorkloadSize,false);
			workloadCount--;
			currentIndex+=maxWorkloadSize;
		}
		
		this.callKernel(currentIndex, lastWorkloadSize,true);

		int count = (int)this.training.getRecordCount();
		this.error= this.error / (count * this.training.getIdealSize());
		
		if (ErrorCalculation.getMode() == ErrorCalculationMode.RMS) {
			this.error = Math.sqrt(error);
		}

		final int len = this.network.getWeights().length;

		for (int i = 0; i < len; i++) {
			this.network.getWeights()[i] = this.kernel.getWeightOutArray()[i];
		}
		// this.owner.report(this.gradients, error, null);

	}
	
	private void callKernel(int start, int size, boolean learn)
	{
		this.kernel.calculate(0,(int)this.training.getRecordCount(),learn);

		double e = 0;

		for (int i = 0; i < this.kernel.getGlobalWork(); i++) {
			e += this.kernel.getErrors()[i];
		}
		this.error = e;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumThreads(int numThreads) {
		// TODO Auto-generated method stub

	}

	public double[] getLastGradient() {
		double[] result = new double[this.network.getWeights().length];
		for(int i=0;i<result.length;i++)
			result[i] = kernel.getTempDataArray()[i];
		return result;
	}

	public double[] getUpdateValues() {
		double[] result = new double[this.network.getWeights().length];
		int len = this.network.getWeights().length;
		for(int i=0;i<result.length;i++)
			result[i] = kernel.getTempDataArray()[len+i];
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void finishTraining() {
		if( this.kernel!=null )
			this.kernel.release();
	}

}
