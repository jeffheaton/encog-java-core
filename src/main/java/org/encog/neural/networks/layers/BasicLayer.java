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
package org.encog.neural.networks.layers;

import java.io.Serializable;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.flat.FlatLayer;
import org.encog.neural.networks.BasicNetwork;

/**
 * Basic functionality that most of the neural layers require. The basic layer
 * is often used by itself to implement forward or recurrent layers. Other layer
 * types are based on the basic layer as well.
 *
 * The following summarizes how basic layers calculate the output for a neural
 * network.
 *
 * Example of a simple XOR network.
 *
 * Input: BasicLayer: 2 Neurons, null biasWeights, null biasActivation
 *
 * Hidden: BasicLayer: 2 Neurons, 2 biasWeights, 1 biasActivation
 *
 * Output: BasicLayer: 1 Neuron, 1 biasWeights, 1 biasActivation
 *
 * Input1Output and Input2Output are both provided.
 *
 * Synapse 1: Input to Hidden Hidden1Activation = (Input1Output *
 * Input1->Hidden1Weight) + (Input2Output * Input2->Hidden1Weight) +
 * (HiddenBiasActivation * Hidden1BiasWeight)
 *
 * Hidden1Output = calculate(Hidden1Activation, HiddenActivationFunction)
 *
 * Hidden2Activation = (Input1Output * Input1->Hidden2Weight) + (Input2Output *
 * Input2->Hidden2Weight) + (HiddenBiasActivation * Hidden2BiasWeight)
 *
 * Hidden2Output = calculate(Hidden2Activation, HiddenActivationFunction)
 *
 * Synapse 2: Hidden to Output
 *
 * Output1Activation = (Hidden1Output * Hidden1->Output1Weight)
 * + (Hidden2Output *
 * Hidden2->Output1Weight) + (OutputBiasActivation * Output1BiasWeight)
 *
 * Output1Output = calculate(Output1Activation, OutputActivationFunction)
 *
 * @author jheaton
 */
public class BasicLayer extends FlatLayer implements Layer, Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5682296868750703898L;

	/**
	 * The network that this layer belongs to.
	 */
	private BasicNetwork network;

	/**
	 * Construct this layer with a non-default activation function, also
	 * determine if a bias is desired or not.
	 *
	 * @param activationFunction
	 *            The activation function to use.
	 * @param neuronCount
	 *            How many neurons in this layer.
	 * @param hasBias
	 *            True if this layer has a bias.
	 */
	public BasicLayer(final ActivationFunction activationFunction,
			final boolean hasBias, final int neuronCount) {
		
		super(activationFunction, neuronCount,
				hasBias?1.0:0.0);
	}

	/**
	 * Construct this layer with a sigmoid activation function.
	 *
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public BasicLayer(final int neuronCount) {
		this(new ActivationTANH(), true, neuronCount);
	}

	/**
	 * @return The network that owns this layer.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Set the network for this layer.
	 *
	 * @param network
	 *            The network for this layer.
	 */
	public void setNetwork(final BasicNetwork network) {
		this.network = network;
	}
	
	public int getNeuronCount() {
		return this.getCount();
	}

	@Override
	public ActivationFunction getActivationFunction() {
		return super.getActivation();
	}
}
