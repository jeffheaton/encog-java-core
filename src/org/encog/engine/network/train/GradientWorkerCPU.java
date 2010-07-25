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

package org.encog.engine.network.train;

import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.data.EngineData;
import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;
import org.encog.engine.util.Stopwatch;


/**
 * Worker class for the mulithreaded training of flat networks.
 */
public class GradientWorkerCPU implements FlatGradientWorker {

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The error calculation method.
	 */
	private final ErrorCalculation errorCalculation = new ErrorCalculation();

	/**
	 * The actual values from the neural network.
	 */
	private final double[] actual;

	/**
	 * The deltas for each layer
	 */
	private final double[] layerDelta;

	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;

	private int[] layerFeedCounts;

	/**
	 * The layer indexes
	 */
	private final int[] layerIndex;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;

	/**
	 * The output from each layer
	 */
	private final double[] layerOutput;

	/**
	 * The gradients
	 */
	private final double[] gradients;

	/**
	 * The weights and thresholds.
	 */
	private final double[] weights;

	/**
	 * The pair to use for training.
	 */
	private final EngineData pair;

	/**
	 * The training data.
	 */
	private final EngineIndexableSet training;

	/**
	 * The high end of the training data.
	 */
	private final int low;

	/**
	 * The low end of the training.
	 */
	private final int high;

	/**
	 * The owner.
	 */
	private final TrainFlatNetworkMulti owner;

	/**
	 * The elapsed time.
	 */
	private long elapsedTime;

	/**
	 * The stopwatch, to evaluate performance.
	 */
	private final Stopwatch stopwatch;

	/**
	 * Construct a gradient worker.
	 * 
	 * @param network
	 *            The network to train.
	 * @param owner
	 *            The owner that is doing the training.
	 * @param training
	 *            The training data.
	 * @param low
	 *            The low index to use in the training data.
	 * @param high
	 *            The high index to use in the training data.
	 */
	public GradientWorkerCPU(final FlatNetwork network,
			final TrainFlatNetworkMulti owner, final EngineIndexableSet training,
			final int low, final int high) {
		this.network = network;
		this.training = training;
		this.low = low;
		this.high = high;
		this.owner = owner;

		this.stopwatch = new Stopwatch();

		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();
		this.layerFeedCounts = network.getLayerFeedCounts();

		this.pair = BasicEngineData.createPair(network.getInputCount(),
				network.getOutputCount());
	}

	/**
	 * @return Elapsed time for the last iteration.
	 */
	public long getElapsedTime() {
		return this.elapsedTime;
	}

	/**
	 * @return The weights for this network.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/**
	 * Process one training set element.
	 * 
	 * @param input
	 *            The network input.
	 * @param ideal
	 *            The ideal values.
	 */
	private void process(final double[] input, final double[] ideal) {
		this.network.compute(input, this.actual);

		this.errorCalculation.updateError(this.actual, ideal);

		for (int i = 0; i < this.actual.length; i++) {

			this.layerDelta[i] = ActivationFunctions.calculateActivationDerivative(
					this.network.getActivationType()[0], this.actual[i],this.network.getSlope()[0])
					* (ideal[i] - this.actual[i]);
		}

		for (int i = 0; i < this.layerCounts.length - 1; i++) {
			processLevel(i);
		}
	}

	/**
	 * Process one level.
	 * 
	 * @param currentLevel
	 *            The level.
	 */
	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = this.layerIndex[currentLevel + 1];
		final int toLayerIndex = this.layerIndex[currentLevel];
		final int fromLayerSize = this.layerCounts[currentLevel + 1];
		final int toLayerSize = this.layerFeedCounts[currentLevel];

		// clear the to-deltas
		for (int i = 0; i < fromLayerSize; i++) {
			this.layerDelta[fromLayerIndex + i] = 0;
		}

		int index = this.weightIndex[currentLevel];

		// handle weights
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
			this.layerDelta[fromLayerIndex + i] *= ActivationFunctions
					.calculateActivationDerivative(this.network
							.getActivationType()[currentLevel + 1],
							this.layerOutput[fromLayerIndex + i],
							this.network.getSlope()[currentLevel + 1]);
		}
	}

	/**
	 * Perform the gradient calculation for the specified index range.
	 */
	public void run() {
		try {
			this.stopwatch.reset();
			this.stopwatch.start();
			this.errorCalculation.reset();
			for (int i = this.low; i <= this.high; i++) {
				this.training.getRecord(i, this.pair);
				process(this.pair.getInput(), this.pair.getIdeal());
			}
			final double error = this.errorCalculation.calculate();
			this.owner.report(this.gradients, error, null);
			EngineArray.fill(this.gradients, 0);
			this.stopwatch.stop();
			this.elapsedTime = this.stopwatch.getElapsedTicks();
		} catch (Throwable ex) {
			this.owner.report(null, 0, ex);
		}
	}

}
