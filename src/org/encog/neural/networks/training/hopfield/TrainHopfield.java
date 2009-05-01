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
package org.encog.neural.networks.training.hopfield;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to train a Hopfield neural network. A hopfield neural
 * network can be created by using the basic layer and connecting it to itself,
 * forming a single layer recurrent neural network.
 * 
 * This is an unsupervised training algorithm.  Ideal values should not
 * be specified in the training set.  If ideal values are present, they
 * will be ignored.
 * 
 * @author jheaton
 * 
 */
public class TrainHopfield extends BasicTraining {

	/**
	 * The network being trained.
	 */
	private final BasicNetwork network;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a Hopfield training class.
	 * @param trainingSet The training set to use.
	 * @param network The network to train.
	 */
	public TrainHopfield(final NeuralDataSet trainingSet,
			final BasicNetwork network) {
		this.network = network;
		setTraining(trainingSet);
		setError(0);
	}

	/**
	 * Update the Hopfield weights after training.
	 * @param target The target synapse.
	 * @param delta The amoun to change the weights by.
	 */
	private void convertHopfieldMatrix(final Synapse target, 
			final Matrix delta) {
		// add the new weight matrix to what is there already
		for (int row = 0; row < delta.getRows(); row++) {
			for (int col = 0; col < delta.getRows(); col++) {
				target.getMatrix().set(row, col, delta.get(row, col));
			}
		}
	}

	/**
	 * @return The network being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Performing Hopfield iteration.");
		}

		preIteration();

		for (final Layer layer : this.network.getStructure().getLayers()) {
			for (final Synapse synapse : layer.getNext()) {
				if (synapse.isSelfConnected()) {
					trainHopfieldSynapse(synapse);
				}
			}
		}

		postIteration();
	}

	/**
	 * Once the hopfield synapse has been found, this method is called
	 * to train it.
	 * @param recurrent The hopfield layer.
	 */
	private void trainHopfieldSynapse(final Synapse recurrent) {
		for (final NeuralDataPair data : getTraining()) {
			trainHopfieldSynapse(recurrent, data.getInput());
		}

	}

	/**
	 * Train the neural network for the specified pattern. The neural network
	 * can be trained for more than one pattern. To do this simply call the
	 * train method more than once.
	 * 
	 * @param synapse
	 *            The synapse to train.
	 * @param pattern
	 *            The pattern to train for.
	 */
	public void trainHopfieldSynapse(final Synapse synapse,
			final NeuralData pattern) {

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
		convertHopfieldMatrix(synapse, m4);
	}
}
