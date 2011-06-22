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
package org.encog.neural.networks.training.propagation.quick;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.train.prop.TrainFlatNetworkBackPropagation;
import org.encog.neural.flat.train.prop.TrainFlatNetworkQPROP;
import org.encog.neural.flat.train.prop.TrainFlatNetworkResilient;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.util.EngineArray;
import org.encog.util.validate.ValidateNetwork;


public class QuickPropagation extends Propagation implements 
		LearningRate {

	/**
	 * Continuation tag for the last gradients.
	 */
	public static final String LAST_GRADIENTS = "LAST_GRADIENTS";

	/**
	 * Construct a QPROP trainer for flat networks.  Uses a learning rate of 2.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 */
	public QuickPropagation(final ContainsFlat network, final MLDataSet training) {
		this(network, training, 0);
		addStrategy(new SmartLearningRate());
	}

	/**
	 * Construct a QPROP trainer for flat networks.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param theLearningRate
	 *            The learning rate.  2 is a good suggestion as 
	 *            a learning rate to start with.  If it fails to converge, 
	 *            then drop it.  Just like backprop, except QPROP can 
	 *            take higher learning rates.
	 */
	public QuickPropagation(final ContainsFlat network,
			final MLDataSet training, final double learnRate) {
		super(network, training);
		ValidateNetwork.validateMethodToData(network, training);
		final TrainFlatNetworkQPROP backFlat = new TrainFlatNetworkQPROP(
				network.getFlat(), getTraining(), learnRate);
		setFlatTraining(backFlat);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * @return The last delta values.
	 */
	public final double[] getLastDelta() {
		return ((TrainFlatNetworkBackPropagation) getFlatTraining())
				.getLastDelta();
	}

	/**
	 * @return The learning rate, this is value is essentially a percent. It is
	 *         the degree to which the gradients are applied to the weight
	 *         matrix to allow learning.
	 */
	@Override
	public final double getLearningRate() {
		return ((TrainFlatNetworkBackPropagation) getFlatTraining())
				.getLearningRate();
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
		if (!state.getContents().containsKey(QuickPropagation.LAST_GRADIENTS)) {
			return false;
		}

		if (!state.getTrainingType().equals(getClass().getSimpleName())) {
			return false;
		}

		final double[] d = (double[]) state.get(QuickPropagation.LAST_GRADIENTS);
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
		final TrainFlatNetworkQPROP qprop = (TrainFlatNetworkQPROP) getFlatTraining();
		final double[] d = qprop.getLastGradient();
		result.set(QuickPropagation.LAST_GRADIENTS, d);
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

		final double[] lastGradient = (double[]) state
				.get(QuickPropagation.LAST_GRADIENTS);

		EngineArray.arrayCopy(lastGradient,
				((TrainFlatNetworkQPROP) getFlatTraining()).getLastGradient());
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
		((TrainFlatNetworkBackPropagation) getFlatTraining())
				.setLearningRate(rate);
	}
}
