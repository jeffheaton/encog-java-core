/*
 * Encog(tm) Core v2.5 - Java Version
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

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.synapse.neat.NEATNeuron;
import org.encog.neural.networks.synapse.neat.NEATSynapse;

public class NEATPattern implements NeuralNetworkPattern {
	/**
	 * The number of input neurons to use. Must be set, default to invalid -1
	 * value.
	 */
	private int inputNeurons = -1;

	/**
	 * The number of hidden neurons to use. Must be set, default to invalid -1
	 * value.
	 */
	private int outputNeurons = -1;
	
	private ActivationFunction neatActivation;
	
	private ActivationFunction outputActivation;
	
	private boolean snapshot;
	
	private final List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();


	/**
	 * Add the hidden layer, this should be called once, as a RBF has a single
	 * hidden layer.
	 * 
	 * @param count
	 *            The number of neurons in the hidden layer.
	 */
	public void addHiddenLayer(final int count) {
		throw new NeuralNetworkError("A NEAT network will evolve its hidden layers, do not specify any.");
	}

	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {		
	}

	/**
	 * Generate the RBF network.
	 * 
	 * @return The neural network.
	 */
	public BasicNetwork generate() {

		int y = PatternConst.START_Y;
		final BasicLayer inputLayer = new BasicLayer(new ActivationLinear(),
				false, this.inputNeurons);
		inputLayer.setX(PatternConst.START_X);
		inputLayer.setY(y);
		y += PatternConst.INC_Y;
		final BasicLayer outputLayer = new BasicLayer(this.outputActivation, false, this.outputNeurons);
		outputLayer.setX(PatternConst.START_X);
		outputLayer.setY(y);
		final NEATSynapse synapse = new NEATSynapse(inputLayer, outputLayer,
				this.neurons, this.neatActivation, 0);
		synapse.setSnapshot(this.snapshot);
		inputLayer.addSynapse(synapse);
		final BasicNetwork network = new BasicNetwork();
		network.tagLayer(BasicNetwork.TAG_INPUT, inputLayer);
		network.tagLayer(BasicNetwork.TAG_OUTPUT, outputLayer);
		network.getStructure().finalizeStructure();
		
		return network;

	}

	/**
	 * Set the activation function to use on the output layer.
	 * 
	 * @param activation
	 *            The new activation function.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		this.outputActivation = activation;
	}
	
	/**
	 * Set the activation function to use on the NEAT neurons.
	 * 
	 * @param activation
	 *            The new activation function.
	 */
	public void setNEATActivationFunction(final ActivationFunction activation) {
		this.neatActivation = activation;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The number of output neurons.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public List<NEATNeuron> getNeurons() {
		return neurons;
	}
	
	
	
	
}
