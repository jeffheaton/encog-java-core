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
import org.encog.mathutil.matrices.Matrix2D;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.WeightedSynapsePersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fully-connected weight based synapse. Inputs will be multiplied by the
 * weight matrix and presented to the layer.
 *
 * This synapse type is teachable.
 *
 * This is a "fully connected" synapse between two layers. If you would like
 * only some neurons to be connected to others, you should use the
 * PartialSynapse.
 *
 * @author jheaton
 *
 */
public class WeightedSynapse extends BasicSynapse {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -1667026867054695646L;

	/**
	 * The weight matrix.
	 */
	private Matrix matrix;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final transient Logger logger = LoggerFactory.getLogger(this
			.getClass());

	/**
	 * Simple default constructor.
	 */
	public WeightedSynapse() {

	}

	/**
	 * Construct a weighted synapse between the two layers.
	 *
	 * @param fromLayer
	 *            The starting layer.
	 * @param toLayer
	 *            The ending layer.
	 */
	public WeightedSynapse(final Layer fromLayer, final Layer toLayer) {
		setFromLayer(fromLayer);
		setToLayer(toLayer);
		this.matrix = new Matrix2D(getFromNeuronCount(), getToNeuronCount());
	}

	/**
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		final WeightedSynapse result = new WeightedSynapse();
		result.setMatrix(getMatrix().clone());
		return result;
	}

	/**
	 * Compute the weighted output from this synapse. Each neuron in the from
	 * layer has a weighted connection to each of the neurons in the next layer.
	 *
	 * @param input
	 *            The input from the synapse.
	 * @return The output from this synapse.
	 */
	public NeuralData compute(final NeuralData input) {
		final NeuralData result = new BasicNeuralData(getToNeuronCount());

		double[] inputArray = input.getData();
		double[] resultArray = result.getData();

		for (int i = 0; i < getToNeuronCount(); i++) {

			double sum = 0;
			for (int j = 0; j < inputArray.length; j++) {
				sum += inputArray[j] * matrix.get(j,i);
			}
			resultArray[i] = sum;
		}
		return result;
	}

	/**
	 * Return a persistor for this object.
	 *
	 * @return A new persistor.
	 */
	public Persistor createPersistor() {
		return new WeightedSynapsePersistor();
	}

	/**
	 * Get the weight matrix.
	 *
	 * @return The weight matrix.
	 */
	public Matrix getMatrix() {
		return this.matrix;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 *
	 * @return The size of the matrix.
	 */
	public int getMatrixSize() {
		if (this.matrix == null) {
			return 0;
		}
		return this.matrix.size();
	}

	/**
	 * @return The type of synapse this is.
	 */
	public SynapseType getType() {
		// TODO Auto-generated method stub
		return SynapseType.Weighted;
	}

	/**
	 * @return True, this is a teachable synapse type.
	 */
	public boolean isTeachable() {
		return true;
	}

	/**
	 * Assign a new weight matrix to this layer.
	 *
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		this.matrix = matrix;

	}

}
