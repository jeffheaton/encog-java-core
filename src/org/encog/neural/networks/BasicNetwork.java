/*
 * Encog Artificial Intelligence Framework v1.x
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.encog.matrix.MatrixCODEC;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.ErrorCalculation;

/**
 * BasicNetwork: This class implements a neural network. This class works in
 * conjunction the Layer classes. Layers are added to the BasicNetwork to
 * specify the structure of the neural network.
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
	 * The description of this object.
	 */
	private String description;

	/**
	 * The name of this object.
	 */
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
		if( this.inputLayer==null)
			this.outputLayer = this.inputLayer = layer;
		else
		{
			this.outputLayer.addNext(layer);
			this.outputLayer = layer;
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

		for (final NeuralDataPair pair : data) {
			NeuralData actual = compute(pair.getInput());
			errorCalculation.updateError(actual, pair
					.getIdeal());
		}
		return errorCalculation.calculateRMS();
	}

	/**
	 * Calculate the total number of neurons in the network across all layers.
	 * 
	 * @return The neuron count.
	 */
	public int calculateNeuronCount() {
		int result = 0;
		for(Layer layer: this.getLayers())
		{
			result+=layer.getNeuronCount();
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
		return null;
	}

	public void checkInputSize(final NeuralData input)
	{
		if (input.size() != this.inputLayer.getNeuronCount()) {
			throw new NeuralNetworkError(
					"Size mismatch: Can't compute outputs for input size="
							+ input.size() + " for input layer size="
							+ this.inputLayer.getNeuronCount());
		}		
	}
	

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input provide to the neural network.
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input) {
		Map<Layer,NeuralData> result = new HashMap<Layer,NeuralData>();
		checkInputSize(input);
		compute(result,this.inputLayer,input);
		return result.get(this.outputLayer);
		
	}
	
	public void compute(Map<Layer,NeuralData> result, Layer layer, NeuralData input)
	{
		NeuralData currentPattern = input;
		
		if( layer.getNextLayer()==null )
		{
			//result.put(layer, currentPattern);
			return;
		}
		else
		{
			currentPattern = layer.compute(currentPattern);
			for(Layer nextLayer: layer.getNextLayers() )
			{
				result.put(nextLayer, currentPattern);
				compute(result, nextLayer,currentPattern);
			}
		}
	}

	/**
	 * Create a persistor for this object.
	 * @return The newly created persistor.
	 */
	public Persistor createPersistor() {
		return null;
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
		/*
		final Iterator<Layer> otherLayers = other.getLayers().iterator();

		for (final Layer layer : getLayers()) {
			final Layer otherLayer = otherLayers.next();

			if (layer.getNeuronCount() != otherLayer.getNeuronCount()) {
				return false;
			}

			// make sure they either both have or do not have
			// a weight matrix.
			if (layer.getSynapse().getMatrix() == null && otherLayer.getSynapse().getMatrix() != null) {
				return false;
			}

			if (layer.getSynapse().getMatrix() != null && otherLayer.getSynapse().getMatrix() == null) {
				return false;
			}

			// if they both have a matrix, then compare the matrices
			if (layer.getSynapse().getMatrix() != null && otherLayer.getSynapse().getMatrix() != null) {
				if (!layer.getSynapse().getMatrix().equals(otherLayer.getSynapse().getMatrix())) {
					return false;
				}
			}
		}*/

		return true;
	}

	/**
	 * @return The description for this object.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the count for how many hidden layers are present.
	 * 
	 * @return The hidden layer count.
	 */
	public int getHiddenLayerCount() {
		int result = this.calculateNeuronCount() - 2;
		if( result<0 )
			return 0;
		else
			return result;
	}

	/**
	 * Get a collection of the hidden layers in the network.
	 * 
	 * @return The hidden layers.
	 */
	public Collection<Layer> getHiddenLayers() {
		final Collection<Layer> result = new ArrayList<Layer>();
		
		for(Layer layer: getLayers() )
		{
			if( isHidden(layer) )
			{
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
	 * @return the name
	 */
	public String getName() {
		return this.name;
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
		for (final Layer layer : this.getLayers() ) {
			result += layer.getNextTemp().getMatrixSize();
		}
		return result;
	}

	/**
	 * Generate a hash code.
	 * 
	 * @return THe hash code.
	 */
	public int hashCode() {
		return super.hashCode();
	}



	/**
	 * Reset the weight matrix and the thresholds.
	 * 
	 * @throws MatrixException
	 */
	public void reset() {
		for (final Layer layer : this.getLayers()) {
			layer.reset();
		}
	}

	/**
	 * Set the description for this object.
	 * @param theDescription The description.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
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

		final NeuralData output = compute(input);

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
	
	public boolean isInput(Layer layer)
	{
		return this.inputLayer==layer;
	}
	
	public boolean isOutput(Layer layer)
	{
		return this.outputLayer==layer;
	}
	
	public boolean isHidden(Layer layer)
	{
		return !isInput(layer) && !isOutput(layer);
	}
	
	/**
	 * Prune one of the neurons from this layer. Remove all entries in this
	 * weight matrix and other layers.
	 * 
	 * @param neuron
	 *            The neuron to prune. Zero specifies the first neuron.
	 */
	public void prune(final Layer targetLayer, final int neuron) {
		// delete a row on this matrix
		if (targetLayer.getNextTemp().getMatrix() != null) {
			targetLayer.getNextTemp().setMatrix(MatrixMath.deleteRow(targetLayer.getNextTemp().getMatrix(), neuron));
		}

		// delete a column on the previous
		final Collection<Layer> previous = this.getPreviousLayers(targetLayer);
		
		for(Layer prevLayer: previous )
		{
		if (previous != null) {
			if (prevLayer.getNextTemp().getMatrix() != null) {
				prevLayer.getNextTemp().setMatrix(MatrixMath.deleteCol(prevLayer.getNextTemp().getMatrix(),
						neuron));
			}
		}
		}

	}
	
	public Collection<Layer> getLayers()
	{
		Set<Layer> map = new HashSet<Layer>();
		Layer current = this.inputLayer;
		while(current!=null)
		{
			if( current!=null )
			{
				map.add(current);
			}
			
			if( current.getNextTemp()!=null )
			{
				if( !map.contains(current.getNextTemp()))
					current = current.getNextTemp().getToLayer();
			}
			else
				current = null;
		}
		
		return map;
	}
	
	public Collection<Layer> getPreviousLayers(Layer targetLayer)
	{
		Collection<Layer> result = new HashSet<Layer>();
		for(Layer layer: this.getLayers())
		{
			if( layer.getNextTemp()!=null && layer.getNextTemp().getToLayer() == targetLayer )
			{
				result.add(layer);
			}
		}
		return result;
	}
}
