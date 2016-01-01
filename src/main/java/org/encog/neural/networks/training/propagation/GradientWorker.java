/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation;

import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.neural.error.ErrorFunction;
import org.encog.neural.flat.FlatNetwork;
import org.encog.util.EngineArray;
import org.encog.util.concurrency.EngineTask;

/**
 * Worker class for the mulithreaded training of flat networks.
 */
public class GradientWorker implements EngineTask {

	/**
	 * Used to generate randomness for dropout
	 */
	protected Random dropoutRandomSource = new Random();
	
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
	 * The sums.
	 */
	private final double[] layerSums;

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
	private final MLDataPair pair;

	/**
	 * The training data.
	 */
	private final MLDataSet training;

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
	private final GradientWorkerOwner owner;
	
	/**
	 * Derivative add constant.  Used to combat flat spot.
	 */
	private double[] flatSpot;
	
	/**
	 * The error function to use.
	 */
	private final ErrorFunction errorFunction;

	private double[] layerDropoutRates;

	/**
	 * Construct a gradient worker.
	 * 
	 * @param theNetwork
	 *            The network to train.
	 * @param theOwner
	 *            The owner that is doing the training.
	 * @param theTraining
	 *            The training data.
	 * @param theLow
	 *            The low index to use in the training data.
	 * @param theHigh
	 *            The high index to use in the training data.
	 */
	public GradientWorker(final FlatNetwork theNetwork,
			final GradientWorkerOwner theOwner,
			final MLDataSet theTraining, final int theLow, 
			final int theHigh, final double[] flatSpot, 
			ErrorFunction ef) {
		this.network = theNetwork;
		this.training = theTraining;
		this.low = theLow;
		this.high = theHigh;
		this.owner = theOwner;
		this.flatSpot = flatSpot;
		this.errorFunction = ef;

		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.layerDropoutRates = network.getLayerDropoutRates();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();
		this.layerSums = network.getLayerSums();
		this.layerFeedCounts = network.getLayerFeedCounts();

		this.pair = BasicMLDataPair.createPair(network.getInputCount(), network
				.getOutputCount());
	}

	/**
	 * @return The network being processed.
	 */
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
	 * @param pair the training data information
	 */
	public void process(final MLDataPair pair) {
		this.network.compute(pair.getInputArray(), this.actual);

		this.errorCalculation.updateError(this.actual, pair.getIdealArray(), pair.getSignificance());
		
		// Calculate error for the output layer.
		this.errorFunction.calculateError(
				this.network.getActivationFunctions()[0], this.layerSums,this.layerOutput,
				pair.getIdeal().getData(), this.actual, this.layerDelta, this.flatSpot[0], 
				pair.getSignificance());
		
		// Apply regularization, if requested.
		if( this.owner.getL1()>Encog.DEFAULT_DOUBLE_EQUAL 
				|| this.owner.getL1()>Encog.DEFAULT_DOUBLE_EQUAL  ) {
			double[] lp = new double[2];
			calculateRegularizationPenalty(lp);
			for(int i=0;i<this.actual.length;i++) {
				double p = (lp[0]*this.owner.getL1()) + (lp[1]*this.owner.getL2());
				this.layerDelta[i]+=p;
			}
		}
		
		// Propagate backwards (chain rule from calculus).
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
		double dropoutRate = 0;
		if(this.layerDropoutRates.length > currentLevel && this.layerDropoutRates[currentLevel] != 0) {
			dropoutRate = this.layerDropoutRates[currentLevel];
		}

		final int index = this.weightIndex[currentLevel];
		final ActivationFunction activation = this.network
				.getActivationFunctions()[currentLevel];
		final double currentFlatSpot = this.flatSpot[currentLevel + 1];

		// handle weights
		// array references are made method local to avoid one indirection
		final double[] layerDelta = this.layerDelta;
		final double[] weights = this.weights;
		final double[] gradients = this.gradients;
		final double[] layerOutput = this.layerOutput;
		final double[] layerSums = this.layerSums;
		int yi = fromLayerIndex;
		for (int y = 0; y < fromLayerSize; y++) {
			final double output = layerOutput[yi];
			double sum = 0;

			int wi = index + y;
			final int loopEnd = toLayerIndex+toLayerSize;
			if(dropoutRate == 0 || dropoutRandomSource.nextDouble() > dropoutRate)
			{
				for (int xi = toLayerIndex; xi < loopEnd; xi++, wi += fromLayerSize) {
					gradients[wi] += output * layerDelta[xi];
					sum += weights[wi] * layerDelta[xi];
				}
				layerDelta[yi] = sum
						* (activation.derivativeFunction(layerSums[yi], layerOutput[yi])+currentFlatSpot);
			} else {
				layerDelta[yi] = 0;
			}
			yi++;
		}
	}

	/**
	 * Perform the gradient calculation for the specified index range.
	 */
	public final void run() {
		try {
			this.errorCalculation.reset();
			for (int i = this.low; i <= this.high; i++) {
				this.training.getRecord(i, this.pair);
				process(pair);
			}
			final double error = this.errorCalculation.calculate();
			this.owner.report(this.gradients, error, null);
			EngineArray.fill(this.gradients, 0);
		} catch (final Throwable ex) {
			this.owner.report(null, 0, ex);
		}
	}
	
	public final void run(int index) {
		this.training.getRecord(index, this.pair);
		process(pair);
		this.owner.report(this.gradients, 0, null);
		EngineArray.fill(this.gradients, 0);
	}

	public ErrorCalculation getErrorCalculation() {
		return errorCalculation;
	}

	/**
	 * @return the gradients
	 */
	public double[] getGradients() {
		return gradients;
	}
	
	public void calculateRegularizationPenalty(double[] l) {
		for (int i = 0; i < network.getLayerCounts().length - 1; i++) {
			layerRegularizationPenalty(i, l);
		}
	}
	
	public void layerRegularizationPenalty(final int fromLayer, final double[] l) {
		final int fromCount = network.getLayerTotalNeuronCount(fromLayer);
		final int toCount = network.getLayerNeuronCount(fromLayer + 1);

		for (int fromNeuron = 0; fromNeuron < fromCount; fromNeuron++) {
			for (int toNeuron = 0; toNeuron < toCount; toNeuron++) {
				double w = this.network.getWeight(fromLayer, fromNeuron, toNeuron);
				l[0]+=Math.abs(w);
				l[1]+=w*w;
			}
		}
	}
}
