/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.encog.matrix.MatrixCODEC;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.BasicNetworkPersistor;
import org.encog.util.ErrorCalculation;
import org.encog.neural.networks.layers.FeedforwardLayer;

/**
 * BasicNetwork: This class implements a neural
 * network. This class works in conjunction the Layer classes. Layers
 * are added to the BasicNetwork to specify the structure of the
 * neural network.
 * 
 * The first layer added is the input layer, the final layer added is the output
 * layer. Any layers added between these two layers are the hidden layers.
 */
public class BasicNetwork implements Serializable, Network,
		EncogPersistedObject {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -136440631687066461L;

	/**
	 * The input layer.
	 */
	private Layer inputLayer;

	/**
	 * The output layer.
	 */
	private Layer outputLayer;

	/**
	 * All of the layers in the neural network.
	 */
	private List<Layer> layers = new ArrayList<Layer>();
	
	private String description;
	
	private String name;

	/**
	 * Construct an empty neural network.
	 */
	public BasicNetwork() {
	}

	/**
	 * Add a layer to the neural network. The first layer added is the input
	 * layer, the last layer added is the output layer.
	 * 
	 * @param layer
	 *            The layer to be added.
	 */
	public void addLayer(final Layer layer) {
		// setup the forward and back pointer
		if (this.outputLayer != null) {
			layer.setPrevious(this.outputLayer);
			this.outputLayer.setNext(layer);
		}

		// update the inputLayer and outputLayer variables
		if (this.layers.size() == 0) {
			this.inputLayer = layer; 
			this.outputLayer = layer;
		} else {
			this.outputLayer = layer;
		}

		// add the new layer to the list
		this.layers.add(layer);
	}
	
	/**
	 * Add a layer after the base layer.
	 * @param baseLayer The layer to add after.
	 * @param newLayer The new layer to add.
	 */
	public void addLayer(final Layer baseLayer,final Layer newLayer)
	{
		int index = 0;
		while( index<layers.size() )
		{
			if( this.layers.get(index)==baseLayer )
				break;
			index++;
		}
		
		if( index==layers.size() )
		{
			throw new NeuralNetworkError("The specified base layer must be part of the network.");
		}
		
		Layer next = baseLayer.getNext();
		baseLayer.setNext(newLayer);
		newLayer.setPrevious(next);
		this.layers.add(index+1, newLayer);
		
	}
	
	/**
	 * Remove a layer, adjust the weight matrixes and back pointers.
	 * @param layer The layer to remove.
	 */
	public void removeLayer(final Layer layer) {
		Layer previous  = layer.getPrevious();
		Layer next = layer.getNext();
				
		this.layers.remove(layer);
		
		if( next!=null )
		{
			next.setPrevious(previous);
		}
		
		if( previous!=null )
		{
			previous.setNext(next);
		}
	}
	

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final NeuralDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		for (NeuralDataPair pair : data) {
			compute(pair.getInput());
			errorCalculation.updateError(this.outputLayer.getFire(), pair
					.getIdeal());
		}
		return (errorCalculation.calculateRMS());
	}

	/**
	 * Calculate the total number of neurons in the network across all layers.
	 * 
	 * @return The neuron count.
	 */
	public int calculateNeuronCount() {
		int result = 0;
		for (final Layer layer : this.layers) {
			result += layer.getNeuronCount();
		}
		return result;
	}

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * threshold values.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		final BasicNetwork result = cloneStructure();
		final Double[] copy = MatrixCODEC.networkToArray(this);
		MatrixCODEC.arrayToNetwork(copy, result);
		return result;
	}

	/**
	 * Return a clone of the structure of this neural network.
	 * 
	 * @return A cloned copy of the structure of the neural network.
	 */

	public BasicNetwork cloneStructure() {
		final BasicNetwork result = new BasicNetwork();

		for (final Layer layer : this.layers) {
			final Layer clonedLayer = new FeedforwardLayer(layer
					.getNeuronCount());
			result.addLayer(clonedLayer);
		}

		return result;
	}

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input provide to the neural network.
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input) {

		if (input.size() != this.inputLayer.getNeuronCount()) {
			throw new NeuralNetworkError(
					"Size mismatch: Can't compute outputs for input size="
							+ input.size() + " for input layer size="
							+ this.inputLayer.getNeuronCount());
		}

		for (final Layer layer : this.layers) {
			if (layer.isInput()) {
				layer.compute(input);
			} else if (layer.isHidden()) {
				layer.compute(null);
			}
		}

		return this.outputLayer.getFire();
	}

	/**
	 * Compare the two neural networks. For them to be equal they must be of the
	 * same structure, and have the same matrix values.
	 * 
	 * @param other
	 *            The other neural network.
	 * @return True if the two networks are equal.
	 */
	public boolean equals(final BasicNetwork other) {
		final Iterator<Layer> otherLayers = other.getLayers().iterator();

		for (final Layer layer : this.getLayers()) {
			final Layer otherLayer = otherLayers.next();

			if (layer.getNeuronCount() != otherLayer.getNeuronCount()) {
				return false;
			}

			// make sure they either both have or do not have
			// a weight matrix.
			if ((layer.getMatrix() == null) 
					&& (otherLayer.getMatrix() != null)) {
				return false;
			}

			if ((layer.getMatrix() != null) 
					&& (otherLayer.getMatrix() == null)) {
				return false;
			}

			// if they both have a matrix, then compare the matrices
			if ((layer.getMatrix() != null) 
					&& (otherLayer.getMatrix() != null)) {
				if (!layer.getMatrix().equals(otherLayer.getMatrix())) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Get the count for how many hidden layers are present.
	 * 
	 * @return The hidden layer count.
	 */
	public int getHiddenLayerCount() {
		return this.layers.size() - 2;
	}

	/**
	 * Get a collection of the hidden layers in the network.
	 * 
	 * @return The hidden layers.
	 */
	public Collection<Layer> getHiddenLayers() {
		final Collection<Layer> result = new ArrayList<Layer>();
		for (final Layer layer : this.layers) {
			if (layer.isHidden()) {
				result.add(layer);
			}
		}
		return result;
	}

	/**
	 * Get the input layer.
	 * 
	 * @return The input layer.
	 */
	public Layer getInputLayer() {
		return this.inputLayer;
	}

	/**
	 * Get all layers.
	 * 
	 * @return All layers.
	 */
	public List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * Get the output layer.
	 * 
	 * @return The output layer.
	 */
	public Layer getOutputLayer() {
		return this.outputLayer;
	}

	/**
	 * Get the size of the weight and threshold matrix.
	 * 
	 * @return The size of the matrix.
	 */
	public int getWeightMatrixSize() {
		int result = 0;
		for (final Layer layer : this.layers) {
			result += layer.getMatrixSize();
		}
		return result;
	}

	/**
	 * Reset the weight matrix and the thresholds.
	 * 
	 * @throws MatrixException
	 */
	public void reset() {
		for (final Layer layer : this.layers) {
			layer.reset();
		}
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(final NeuralData input) {

		NeuralData output = compute(input);

		int win = 0;

		double biggest = Double.MIN_VALUE;
		for (int i = 0; i < output.size(); i++) {

			if (output.getData(i) > biggest) {
				biggest = output.getData(i);
				win = i;
			}
		}

		return win;
	}
	
	/**
	 * Generate a hash code.
	 * @return THe hash code.
	 */
	public int hashCode() {
		return super.hashCode();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String theDescription) {
		this.description = theDescription;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public Persistor createPersistor() {
		return new BasicNetworkPersistor();
	}
	
	
}
