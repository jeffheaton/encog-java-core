/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

import org.encog.matrix.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.WeightlessSynapsePersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fully connected synapse that simply sums all input to each neuron, no
 * weights are applied.
 * 
 * This synapse type is not teachable.
 * 
 * @author jheaton
 * 
 */
public class WeightlessSynapse extends BasicSynapse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1899517385166651263L;
	
	/**
	 * The logging object.
	 */
	private final transient Logger logger = 
		LoggerFactory.getLogger(this.getClass());

	/**
	 * Simple default constructor.
	 */
	public WeightlessSynapse() {
	}

	/**
	 * Construct a weighted synapse between the two layers.
	 * @param fromLayer The starting layer.
	 * @param toLayer The ending layer.
	 */
	public WeightlessSynapse(final Layer fromLayer, final Layer toLayer) {
		setFromLayer(fromLayer);
		setToLayer(toLayer);
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		final WeightlessSynapse result = new WeightlessSynapse();
		result.setMatrix(getMatrix().clone());
		return result;
	}

	/**
	 * Compute the weightless output from this synapse. Each neuron
	 * in the from layer has a weightless connection to each of the
	 * neurons in the next layer. 
	 * @param input The input from the synapse.
	 * @return The output from this synapse.
	 */
	public NeuralData compute(final NeuralData input) {
		final NeuralData result = new BasicNeuralData(getToNeuronCount());
		// just sum the input
		double sum = 0;
		for (int i = 0; i < input.size(); i++) {
			sum += input.getData(i);
		}

		for (int i = 0; i < getToNeuronCount(); i++) {
			result.setData(i, sum);
		}
		return result;
	}

	/**
	 * Return a persistor for this object.
	 * @return A new persistor.
	 */
	public Persistor createPersistor() {
		return new WeightlessSynapsePersistor();
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
		return SynapseType.Weighted;
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
		final String str = "Can't set the matrix for a WeightlessSynapse";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new NeuralNetworkError(str);
	}

}
