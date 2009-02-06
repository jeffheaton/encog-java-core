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

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.HopfieldLayerPersistor;

/**
 * HopfieldLayer: This class implements a Hopfield neural network. A Hopfield
 * neural network is fully connected and consists of a single layer. Hopfield
 * neural networks are usually used for pattern recognition.
 */
public class HopfieldLayer extends BasicLayer implements EncogPersistedObject {

	/**
	 * This serial id.
	 */
	private static final long serialVersionUID = -668012704280836167L;

	/**
	 * Construct a hopfield layer of the specified size.
	 * 
	 * @param size
	 *            The number of neurons in this layer.
	 */
	public HopfieldLayer(final int size) {
		super(size);
		this.setFire(new BiPolarNeuralData(size));
		setMatrix(new Matrix(size, size));
	}

	/**
	 * Present a pattern to the neural network and receive the result.
	 * 
	 * @param pattern
	 *            The pattern to be presented to the neural network.
	 * @return The output from the neural network.
	 */
	public NeuralData compute(final NeuralData pattern) {

		// convert the input pattern into a matrix with a single row.
		// also convert the boolean values to bipolar(-1=false, 1=true)
		final Matrix inputMatrix = Matrix.createRowMatrix(pattern.getData());

		// Process each value in the pattern
		for (int col = 0; col < pattern.size(); col++) {
			Matrix columnMatrix = getMatrix().getCol(col);
			columnMatrix = MatrixMath.transpose(columnMatrix);

			// The output for this input element is the dot product of the
			// input matrix and one column from the weight matrix.
			final double dotProduct = MatrixMath.dotProduct(inputMatrix,
					columnMatrix);

			// Convert the dot product to either true or false.
			if (dotProduct > 0) {
				this.getFire().setData(col, true);
			} else {
				this.getFire().setData(col, false);
			}
		}

		return this.getFire();
	}

	/**
	 * Create a persistor for this layer.
	 * @return A persistor.
	 */
	public Persistor createPersistor() {
		return new HopfieldLayerPersistor();
	}

	/**
	 * @return Get the fire data.
	 */
	@Override
	public BiPolarNeuralData getFire() {
		return (BiPolarNeuralData) super.getFire();
	}

	/**
	 * Set the neuron count for this layer.
	 * 
	 * @param count
	 *            The neuron count.
	 */
	public void setNeuronCount(final int count) {
		this.setFire(new BiPolarNeuralData(count));
		setMatrix(new Matrix(count, count));
	}
}