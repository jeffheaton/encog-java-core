/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.flat.ValidateForOpenCL;
import org.encog.engine.network.train.TrainFlatNetwork;
import org.encog.engine.opencl.kernels.KernelNetworkTrain;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;
import org.encog.engine.util.ErrorCalculationMode;

/**
 * Train a flat network using OpenCL.
 */
public class TrainFlatNetworkOpenCL implements TrainFlatNetwork {

	/**
	 * Learn RPROP.
	 */
	public static final int LEARN_RPROP = 0;

	/**
	 * Learn backpropagation.
	 */
	public static final int LEARN_BPROP = 1;

	/**
	 * Learn Manhattan update rule.
	 */
	public static final int LEARN_MANHATTAN = 2;

	/**
	 * The error.
	 */
	private double error;

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The training data.
	 */
	private final EngineDataSet training;

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

	/**
	 * The kernel in use.
	 */
	private KernelNetworkTrain kernel;

	/**
	 * The iteration.
	 */
	private int iteration;

	private final OpenCLTrainingProfile profile;

	/**
	 * Train a flat network multithreaded.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param profile
	 *            The OpenCL training profile.
	 */
	public TrainFlatNetworkOpenCL(final FlatNetwork network,
			final EngineDataSet training, final OpenCLTrainingProfile profile) {

		(new ValidateForOpenCL()).validate(network);

		if (!(training instanceof EngineDataSet)) {
			throw new EncogEngineError(
					"Training data must be Indexable for this training type.");
		}

		if (EncogEngine.getInstance().getCL() == null) {
			throw new EncogEngineError(
					"You must enable OpenCL before using this training type.");

		}

		this.profile = profile;
		this.network = network;
		this.training = (EngineDataSet) training;
	}

	/**
	 * Call the kernel.
	 * 
	 * @param start
	 *            The starting training element.
	 * @param size
	 *            The number of training elements.
	 * @param learn
	 *            Should we learn?
	 * @param iterations
	 *            The number of iterations.
	 */
	private void callKernel(final int start, final int size,
			final boolean learn, final int iterations) {
		// System.out.println("Iteration: start=" + start + ",sizePer=" + size +
		// ",total=" + (size*this.kernel.getGlobalWork()) );
		this.kernel.calculate(start, size, learn, iterations);

		double e = 0;

		for (int i = 0; i < this.kernel.getGlobalWork(); i++) {
			e += this.kernel.getErrors()[i];
		}

		this.error += e;
	}

