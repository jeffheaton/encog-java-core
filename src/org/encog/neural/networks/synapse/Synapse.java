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
package org.encog.neural.networks.synapse;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.persist.EncogPersistedObject;

/**
 * A synapse is the connection between two layers of a neural network. The
 * various synapse types define how layers will interact with each other. Some
 * synapses contain a weight matrix, which cause them to be teachable. Others
 * simply feed the data between layers in various ways, and are not teachable.
 * 
 * @author jheaton
 * 
 */
public interface Synapse extends EncogPersistedObject {

	/**
	 * @return A clone of this object.
	 */
	Object clone();

	/**
	 * Compute the output from this synapse.
	 * @param input The input to this synapse.
	 * @return The output from this synapse.
	 */
	NeuralData compute(NeuralData input);

	
	/**
	 * @return The from layer.
	 */
	Layer getFromLayer();

	/**
	 * @return The neuron count from the "from layer".
	 */
	int getFromNeuronCount();

	/**
	 * Get the weight and threshold matrix.
	 * 
	 * @return The weight and threshold matrix.
	 */
	Matrix getMatrix();

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * 
	 * @return The size of the matrix.
	 */
	int getMatrixSize();

	/**
	 * @return The "to layer".
	 */
	Layer getToLayer();

	/**
	 * @return The neuron count from the "to layer".
	 */
	int getToNeuronCount();

	/**
	 * @return The type of synapse that this is.
	 */
	SynapseType getType();

	/**
	 * @return True if this is a self-connected synapse.  That is,
	 * the from and to layers are the same.
	 */
	boolean isSelfConnected();

	/**
	 * @return True if the weights for this synapse can be modified.
	 */
	boolean isTeachable();

	/**
	 * Set the from layer for this synapse.
	 * @param fromLayer The from layer for this synapse.
	 */
	void setFromLayer(Layer fromLayer);

	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	void setMatrix(final Matrix matrix);

	/**
	 * Set the target layer from this synapse.
	 * @param toLayer The target layer from this synapse.
	 */
	void setToLayer(Layer toLayer);

}
