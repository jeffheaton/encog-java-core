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

package org.encog.engine.network.flat;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngineError;
import org.encog.engine.EngineNeuralNetwork;
import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.util.BoundMath;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;


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
public class FlatNetwork implements EngineNeuralNetwork {

	/**
	 * The number of input neurons in this network.
	 */
	private int inputCount;

	/**
	 * The number of neurons in each of the layers.
	 */
	private int[] layerCounts;
	
	private int[] layerFeedCounts;

	/**
	 * An index to where each layer begins (based on the number of neurons in
	 * each layer).
	 */
	private int[] layerIndex;

	/**
	 * The outputs from each of the neurons.
	 */
	private double[] layerOutput;

	/**
	 * The number of output neurons in this network.
	 */
	private int outputCount;

	/**
	 * The index to where the weights that are stored at for a given
	 * layer.
	 */
	private int[] weightIndex;

	/**
	 * The weights for a neural network.
	 */
	private double[] weights;

	/**
	 * The activation types.
	 */
	private int[] activationType;
	
	public FlatNetwork()
	{
		
	}
	 
	
	public FlatNetwork(final int input,
			final int hidden1, final int hidden2, final int output,
			final boolean tanh)
	{
		FlatLayer[] layers;
		int act = tanh?ActivationFunctions.ACTIVATION_TANH:ActivationFunctions.ACTIVATION_SIGMOID;
		
		if( hidden1==0 && hidden2==0 )
		{
			layers = new FlatLayer[2];	
			layers[0] = new FlatLayer(act,input,true);
			layers[1] = new FlatLayer(act,output,false);
		}
		else if( hidden1==0 || hidden2==0 )
		{
			int count = Math.max(hidden1, hidden2);
			layers = new FlatLayer[3];
			layers[0] = new FlatLayer(act,input,true);
			layers[1] = new FlatLayer(act,count,true);
			layers[2] = new FlatLayer(act,output,false);
		}
		else
		{
			layers = new FlatLayer[4];
			layers[0] = new FlatLayer(act,input,true);
			layers[1] = new FlatLayer(act,hidden1,true);
			layers[2] = new FlatLayer(act,hidden2,true);
			layers[3] = new FlatLayer(act,output,false);
		}
		
		init(layers);		
	}
	
	/**
	 * Construct a flat network.
	 * @param network The network to construct the flat network
	 * from.
	 */
	public void init(FlatLayer[] layers) {
		
		int layerCount = layers.length;

		this.inputCount = layers[0].getCount();
		this.outputCount = layers[layerCount-1].getCount();

		this.layerCounts = new int[layerCount];
		this.weightIndex = new int[layerCount];
		this.layerIndex = new int[layerCount];
		this.activationType = new int[layerCount];
		this.layerFeedCounts = new int[layerCount];

		int index = 0;
		int neuronCount = 0;
		int weightCount = 0;

		for(int i=layers.length-1;i>=0;i--) {
			
			FlatLayer layer = layers[i];
			FlatLayer nextLayer = null;
			
			if( i>0 )
				nextLayer = layers[i-1];
			
			this.layerCounts[index] = layer.getTotalCount();
			this.layerFeedCounts[index] = layer.getCount();
			this.activationType[index] = layer.getActivation();

			neuronCount += layer.getTotalCount();
			
			if( nextLayer!=null )
				weightCount+=layer.getCount()*nextLayer.getTotalCount();

			if (index == 0) {
				this.weightIndex[index] = 0;
				this.layerIndex[index] = 0;
			} else {
				this.weightIndex[index] = this.weightIndex[index - 1]
						+ (this.layerCounts[index] * this.layerFeedCounts[index - 1]);
				this.layerIndex[index] = this.layerIndex[index - 1]
						+ this.layerCounts[index - 1];
			}

			index++;
		}

		this.weights = new double[weightCount];
		this.layerOutput = new double[neuronCount];

	}

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * @param data The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final EngineDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		final double[] actual = new double[this.outputCount];

		for (final EngineData pair : data) {
			compute(pair.getInput(), actual);
			errorCalculation.updateError(actual, pair.getIdeal());
		}
		return errorCalculation.calculate();
	}

	
	/**
	 * Clone the network.
	 * @return A clone of the network.
	 */
	public FlatNetwork clone() {
		FlatNetwork result = new FlatNetwork();
		
		result.inputCount = this.inputCount;
		result.layerCounts = EngineArray.arrayCopy(this.layerCounts);
		result.layerIndex = EngineArray.arrayCopy(this.layerIndex);
		result.layerOutput = EngineArray.arrayCopy(this.layerOutput);
		result.layerFeedCounts = EngineArray.arrayCopy(this.layerFeedCounts);
		result.outputCount = this.outputCount;
		result.weightIndex = this.weightIndex;
		result.weights = weights;
		result.activationType = this.activationType;
		return result;
	}


	/**
	 * Calculate the output for the given input.
	 * @param input The input.
	 * @param output Output will be placed here.
	 */
	public void compute(final double[] input, final double[] output) {
		final int sourceIndex = this.layerOutput.length - this.layerCounts[this.layerCounts.length-1];

		EngineArray.fill(this.layerOutput, 1);
		EngineArray.arrayCopy(input, 0, this.layerOutput, sourceIndex,
				this.inputCount);

		for (int i = this.layerIndex.length - 1; i > 0; i--) {
			computeLayer(i);
		}

		EngineArray.arrayCopy(this.layerOutput, 0, output, 0, this.outputCount);
	}

	/**
	 * Calculate a layer.
	 * @param currentLayer The layer to calculate.
	 */
	private void computeLayer(final int currentLayer) {

		final int inputIndex = this.layerIndex[currentLayer];
		final int outputIndex = this.layerIndex[currentLayer - 1];
		final int inputSize = this.layerCounts[currentLayer];
		final int outputSize = this.layerFeedCounts[currentLayer - 1];

		int index = this.weightIndex[currentLayer - 1];

		for (int i = 0; i < outputSize; i++) {
			this.layerOutput[i + outputIndex] = 0;
		}

		// weight values
		for (int x = 0; x < outputSize; x++) {
			double sum = 0;
			for (int y = 0; y < inputSize; y++) {
				sum += this.weights[index++] * this.layerOutput[inputIndex + y];
			}
			this.layerOutput[outputIndex + x] += sum;

			this.layerOutput[outputIndex + x] = ActivationFunctions
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


	public int[] getLayerFeedCounts() {
		return layerFeedCounts;
	}


	@Override
	public void decodeNetwork(double[] data) {
		if( data.length!=this.weights.length) {
			throw new EncogEngineError("Incompatable weight sizes, can't assign length="+data.length + " to length="+data.length);
		}
		this.weights = data;
		
	}


	@Override
	public double[] encodeNetwork() {
		return this.weights;
	}


	@Override
	public int getEncodeLength() {
		// TODO Auto-generated method stub
		return this.weights.length;
	}


	@Override
	public void computeSparse(
			double[] input, 
			boolean[] provided,
			double[] output) {
		throw new EncogEngineError("Sparse computation is not supported in this type of neural network.");		
	}
	
	
}
