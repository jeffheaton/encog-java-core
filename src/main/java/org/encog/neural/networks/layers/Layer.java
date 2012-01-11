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

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;

/**
 * This interface defines all necessary methods for a neural network layer.
 * 
 * @author jheaton
 */
public interface Layer {

	/**
	 * @return The activation function used for this layer.
	 */
	ActivationFunction getActivationFunction();

	/**
	 * @return The network that this layer is attached to.
	 */
	BasicNetwork getNetwork();

	/**
	 * @return The neuron count.
	 */
	int getNeuronCount();

	/**
	 * @return True if this layer has a bias.
	 */
	boolean hasBias();

	/**
	 * Set the network that this layer belongs to.
	 * 
	 * @param network
	 *            The network.
	 */
	void setNetwork(BasicNetwork network);

	/**
	 * Most layer types will default this value to one. However, it is possible
	 * to use other values. This is the activation that will be passed over the
	 * bias weights to the inputs of this layer. See the Layer interface
	 * documentation for more information on how Encog handles bias values.
	 * 
	 * @param activation
	 *            The activation for the bias weights.
	 */
	void setBiasActivation(double activation);

	/**
	 * Most layer types will default this value to one. However, it is possible
	 * to use other values. This is the activation that will be passed over the
	 * bias weights to the inputs of this layer. See the Layer interface
	 * documentation for more information on how Encog handles bias values.
	 * 
	 * @return The bias activation for this layer.
	 */
	double getBiasActivation();

	/**
	 * Set the activation function.
	 * @param activation The activation function.
	 */
	void setActivation(ActivationFunction activation);
}
