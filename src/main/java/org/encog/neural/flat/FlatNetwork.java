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
package org.encog.neural.flat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.error.ErrorCalculation;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;

/**
 * Implements a flat (vector based) neural network in the Encog Engine. This is
 * meant to be a very highly efficient feedforward, or simple recurrent, neural
 * network. It uses a minimum of objects and is designed with one principal in
 * mind-- SPEED. Readability, code reuse, object oriented programming are all
 * secondary in consideration.
 * 
 * Vector based neural networks are also very good for GPU processing. The flat
 * network classes will make use of the GPU if you have enabled GPU processing.
 * See the Encog class for more info.
 */
public class FlatNetwork implements Serializable, Cloneable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default bias activation.
	 */
	public static final double DEFAULT_BIAS_ACTIVATION = 1.0;

	/**
	 * The value that indicates that there is no bias activation.
	 */
	public static final double NO_BIAS_ACTIVATION = 0.0;

	/**
	 * The number of input neurons in this network.
	 */
	private int inputCount;

	/**
	 * The number of neurons in each of the layers.
	 */
	private int[] layerCounts;
	
	/**
	 * The dropout rate for each layer.
	 */
	private double[] layerDropoutRates;

	/**
	 * The number of context neurons in each layer. These context neurons will
	 * feed the next layer.
	 */
	private int[] layerContextCount;

	/**
	 * The number of neurons in each layer that are actually fed by neurons in
	 * the previous layer. Bias neurons, as well as context neurons, are not fed
	 * from the previous layer.
	 */
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
	 * The sum of the layer, before the activation function is applied, producing the layerOutput.
	 */
	private double[] layerSums;

	/**
	 * The number of output neurons in this network.
	 */
	private int outputCount;

	/**
	 * The index to where the weights that are stored at for a given layer.
	 */
	private int[] weightIndex;

	/**
	 * The weights for a neural network.
	 */
	private double[] weights;

	/**
	 * The activation types.
	 */
	private ActivationFunction[] activationFunctions;

	/**
	 * The context target for each layer. This is how the backwards connections
	 * are formed for the recurrent neural network. Each layer either has a
	 * zero, which means no context target, or a layer number that indicates the
	 * target layer.
	 */
	private int[] contextTargetOffset;

	/**
	 * The size of each of the context targets. If a layer's contextTargetOffset
	 * is zero, its contextTargetSize should also be zero. The contextTargetSize
	 * should always match the feed count of the targeted context layer.
	 */
	private int[] contextTargetSize;

	/**
	 * The bias activation for each layer. This is usually either 1, for a bias,
	 * or zero for no bias.
	 */
	private double[] biasActivation;

	/**
	 * The layer that training should begin on.
	 */
	private int beginTraining;

	/**
	 * The layer that training should end on.
	 */
	private int endTraining;

	/**
	 * Does this network have some connections disabled.
	 */
	private boolean isLimited;

	/**
	 * The limit, under which, all a cconnection is not considered to exist.
	 */
	private double connectionLimit;

	/**
	 * True if the network has context.
	 */
	private boolean hasContext;

	/**
	 * Default constructor.
	 */
	public FlatNetwork() {
		this.layerDropoutRates = new double[0];
	}

	/**
	 * Create a flat network from an array of layers.
	 * 
	 * @param layers
	 *            The layers.
	 */
	public FlatNetwork(final FlatLayer[] layers) {
		init(layers,false);
	}
	public FlatNetwork(final FlatLayer[] layers, boolean dropout) {
		init(layers,dropout);
	}

	/**
	 * Construct a flat neural network.
	 * 
	 * @param input
	 *            Neurons in the input layer.
	 * @param hidden1
	 *            Neurons in the first hidden layer. Zero for no first hidden
	 *            layer.
	 * @param hidden2
	 *            Neurons in the second hidden layer. Zero for no second hidden
	 *            layer.
	 * @param output
	 *            Neurons in the output layer.
	 * @param tanh
	 *            True if this is a tanh activation, false for sigmoid.
	 */
	public FlatNetwork(final int input, final int hidden1, final int hidden2,
			final int output, final boolean tanh) {

		final ActivationFunction linearAct = new ActivationLinear();
		FlatLayer[] layers;
		final ActivationFunction act = tanh ? new ActivationTANH()
				: new ActivationSigmoid();

		if ((hidden1 == 0) && (hidden2 == 0)) {
			layers = new FlatLayer[2];
			layers[0] = new FlatLayer(linearAct, input,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION);
			layers[1] = new FlatLayer(act, output,
					FlatNetwork.NO_BIAS_ACTIVATION);
		} else if ((hidden1 == 0) || (hidden2 == 0)) {
			final int count = Math.max(hidden1, hidden2);
			layers = new FlatLayer[3];
			layers[0] = new FlatLayer(linearAct, input,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION);
			layers[1] = new FlatLayer(act, count,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION);
			layers[2] = new FlatLayer(act, output,
					FlatNetwork.NO_BIAS_ACTIVATION);
		} else {
			layers = new FlatLayer[4];
			layers[0] = new FlatLayer(linearAct, input,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION);
			layers[1] = new FlatLayer(act, hidden1,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION);
			layers[2] = new FlatLayer(act, hidden2,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION);
			layers[3] = new FlatLayer(act, output,
					FlatNetwork.NO_BIAS_ACTIVATION);
		}

		this.isLimited = false;
		this.connectionLimit = 0.0;

		init(layers,false);
	}

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final MLDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		final double[] actual = new double[this.outputCount];
		final MLDataPair pair = BasicMLDataPair.createPair(data.getInputSize(),
				data.getIdealSize());

		for (int i = 0; i < data.getRecordCount(); i++) {
			data.getRecord(i, pair);
			compute(pair.getInputArray(), actual);
			errorCalculation.updateError(actual, pair.getIdealArray(), pair.getSignificance());
		}
		return errorCalculation.calculate();
	}

	/**
	 * Clear any connection limits.
	 */
	public void clearConnectionLimit() {
		this.connectionLimit = 0.0;
		this.isLimited = false;
	}

	/**
	 * Clear any context neurons.
	 */
	public void clearContext() {
		int index = 0;

		for (int i = 0; i < this.layerIndex.length; i++) {

			final boolean hasBias = (this.layerContextCount[i] + this.layerFeedCounts[i]) != this.layerCounts[i];

			// fill in regular neurons
			Arrays.fill(this.layerOutput, index, index+this.layerFeedCounts[i], 0);
			index += this.layerFeedCounts[i];

			// fill in the bias
			if (hasBias) {
				this.layerOutput[index++] = this.biasActivation[i];
			}
			
			// fill in context
			Arrays.fill(this.layerOutput, index, index+this.layerContextCount[i], 0);
			index += this.layerContextCount[i];
		}
	}

	/**
	 * Clone the network.
	 * 
	 * @return A clone of the network.
	 */
	@Override
	public FlatNetwork clone() {
		final FlatNetwork result = new FlatNetwork();
		cloneFlatNetwork(result);
		return result;
	}

	/**
	 * Clone into the flat network passed in.
	 * 
	 * @param result
	 *            The network to copy into.
	 */
	public void cloneFlatNetwork(final FlatNetwork result) {
		result.inputCount = this.inputCount;
		result.layerCounts = EngineArray.arrayCopy(this.layerCounts);
		result.layerIndex = EngineArray.arrayCopy(this.layerIndex);
		result.layerOutput = EngineArray.arrayCopy(this.layerOutput);
		result.layerSums = EngineArray.arrayCopy(this.layerSums);
		result.layerFeedCounts = EngineArray.arrayCopy(this.layerFeedCounts);
		result.contextTargetOffset = EngineArray
				.arrayCopy(this.contextTargetOffset);
		result.contextTargetSize = EngineArray
				.arrayCopy(this.contextTargetSize);
		result.layerContextCount = EngineArray
				.arrayCopy(this.layerContextCount);
		result.biasActivation = EngineArray.arrayCopy(this.biasActivation);
		result.outputCount = this.outputCount;
		result.weightIndex = this.weightIndex;
		result.weights = this.weights;
		result.layerDropoutRates = EngineArray.arrayCopy(this.layerDropoutRates);

		result.activationFunctions = new ActivationFunction[this.activationFunctions.length];
		for (int i = 0; i < result.activationFunctions.length; i++) {
			result.activationFunctions[i] = this.activationFunctions[i].clone();
		}

		result.beginTraining = this.beginTraining;
		result.endTraining = this.endTraining;
	}

	/**
	 * Calculate the output for the given input.
	 * 
	 * @param input
	 *            The input.
	 * @param output
	 *            Output will be placed here.
	 */
	public void compute(final double[] input, final double[] output) {
		final int sourceIndex = this.layerOutput.length
				- this.layerCounts[this.layerCounts.length - 1];

		EngineArray.arrayCopy(input, 0, this.layerOutput, sourceIndex,
				this.inputCount);

		for (int i = this.layerIndex.length - 1; i > 0; i--) {
			computeLayer(i);
		}
		
		// update context values
		final int offset = this.contextTargetOffset[0];

		EngineArray.arrayCopy(this.layerOutput, 0, layerOutput,
				offset, this.contextTargetSize[0]);

		EngineArray.arrayCopy(this.layerOutput, 0, output, 0, this.outputCount);
	}

	/**
	 * Calculate a layer.
	 * 
	 * @param currentLayer
	 *            The layer to calculate.
	 */
	protected void computeLayer(final int currentLayer) {

		final int inputIndex = this.layerIndex[currentLayer];
		final int outputIndex = this.layerIndex[currentLayer - 1];
		final int inputSize = this.layerCounts[currentLayer];
		final int outputSize = this.layerFeedCounts[currentLayer - 1];
		final double dropoutRate;
		if(this.layerDropoutRates.length > currentLayer - 1) {
			dropoutRate = this.layerDropoutRates[currentLayer - 1];
		} else {
			dropoutRate = 0;
		}

		int index = this.weightIndex[currentLayer - 1];

		final int limitX = outputIndex + outputSize;
		final int limitY = inputIndex + inputSize;

		// weight values
		for (int x = outputIndex; x < limitX; x++) {
			double sum = 0;
			for (int y = inputIndex; y < limitY; y++) {
				sum += this.weights[index++] * this.layerOutput[y] * (1 - dropoutRate);
			}
			this.layerSums[x] = sum;
			this.layerOutput[x] = sum;
		}

		this.activationFunctions[currentLayer - 1].activationFunction(
				this.layerOutput, outputIndex, outputSize);

		// update context values
		final int offset = this.contextTargetOffset[currentLayer];

		EngineArray.arrayCopy(this.layerOutput, outputIndex,
				this.layerOutput, offset, this.contextTargetSize[currentLayer]);
	}

	/**
	 * Decode the specified data into the weights of the neural network. This
	 * method performs the opposite of encodeNetwork.
	 * 
	 * @param data
	 *            The data to be decoded.
	 */
	public void decodeNetwork(final double[] data) {
		if (data.length != this.weights.length) {
			throw new EncogError(
					"Incompatible weight sizes, can't assign length="
							+ data.length + " to length=" + this.weights.length);
		}
		this.weights = EngineArray.arrayCopy(data);
	}

	/**
	 * Encode the neural network to an array of doubles. This includes the
	 * network weights. To read this into a neural network, use the
	 * decodeNetwork method.
	 * 
	 * @return The encoded network.
	 */
	public double[] encodeNetwork() {
		return this.weights;
	}

	/**
	 * @return The activation functions.
	 */
	public ActivationFunction[] getActivationFunctions() {
		return this.activationFunctions;
	}

	/**
	 * @return the beginTraining
	 */
	public int getBeginTraining() {
		return this.beginTraining;
	}

	/**
	 * @return The bias activation.
	 */
	public double[] getBiasActivation() {
		return this.biasActivation;
	}

	/**
	 * @return the connectionLimit
	 */
	public double getConnectionLimit() {
		return this.connectionLimit;
	}

	/**
	 * @return The offset of the context target for each layer.
	 */
	public int[] getContextTargetOffset() {
		return this.contextTargetOffset;
	}

	/**
	 * @return The context target size for each layer. Zero if the layer does
	 *         not feed a context layer.
	 */
	public int[] getContextTargetSize() {
		return this.contextTargetSize;
	}

	/**
	 * @return The length of the array the network would encode to.
	 */
	public int getEncodeLength() {
		return this.weights.length;
	}

	/**
	 * @return the endTraining
	 */
	public int getEndTraining() {
		return this.endTraining;
	}

	/**
	 * @return True if this network has context.
	 */
	public boolean getHasContext() {
		return this.hasContext;
	}

	/**
	 * @return The number of input neurons.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The layer context count.
	 */
	public int[] getLayerContextCount() {
		return this.layerContextCount;
	}

	/**
	 * @return The number of neurons in each layer.
	 */
	public int[] getLayerCounts() {
		return this.layerCounts;
	}

	/**
	 * @return The number of neurons in each layer that are fed by the previous
	 *         layer.
	 */
	public int[] getLayerFeedCounts() {
		return this.layerFeedCounts;
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
	 * optimization options. This method determines if only a single activation
	 * function is used.
	 * 
	 * @return The number of the single activation function, or -1 if there are
	 *         no activation functions or more than one type of activation
	 *         function.
	 */
	public Class<?> hasSameActivationFunction() {
		final List<Class<?>> map = new ArrayList<Class<?>>();

		for (final ActivationFunction activation : this.activationFunctions) {
			if (!map.contains(activation.getClass())) {
				map.add(activation.getClass());
			}
		}

		if (map.size() != 1) {
			return null;
		} else {
			return map.get(0);
		}
	}

	/**
	 * Construct a flat network.
	 * 
	 * @param layers
	 *            The layers of the network to create.
	 * @param dropout
	 *            Flag to enable dropout rate for each layer.
	 */
	public void init(final FlatLayer[] layers, boolean dropout) {

		final int layerCount = layers.length;

		this.inputCount = layers[0].getCount();
		this.outputCount = layers[layerCount - 1].getCount();

		this.layerCounts = new int[layerCount];
		this.layerContextCount = new int[layerCount];
		this.weightIndex = new int[layerCount];
		this.layerIndex = new int[layerCount];
		if(dropout){
			this.layerDropoutRates = new double[layerCount];
		} else {
			this.layerDropoutRates = new double[0];
		}
		this.activationFunctions = new ActivationFunction[layerCount];
		this.layerFeedCounts = new int[layerCount];
		this.contextTargetOffset = new int[layerCount];
		this.contextTargetSize = new int[layerCount];
		this.biasActivation = new double[layerCount];

		int index = 0;
		int neuronCount = 0;
		int weightCount = 0;

		for (int i = layers.length - 1; i >= 0; i--) {

			final FlatLayer layer = layers[i];
			FlatLayer nextLayer = null;

			if (i > 0) {
				nextLayer = layers[i - 1];
			}

			this.biasActivation[index] = layer.getBiasActivation();
			this.layerCounts[index] = layer.getTotalCount();
			this.layerFeedCounts[index] = layer.getCount();
			this.layerContextCount[index] = layer.getContextCount();
			this.activationFunctions[index] = layer.getActivation();
			if(dropout)
			{
				this.layerDropoutRates[index] = layer.getDropoutRate(); 
			}

			neuronCount += layer.getTotalCount();

			if (nextLayer != null) {
				weightCount += layer.getCount() * nextLayer.getTotalCount();
			}

			if (index == 0) {
				this.weightIndex[index] = 0;
				this.layerIndex[index] = 0;
			} else {
				this.weightIndex[index] = this.weightIndex[index - 1]
						+ (this.layerCounts[index] * this.layerFeedCounts[index - 1]);
				this.layerIndex[index] = this.layerIndex[index - 1]
						+ this.layerCounts[index - 1];
			}

			int neuronIndex = 0;
			for (int j = layers.length - 1; j >= 0; j--) {
				if (layers[j].getContextFedBy() == layer) {
					this.hasContext = true;
					this.contextTargetSize[index] = layers[j].getContextCount();
					this.contextTargetOffset[index] = neuronIndex
							+ (layers[j].getTotalCount() - layers[j]
									.getContextCount());
				}
				neuronIndex += layers[j].getTotalCount();
			}

			index++;
		}

		this.beginTraining = 0;
		this.endTraining = this.layerCounts.length - 1;

		this.weights = new double[weightCount];
		this.layerOutput = new double[neuronCount];
		this.layerSums = new double[neuronCount];

		clearContext();
	}

	/**
	 * @return the isLimited
	 */
	public boolean isLimited() {
		return this.isLimited;
	}

	/**
	 * Perform a simple randomization of the weights of the neural network
	 * between -1 and 1.
	 */
	public void randomize() {
		randomize(1, -1);
	}

	/**
	 * Perform a simple randomization of the weights of the neural network
	 * between the specified hi and lo.
	 * 
	 * @param hi
	 *            The network high.
	 * @param lo
	 *            The network low.
	 */
	public void randomize(final double hi, final double lo) {
		for (int i = 0; i < this.weights.length; i++) {
			this.weights[i] = (Math.random() * (hi - lo)) + lo;
		}
	}

	/**
	 * Set the activation functions.
	 * @param af The activation functions.
	 */
	public void setActivationFunctions(final ActivationFunction[] af) {
		this.activationFunctions = Arrays.copyOf(af, af.length);
	}

	/**
	 * @param beginTraining
	 *            the beginTraining to set
	 */
	public void setBeginTraining(final int beginTraining) {
		this.beginTraining = beginTraining;
	}

	/**
	 * Set the bias activation.
	 * @param biasActivation The bias activation.
	 */
	public void setBiasActivation(final double[] biasActivation) {
		this.biasActivation = biasActivation;
	}

	/**
	 * @param connectionLimit
	 *            the connectionLimit to set
	 */
	public void setConnectionLimit(final double connectionLimit) {
		this.connectionLimit = connectionLimit;
		if (Math.abs(this.connectionLimit
				- BasicNetwork.DEFAULT_CONNECTION_LIMIT) < Encog.DEFAULT_DOUBLE_EQUAL) {
			this.isLimited = true;
		}
	}

	/**
	 * Set the context target offset.
	 * @param contextTargetOffset The context target offset.
	 */
	public void setContextTargetOffset(final int[] contextTargetOffset) {
		this.contextTargetOffset = EngineArray.arrayCopy(contextTargetOffset);

	}

	/**
	 * Set the context target size.
	 * @param contextTargetSize The context target size.
	 */
	public void setContextTargetSize(final int[] contextTargetSize) {
		this.contextTargetSize = EngineArray.arrayCopy(contextTargetSize);

	}

	/**
	 * @param endTraining
	 *            the endTraining to set
	 */
	public void setEndTraining(final int endTraining) {
		this.endTraining = endTraining;
	}

	/**
	 * Set the hasContext property.
	 * @param hasContext True if the network has context.
	 */
	public void setHasContext(final boolean hasContext) {
		this.hasContext = hasContext;
	}

	/**
	 * Set the input count.
	 * @param inputCount The input count.
	 */
	public void setInputCount(final int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * Set the layer context count.
	 * @param layerContextCount The layer context count.
	 */
	public void setLayerContextCount(final int[] layerContextCount) {
		this.layerContextCount = EngineArray.arrayCopy(layerContextCount);
	}

	/**
	 * Set the layer counts.
	 * @param layerCounts The layer counts.
	 */
	public void setLayerCounts(final int[] layerCounts) {
		this.layerCounts = EngineArray.arrayCopy(layerCounts);

	}

	public void setLayerFeedCounts(final int[] layerFeedCounts) {
		this.layerFeedCounts = EngineArray.arrayCopy(layerFeedCounts);

	}

	/**
	 * Set the layer index.
	 * @param i The layer index.
	 */
	public void setLayerIndex(final int[] i) {
		this.layerIndex = EngineArray.arrayCopy(i);
	}

	/**
	 * Set the layer output.
	 * @param layerOutput The layer output.
	 */
	public void setLayerOutput(final double[] layerOutput) {
		this.layerOutput = EngineArray.arrayCopy(layerOutput);
	}

	/**
	 * Set the output count.
	 * @param outputCount The output count.
	 */
	public void setOutputCount(final int outputCount) {
		this.outputCount = outputCount;
	}

	/**
	 * Set the weight index.
	 * @param weightIndex The weight index.
	 */
	public void setWeightIndex(final int[] weightIndex) {
		this.weightIndex = EngineArray.arrayCopy(weightIndex);

	}

	/**
	 * Set the weights.
	 * @param weights The weights.
	 */
	public void setWeights(final double[] weights) {
		this.weights = EngineArray.arrayCopy(weights);
	}

	/**
	 * @return the layerSums
	 */
	public double[] getLayerSums() {
		return layerSums;
	}

	/**
	 * Set the layer sums.
	 * @param d The layer sums.
	 */
	public void setLayerSums(double[] d) {
		this.layerSums = EngineArray.arrayCopy(d);
		
	}

	public double[] getLayerDropoutRates() {
		return layerDropoutRates;
	}

	public void setLayerDropoutRates(double[] layerDropoutRates) {
		this.layerDropoutRates = layerDropoutRates;
	}
	
	/**
	 * Get the weight between the two layers.
	 * @param fromLayer The from layer.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @return The weight value.
	 */
	public double getWeight(final int fromLayer, 
			final int fromNeuron,
			final int toNeuron) {
		validateNeuron(fromLayer, fromNeuron);
		validateNeuron(fromLayer + 1, toNeuron);
		final int fromLayerNumber = this.layerContextCount.length - fromLayer - 1;
		final int toLayerNumber = fromLayerNumber - 1;

		if (toLayerNumber < 0) {
			throw new NeuralNetworkError(
					"The specified layer is not connected to another layer: "
							+ fromLayer);
		}

		final int weightBaseIndex 
			= this.weightIndex[toLayerNumber];
		final int count 
			= this.layerCounts[fromLayerNumber];
		final int weightIndex = weightBaseIndex + fromNeuron
				+ (toNeuron * count);

		return this.weights[weightIndex];
	}
	
	/**
	 * Validate the the specified targetLayer and neuron are valid.
	 * @param targetLayer The target layer.
	 * @param neuron The target neuron.
	 */
	public void validateNeuron(final int targetLayer, final int neuron) {
		if ((targetLayer < 0) || (targetLayer >= this.layerCounts.length)) {
			throw new NeuralNetworkError("Invalid layer count: " + targetLayer);
		}

		if ((neuron < 0) || (neuron >= getLayerTotalNeuronCount(targetLayer))) {
			throw new NeuralNetworkError("Invalid neuron number: " + neuron);
		}
	}
	
	/**
	 * Get the total (including bias and context) neuron cont for a layer.
	 * @param l The layer.
	 * @return The count.
	 */
	public int getLayerTotalNeuronCount(final int l) {
		final int layerNumber = this.layerCounts.length - l - 1;
		return this.layerCounts[layerNumber];
	}
	
	/**
	 * Get the neuron count.
	 * @param l The layer.
	 * @return The neuron count.
	 */
	public int getLayerNeuronCount(final int l) {
		final int layerNumber = this.layerCounts.length - l - 1;
		return this.layerFeedCounts[layerNumber];
	}
}
