/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation.sgd;

import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
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

public class StochasticGradientDescent extends Propagation implements Momentum,
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
	
	private StochasticDataSet dataset;
	
	
	/**
	 * Create a class to train using backpropagation. Use auto learn rate and
	 * momentum. Use the CPU to train.
	 * 
	 * @param network
	 *            The network that is to be trained.
	 * @param training
	 *            The training data to be used for backpropagation.
	 */
	public StochasticGradientDescent(final ContainsFlat network, final MLDataSet training) {
		this(network, training, 600, 0.001, 0.9);
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
	public StochasticGradientDescent(final ContainsFlat network,
			final MLDataSet training, final int batchSize, final double theLearnRate,
			final double theMomentum) {
		super(network, new StochasticDataSet(training,new MersenneTwisterGenerateRandom()));
		ValidateNetwork.validateMethodToData(network, training);
		this.dataset = (StochasticDataSet) getTraining();
		this.momentum = theMomentum;
		this.learningRate = theLearnRate;
		this.lastDelta = new double[network.getFlat().getWeights().length];
		this.setBatchSize(0);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return The last delta values.
	 */
	public double[] getLastDelta() {
		return this.lastDelta;
	}

	/**
	 * @return The learning rate, this is value is essentially a percent. It is
	 *         the degree to which the gradients are applied to the weight
	 *         matrix to allow learning.
	 */
	@Override
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The momentum for training. This is the degree to which changes
	 *         from which the previous training iteration will affect this
	 *         training iteration. This can be useful to overcome local minima.
	 */
	@Override
	public double getMomentum() {
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
	public boolean isValidResume(final TrainingContinuation state) {
		if (!state.getContents().containsKey(StochasticGradientDescent.LAST_DELTA)) {
			return false;
		}

		if (!state.getTrainingType().equals(getClass().getSimpleName())) {
			return false;
		}

		final double[] d = (double[]) state.get(StochasticGradientDescent.LAST_DELTA);
		return d.length == ((ContainsFlat) getMethod()).getFlat().getWeights().length;
	}

	/**
	 * Pause the training.
	 * 
	 * @return A training continuation object to continue with.
	 */
	@Override
	public TrainingContinuation pause() {
		final TrainingContinuation result = new TrainingContinuation();
		result.setTrainingType(this.getClass().getSimpleName());
		result.set(StochasticGradientDescent.LAST_DELTA, this.lastDelta);
		return result;
	}

	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training state to return to.
	 */
	@Override
	public void resume(final TrainingContinuation state) {
		if (!isValidResume(state)) {
			throw new TrainingError("Invalid training resume data length");
		}

		this.lastDelta = ((double[]) state.get(StochasticGradientDescent.LAST_DELTA));

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
	public void setLearningRate(final double rate) {
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
	public void setMomentum(final double m) {
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
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {
		final double delta = (gradients[index] * this.learningRate)
				+ (this.lastDelta[index] * this.momentum);
		this.lastDelta[index] = delta;
		return delta;
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
	 * @param dropoutRate
	 * 			  The dropout rate.
	 * @return The weight delta.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index, double dropoutRate) {
		
		if (dropoutRate > 0 && dropoutRandomSource.nextDouble() < dropoutRate) {
			return 0;
		};
		
		final double delta = (gradients[index] * this.learningRate)
				+ (this.lastDelta[index] * this.momentum);
		this.lastDelta[index] = delta;
		return delta;
	}	/**
	 * Perform training method specific init.
	 */
	public void initOthers() {
		
	}
	
	public void preIteration() {
		super.preIteration();
		this.dataset.resample();
	}
}