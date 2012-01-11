/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.BasicML;
import org.encog.ml.MLContext;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.NeuralNetworkError;
import org.encog.util.simple.EncogUtility;

/**
 * Implements a NEAT network as a synapse between two layers. In Encog, a NEAT
 * network is created by using a NEATSynapse between an input and output layer.
 * 
 * NEAT networks only have an input and an output layer. There are no actual
 * hidden layers. Rather this synapse will evolve many hidden neurons that have
 * connections that are not easily defined by layers. Connections can be
 * feedforward, recurrent, or self-connected.
 * 
 * NEAT networks relieve the programmer of the need to define the hidden layer
 * structure of the neural network.
 * 
 * The output from the neural network can be calculated normally or using a
 * snapshot. The snapshot mode is slower, but it can be more accurate. The
 * snapshot handles recurrent layers better, as it takes the time to loop
 * through the network multiple times to "flush out" the recurrent links.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATNetwork extends BasicML implements MLRegression, MLError {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3660295468309926508L;

	public static final String PROPERTY_NETWORK_DEPTH = "depth";
	public static final String PROPERTY_LINKS = "links";
	public static final String PROPERTY_SNAPSHOT = "snapshot";
	
	/**
	 * The activation function.
	 */
	private ActivationFunction activationFunction;

	/**
	 * The depth of the network.
	 */
	private int networkDepth;

	/**
	 * The neurons that make up this network.
	 */
	private final List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();
	
	private int inputCount;
	private int outputCount;
	
	private int activationCycles = 1;


	/**
	 * Default constructor.
	 */
	public NEATNetwork() {

	}

	/**
	 * Construct a NEAT synapse.
	 * 
	 * @param inputCount
	 *            The number of input neurons.
	 * @param outputCount
	 *            The number of output neurons.
	 * @param neurons
	 *            The neurons in this synapse.
	 * @param activationFunction
	 *            The activation function to use.
	 * @param networkDepth
	 *            The depth of the network.
	 */
	public NEATNetwork(final int inputCount, 
			final int outputCount,
			final List<NEATNeuron> neurons,
			final ActivationFunction activationFunction,
			final int networkDepth) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.neurons.addAll(neurons);
		this.networkDepth = networkDepth;
		this.activationFunction = activationFunction;
	}

	/**
	 * Construct a NEAT network.
	 * 
	 * @param inputCount
	 *            The input count.
	 * @param outputCount
	 *            The output count.
	 */
	public NEATNetwork(final int inputCount, final int outputCount) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.networkDepth = 0;
		this.activationFunction = new ActivationSigmoid();
	}

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output from this synapse.
	 */
	public MLData compute(final MLData input) {
		final MLData result = new BasicMLData(this.outputCount);

		if (this.neurons.size() == 0) {
			throw new NeuralNetworkError("This network has not been evolved yet.");
		}
		
		// clear from previous run
		for (final NEATNeuron neuron : this.neurons) {
			neuron.setOutput(0);
		}

		// iterate through the network FlushCount times
		for (int i = 0; i < activationCycles; ++i) {
			int outputIndex = 0;
			int index = 0;

			result.clear();

			// populate the input neurons
			while (this.neurons.get(index).getNeuronType() 
					== NEATNeuronType.Input) {
				this.neurons.get(index).setOutput(input.getData(index));

				index++;
			}

			// set the bias neuron
			this.neurons.get(index++).setOutput(1);

			while (index < this.neurons.size()) {

				final NEATNeuron currentNeuron = this.neurons.get(index);

				double sum = 0;

				for (final NEATLink link : currentNeuron.getInboundLinks()) {
					final double weight = link.getWeight();
					final double neuronOutput = link.getFromNeuron()
							.getOutput();
					sum += weight * neuronOutput;
				}

				final double[] d = { sum };
				this.activationFunction.activationFunction(d,0,d.length);

				currentNeuron.setOutput(d[0]);

				if (currentNeuron.getNeuronType() == NEATNeuronType.Output) {
					result.setData(outputIndex++, currentNeuron.getOutput());
				}
				index++;
			}
		}

		return result;
	}

	/**
	 * @return The activation function.
	 */
	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

	/**
	 * @return The network depth.
	 */
	public int getNetworkDepth() {
		return this.networkDepth;
	}

	/**
	 * @return The NEAT neurons.
	 */
	public List<NEATNeuron> getNeurons() {
		return this.neurons;
	}

	/**
	 * Set the activation function.
	 * @param activationFunction The activation function.
	 */
	public void setActivationFunction(
			final ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	@Override
	public int getOutputCount() {
		return this.outputCount;
	}
	
	@Override
	public void updateProperties() {
		
	}

	public void setInputCount(int i) {
		this.inputCount = i;		
	}
	
	public void setOutputCount(int i) {
		this.outputCount = i;		
	}

	public void setNetworkDepth(int i) {
		this.networkDepth = i;
		
	}

	/**
	 * Calculate the error for this neural network. 
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this,data);
	}

	public int getActivationCycles() {
		return activationCycles;
	}

	public void setActivationCycles(int activationCycles) {
		this.activationCycles = activationCycles;
	}	
	
	
}
