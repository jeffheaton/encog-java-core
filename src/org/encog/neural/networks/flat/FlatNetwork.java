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

import org.encog.mathutil.BoundMath;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;

/**
 * Implements a flat (vector based) neural network in Encog. This is meant to be
 * a very highly efficient feedforward neural network. It uses a minimum of
 * objects and is designed with one principal in mind-- SPEED. Readability, code
 * reuse, object oriented programming are all secondary in consideration.
 * 
 * Currently, the flat networks only support feedforward networks with either a
 * sigmoid or tanh activation function.  Specifically, a flat network must:
 * 
 * 1. Feedforward only, no self-connections or recurrent links
 * 2. Sigmoid or TANH activation only
 * 3. All layers the same activation function
 * 4. Must have threshold values
 * 
 * Vector based neural networks are also very good for GPU processing. The flat
 * network classes will make use of the GPU if you have enabled GPU processing.
 * See the Encog class for more info.
 */
public class FlatNetwork {

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
	 * Are we using the TANH function? If not, then the sigmoid.
	 */
	private final boolean tanh;

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
	 * Construct a flat network.
	 * 
	 * @param network
	 *            The network to construct the flat network from.
	 */
	public FlatNetwork(final BasicNetwork network) {
		ValidateForFlat.validateNetwork(network);

		final Layer input = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer output = network.getLayer(BasicNetwork.TAG_OUTPUT);

		inputCount = input.getNeuronCount();
		outputCount = output.getNeuronCount();

		final int layerCount = network.getStructure().getLayers().size();

		layerCounts = new int[layerCount];
		weightIndex = new int[layerCount];
		layerIndex = new int[layerCount];

		int index = 0;
		int neuronCount = 0;

		for (final Layer layer : network.getStructure().getLayers()) {
			layerCounts[index] = layer.getNeuronCount();
			neuronCount += layer.getNeuronCount();

			if (index == 0) {
				weightIndex[index] = 0;
				layerIndex[index] = 0;
			} else {
				weightIndex[index] = weightIndex[index - 1]
						+ (layerCounts[index - 1] + (layerCounts[index] * layerCounts[index - 1]));
				layerIndex[index] = layerIndex[index - 1]
						+ layerCounts[index - 1];
			}

			index++;
		}

		weights = NetworkCODEC.networkToArray(network);
		layerOutput = new double[neuronCount];

		if (input.getActivationFunction() instanceof ActivationSigmoid) {
			tanh = false;
		} else {
			tanh = true;
		}
	}
	
	/**
	 * Generate a regular Encog neural network from this flat network.
	 * @return A regular Encog neural network.
	 */
	public BasicNetwork unflatten()
	{
		ActivationFunction activation;
		BasicNetwork result = new BasicNetwork();
		
		if( this.tanh )
			activation = new ActivationTANH();
		else
			activation = new ActivationSigmoid();
		
		for(int i=this.layerCounts.length-1;i>=0;i--)
		{
			Layer layer = new BasicLayer(activation,true,this.layerCounts[i]);
			result.addLayer(layer);
		}
		result.getStructure().finalizeStructure();
		
		NetworkCODEC.arrayToNetwork(this.weights, result);
		
		return result;
	}

	/**
	 * Calculate the output for the given input.
	 * @param input The input.
	 * @param output Output will be placed here.
	 */
	public void calculate(final double[] input, final double[] output) {
		final int sourceIndex = layerOutput.length - inputCount;

		System.arraycopy(input, 0, layerOutput, sourceIndex, inputCount);

		for (int i = layerIndex.length - 1; i > 0; i--) {
			calculateLayer(i);
		}

		System.arraycopy(layerOutput, 0, output, 0, outputCount);
	}

	/**
	 * Calculate a layer.
	 * @param currentLayer The layer to calculate.
	 */
	private void calculateLayer(final int currentLayer) {

		final int inputIndex = layerIndex[currentLayer];
		final int outputIndex = layerIndex[currentLayer - 1];
		final int inputSize = layerCounts[currentLayer];
		final int outputSize = layerCounts[currentLayer - 1];

		int index = weightIndex[currentLayer - 1];

		// threshold values
		for (int i = 0; i < outputSize; i++) {
			layerOutput[i + outputIndex] = weights[index++];
		}

		// weight values
		for (int x = 0; x < outputSize; x++) {
			double sum = 0;
			for (int y = 0; y < inputSize; y++) {
				sum += weights[index++] * layerOutput[inputIndex + y];
			}
			layerOutput[outputIndex + x] += sum;

			if (tanh) {
				layerOutput[outputIndex + x] = tanh(layerOutput[outputIndex + x]);
			} else {
				layerOutput[outputIndex + x] = sigmoid(layerOutput[outputIndex
						+ x]);
			}
		}
	}

	/**
	 * @return The number of input neurons.
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @return The number of neurons in each layer.
	 */
	public int[] getLayerCounts() {
		return layerCounts;
	}

	/**
	 * @return Indexes into the weights for the start of each layer.
	 */
	public int[] getLayerIndex() {
		return layerIndex;
	}

	/**
	 * @return The output for each layer.
	 */
	public double[] getLayerOutput() {
		return layerOutput;
	}

	/**
	 * @return The number of output neurons.
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * @return The index of each layer in the weight and threshold array.
	 */
	public int[] getWeightIndex() {
		return weightIndex;
	}

	/**
	 * @return The weight and threshold array.
	 */
	public double[] getWeights() {
		return weights;
	}

	/**
	 * @return True if this is a TANH activation function.
	 */
	public boolean isTanh() {
		return tanh;
	}

	/**
	 * Implements a sigmoid activation function.
	 * 
	 * @param d
	 *            The value to take the sigmoid of.
	 * @return The result.
	 */
	private double sigmoid(final double d) {
		return 1.0 / (1 + BoundMath.exp(-1.0 * d));
	}

	/**
	 * Implements a hyperbolic tangent function.
	 * 
	 * @param d
	 *            The value to take the htan of.
	 * @return The htan of the specified value.
	 */
	private double tanh(final double d) {
		return -1 + (2 / (1 + BoundMath.exp(-2 * d)));
	}
	
	public FlatNetwork clone()
	{
		BasicNetwork temp = this.unflatten();
		return new FlatNetwork(temp);
	}

}
