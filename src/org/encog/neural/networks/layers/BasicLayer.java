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
package org.encog.neural.networks.layers;

import java.io.Serializable;

import org.encog.matrix.Matrix;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.BasicLayerPersistor;

/**
 * Basic functionality that most of the neural layers require.
 * 
 * @author jheaton
 */
public class BasicLayer implements Layer, EncogPersistedObject, Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5682296868750703898L;	
	
	private Synapse next;
	
	/**
	 * Which activation function to use for this layer.
	 */
	private ActivationFunction activationFunction;


	/**
	 * The description for this object.
	 */
	private String description;

	/**
	 * The name for this object.
	 */
	private String name;
	
	private int neuronCount;

	/**
	 * Construct a basic layer with the specified neuron count.
	 * 
	 * @param neuronCount
	 *            How many neurons does this layer have.
	 */
	public BasicLayer(final int neuronCount) {
		this.neuronCount = neuronCount;
	}

	/**
	 * This layer is too basic to know how to compute a pattern, it simply
	 * passes the pattern on.
	 * 
	 * @param pattern
	 *            The pattern to compute against.
	 * @return The input pattern is returned.
	 */
	public NeuralData compute(final NeuralData pattern) {
		return pattern;
	}

	/**
	 * Create a persistor for this layer.
	 * @return The new persistor.
	 */
	public Persistor createPersistor() {
		return new BasicLayerPersistor();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the neuron count for this layer.
	 * 
	 * @return the neuronCount
	 */
	public int getNeuronCount() {
		return this.neuronCount;
	}

	/**
	 * Reset the weight matrix and threshold values to random numbers between -1
	 * and 1.
	 */
	public void reset() {

		if (this.next != null) {
			this.next.getMatrix().ramdomize(-1, 1);

		}

	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}



	public Synapse getNext() {
		return next;
	}

	public void setNext(Synapse synapse) {
		this.next = synapse;
	}

	@Override
	public void setNext(Layer next) {
		Synapse synapse = new WeightedSynapse(this,next);
		setNext(synapse);		
	}

	@Override
	public Layer getNextLayer() {
		// get the next layer
		if( getNext()!=null )
		{
			return getNext().getToLayer();
		}
		else
			return null;
	}

	
	/**
	 * @return The activation function for this layer.
	 */
	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

	/**
	 * Set the activation function for this layer.
	 * @param f The activation function.
	 */
	public void setActivationFunction(final ActivationFunction f) {
		this.activationFunction = f;
	}

}
