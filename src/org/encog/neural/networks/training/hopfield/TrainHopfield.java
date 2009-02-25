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
package org.encog.neural.networks.training.hopfield;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Layer;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.layers.HopfieldLayer;

/**
 * Train the hopfield layer.
 * @author jheaton
 */
public class TrainHopfield implements Train {

	/**
	 * The training set to use.
	 */
	private final NeuralDataSet trainingSet;
	
	/**
	 * The network to train.
	 */
	private final BasicNetwork network;

	/**
	 * Construct a trainer.
	 * @param pattern The pattern to train for.
	 * @param network The network to train.
	 */
	public TrainHopfield(final NeuralData pattern, final BasicNetwork network) {
		this.network = network;
		this.trainingSet = new BasicNeuralDataSet();
		this.trainingSet.add(new BasicNeuralDataPair(pattern));
	}

	/**
	 * Construct a trainer.
	 * @param trainingSet The training set to use.
	 * @param network The network to train.
	 */
	public TrainHopfield(final NeuralDataSet trainingSet,
			final BasicNetwork network) {
		this.trainingSet = trainingSet;
		this.network = network;
	}

	/**
	 * Not really used, for hopfield.
	 * @return Not used.
	 */
	public double getError() {
		return 0;
	}

	/**
	 * @return Get the network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Perform one iteration of training.
	 */
	public void iteration() {
		for (final Layer layer : this.network.getLayers()) {
			if (layer instanceof HopfieldLayer) {
				for (final NeuralDataPair pair : this.trainingSet) {
					trainHopfieldLayer((HopfieldLayer) layer, pair.getInput());
				}
			}
		}
	}

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param layer
	 *            The layer to train.
	 * @param pattern
	 * 		The pattern to train for.
	 */
	public void trainHopfieldLayer(final HopfieldLayer layer,
			final NeuralData pattern) {
		if (pattern.size() != layer.getSynapse().getMatrix().getRows()) {
			throw new NeuralNetworkError("Can't train a pattern of size "
					+ pattern.size() + " on a hopfield network of size "
					+ layer.getSynapse().getMatrix().getRows());
		}

		// Create a row matrix from the input, convert boolean to bipolar
		final Matrix m2 = Matrix.createRowMatrix(pattern.getData());
		// Transpose the matrix and multiply by the original input matrix
		final Matrix m1 = MatrixMath.transpose(m2);
		final Matrix m3 = MatrixMath.multiply(m1, m2);

		// matrix 3 should be square by now, so create an identity
		// matrix of the same size.
		final Matrix identity = MatrixMath.identity(m3.getRows());

		// subtract the identity matrix
		final Matrix m4 = MatrixMath.subtract(m3, identity);

		// now add the calculated matrix, for this pattern, to the
		// existing weight matrix.
		layer.getSynapse().setMatrix(MatrixMath.add(layer.getSynapse().getMatrix(), m4));
	}
}
