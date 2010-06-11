/*
 * Encog(tm) Core v2.5 
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
