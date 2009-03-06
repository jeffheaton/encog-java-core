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

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;

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
	
	private Synapse nextRecurrent;
	
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
	 * Construct this layer with a non-default threshold function.
	 * 
	 * @param thresholdFunction
	 *            The threshold function to use.
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public BasicLayer(final ActivationFunction thresholdFunction,
			final int neuronCount) {
		this.neuronCount = neuronCount;
		this.setActivationFunction( thresholdFunction );
	}

	/**
	 * Construct this layer with a sigmoid threshold function.
	 * 
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public BasicLayer(final int neuronCount) {
		this(new ActivationTANH(), neuronCount);
	}

	/**
	 * Compute the outputs for this layer given the input pattern. The output is
	 * also stored in the fire instance variable.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		
		// do we have a recurrent connection to ourself?
		if( this.getNextRecurrent()!=null )
		{
			NeuralData result = getNextRecurrent().compute(pattern);
			// apply the activation function
			this.getActivationFunction().activationFunction(result.getData());
			return result;
		}
		
		if( this.getNext()!=null )
		{
			NeuralData result = getNext().compute(pattern);
			
			// apply the activation function
			this.getActivationFunction().activationFunction(result.getData());
	
			return result;
		}
		else
		{
			// must be the output layer, so just return it
			return pattern;
		}
	}

	/**
	 * Create a persistor for this layer.
	 * @return The new persistor.
	 */
	public Persistor createPersistor() {
		return null;
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
		
		if( this.nextRecurrent!=null )
		{
			this.nextRecurrent.getMatrix().ramdomize(-1, 1);
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

	public void setNext(Layer next) {
		Synapse synapse = new WeightedSynapse(this,next);
		this.next = synapse;		
	}
	
	public void setNextRecurrant(Layer next) {
		Synapse synapse = new WeightedSynapse(this,next);
		this.nextRecurrent = synapse;
	}

	public Layer getNextLayer() {
		// get the next layer
		if( getNext()!=null )
		{
			return getNext().getToLayer();
		}
		else
			return null;
	}
	
	public Synapse getNextRecurrent()
	{
		return this.nextRecurrent;
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
