/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.networks.training.simple;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.util.ErrorCalculation;

/**
 * Train an ADALINE neural network.
 */
public class TrainAdaline extends BasicTraining implements LearningRate {

	/**
	 * The network to train.
	 */
	private final BasicNetwork network;

	/**
	 * The synapse to train.
	 */
	private final Synapse synapse;

	/**
	 * The training data to use.
	 */
	private final NeuralDataSet training;

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
	public TrainAdaline(final BasicNetwork network,
			final NeuralDataSet training, final double learningRate) {
		if (network.getStructure().getLayers().size() > 2) {
			throw new NeuralNetworkError(
					"An ADALINE network only has two layers.");
		}
		this.network = network;

		final Layer input = network.getLayer(BasicNetwork.TAG_INPUT);

		this.synapse = input.getNext().get(0);
		this.training = training;
		this.learningRate = learningRate;
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
	 * Perform a training iteration.
	 */
	public void iteration() {

		final ErrorCalculation errorCalculation = new ErrorCalculation();

		final Layer inputLayer = this.network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = this.network
				.getLayer(BasicNetwork.TAG_OUTPUT);

		for (final NeuralDataPair pair : this.training) {
			// calculate the error
			final NeuralData output = this.network.compute(pair.getInput());

			for (int currentAdaline = 0; currentAdaline < output.size(); 
				currentAdaline++) {
				final double diff = pair.getIdeal().getData(currentAdaline)
						- output.getData(currentAdaline);

				// weights
				for (int i = 0; i < inputLayer.getNeuronCount(); i++) {
					final double input = pair.getInput().getData(i);
					this.synapse.getMatrix().add(i, currentAdaline,
							this.learningRate * diff * input);
				}

				// threshold (bias)
				double t = outputLayer.getThreshold(currentAdaline);
				t += this.learningRate * diff;
				outputLayer.setThreshold(currentAdaline, t);
			}

			errorCalculation.updateError(output, pair.getIdeal());
		}

		// set the global error
		setError(errorCalculation.calculateRMS());
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
