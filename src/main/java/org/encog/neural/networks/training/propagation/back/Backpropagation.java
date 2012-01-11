/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation.back;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.util.validate.ValidateNetwork;

/**
 * This class implements a backpropagation training algorithm for feed forward
 * neural networks. It is used in the same manner as any other training class
 * that implements the Train interface.
 * 
 * Backpropagation is a common neural network training algorithm. It works by
 * analyzing the error of the output of the neural network. Each neuron in the
 * output layer's contribution, according to weight, to this error is
 * determined. These weights are then adjusted to minimize this error. This
 * process continues working its way backwards through the layers of the neural
 * network.
 * 
 * This implementation of the backpropagation algorithm uses both momentum and a
 * learning rate. The learning rate specifies the degree to which the weight
 * matrixes will be modified through each iteration. The momentum specifies how
 * much the previous learning iteration affects the current. To use no momentum
 * at all specify zero.
 * 
 * One primary problem with backpropagation is that the magnitude of the partial
 * derivative is often detrimental to the training of the neural network. The
 * other propagation methods of Manhatten and Resilient address this issue in
 * different ways. In general, it is suggested that you use the resilient
 * propagation technique for most Encog training tasks over back propagation.
 */
public class Backpropagation extends Propagation implements Momentum,
		LearningRate {

	/**
	 * The resume key for backpropagation.
	 */
	public static final String LAST_DELTA = "LAST_DELTA";
	
	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The momentum.
	 */
	private double momentum;

	/**
	 * The last delta values.
	 */
	private double[] lastDelta;


	/**
	 * Create a class to train using backpropagation. Use auto learn rate and
	 * momentum. Use the CPU to train.
	 * 
	 * @param network
	 *            The network that is to be trained.
	 * @param training
	 *            The training data to be used for backpropagation.
	 */
	public Backpropagation(final ContainsFlat network, final MLDataSet training) {
		this(network, training, 0, 0);
		addStrategy(new SmartLearningRate());
		addStrategy(new SmartMomentum());
	}

	/**
	 * 
	 * @param network
	 *            The network that is to be trained
	 * @param training
	 *            The training set
	 * @param theLearnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param theMomentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Backpropagation(final ContainsFlat network,
			final MLDataSet training, final double theLearnRate,
			final double theMomentum) {
		super(network, training);
		ValidateNetwork.validateMethodToData(network, training);
		this.momentum = theMomentum;
		this.learningRate = theLearnRate;
		this.lastDelta = new double[network.getFlat().getWeights().length];

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * @return Ther last delta values.
	 */
	public final double[] getLastDelta() {
		return this.lastDelta;
	}

	/**
	 * @return The learning rate, this is value is essentially a percent. It is
	 *         the degree to which the gradients are applied to the weight
	 *         matrix to allow learning.
	 */
	@Override
	public final double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The momentum for training. This is the degree to which changes
	 *         from which the previous training iteration will affect this
	 *         training iteration. This can be useful to overcome local minima.
	 */
	@Override
	public final double getMomentum() {
		return this.momentum;
	}

	/**
	 * Determine if the specified continuation object is valid to resume with.
	 * 
	 * @param state
	 *            The continuation object to check.
	 * @return True if the specified continuation object is valid for this
	 *         training method and network.
	 */
	public final boolean isValidResume(final TrainingContinuation state) {
		if (!state.getContents().containsKey(Backpropagation.LAST_DELTA)) {
			return false;
		}

		if (!state.getTrainingType().equals(getClass().getSimpleName())) {
			return false;
		}

		final double[] d = (double[]) state.get(Backpropagation.LAST_DELTA);
		return d.length == ((ContainsFlat) getMethod()).getFlat().getWeights().length;
	}

	/**
	 * Pause the training.
	 * 
	 * @return A training continuation object to continue with.
	 */
	@Override
	public final TrainingContinuation pause() {
		final TrainingContinuation result = new TrainingContinuation();
		result.setTrainingType(this.getClass().getSimpleName());
		result.set(Backpropagation.LAST_DELTA, this.lastDelta);
		return result;
	}

	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training state to return to.
	 */
	@Override
	public final void resume(final TrainingContinuation state) {
		if (!isValidResume(state)) {
			throw new TrainingError("Invalid training resume data length");
		}

		this.lastDelta = ((double[]) state.get(Backpropagation.LAST_DELTA));

	}

	/**
	 * Set the learning rate, this is value is essentially a percent. It is the
	 * degree to which the gradients are applied to the weight matrix to allow
	 * learning.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	@Override
	public final void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	/**
	 * Set the momentum for training. This is the degree to which changes from
	 * which the previous training iteration will affect this training
	 * iteration. This can be useful to overcome local minima.
	 * 
	 * @param m
	 *            The momentum.
	 */
	@Override
	public final void setMomentum(final double m) {
		this.momentum = m;
	}
	
	/**
	 * Update a weight.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index.
	 * @return The weight delta.
	 */
	@Override
	public final double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {
		final double delta = (gradients[index] * this.learningRate)
				+ (this.lastDelta[index] * this.momentum);
		this.lastDelta[index] = delta;
		return delta;
	}

	/**
	 * Perform training method specific init.
	 */
	public void initOthers() {
		
	}
}
