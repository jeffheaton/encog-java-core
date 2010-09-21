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

package org.encog.neural.networks.training.cpn;

import org.encog.engine.util.BoundMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;

/**
 * Used for Instar training of a CPN neural network. A CPN network is a hybrid
 * supervised/unsupervised network. The Instar training handles the unsupervised
 * portion of the training.
 * 
 */
public class TrainInstar extends BasicTraining implements LearningRate {

	/**
	 * The network being trained.
	 */
	private final BasicNetwork network;

	/**
	 * The training data. This is unsupervised training, so only the input
	 * portion of the training data will be used.
	 */
	private final NeuralDataSet training;

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * If the weights have not been initialized, then they must be initialized
	 * before training begins. This will be done on the first iteration.
	 */
	private boolean mustInit = true;

	/**
	 * Used to find the parts of the CPN network.
	 */
	private final FindCPN parts;

	/**
	 * Construct the instar training object.
	 * 
	 * @param network
	 *            The network to be trained.
	 * @param training
	 *            The training data.
	 * @param learningRate
	 *            The learning rate.
	 */
	public TrainInstar(final BasicNetwork network,
			final NeuralDataSet training, final double learningRate) {
		this.network = network;
		this.training = training;
		this.learningRate = learningRate;
		this.parts = new FindCPN(network);
	}

	/**
	 * @return The learning rate.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The network being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Approximate the weights based on the input values.
	 */
	private void initWeights() {
		int i = 0;
		for (final NeuralDataPair pair : this.training) {
			for (int j = 0; j 
			< this.parts.getInputLayer().getNeuronCount(); j++) {
				this.parts.getInstarSynapse().getMatrix().set(j, i,
						pair.getInput().getData(j));
			}
			i++;
		}

		this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
		this.mustInit = false;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.mustInit) {
			initWeights();
		}

		double worstDistance = Double.NEGATIVE_INFINITY;

		for (final NeuralDataPair pair : this.training) {
			final NeuralData out = this.parts.getInstarSynapse().compute(
					pair.getInput());

			// determine winner
			final int winner = this.parts.winner(out);

			// calculate the distance
			double distance = 0;
			for (int i = 0; i < pair.getInput().size(); i++) {
				final double diff = pair.getInput().getData(i)
						- this.parts.getInstarSynapse().getMatrix().get(i,
								winner);
				distance += diff * diff;
			}
			distance = BoundMath.sqrt(distance);

			if (distance > worstDistance) {
				worstDistance = distance;
			}

			// train
			for (int j = 0; j < this.parts.getInstarSynapse()
					.getFromNeuronCount(); j++) {
				final double delta = this.learningRate
						* (pair.getInput().getData(j) - this.parts
								.getInstarSynapse().getMatrix().get(j, winner));

				this.parts.getInstarSynapse().getMatrix().add(j, winner, delta);

			}
		}

		this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
		setError(worstDistance);
	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The new learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

}
