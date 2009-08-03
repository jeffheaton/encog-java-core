/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.layers;

import java.util.Collection;
import java.util.List;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.EncogPersistedObject;

/**
 * This interface defines all necessary methods for a neural network layer.
 * 
 * @author jheaton
 */
public interface Layer extends EncogPersistedObject {

	/**
	 * Add a layer to this layer.  The "next" layer being added will
	 * receive input from this layer.  You can also add a layer to
	 * itself, this will create a self-connected layer.  This method
	 * will create a weighted synapse connection between this layer
	 * and the next.
	 * @param next The layer that is to be added.
	 */
	void addNext(Layer next);

	/**
	 * Add a layer to this layer.  The "next" layer being added will
	 * receive input from this layer.  You can also add a layer to
	 * itself, this will create a self-connected layer.
	 * @param next The layer that is to be added.
	 * @param type The type of synapse to add.
	 */
	void addNext(Layer next, SynapseType type);

	/**
	 * This method adds a synapse to the neural network.  Usually
	 * you will want to use the addNext method rather than directly
	 * adding synapses.
	 * @param synapse The synapse to add.
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
	 * @return The neuron count.
	 */
	int getNeuronCount();

	/**
	 * Get a list of all of the outbound synapse connections from this
	 * layer.
	 * @return The outbound connections.
	 */
	List<Synapse> getNext();

	/**
	 * @return The outbound layers from this layer.
	 */
	Collection<Layer> getNextLayers();

	/**
	 * @return This layer's threshold values, if present, otherwise
	 * this function returns null.
	 */
	double[] getThreshold();

	/**
	 * Get an idividual threshold value.
	 * @param index The threshold value to get.
	 * @return The threshold value.
	 */
	double getThreshold(int index);

	/**
	 * @return The x-coordinate that this layer should be displayed
	 * at in a GUI.
	 */
	int getX();

	/**
	 * @return The y-coordinate that this layer should be displayed
	 * at in a GUI.
	 */
	int getY();

	/**
	 * @return True if this layer has threshold values.
	 */
	boolean hasThreshold();

	/**
	 * Determine if this layer is connected to another.
	 * @param layer The second layer, checked to see if it is connected
	 * to this layer.
	 * @return True if the two layers are connected.
	 */
	boolean isConnectedTo(Layer layer);

	/**
	 * Process the data before it is modified by this layer. This 
	 * method is useful for the context layer to remember the pattern
	 * it was presented with.
	 * @param pattern The pattern.
	 */
	void process(final NeuralData pattern);

	/**
	 * Called on recurrent layers to provide recurrent output.  This
	 * is where the context layer will return the patter that it 
	 * previously remembered.
	 * @return The recurrent output.
	 */
	NeuralData recur();

	/**
	 * Set the neuron count, this will NOT adjust the synapses, or thresholds
	 * other code must do that.
	 * 
	 * @param neuronCount
	 *            The new neuron count
	 */
	void setNeuronCount(int neuronCount);

	/**
	 * Set the threshold array for this layer.
	 * @param d The new threshold array.
	 */
	void setThreshold(double[] d);

	/**
	 * Set an individual threshold value.
	 * @param index The index of the threshold value.
	 * @param d The new threshold value.
	 */
	void setThreshold(int index, double d);

	/**
	 * Set the x coordinate. The x&y coordinates are used to display
	 * the level on a GUI.
	 * @param x The x-coordinate.
	 */
	void setX(int x);

	/**
	 * Set the y coordinate. The x&y coordinates are used to display
	 * the level on a GUI.
	 * @param y The y-coordinate.
	 */
	void setY(int y);

	/**
	 * Set a new activation function for this layer.
	 * @param activationFunction The new activation function.
	 */
	void setActivationFunction(ActivationFunction activationFunction);
}
