/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 *
 * Copyright 2008-2010 by Heaton Research Inc.
 *
 * Released under the LGPL.
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
 *
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 *
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks;

import org.encog.engine.EngineMachineLearning;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NeuralStructure;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * Interface that defines a neural network.
 *
 * @author jheaton
 *
 */
public interface Network extends EncogPersistedObject, EngineMachineLearning {

	/**
	 * Add a layer to the neural network. The first layer added is the input
	 * layer, the last layer added is the output layer. This layer is added with
	 * a weighted synapse.
	 *
	 * @param layer
	 *            The layer to be added.
	 */
	void addLayer(final Layer layer);

	/**
	 * Add a layer to the neural network. If there are no layers added this
	 * layer will become the input layer. This function automatically updates
	 * both the input and output layer references.
	 *
	 * @param layer
	 *            The layer to be added to the network.
	 * @param type
	 *            What sort of synapse should connect this layer to the last.
	 */
	void addLayer(final Layer layer, final SynapseType type);

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 *
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	double calculateError(final NeuralDataSet data);

	/**
	 * Calculate the total number of neurons in the network across all layers.
	 *
	 * @return The neuron count.
	 */
	int calculateNeuronCount();

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * bias values.
	 *
	 * @return A cloned copy of the neural network.
	 */
	Object clone();

	/**
	 * Compute the output for a given input to the neural network.
	 *
	 * @param input
	 *            The input to the neural network.
	 * @return The output from the neural network.
	 */
	NeuralData compute(final NeuralData input);

	/**
	 * Compute the output for a given input to the neural network. This method
	 * provides a parameter to specify an output holder to use. This holder
	 * allows propagation training to track the output from each layer. If you
	 * do not need this holder pass null, or use the other compare method.
	 *
	 * @param input
	 *            The input provide to the neural network.
	 * @param useHolder
	 *            Allows a holder to be specified, this allows propagation
	 *            training to check the output of each layer.
	 * @return The results from the output neurons.
	 */
	NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder);

	/**
	 * Create a persistor for this object.
	 *
	 * @return The newly created persistor.
	 */
	Persistor createPersistor();

	/**
	 * Compare the two neural networks. For them to be equal they must be of the
	 * same structure, and have the same matrix values.
	 *
	 * @param other
	 *            The other neural network.
	 * @return True if the two networks are equal.
	 */
	boolean equals(final BasicNetwork other);

	/**
	 * Determine if this neural network is equal to another. Equal neural
	 * networks have the same weight matrix and bias values, within a
	 * specified precision.
	 *
	 * @param other
	 *            The other neural network.
	 * @param precision
	 *            The number of decimal places to compare to.
	 * @return True if the two neural networks are equal.
	 */
	boolean equals(final BasicNetwork other, final int precision);

	/**
	 * @return The description for this object.
	 */
	String getDescription();

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @return Get the structure of the neural network. The structure allows you
	 *         to quickly obtain synapses and layers without traversing the
	 *         network.
	 */
	NeuralStructure getStructure();

	/**
	 * @return The size of the matrix.
	 */
	int getWeightMatrixSize();

	/**
	 * Generate a hash code.
	 *
	 * @return THe hash code.
	 */
	int hashCode();

	/**
	 * Reset the weight matrix and the bias values.
	 *
	 */
	void reset();

	/**
	 * Set the description for this object.
	 *
	 * @param theDescription
	 *            The description.
	 */
	void setDescription(final String theDescription);

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(final String name);

	/**
	 * {@inheritDoc}
	 */
	String toString();

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 *
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	int winner(final NeuralData input);
}
