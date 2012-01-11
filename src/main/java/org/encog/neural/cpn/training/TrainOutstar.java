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
package org.encog.neural.cpn.training;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.cpn.CPN;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.EngineArray;

/**
 * Used for Instar training of a CPN neural network. A CPN network is a hybrid
 * supervised/unsupervised network. The Outstar training handles the supervised
 * portion of the training.
 * 
 */
public class TrainOutstar extends BasicTraining implements LearningRate {

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The network being trained.
	 */
	private final CPN network;

	/**
	 * The training data. Supervised training, so both input and ideal must be
	 * provided.
	 */
	private final MLDataSet training;

	/**
	 * If the weights have not been initialized, then they must be initialized
	 * before training begins. This will be done on the first iteration.
	 */
	private boolean mustInit = true;

	/**
	 * Construct the outstar trainer.
	 * 
	 * @param theNetwork
	 *            The network to train.
	 * @param theTraining
	 *            The training data, must provide ideal outputs.
	 * @param theLearningRate
	 *            The learning rate.
	 */
	public TrainOutstar(final CPN theNetwork, final MLDataSet theTraining,
			final double theLearningRate) {
		super(TrainingImplementationType.Iterative);
		this.network = theNetwork;
		this.training = theTraining;
		this.learningRate = theLearningRate;
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
	 * Approximate the weights based on the input values.
	 */
	private void initWeight() {
		for (int i = 0; i < this.network.getOutstarCount(); i++) {
			int j = 0;
			for (final MLDataPair pair : this.training) {
				this.network.getWeightsInstarToOutstar().set(j++, i,
						pair.getIdeal().getData(i));
			}
		}
		this.mustInit = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void iteration() {

		if (this.mustInit) {
			initWeight();
		}

		final ErrorCalculation error = new ErrorCalculation();

		for (final MLDataPair pair : this.training) {
			final MLData out = this.network.computeInstar(pair.getInput());

			final int j = EngineArray.indexOfLargest(out.getData());
			for (int i = 0; i < this.network.getOutstarCount(); i++) {
				final double delta = this.learningRate
						* (pair.getIdeal().getData(i) - this.network
								.getWeightsInstarToOutstar().get(j, i));
				this.network.getWeightsInstarToOutstar().add(j, i, delta);
			}

			final MLData out2 = this.network.computeOutstar(out);
			error.updateError(out2.getData(), pair.getIdeal().getData(), pair.getSignificance());
		}

		setError(error.calculate());
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
	public final void resume(final TrainingContinuation state) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

}
