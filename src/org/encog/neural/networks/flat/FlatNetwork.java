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

package org.encog.neural.networks.flat;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.BoundMath;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.util.EncogArray;

/**
 * Implements a flat (vector based) neural network in Encog. This is meant to be
 * a very highly efficient feedforward neural network. It uses a minimum of
 * objects and is designed with one principal in mind-- SPEED. Readability, code
 * reuse, object oriented programming are all secondary in consideration.
 * 
 * Currently, the flat networks only support feedforward networks with either a
 * sigmoid or tanh activation function. Specifically, a flat network must:
 * 
 * 1. Feedforward only, no self-connections or recurrent links
 * 
 * 2. Sigmoid or TANH activation only
 * 
 * 3. All layers the same activation function
 * 
 * 4. Must have bias values
 * 
 * Vector based neural networks are also very good for GPU processing. The flat
 * network classes will make use of the GPU if you have enabled GPU processing.
 * See the Encog class for more info.
 */
public class FlatNetwork {

	/**
	 * A linear activation function.
	 */
	public static final int ACTIVATION_LINEAR = 0;

	/**
	 * A TANH activation function.
	 */
	public static final int ACTIVATION_TANH = 1;

	/**
	 * A sigmoid activation function.
	 */
	public static final int ACTIVATION_SIGMOID = 2;


	/**
	 * Calculate an activation.
	 * @param type The type of activation.
	 * @param x The value to calculate the activation for.
	 * @return The resulting value.
	 */
	public static double calculateActivation(final int type, final double x) {
		switch (type) {
		case FlatNetwork.ACTIVATION_LINEAR:
			return x;
		case FlatNetwork.ACTIVATION_TANH:
			return -1.0 + (2.0 / (1.0 + BoundMath.exp(-2.0 * x)));
		case FlatNetwork.ACTIVATION_SIGMOID:
			return 1.0 / (1.0 + BoundMath.exp(-1.0 * x));
		default:
			throw new NeuralNetworkError("Unknown activation type: " + type);
		}
	}

	/**
	 * Calculate the derivative of the activation.
	 * @param type The type of activation.
	 * @param x The value to calculate for.
	 * @return The result.
	 */
	public static double calculateActivationDerivative(final int type,
			final double x) {
		switch (type) {
		case FlatNetwork.ACTIVATION_LINEAR:
			return 1;
		case FlatNetwork.ACTIVATION_TANH:
			return (1.0 + x) * (1.0 - x);
		case FlatNetwork.ACTIVATION_SIGMOID:
			return x * (1.0 - x);
		default:
			throw new NeuralNetworkError("Unknown activation type: " + type);
		}
	}

	/**
	 * The number of input neurons in this network.
	 */
	private final int inputCount;

	/**
	 * The number of neurons in each of the layers.
	 */
	private final int[] layerCounts;

	/**
	 * An index to where each layer begins (based on the number of neurons in
	 * each layer).
	 */
	private final int[] layerIndex;

	/**
	 * The outputs from each of the neurons.
	 */
	private final double[] layerOutput;

	/**
	 * The number of output neurons in this network.
	 */
	private final int outputCount;

	/**
	 * The index to where the weights and thresholds are stored at for a given
	 * layer.
	 */
	private final int[] weightIndex;

	/**
	 * The weights and thresholds for a neural network.
	 */
	private final double[] weights;

	/**
	 * The activation types.
	 */
	private final int[] activationType;

	/**
	 * Bias values on the input layer serve no value. But some networks are
	 * constructed in this way, because they use the default BasicLayer
	 * constructor. We need to remember that there was an input bias, so that
	 * the network is unflattened this way.
	 */
	private boolean hasInputBias;
	
	/**
	 * Construct a flat network.
	 * @param network The network to construct the flat network
	 * from.
	 */
	public FlatNetwork(final BasicNetwork network) {
		ValidateForFlat.validateNetwork(network);

		final Layer input = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer output = network.getLayer(BasicNetwork.TAG_OUTPUT);

		this.inputCount = input.getNeuronCount();
		this.outputCount = output.getNeuronCount();

		this.hasInputBias = input.hasBias();

		final int layerCount = network.getStructure().getLayers().size();

		this.layerCounts = new int[layerCount];
		this.weightIndex = new int[layerCount];
		this.layerIndex = new int[layerCount];
		this.activationType = new int[layerCount];

		int index = 0;
		int neuronCount = 0;

		for (final Layer layer : network.getStructure().getLayers()) {
			this.layerCounts[index] = layer.getNeuronCount();

			if (layer.getActivationFunction() instanceof ActivationLinear) {
				this.activationType[index] = FlatNetwork.ACTIVATION_LINEAR;
			} else if (layer.getActivationFunction() instanceof ActivationTANH) {
				this.activationType[index] = FlatNetwork.ACTIVATION_TANH;
			} else if (layer.getActivationFunction() instanceof ActivationSigmoid) {
				this.activationType[index] = FlatNetwork.ACTIVATION_SIGMOID;
			}

			neuronCount += layer.getNeuronCount();

			if (index == 0) {
				this.weightIndex[index] = 0;
				this.layerIndex[index] = 0;
			} else {
				this.weightIndex[index] = this.weightIndex[index - 1]
						+ (this.layerCounts[index - 1] + (this.layerCounts[index] * this.layerCounts[index - 1]));
				this.layerIndex[index] = this.layerIndex[index - 1]
						+ this.layerCounts[index - 1];
			}

			index++;
		}

		this.weights = NetworkCODEC.networkToArray(network);
		this.layerOutput = new double[neuronCount];

	}

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * @param data The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final NeuralDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		final double[] actual = new double[this.outputCount];

