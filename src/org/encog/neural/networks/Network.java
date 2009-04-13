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
package org.encog.neural.networks;

import java.util.Collection;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * Interface that defines a neural network.
 * @author jheaton
 *
 */
public interface Network extends EncogPersistedObject {
	
	public void addLayer(final Layer layer, final SynapseType type);

	/**
	 * Add a layer to the neural network. The first layer added is the input
	 * layer, the last layer added is the output layer.
	 * 
	 * @param layer
	 *            The layer to be added.
	 */
	public void addLayer(final Layer layer);

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final NeuralDataSet data);


	/**
	 * Calculate the total number of neurons in the network across all layers.
	 * 
	 * @return The neuron count.
	 */
	public int calculateNeuronCount();

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * threshold values.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	public Object clone();

	public void setInputLayer(Layer input);


	public NeuralData compute(final NeuralData input);

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input provide to the neural network.
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input,
			NeuralOutputHolder useHolder);


	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	public Persistor createPersistor();

	/**
	 * Compare the two neural networks. For them to be equal they must be of the
	 * same structure, and have the same matrix values.
	 * 
	 * @param other
	 *            The other neural network.
	 * @return True if the two networks are equal.
	 */
	public boolean equals(final BasicNetwork other);

	public boolean equals(final BasicNetwork other, int precision);

	public boolean compareLayer(Layer layerThis, Layer layerOther, int precision);

	/**
	 * @return The description for this object.
	 */
	public String getDescription();

	/**
	 * Get the count for how many hidden layers are present.
	 * 
	 * @return The hidden layer count.
	 */
	public int getHiddenLayerCount();

	/**
	 * Get a collection of the hidden layers in the network.
	 * 
	 * @return The hidden layers.
	 */
	public Collection<Layer> getHiddenLayers();

	/**
	 * Get the input layer.
	 * 
	 * @return The input layer.
	 */
	public Layer getInputLayer();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * Get the output layer.
	 * 
	 * @return The output layer.
	 */
	public Layer getOutputLayer();

	/**
	 * Get the size of the weight and threshold matrix.
	 * 
	 * @return The size of the matrix.
	 */
	public int getWeightMatrixSize();

	/**
	 * Generate a hash code.
	 * 
	 * @return THe hash code.
	 */
	public int hashCode();

	/**
	 * Reset the weight matrix and the thresholds.
	 * 
	 */
	public void reset();

	/**
	 * Set the description for this object.
	 * 
	 * @param theDescription
	 *            The description.
	 */
	public void setDescription(final String theDescription);

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name);

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(final NeuralData input);

	public boolean isInput(Layer layer);

	public boolean isOutput(Layer layer);

	public boolean isHidden(Layer layer);

	/**
	 * Prune one of the neurons from this layer. Remove all entries in this
	 * weight matrix and other layers.
	 * 
	 * @param neuron
	 *            The neuron to prune. Zero specifies the first neuron.
	 */
	public void prune(final Layer targetLayer, final int neuron);


	/**
	 * @param outputLayer
	 *            the outputLayer to set
	 */
	public void setOutputLayer(Layer outputLayer);

	public NeuralStructure getStructure();
}
