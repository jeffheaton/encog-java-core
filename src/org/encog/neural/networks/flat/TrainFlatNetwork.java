/*
 * Encog(tm) Core v2.4
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
package org.encog.neural.networks.flat;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Train a flat network single-threaded. This class is left in mainly for
 * testing purposes. Usually, you will use TrainFlatNetworkMulti.
 * 
 * @author jheaton
 * 
 */
public class TrainFlatNetwork {

	/**
	 * The error calculation method.
	 */
	private final ErrorCalculation errorCalculation = new ErrorCalculation();

	/**
	 * The gradients.
	 */
	private final double[] gradients;

	/**
	 * The last gradients, from the last training iteration.
	 */
	private final double[] lastGradient;

	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;

	/**
	 * The deltas for each layer.
	 */
	private final double[] layerDelta;

	/**
	 * The layer indexes.
	 */
	private final int[] layerIndex;

	/**
	 * The output from each layer.
	 */
	private final double[] layerOutput;

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The training data.
	 */
	private final NeuralDataSet training;

	/**
	 * The update values, for the weights and thresholds.
	 */
	private final double[] updateValues;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;

	/**
	 * The weights and thresholds.
	 */
	private final double[] weights;

	/**
	 * Construct a training class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 */
	public TrainFlatNetwork(final FlatNetwork network,
			final NeuralDataSet training) {
		this.training = training;
		this.network = network;

		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.updateValues = new double[network.getWeights().length];
		this.lastGradient = new double[network.getWeights().length];

		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();

		for (int i = 0; i < this.updateValues.length; i++) {
			this.updateValues[i] = ResilientPropagation.DEFAULT_INITIAL_UPDATE;
		}
	}

	/**
	 * @return The overall error.
	 */
	public double getError() {
		return this.errorCalculation.calculate();
	}

	/**
	 * Perform a training iteration.
	 */
	public void iteration() {
		final double[] actual = new double[this.network.getOutputCount()];
		this.errorCalculation.reset();

		for (final NeuralDataPair pair : this.training) {
			final double[] input = pair.getInput().getData();
			final double[] ideal = pair.getIdeal().getData();

			this.network.compute(input, actual);

			this.errorCalculation.updateError(actual, ideal);

			for (int i = 0; i < actual.length; i++) {
				this.layerDelta[i] = FlatNetwork.calculateActivationDerivative(
						this.network.getActivationType()[0], actual[i])
						* (ideal[i] - actual[i]);
			}

			for (int i = 0; i < this.layerCounts.length - 1; i++) {
				processLevel(i);
			}
		}

		learn();
	}

	/**
	 * Update the neural network weights.
	 */
	private void learn() {
		for (int i = 0; i < this.gradients.length; i++) {
			this.weights[i] += updateWeight(this.gradients, i);
			this.gradients[i] = 0;
		}
	}

	/**
	 * Process a level.
	 * 
	 * @param currentLevel
	 *            The level to process.
	 */
	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = this.layerIndex[currentLevel + 1];
		final int toLayerIndex = this.layerIndex[currentLevel];
		final int fromLayerSize = this.layerCounts[currentLevel + 1];
		final int toLayerSize = this.layerCounts[currentLevel];

		// clear the to-deltas
		for (int i = 0; i < fromLayerSize; i++) {
			this.layerDelta[fromLayerIndex + i] = 0;
		}

		int index = this.weightIndex[currentLevel] + toLayerSize;

		for (int x = 0; x < toLayerSize; x++) {
			for (int y = 0; y < fromLayerSize; y++) {
				final double value = this.layerOutput[fromLayerIndex + y]
						* this.layerDelta[toLayerIndex + x];
				this.gradients[index] += value;
				this.layerDelta[fromLayerIndex + y] += this.weights[index]
						* this.layerDelta[toLayerIndex + x];
				index++;
			}
		}

		for (int i = 0; i < fromLayerSize; i++) {
			this.layerDelta[fromLayerIndex + i] *= FlatNetwork
					.calculateActivationDerivative(this.network
							.getActivationType()[currentLevel],
							this.layerOutput[fromLayerIndex + i]);
		}
	}

	/**
	 * Determine the sign of the value.
	 * 
	 * @param value
	 *            The value to check.
	 * @return -1 if less than zero, 1 if greater, or 0 if zero.
	 */
	private int sign(final double value) {
		if (Math.abs(value) < ResilientPropagation.DEFAULT_ZERO_TOLERANCE) {
			return 0;
		} else if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Determine the amount to change a weight by.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param index
	 *            The weight to adjust.
	 * @return The amount to change this weight by.
	 */
	private double updateWeight(final double[] gradients, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = sign(this.gradients[index]
				* this.lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = this.updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, ResilientPropagation.DEFAULT_MAX_STEP);
			weightChange = sign(this.gradients[index]) * delta;
			this.updateValues[index] = delta;
			this.lastGradient[index] = this.gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = this.updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			this.updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			this.lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = this.lastGradient[index];
			weightChange = sign(this.gradients[index]) * delta;
			this.lastGradient[index] = this.gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}

}