		for (final NeuralDataPair pair : data) {
			compute(pair.getInput().getData(), actual);
			errorCalculation.updateError(actual, pair.getIdeal().getData());
		}
		return errorCalculation.calculate();
	}

	
	/**
	 * Clone the network.
	 * @return A clone of the network.
	 */
	public FlatNetwork clone() {
		final BasicNetwork temp = unflatten();
		return new FlatNetwork(temp);
	}


	/**
	 * Calculate the output for the given input.
	 * @param input The input.
	 * @param output Output will be placed here.
	 */
	public void compute(final double[] input, final double[] output) {
		final int sourceIndex = this.layerOutput.length - this.inputCount;

		EncogArray.arrayCopy(input, 0, this.layerOutput, sourceIndex,
				this.inputCount);

		for (int i = this.layerIndex.length - 1; i > 0; i--) {
			computeLayer(i);
		}

		EncogArray.arrayCopy(this.layerOutput, 0, output, 0, this.outputCount);
	}

	/**
	 * Calculate a layer.
	 * @param currentLayer The layer to calculate.
	 */
	private void computeLayer(final int currentLayer) {

		final int inputIndex = this.layerIndex[currentLayer];
		final int outputIndex = this.layerIndex[currentLayer - 1];
		final int inputSize = this.layerCounts[currentLayer];
		final int outputSize = this.layerCounts[currentLayer - 1];

		int index = this.weightIndex[currentLayer - 1];

		// threshold values
		for (int i = 0; i < outputSize; i++) {
			this.layerOutput[i + outputIndex] = this.weights[index++];
		}

		// weight values
		for (int x = 0; x < outputSize; x++) {
			double sum = 0;
			for (int y = 0; y < inputSize; y++) {
				sum += this.weights[index++] * this.layerOutput[inputIndex + y];
			}
			this.layerOutput[outputIndex + x] += sum;

			this.layerOutput[outputIndex + x] = FlatNetwork
					.calculateActivation(this.activationType[0],
							this.layerOutput[outputIndex + x]);
		}
	}

	/**
	 * @return The activation types for each of the layers.
	 */
	public int[] getActivationType() {
		return this.activationType;
	}

	/**
	 * @return The number of input neurons.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The number of neurons in each layer.
	 */
	public int[] getLayerCounts() {
		return this.layerCounts;
	}

	/**
	 * @return Indexes into the weights for the start of each layer.
	 */
	public int[] getLayerIndex() {
		return this.layerIndex;
	}

	/**
	 * @return The output for each layer.
	 */
	public double[] getLayerOutput() {
		return this.layerOutput;
	}

	/**
	 * @return The neuron count.
	 */
	public int getNeuronCount() {
		int result = 0;
		for (final int element : this.layerCounts) {
			result += element;
		}
		return result;
	}

	/**
	 * @return The number of output neurons.
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return The index of each layer in the weight and threshold array.
	 */
	public int[] getWeightIndex() {
		return this.weightIndex;
	}

	/**
	 * @return The index of each layer in the weight and threshold array.
	 */
	public double[] getWeights() {
		return this.weights;
	}
	
	/**
	 * Neural networks with only one type of activation function offer certain
	 * optimization options. This method determines if only a single
	 * activation
	 * function is used.
	 * @return The number of the single activation function, or -1 if there
	 * are no activation functions or more than one type of activation
	 * function.
	 */
	public int hasSameActivationFunction() {
		final List<Integer> map = new ArrayList<Integer>();

		for (final int activation : this.activationType) {
			if (!map.contains(activation)) {
				map.add(activation);
			}
		}

		if (map.size() != 1) {
			return -1;
		} else {
			return map.get(0);
		}
	}

	/**
	 * Generate a regular Encog neural network from this flat network.
	 * @return A regular Encog neural network.
	 */
	public BasicNetwork unflatten() {
		final BasicNetwork result = new BasicNetwork();
		boolean useBias = this.hasInputBias;

		for (int i = this.layerCounts.length - 1; i >= 0; i--) {
			ActivationFunction activation;

			switch (this.activationType[i]) {
			case FlatNetwork.ACTIVATION_LINEAR:
				activation = new ActivationLinear();
				break;
			case FlatNetwork.ACTIVATION_SIGMOID:
				activation = new ActivationSigmoid();
				break;
			case FlatNetwork.ACTIVATION_TANH:
				activation = new ActivationTANH();
				break;
			default:
				activation = null;
				break;
			}

			final Layer layer = new BasicLayer(activation, useBias,
					this.layerCounts[i]);
			useBias = true;
			result.addLayer(layer);
		}
		result.getStructure().finalizeStructure();

		NetworkCODEC.arrayToNetwork(this.weights, result);

		return result;
	}

}
