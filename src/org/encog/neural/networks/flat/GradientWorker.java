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

import java.util.Arrays;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.concurrency.EncogTask;

/**
 * Worker class for the mulithreaded training of flat networks.
 */
public class GradientWorker implements EncogTask {

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
	private double[] actual;
	
	/**
	 * The deltas for each layer
	 */
	private final double[] layerDelta;
	
	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;
	
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
	
	private NeuralDataPair pair;
	
	private Indexable training;
	private int low;
	private int high;
	private TrainFlatNetworkMulti owner;
	
	/**
	 * Construct a gradient worker.
	 * @param network The network to train.
	 * @param owner The owner that is doing the training.
	 * @param training The training data.
	 * @param low The low index to use in the training data.
	 * @param high The high index to use in the training data.
	 */
	public GradientWorker(FlatNetwork network, TrainFlatNetworkMulti owner, Indexable training, int low, int high )
	{
		this.network = network;
		this.training = training;
		this.low = low;
		this.high = high;
		this.owner = owner;
		
		layerDelta = new double[network.getLayerOutput().length];
		gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		weights = network.getWeights();
		layerIndex = network.getLayerIndex();
		layerCounts = network.getLayerCounts();
		weightIndex = network.getWeightIndex();
		layerOutput = network.getLayerOutput();
		
		this.pair = BasicNeuralDataPair.createPair(network.getInputCount(), network.getOutputCount());
	}
	
	
	/**
	 * Perform the gradient calculation for the specified index range.
	 */
	public void run() {
		this.errorCalculation.reset();
		for(int i = this.low; i<high; i++)
		{
			this.training.getRecord(i, this.pair);
			process(pair.getInput().getData(),pair.getIdeal().getData());
		}
		this.owner.report(this.gradients, this.errorCalculation.calculateRMS());
		Arrays.fill(this.gradients, 0);
	}
	
	/**
	 * Process one training set element.
	 * @param input The network input.
	 * @param ideal The ideal values.
	 */
	private void process(double[] input, double[] ideal)
	{
		network.calculate(input, actual);

		errorCalculation.updateError(actual, ideal);

		for (int i = 0; i < actual.length; i++) {
			if (network.isTanh()) {
				layerDelta[i] = TrainFlatNetwork.derivativeTANH(actual[i])
						* (ideal[i] - actual[i]);
			} else {
				layerDelta[i] = TrainFlatNetwork.derivativeSigmoid(actual[i])
						* (ideal[i] - actual[i]);
			}
		}

		for (int i = 0; i < layerCounts.length - 1; i++) {
			processLevel(i);
		}
	}
	
	/**
	 * Process one level.
	 * @param currentLevel The level.
	 */
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
				layerDelta[fromLayerIndex + i] *= TrainFlatNetwork.derivativeTANH(layerOutput[fromLayerIndex
						+ i]);
			} else {
				layerDelta[fromLayerIndex + i] *= TrainFlatNetwork.derivativeSigmoid(layerOutput[fromLayerIndex
						+ i]);
			}
		}
	}


	/**
	 * @return The weights for this network.
	 */
	public double[] getWeights() {
		return weights;
	}
	
	

}