	/**
	 * {@inheritDoc}
	 */
	public void finishTraining() {
		if (this.kernel != null) {
			this.kernel.release();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getError() {
		return this.error;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getIteration() {
		return this.iteration;
	}

	/**
	 * @return The last gradients.
	 */
	public double[] getLastGradient() {
		final double[] result = new double[this.network.getWeights().length];
		for (int i = 0; i < result.length; i++) {
			result[i] = this.kernel.getTempDataArray()[i];
		}
		return result;
	}

	/**
	 * @return the learningRate
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return the learningType
	 */
	public int getLearningType() {
		return this.learningType;
	}

	/**
	 * @return the maxStep
	 */
	public double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * @return the momentum
	 */
	public double getMomentum() {
		return this.momentum;
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
		return 0;
	}

	/**
	 * Get the learning properties.
	 * 
	 * @param learningType
	 *            The learning type.
	 * @return The options.
	 */
	private Map<String, String> getOptions(final String learningType) {
		final Map<String, String> options = new HashMap<String, String>();
		options.put("NEURON_COUNT", "" + this.network.getNeuronCount());
		options.put("WEIGHT_COUNT", "" + this.network.getWeights().length);
		options.put(learningType, null);

		return options;
	}

	/**
	 * @return The training data to use.
	 */
	@Override
	public EngineDataSet getTraining() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return The update values.
	 */
	public double[] getUpdateValues() {
		final double[] result = new double[this.network.getWeights().length];
		final int len = this.network.getWeights().length;
		for (int i = 0; i < result.length; i++) {
			result[i] = this.kernel.getTempDataArray()[len + i];
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public void iteration() {
		iteration(1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iteration(final int iterations) {

		if (this.learningType == -1) {
			throw new EncogEngineError(
					"Learning type has not been defined yet, you must first call one of the learnXXXX methods, such as learnRPROP.");
		}

		this.iteration += iterations;
		int currentIndex = 0;
		this.error = 0;

		int count = this.profile.getKernelNumberOfCalls();

		// If we are using an OpenCL ratio other than 1.0, which means that we
		// are
		// braining up a single training iteration, there is no reason to try
		// and batch
		// up multiple iterations.
		if ((count > 0) && (iterations > 1)) {
			throw new EncogEngineError(
					"Must use an OpenCL ratio of 1.0 if you are going to use an iteration count > 1.");
		}

		this.kernel.setGlobalWork(this.profile.getKernelGlobalWorkgroup());
		this.kernel.setLocalWork(this.profile.getKernelLocalWorkgroup());

		// handle workloads
		while (count > 0) {
			callKernel(currentIndex, this.profile.getKernelWorkPerCall(),
					false, 1);
			count--;
			currentIndex += this.profile.getKernelWorkPerCall()
					* this.kernel.getGlobalWork();
		}

		// handle the final workload
		this.kernel.setGlobalWork(this.profile.getKernelRemainderGlobal());
		this.kernel.setLocalWork(this.profile.getKernelRemainderGlobal());

		callKernel(currentIndex, this.profile.getKernelRemainderPer(), true,
				iterations);

		count = (int) this.training.getRecordCount();
		this.error = this.error / (count * this.training.getIdealSize());

		if (ErrorCalculation.getMode() == ErrorCalculationMode.RMS) {
			this.error = Math.sqrt(this.error);
		}

		EngineArray.arrayCopy(this.kernel.getWeightOutArray(), this.network
				.getWeights());

	}

	/**
	 * Learn using backpropagation.
	 * 
	 * @param learningRate
	 *            The learning rate.
	 * @param momentum
	 *            The momentum.
	 */
	public void learnBPROP(final double learningRate, final double momentum) {
		this.learningType = TrainFlatNetworkOpenCL.LEARN_BPROP;
		this.momentum = momentum;
		this.learningRate = learningRate;

		this.learningType = TrainFlatNetworkOpenCL.LEARN_BPROP;

		final Map<String, String> options = getOptions("LEARN_BPROP");

		this.kernel = new KernelNetworkTrain(this.profile.getDevice(),
				this.network, this.training,
				this.network.getWeights().length + 2);
		this.kernel.compile(options, this.profile, this.network);

		this.kernel.getTempDataArray()[0] = (float) learningRate;
		this.kernel.getTempDataArray()[1] = (float) momentum;
	}

	/**
	 * Learn using the Manhattan update rule.
	 * 
	 * @param learningRate
	 *            The learning rate.
	 */
	public void learnManhattan(final double learningRate) {
		this.learningType = TrainFlatNetworkOpenCL.LEARN_MANHATTAN;
		this.learningRate = learningRate;

		final Map<String, String> options = getOptions("LEARN_MANHATTAN");

		this.kernel = new KernelNetworkTrain(this.profile.getDevice(),
				this.network, this.training, 1);
		this.kernel.compile(options, this.profile, this.network);

		this.kernel.getTempDataArray()[0] = (float) learningRate;
	}

	/**
	 * Learn using RPROP. Use default max step and initial update.
	 */
	public void learnRPROP() {
		learnRPROP(RPROPConst.DEFAULT_INITIAL_UPDATE,
				RPROPConst.DEFAULT_MAX_STEP);
	}

	/**
	 * Learn using RPROP with a custom initial update and max step.
	 * 
	 * @param initialUpdate
	 *            The initial update value.
	 * @param maxStep
	 *            The max step.
	 */
	public void learnRPROP(final double initialUpdate, final double maxStep) {
		this.learningType = TrainFlatNetworkOpenCL.LEARN_RPROP;
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;

		final Map<String, String> options = getOptions("LEARN_RPROP");

		this.kernel = new KernelNetworkTrain(this.profile.getDevice(),
				this.network, this.training,
				this.network.getWeights().length * 2);

		this.kernel.compile(options, this.profile, this.network);

		final int weightLength = this.network.getWeights().length;

		for (int i = 0; i < weightLength; i++) {
			this.kernel.getTempDataArray()[i] = 0;
			this.kernel.getTempDataArray()[i + weightLength] = (float) this.initialUpdate;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumThreads(final int numThreads) {

	}
}
