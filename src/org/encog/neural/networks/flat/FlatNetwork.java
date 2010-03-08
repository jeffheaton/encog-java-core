package org.encog.neural.networks.flat;

import org.encog.mathutil.BoundMath;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;

/**
 * Implements a flat (vector based) neural network in Encog. This is meant to be
 * a very highly efficient feedforward neural network. It uses a minimum of
 * objects and is designed with one principal in mind-- SPEED. Readability, code
 * reuse, object oriented programming are all secondary in consideration.
 * 
 * Currently, the flat networks only support feedforward networks with either a
 * sigmoid or tanh activation function.
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
	 * The number of output neurons in this network.
	 */
	private final int outputCount;

	/**
	 * The number of neurons in each of the layers.
	 */
	private final int[] layerCounts;

	/**
	 * The index to where the weights and thresholds are stored at for a given
	 * layer.
	 */
	private final int[] weightIndex;

	/**
	 * An index to where each layer begins (based on the number of neurons in
	 * each layer).
	 */
	private final int[] layerIndex;

	/**
	 * Are we using the TANH function? If not, then the sigmoid.
	 */
	private final boolean tanh;

	/**
	 * The weights and thresholds for a neural network.
	 */
	private double[] weights;

	/**
	 * The outputs from each of the neurons.
	 */
	private double[] layerOutput;

	/**
	 * Construct a flat network.
	 * @param network The network to construct the flat network from.
	 */
	public FlatNetwork(BasicNetwork network) {
		ValidateForFlat.validateNetwork(network);

		Layer input = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer output = network.getLayer(BasicNetwork.TAG_OUTPUT);

		this.inputCount = input.getNeuronCount();
		this.outputCount = output.getNeuronCount();

		int layerCount = network.getStructure().getLayers().size();

		this.layerCounts = new int[layerCount];
		this.weightIndex = new int[layerCount];
		this.layerIndex = new int[layerCount];

		int index = 0;
		int neuronCount = 0;

		for (Layer layer : network.getStructure().getLayers()) {
			this.layerCounts[index] = layer.getNeuronCount();
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

		if (input.getActivationFunction() instanceof ActivationSigmoid)
			this.tanh = false;
		else
			this.tanh = true;
	}

	/**
	 * Implements a sigmoid activation function.
	 * @param d The value to take the sigmoid of.
	 * @return The result.
	 */
	private double sigmoid(double d) {
		return 1.0 / (1 + BoundMath.exp(-1.0 * d));
	}

	/**
	 * Implements a hyperbolic tangent function.
	 * @param d The value to take the htan of.
	 * @return The htan of the specified value.
	 */
	private double tanh(double d) {
		return -1 + (2 / (1 + BoundMath.exp(-2 * d)));
	}

	public void calculate(double[] input, double[] output) {
		int sourceIndex = this.layerOutput.length - this.inputCount;

		System.arraycopy(input, 0, this.layerOutput, sourceIndex,
				this.inputCount);

		for (int i = this.layerIndex.length - 1; i > 0; i--) {
			calculateLayer(i);
		}

		System.arraycopy(layerOutput, 0, output, 0, this.outputCount);
	}

	private void calculateLayer(int currentLayer) {
		// double[] inputData = (double[])output[layerIndex];
		// double[] outputData = (double[])output[layerIndex-1];

		int inputIndex = this.layerIndex[currentLayer];
		int outputIndex = this.layerIndex[currentLayer - 1];
		int inputSize = this.layerCounts[currentLayer];
		int outputSize = this.layerCounts[currentLayer - 1];

		int index = this.weightIndex[currentLayer - 1];

		// threshold values
		for (int i = 0; i < outputSize; i++) {
			this.layerOutput[i + outputIndex] = this.weights[index++];
		}

		// weight values
		for (int x = 0; x < outputSize; x++) {
			double sum = 0;
			for (int y = 0; y < inputSize; y++) {
				sum += this.weights[index++] * layerOutput[inputIndex + y];
			}
			layerOutput[outputIndex + x] += sum;

			if (this.tanh)
				layerOutput[outputIndex + x] = tanh(layerOutput[outputIndex + x]);
			else
				layerOutput[outputIndex + x] = sigmoid(layerOutput[outputIndex
						+ x]);
		}
	}

	public int getInputCount() {
		return inputCount;
	}

	public int getOutputCount() {
		return outputCount;
	}

	public int[] getLayerCounts() {
		return layerCounts;
	}

	public int[] getWeightIndex() {
		return weightIndex;
	}

	public boolean isTanh() {
		return tanh;
	}

	public double[] getWeights() {
		return weights;
	}

	public double[] getLayerOutput() {
		return this.layerOutput;
	}

	public int[] getLayerIndex() {
		return layerIndex;
	}

}
