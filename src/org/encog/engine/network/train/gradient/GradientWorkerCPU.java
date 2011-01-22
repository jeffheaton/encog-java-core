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
package org.encog.engine.network.train.gradient;

import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.train.prop.TrainFlatNetworkProp;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;

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
	 * The deltas for each layer.
	 */
	private final double[] layerDelta;

	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;

	/**
	 * The feed counts, per layer.
	 */
	private final int[] layerFeedCounts;

	/**
	 * The layer indexes.
	 */
	private final int[] layerIndex;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;

	/**
	 * The output from each layer.
	 */
	private final double[] layerOutput;

	/**
	 * The gradients.
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
	private final EngineDataSet training;

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
	private final TrainFlatNetworkProp owner;

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
			final TrainFlatNetworkProp owner,
			final EngineDataSet training, final int low, final int high) {
		this.network = network;
		this.training = training;
		this.low = low;
		this.high = high;
		this.owner = owner;

		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();
		this.layerFeedCounts = network.getLayerFeedCounts();

		this.pair = BasicEngineData.createPair(network.getInputCount(), network
				.getOutputCount());
	}

	/**
	 * @return The network training.
	 */
	@Override
	public FlatNetwork getNetwork() {
		return this.network;
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

			this.layerDelta[i] = this.network.getActivationFunctions()[0]
					.derivativeFunction(this.actual[i])
					* (ideal[i] - this.actual[i]);
		}

		for (int i = this.network.getBeginTraining(); i < this.network
				.getEndTraining(); i++) {
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

		final int index = this.weightIndex[currentLevel];
		final ActivationFunction activation = this.network
				.getActivationFunctions()[currentLevel + 1];

		// handle weights
		int yi = fromLayerIndex;
		for (int y = 0; y < fromLayerSize; y++) {
			final double output = this.layerOutput[yi];
			double sum = 0;
			int xi = toLayerIndex;
			int wi = index + y;
			for (int x = 0; x < toLayerSize; x++) {
				this.gradients[wi] += output * this.layerDelta[xi];
				sum += this.weights[wi] * this.layerDelta[xi];
				wi += fromLayerSize;
				xi++;
			}

			this.layerDelta[yi] = sum
					* activation.derivativeFunction(this.layerOutput[yi]);
			yi++;
		}
	}

	/**
	 * Perform the gradient calculation for the specified index range.
	 */
	public void run() {
		try {
			this.errorCalculation.reset();
			for (int i = this.low; i <= this.high; i++) {
				this.training.getRecord(i, this.pair);
				process(this.pair.getInputArray(), this.pair.getIdealArray());
			}
			final double error = this.errorCalculation.calculate();
			this.owner.report(this.gradients, error, null);
			EngineArray.fill(this.gradients, 0);
		} catch (final Throwable ex) {
			this.owner.report(null, 0, ex);
		}
	}

}
