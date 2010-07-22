/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.cpn;

import org.encog.engine.util.BoundMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
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
