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

public class TrainFlatNetwork {

	private final ErrorCalculation errorCalculation = new ErrorCalculation();
	private final double[] gradients;
	private final double[] lastGradient;
	private final int[] layerCounts;
	private final double[] layerDelta;
	private final int[] layerIndex;
	private final double[] layerOutput;
	private final FlatNetwork network;
	private final NeuralDataSet training;
	private final double[] updateValues;
	private final int[] weightIndex;
	private final double[] weights;

	public TrainFlatNetwork(final FlatNetwork network,
			final NeuralDataSet training) {
		this.training = training;
		this.network = network;

		layerDelta = new double[network.getLayerOutput().length];
		gradients = new double[network.getWeights().length];
		updateValues = new double[network.getWeights().length];
		lastGradient = new double[network.getWeights().length];

		weights = network.getWeights();
		layerIndex = network.getLayerIndex();
		layerCounts = network.getLayerCounts();
		weightIndex = network.getWeightIndex();
		layerOutput = network.getLayerOutput();

		for (int i = 0; i < updateValues.length; i++) {
			updateValues[i] = ResilientPropagation.DEFAULT_INITIAL_UPDATE;
		}
	}

	public double derivativeSigmoid(final double d) {
		return d * (1.0 - d);
	}

	public double derivativeTANH(final double d) {
		return ((1 + d) * (1 - d));
	}

	public double getError() {
		return errorCalculation.calculateRMS();
	}

	public void iteration() {
		final double[] actual = new double[network.getOutputCount()];
		errorCalculation.reset();

		for (final NeuralDataPair pair : training) {
			final double[] input = pair.getInput().getData();
			final double[] ideal = pair.getIdeal().getData();

			network.calculate(input, actual);

			errorCalculation.updateError(actual, ideal);

			for (int i = 0; i < actual.length; i++) {
				if (network.isTanh()) {
					layerDelta[i] = derivativeTANH(actual[i])
							* (ideal[i] - actual[i]);
				} else {
					layerDelta[i] = derivativeSigmoid(actual[i])
							* (ideal[i] - actual[i]);
				}
			}

			for (int i = 0; i < layerCounts.length - 1; i++) {
				processLevel(i);
			}
		}

		learn();
	}

	private void learn() {
		for (int i = 0; i < gradients.length; i++) {
			weights[i] += updateWeight(gradients, i);
			gradients[i] = 0;
		}
	}

	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = layerIndex[currentLevel + 1];
		final int toLayerIndex = layerIndex[currentLevel];
		final int fromLayerSize = layerCounts[currentLevel + 1];
		final int toLayerSize = layerCounts[currentLevel];

		// clear the to-deltas
		for (int i = 0; i < fromLayerSize; i++) {
			layerDelta[fromLayerIndex + i] = 0;
		}

		int index = weightIndex[currentLevel] + toLayerSize;

		for (int x = 0; x < toLayerSize; x++) {
			for (int y = 0; y < fromLayerSize; y++) {
				final double value = layerOutput[fromLayerIndex + y]
						* layerDelta[toLayerIndex + x];
				gradients[index] += value;
				layerDelta[fromLayerIndex + y] += weights[index]
						* layerDelta[toLayerIndex + x];
				index++;
			}
		}

		for (int i = 0; i < fromLayerSize; i++) {
			if (network.isTanh()) {
				layerDelta[fromLayerIndex + i] *= derivativeTANH(layerOutput[fromLayerIndex
						+ i]);
			} else {
				layerDelta[fromLayerIndex + i] *= derivativeSigmoid(layerOutput[fromLayerIndex
						+ i]);
			}
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
		final int change = sign(this.gradients[index] * lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, ResilientPropagation.DEFAULT_MAX_STEP);
			weightChange = sign(this.gradients[index]) * delta;
			updateValues[index] = delta;
			lastGradient[index] = this.gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = lastGradient[index];
			weightChange = sign(this.gradients[index]) * delta;
			lastGradient[index] = this.gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}

}
