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
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.persist.EncogPersistedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to generate an Jordan style recurrent neural network. This
 * network type consists of three regular layers, an input output and hidden
 * layer. There is also a context layer which accepts output from the output
 * layer and outputs back to the hidden layer. This makes it a recurrent neural
 * network.
 * 
 * The Jordan neural network is useful for temporal input data. The specified
 * activation function will be used on all layers. The Jordan neural network is
 * similar to the Elman neural network.
 * 
 * @author jheaton
 * 
 */
public class JordanPattern implements NeuralNetworkPattern {

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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an object to create a Jordan type neural network.
	 */
	public JordanPattern() {
		this.inputNeurons = -1;
		this.outputNeurons = -1;
		this.hiddenNeurons = -1;
	}

	/**
	 * Add a hidden layer, there should be only one.
	 * 
	 * @param count
	 *            The number of neurons in this hidden layer.
	 */
	public void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			final String str = "A Jordan neural network should have only one hidden "
					+ "layer.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PatternError(str);
		}

		this.hiddenNeurons = count;

	}

	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {
		this.hiddenNeurons = -1;
	}

	/**
	 * Generate a Jordan neural network.
	 * 
	 * @return A Jordan neural network.
	 */
	public EncogPersistedObject generate() {
		
		BasicLayer input,hidden,output;
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(input = new BasicLayer(this.activation, true,this.inputNeurons));
		network.addLayer(hidden = new BasicLayer(this.activation, true,this.hiddenNeurons));
		network.addLayer(output = new BasicLayer(null, false,this.outputNeurons));
		hidden.setContextFedBy(output);
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
	public void setActivationFunction(final ActivationFunction activation) {
		this.activation = activation;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}
