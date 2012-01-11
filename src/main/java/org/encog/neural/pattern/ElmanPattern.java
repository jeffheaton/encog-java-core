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
package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

/**
 * This class is used to generate an Elman style recurrent neural network. This
 * network type consists of three regular layers, an input output and hidden
 * layer. There is also a context layer which accepts output from the hidden
 * layer and outputs back to the hidden layer. This makes it a recurrent neural
 * network.
 * 
 * The Elman neural network is useful for temporal input data. The specified
 * activation function will be used on all layers. The Elman neural network is
 * similar to the Jordan neural network.
 * 
 * @author jheaton
 * 
 */
public class ElmanPattern implements NeuralNetworkPattern {

	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * The number of hidden neurons.
	 */
	private int hiddenNeurons;

	/**
	 * The activation function.
	 */
	private ActivationFunction activation;

	/**
	 * Create an object to generate Elman neural networks.
	 */
	public ElmanPattern() {
		this.inputNeurons = -1;
		this.outputNeurons = -1;
		this.hiddenNeurons = -1;
	}

	/**
	 * Add a hidden layer with the specified number of neurons.
	 * 
	 * @param count
	 *            The number of neurons in this hidden layer.
	 */
	@Override
	public final void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			throw new PatternError(
					"An Elman neural network should have only one hidden layer.");
		}

		this.hiddenNeurons = count;

	}

	/**
	 * Clear out any hidden neurons.
	 */
	@Override
	public final void clear() {
		this.hiddenNeurons = -1;
	}

	/**
	 * Generate the Elman neural network.
	 * 
	 * @return The Elman neural network.
	 */
	@Override
	public final MLMethod generate() {
		BasicLayer hidden, input;

		final BasicNetwork network = new BasicNetwork();
		network.addLayer(input = new BasicLayer(this.activation, true,
				this.inputNeurons));
		network.addLayer(hidden = new BasicLayer(this.activation, true,
				this.hiddenNeurons));
		network.addLayer(new BasicLayer(null, false, this.outputNeurons));
		input.setContextFedBy(hidden);
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	/**
	 * Set the activation function to use on each of the layers.
	 * 
	 * @param activation
	 *            The activation function.
	 */
	@Override
	public final void setActivationFunction(final ActivationFunction activation) {
		this.activation = activation;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	@Override
	public final void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	@Override
	public final void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}
