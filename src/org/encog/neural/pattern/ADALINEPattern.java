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
package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Not used, the ADALINE has no hidden layers, this will throw an error.
	 * 
	 * @param count
	 *            The neuron count.
	 */
	public void addHiddenLayer(final int count) {
		final String str = "An ADALINE network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Clear out any parameters.
	 */
	public void clear() {
		this.inputNeurons = 0; 
		this.outputNeurons = 0;
	}

	/**
	 * Generate the network.
	 * 
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		final BasicNetwork network = new BasicNetwork();

		int y = PatternConst.START_Y;

		final Layer inputLayer = new BasicLayer(new ActivationLinear(), false,
				this.inputNeurons);
		final Layer outputLayer = new BasicLayer(new ActivationLinear(), true,
				this.outputNeurons);

		network.addLayer(inputLayer);
		network.addLayer(outputLayer);
		network.getStructure().finalizeStructure();

		(new RangeRandomizer(-0.5, 0.5)).randomize(network);

		inputLayer.setX(PatternConst.START_X);
		inputLayer.setY(y);
		y += PatternConst.INC_Y;

		outputLayer.setX(PatternConst.START_X);
		outputLayer.setY(y);

		return network;
	}

	/**
	 * Not used, ADALINE does not use custom activation functions.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "A ADALINE network can't specify a custom activation function.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Set the input neurons.
	 * 
	 * @param count
	 *            The number of neurons in the input layer.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the output neurons.
	 * 
	 * @param count
	 *            The number of neurons in the output layer.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}
