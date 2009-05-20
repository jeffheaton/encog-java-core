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
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.util.NormalizeInput;
import org.encog.util.NormalizeInput.NormalizationType;

/**
 * A synapse that normalizes data. This is usually used with a self organizing
 * map (SOM).
 * 
 * @author jheaton
 * 
 */
public class NormalizeSynapse extends WeightedSynapse {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 5358640740526459614L;

	/**
	 * The normalization type.
	 */
	private NormalizationType normalizationType = NormalizationType.Z_AXIS;

	/**
	 * Construct a normalized synapse between the two layers.
	 * 
	 * @param fromLayer
	 *            The starting layer.
	 * @param toLayer
	 *            The ending layer.
	 */
	public NormalizeSynapse(final Layer fromLayer, final Layer toLayer) {
		setFromLayer(fromLayer);
		setToLayer(toLayer);
		setMatrix(new Matrix(getToNeuronCount(),getFromNeuronCount() + 1));
	}

	/**
	 * Compute the weighted output from this synapse. Each neuron in the from
	 * layer has a weighted connection to each of the neurons in the next layer.
	 * 
	 * @param input
	 *            The input from the synapse.
	 * @return The output from this synapse.
	 */
	@Override
	public NeuralData compute(final NeuralData input) {
		
		final NormalizeInput norm = new NormalizeInput(input,
				this.normalizationType);

		final NeuralData output = new BasicNeuralData(this.getToNeuronCount());

		for (int i = 0; i < this.getToNeuronCount(); i++) {
			final Matrix optr = getMatrix().getRow(i);
			output.setData(i, MatrixMath.dotProduct(norm.getInputMatrix(),
					optr)
					* norm.getNormfac());

			final double d = (output.getData(i) + 1.0) / 2.0;

			if (d < 0) {
				output.setData(i, 0.0);
			}

			if (d > 1) {
				output.setData(i, 1.0);
			}
		}

		return output;
	}

	/**
	 * @return The normalization type.
	 */
	public NormalizationType getNormalizationType() {
		return this.normalizationType;
	}

	/**
	 * Set the normalization type.
	 * 
	 * @param normalizationType
	 *            The normalization type.
	 */
	public void setNormalizationType(final NormalizationType normalizationType) {
		this.normalizationType = normalizationType;
	}

}
