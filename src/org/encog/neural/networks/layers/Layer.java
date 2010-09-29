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

package org.encog.neural.networks.layers;

import java.util.Collection;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.EncogPersistedObject;

/**
 * This interface defines all necessary methods for a neural network layer.
 * 
 * @author jheaton
 */
public interface Layer extends EncogPersistedObject, Comparable<Layer> {

	/**
	 * Add a layer to this layer. The "next" layer being added will receive
	 * input from this layer. You can also add a layer to itself, this will
	 * create a self-connected layer. This method will create a weighted synapse
	 * connection between this layer and the next.
	 * 
	 * Layers can also have bias values attached. This makes up sort of "virtual
	 * layer" that connects to this layer. This allows you to specify a bias
	 * activation connected via bias weights to the neurons of this level. The
	 * bias weights and bias activations are set by the biasWeights and
	 * biasActivation properties.
	 * 
	 * @param next
	 *            The layer that is to be added.
	 */
	void addNext(Layer next);

	/**
	 * Add a layer to this layer. The "next" layer being added will receive
	 * input from this layer. You can also add a layer to itself, this will
	 * create a self-connected layer.
	 * 
	 * @param next
	 *            The layer that is to be added.
	 * @param type
	 *            The type of synapse to add.
	 */
	void addNext(Layer next, SynapseType type);

	/**
	 * This method adds a synapse to the neural network. Usually you will want
	 * to use the addNext method rather than directly adding synapses.
	 * 
	 * @param synapse
	 *            The synapse to add.
	 */
	void addSynapse(Synapse synapse);

	/**
	 * Compute the output for this layer.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	NeuralData compute(final NeuralData pattern);

	/**
	 * @return The activation function used for this layer.
	 */
	ActivationFunction getActivationFunction();

	/**
	 * @return The id for this layer.
	 */
	int getID();

	/**
	 * @return The network that this layer is attached to.
	 */
	BasicNetwork getNetwork();

	/**
	 * @return The neuron count.
	 */
	int getNeuronCount();

	/**
	 * Get a list of all of the outbound synapse connections from this layer.
	 * 
	 * @return The outbound connections.
	 */
	List<Synapse> getNext();

	/**
	 * @return The outbound layers from this layer.
	 */
	Collection<Layer> getNextLayers();

	/**
	 * @return The weights between this layer an an optional preceding bias
	 *         layer. Returns null, if there is no bias. See the Layer interface
	 *         documentation for more information on how Encog handles bias
	 *         values.
	 */
	double[] getBiasWeights();

	/**
	 * Get an bias weight value. See the Layer interface documentation for more
	 * information on how Encog handles bias values.
	 * 
	 * @param index
	 *            The bias value to get.
	 * @return The bias value.
	 */
	double getBiasWeight(int index);

	/**
	 * @return The x-coordinate that this layer should be displayed at in a GUI.
	 */
	int getX();

	/**
	 * @return The y-coordinate that this layer should be displayed at in a GUI.
	 */
	int getY();

	/**
	 * @return True if this layer has a bias.
	 */
	boolean hasBias();

	/**
	 * Determine if this layer is connected to another.
	 * 
	 * @param layer
	 *            The second layer, checked to see if it is connected to this
	 *            layer.
	 * @return True if the two layers are connected.
	 */
	boolean isConnectedTo(Layer layer);

	/**
	 * Process the data before it is modified by this layer. This method is
	 * useful for the context layer to remember the pattern it was presented
	 * with.
	 * 
	 * @param pattern
	 *            The pattern.
	 */
	void process(final NeuralData pattern);

	/**
	 * Called on recurrent layers to provide recurrent output. This is where the
	 * context layer will return the patter that it previously remembered.
	 * 
	 * @return The recurrent output.
	 */
	NeuralData recur();

	/**
	 * Set a new activation function for this layer.
	 * 
	 * @param activationFunction
	 *            The new activation function.
	 */
	void setActivationFunction(ActivationFunction activationFunction);

	/**
	 * Set the id for this layer.
	 * 
	 * @param id
	 *            The id for this layer.
	 */
	void setID(int id);

	/**
	 * Set the network that this layer belongs to.
	 * 
	 * @param network
	 *            The network.
	 */
	void setNetwork(BasicNetwork network);

	/**
	 * Set the neuron count, this will NOT adjust the synapses or bias weights
	 * other code must do that.
	 * 
	 * @param neuronCount
	 *            The new neuron count
	 */
	void setNeuronCount(int neuronCount);

	/**
	 * Set the bias weight array for this layer.
	 * 
	 * @param d
	 *            The new bias weight array.
	 */
	void setBiasWeights(double[] d);

	/**
	 * Set an individual bias weight value.
	 * 
	 * @param index
	 *            The index of the bias weight value.
	 * @param d
	 *            The new bias weight value.
	 */
	void setBiasWeight(int index, double d);

	/**
	 * Set the x coordinate. The x&y coordinates are used to display the level
	 * on a GUI.
	 * 
	 * @param x
	 *            The x-coordinate.
	 */
	void setX(int x);

	/**
	 * Set the y coordinate. The x&y coordinates are used to display the level
	 * on a GUI.
	 * 
	 * @param y
	 *            The y-coordinate.
	 */
	void setY(int y);

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
}
