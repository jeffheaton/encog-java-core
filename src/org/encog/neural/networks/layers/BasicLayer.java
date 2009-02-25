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
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.Layer;
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

	/**
	 * The next layer in the neural network.
	 */
	private Layer next;
	
	private Synapse synapse;

	/**
	 * The description for this object.
	 */
	private String description;

	/**
	 * The name for this object.
	 */
	private String name;

	/**
	 * Construct a basic layer with the specified neuron count.
	 * 
	 * @param neuronCount
	 *            How many neurons does this layer have.
	 */
	public BasicLayer(final int neuronCount) {
		this.setSynapse(new Synapse(neuronCount,0));
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
		return this.synapse.getNeuronCount();
	}

	/**
	 * @return the next layer.
	 */
	public Layer getNext() {
		return this.next;
	}

	/**
	 * Reset the weight matrix and threshold values to random numbers between -1
	 * and 1.
	 */
	public void reset() {

		if (this.synapse.getMatrix() != null) {
			this.synapse.getMatrix().ramdomize(-1, 1);

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

	/**
	 * Set the next layer.
	 * 
	 * @param next
	 *            the next layer.
	 */
	public void setNext(final Layer next) {
		this.next = next;
	}

	public Synapse getSynapse() {
		return synapse;
	}

	public void setSynapse(Synapse synapse) {
		this.synapse = synapse;
	}
	
	

}
