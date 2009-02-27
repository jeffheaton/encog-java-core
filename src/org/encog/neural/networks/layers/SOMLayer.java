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
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.Layer;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.SOMLayerPersistor;
import org.encog.util.NormalizeInput;
import org.encog.util.NormalizeInput.NormalizationType;

/**
 * SelfOrganizingMap: The Self Organizing Map, or Kohonen Neural Network, is a
 * special type of neural network that is used to classify input into groups.
 * The SOM makes use of unsupervised training.
 */
public class SOMLayer extends BasicLayer implements Serializable,
		EncogPersistedObject {

	/**
	 * The serial id for this class.
	 */
	private static final long serialVersionUID = -3514494417789856185L;

	/**
	 * Do not allow patterns to go below this very small number.
	 */
	public static final double VERYSMALL = 1.E-30;

	/**
	 * The normalization type.
	 */
	private NormalizationType normalizationType;

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of input neurons
	 * @param normalizationType
	 *            The normalization type to use.
	 */
	public SOMLayer(final int inputCount,
			final NormalizationType normalizationType) {

		super(inputCount);
		this.normalizationType = normalizationType;
	}

	/**
	 * Compute the output from this layer.
	 * 
	 * @param pattern
	 *            The pattern to compute for.
	 * @return The output from the layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		final NormalizeInput input = new NormalizeInput(pattern,
				this.normalizationType);

		final NeuralData output = new BasicNeuralData(getNext()
				.getToNeuronCount());

		for (int i = 0; i < getNext().getToNeuronCount(); i++) {
			final Matrix optr = getNext().getMatrix().getRow(i);
			output.setData(i, MatrixMath.dotProduct(input.getInputMatrix(),
					optr)
					* input.getNormfac());

			final double d = (output.getData(i) + 1.0) / 2.0;

			if (d < 0) {
				output.setData(i, 0.0);
			}

			if (d > 1) {
				output.setData(i, 1.0);
			}

			//getNext().setFire(i, output.getData(i));
		}

		return output;
	}

	/**
	 * Create a persistor.
	 * @return The newly created persistor.
	 */
	public Persistor createPersistor() {
		return new SOMLayerPersistor();
	}

	/**
	 * Get the normalization type.
	 * 
	 * @return The normalization type.
	 */
	public NormalizationType getNormalizationType() {
		return this.normalizationType;
	}



	/**
	 * Set the next layer.
	 * 
	 * @param next
	 *            the next layer.
	 */
	public void setNext(final Layer next) {
		Synapse synapse = new Synapse(this,next);
		super.setNext(synapse);
	}

	/**
	 * Set the normalization type.
	 * @param normalizationType
	 *            the normalizationType to set
	 */
	public void setNormalizationType(
			final NormalizationType normalizationType) {
		this.normalizationType = normalizationType;
	}

}