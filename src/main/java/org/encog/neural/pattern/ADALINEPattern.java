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
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * Construct an ADALINE neural network.
 */
public class ADALINEPattern implements NeuralNetworkPattern {

	/**
	 * The number of neurons in the input layer.
	 */
	private int inputNeurons;

	/**
	 * The number of neurons in the output layer.
	 */
	private int outputNeurons;


	/**
	 * Not used, the ADALINE has no hidden layers, this will throw an error.
	 * 
	 * @param count
	 *            The neuron count.
	 */
	public final void addHiddenLayer(final int count) {
		throw new PatternError("An ADALINE network has no hidden layers.");
	}

	/**
	 * Clear out any parameters.
	 */
	public final void clear() {
		this.inputNeurons = 0; 
		this.outputNeurons = 0;
	}

	/**
	 * Generate the network.
	 * 
	 * @return The generated network.
	 */
	public final MLMethod generate() {
		final BasicNetwork network = new BasicNetwork();

		final Layer inputLayer = new BasicLayer(new ActivationLinear(), true,
				this.inputNeurons);
		final Layer outputLayer = new BasicLayer(new ActivationLinear(), false,
				this.outputNeurons);

		network.addLayer(inputLayer);
		network.addLayer(outputLayer);
		network.getStructure().finalizeStructure();

		(new RangeRandomizer(-0.5, 0.5)).randomize(network);

		return network;
	}

	/**
	 * Not used, ADALINE does not use custom activation functions.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError( "A ADALINE network can't specify a custom activation function.");
	}

	/**
	 * Set the input neurons.
	 * 
	 * @param count
	 *            The number of neurons in the input layer.
	 */
	public final void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the output neurons.
	 * 
	 * @param count
	 *            The number of neurons in the output layer.
	 */
	public final void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}
