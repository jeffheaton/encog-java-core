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
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.OneToOneSynapsePersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A one-to-one synapse requires that the from and to layers have exactly the
 * same number of neurons. A one-to-one synapse can be useful, when used in
 * conjunction with a ContextLayer.
 * 
 * This synapse is not teachable.
 * 
 * @author jheaton
 * 
 */
public class OneToOneSynapse extends BasicSynapse {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -8098797476221631089L;
	
	/**
	 * The logging object.
	 */
	private final transient Logger logger = 
		LoggerFactory.getLogger(this.getClass());

	/**
	 * Simple default constructor.
	 */
	public OneToOneSynapse() {

	}

	/**
	 * Construct a one-to-one synapse between the two layers.
	 * @param fromLayer The starting layer.
	 * @param toLayer The ending layer.
	 */
	public OneToOneSynapse(final Layer fromLayer, final Layer toLayer) {
		if (fromLayer.getNeuronCount() != toLayer.getNeuronCount()) {
			final String str = 
				"From and to layers must have the same number of " 
				+ "neurons.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}

			throw new NeuralNetworkError(str);
		}
		setFromLayer(fromLayer);
		setToLayer(toLayer);
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		final OneToOneSynapse result = new OneToOneSynapse(getFromLayer(),
				getToLayer());
		return result;
	}

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output is the same as the input.
	 */
	public NeuralData compute(final NeuralData input) {
		return input;
	}

	/**
	 * @return null, this synapse type has no matrix.
	 */
	public Persistor createPersistor() {
		return new OneToOneSynapsePersistor();
	}

	/**
	 * @return null, this synapse type has no matrix.
	 */
	public Matrix getMatrix() {
		return null;
	}

	/**
	 * @return 0, this synapse type has no matrix.
	 */
	public int getMatrixSize() {
		return 0;
	}

	/**
	 * @return The type of synapse that this is.
	 */
	public SynapseType getType() {
		return SynapseType.OneToOne;
	}

	/**
	 * @return False, because this type of synapse is not teachable.
	 */
	public boolean isTeachable() {
		return false;
	}

	/**
	 * Attempt to set the matrix for this layer. This will throw an error,
	 * because this layer type does not have a matrix.
	 * 
	 * @param matrix
	 *            Not used.
	 */
	public void setMatrix(final Matrix matrix) {
		throw new NeuralNetworkError(
				"Can't set the matrix for a OneToOneSynapse");
	}

}
