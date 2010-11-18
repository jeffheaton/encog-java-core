/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.neural.networks.synapse;

import org.encog.mathutil.matrices.Matrix;
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
	 * Get the weight matrix.
	 *
	 * @return The weight matrix.
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
	 * Assign a new weight matrix to this layer.
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
