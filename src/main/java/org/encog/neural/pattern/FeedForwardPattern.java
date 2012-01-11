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

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * Used to create feedforward neural networks. A feedforward network has an
 * input and output layers separated by zero or more hidden layers. The
 * feedforward neural network is one of the most common neural network patterns.
 * 
 * @author jheaton
 * 
 */
public class FeedForwardPattern implements NeuralNetworkPattern {
	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * The activation function.
	 */
	private ActivationFunction activationHidden;
	
	/**
	 * The activation function.
	 */
	private ActivationFunction activationOutput;

	/**
	 * The number of hidden neurons.
	 */
	private final List<Integer> hidden = new ArrayList<Integer>();


	/**
	 * Add a hidden layer, with the specified number of neurons.
	 * 
	 * @param count
	 *            The number of neurons to add.
	 */
	public final void addHiddenLayer(final int count) {
		this.hidden.add(count);
	}

	/**
	 * Clear out any hidden neurons.
	 */
	public final void clear() {
		this.hidden.clear();
	}

	/**
	 * Generate the feedforward neural network.
	 * 
	 * @return The feedforward neural network.
	 */
	public final MLMethod generate() {

		if( this.activationOutput==null )
			this.activationOutput = this.activationHidden;
		
		final Layer input = new BasicLayer(null, true,
				this.inputNeurons);

		final BasicNetwork result = new BasicNetwork();
		result.addLayer(input);


		for (final Integer count : this.hidden) {

			final Layer hidden = new BasicLayer(this.activationHidden, true, count);

			result.addLayer(hidden);
		}

		final Layer output = new BasicLayer(this.activationOutput, false,
				this.outputNeurons);
		result.addLayer(output);

		result.getStructure().finalizeStructure();
		result.reset();

		return result;
	}

	/**
	 * Set the activation function to use on each of the layers.
	 * 
	 * @param activation
	 *            The activation function.
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		this.activationHidden = activation;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	public final void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	public final void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

	/**
	 * @return the activationOutput
	 */
	public final ActivationFunction getActivationOutput() {
		return activationOutput;
	}

	/**
	 * @param activationOutput the activationOutput to set
	 */
	public final void setActivationOutput(ActivationFunction activationOutput) {
		this.activationOutput = activationOutput;
	}

	

}
