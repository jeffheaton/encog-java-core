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
package org.encog.neural.networks.training.simple;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * Train an ADALINE neural network.
 */
public class TrainAdaline extends BasicTraining implements LearningRate {

	/**
	 * The network to train.
	 */
	private final BasicNetwork network;

	/**
	 * The training data to use.
	 */
	private final MLDataSet training;

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * Construct an ADALINE trainer.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param learningRate
	 *            The learning rate.
	 */
	public TrainAdaline(final BasicNetwork network, final MLDataSet training,
			final double learningRate) {
		super(TrainingImplementationType.Iterative);
		if (network.getLayerCount() > 2) {
			throw new NeuralNetworkError(
					"An ADALINE network only has two layers.");
		}
		this.network = network;

		this.training = training;
		this.learningRate = learningRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void iteration() {

		final ErrorCalculation errorCalculation = new ErrorCalculation();

		for (final MLDataPair pair : this.training) {
			// calculate the error
			final MLData output = this.network.compute(pair.getInput());

			for (int currentAdaline = 0; currentAdaline < output.size(); currentAdaline++) {
				final double diff = pair.getIdeal().getData(currentAdaline)
						- output.getData(currentAdaline);

				// weights
				for (int i = 0; i <= this.network.getInputCount(); i++) {
					final double input;

					if (i == this.network.getInputCount()) {
						input = 1.0;
					} else {
						input = pair.getInput().getData(i);
					}

					this.network.addWeight(0, i, currentAdaline,
							this.learningRate * diff * input);
				}
			}

			errorCalculation.updateError(output.getData(), pair.getIdeal()
					.getData(),pair.getSignificance());
		}

		// set the global error
		setError(errorCalculation.calculate());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The new learning rate.
	 */
	@Override
	public final void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

}
