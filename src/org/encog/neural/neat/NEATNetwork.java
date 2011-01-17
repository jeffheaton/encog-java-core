/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.ContextClearable;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.BasicPersistedSubObject;
import org.encog.persist.EncogCollection;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.persistors.generic.GenericPersistor;

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
public class NEATNetwork extends BasicPersistedObject implements ContextClearable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3660295468309926508L;
	
	/**
	 * The activation function.
	 */
	private ActivationFunction activationFunction;

	/**
	 * The depth of the network.
	 */
	@EGAttribute
	private int networkDepth;

	/**
	 * The neurons that make up this network.
	 */
	private final List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();

	/**
	 * Should snapshot be used to calculate the output of the neural network.
	 */
	@EGAttribute
	private boolean snapshot = false;
	
	private int inputCount;
	private int outputCount;


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
	 * Construct a NEAT synapse.
	 * 
	 * @param fromLayer
	 *            The input layer.
	 * @param toLayer
	 *            The output layer.
	 */
	public NEATNetwork(final int inputCount, final int outputCount) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.networkDepth = 0;
		this.activationFunction = new ActivationSigmoid();
	}

	/**
	 * Clear any context from previous runs. This sets the activation of all
	 * neurons to zero.
	 */
	public void clearContext() {
		for (final NEATNeuron neuron : this.neurons) {
			neuron.setOutput(0);
		}
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		return null;
	}

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output from this synapse.
	 */
	public NeuralData compute(final NeuralData input) {
		final NeuralData result = new BasicNeuralData(this.outputCount);

		if (this.neurons.size() == 0) {
			throw new NeuralNetworkError(
"This network has not been evolved yet, it has no neurons in the NEAT synapse.");
		}

		int flushCount = 1;

		if (this.snapshot) {
			flushCount = this.networkDepth;
		}

		// iterate through the network FlushCount times
		for (int i = 0; i < flushCount; ++i) {
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

				final double[] d = new double[1];
				d[0] = sum / currentNeuron.getActivationResponse();
				this.activationFunction.activationFunction(d,0,d.length);

				this.neurons.get(index).setOutput(d[0]);

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
	 * Get the weight matrix. Not used for a NEAT synapse.
	 * 
	 * @return The weight matrix.
	 */
	public Matrix getMatrix() {
		return null;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * 
	 * @return The size of the matrix.
	 */
	public int getMatrixSize() {
		return 0;
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
	 * @return The type of synapse that this is.
	 */
	public SynapseType getType() {
		return null;
	}

	/**
	 * @return True if this is a self-connected synapse. That is, the from and
	 *         to layers are the same.
	 */
	public boolean isSelfConnected() {
		return false;
	}

	/**
	 * @return True if snapshot is being used.
	 */
	public boolean isSnapshot() {
		return this.snapshot;
	}

	/**
	 * @return True if the weights for this synapse can be modified.
	 */
	public boolean isTeachable() {
		return false;
	}

	/**
	 * Set the activation function.
	 * @param activationFunction The activation function.
	 */
	public void setActivationFunction(
			final ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	/**
	 * Not used.
	 * 
	 * @param description
	 *            Not used.
	 */
	public void setDescription(final String description) {

	}

	/**
	 * Assign a new weight matrix to this layer. Not used by a NEAT network.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		throw new NeuralNetworkError(
				"Neat synapse cannot have a simple matrix.");
	}

	/**
	 * Sets if snapshot is used.
	 * 
	 * @param snapshot
	 *            True if snapshot is used.
	 */
	public void setSnapshot(final boolean snapshot) {
		this.snapshot = snapshot;
	}
}
