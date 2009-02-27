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
package org.encog.neural.networks.training.backpropagation;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.FeedforwardLayer;
import org.encog.util.BoundNumbers;

/**
 * BackpropagationLayer: The back propagation training algorithm requires
 * training data to be stored for each of the layers. The Backpropagation class
 * creates a BackpropagationLayer object for each of the layers in the neural
 * network that it is training.
 */

public class BackpropagationLayer {


	/**
	 * Accumulate the error deltas for each weight matrix and bias value.
	 */
	private Matrix accMatrixDelta;

	/**
	 * The bias values are stored in a "virtual row" just beyond the regular
	 * weight rows. This variable holds the index to that location.
	 */
	private int biasRow;

	/**
	 * Hold the previous matrix deltas so that "momentum" can be implemented.
	 * This handles both weights and thresholds.
	 */
	private Matrix matrixDelta;

	/**
	 * The parent object.
	 */
	private final Backpropagation backpropagation;

	/**
	 * The actual layer that this training layer corresponds to.
	 */
	private final FeedforwardLayer layer;
	
	private NeuralData lastOutput;

	/**
	 * Construct a BackpropagationLayer object that corresponds to a specific
	 * neuron layer.
	 * 
	 * @param backpropagation
	 *            The back propagation training object.
	 * @param layer
	 *            The layer that this object corresponds to.
	 */
	public BackpropagationLayer(final Backpropagation backpropagation,
			final FeedforwardLayer layer) {
		this.backpropagation = backpropagation;
		this.layer = layer;

		if (layer.getNext() != null) {
			this.accMatrixDelta = new Matrix(layer.getNeuronCount() + 1, layer
					.getNext().getToNeuronCount());
			this.matrixDelta = new Matrix(layer.getNeuronCount() + 1, layer
					.getNext().getToNeuronCount());
			this.biasRow = layer.getNeuronCount();
		}
	}

	/**
	 * Accumulate a matrix delta.
	 * 
	 * @param i1
	 *            The matrix row.
	 * @param i2
	 *            The matrix column.
	 * @param value
	 *            The delta value.
	 */
	public void accumulateMatrixDelta(final int i1, final int i2,
			final double value) {
		this.accMatrixDelta.add(i1, i2, value);
	}

	/**
	 * Accumulate a threshold delta.
	 * 
	 * @param index
	 *            The threshold index.
	 * @param value
	 *            The threshold value.
	 */
	public void accumulateThresholdDelta(final int index, final double value) {
		this.accMatrixDelta.add(this.biasRow, index, value);
	}

	/**
	 * Calculate the current error.
	 */
	public double []calcError(double[] lastDeltas, boolean hidden) {
		
		double[] thisDeltas = new double[layer.getNeuronCount()];
		double[] error = new double[this.layer.getNeuronCount()];
		
		final BackpropagationLayer next = this.backpropagation
				.getBackpropagationLayer(this.layer.getNext().getToLayer());

		for (int i = 0; i < this.layer.getNext().getToNeuronCount(); i++) {
			for (int j = 0; j < this.layer.getNext().getFromNeuronCount(); j++) {
				accumulateMatrixDelta(j, i, lastDeltas[i]
						* this.lastOutput.getData(j));
				
				error[j]+=(this.layer.getNext().getMatrix().get(j, i)
						* lastDeltas[i]);
			}
			accumulateThresholdDelta(i, lastDeltas[i]);
		}

		if (hidden) {
			NeuralData actual = this.lastOutput;
			// hidden layer deltas
			for (int i = 0; i < this.layer.getNeuronCount(); i++) {
				thisDeltas[i] = actual.getData(i);
			}
			
			this.layer.getActivationFunction().derivativeFunction(thisDeltas);	
			
			for (int i = 0; i < this.layer.getNeuronCount(); i++) {
				thisDeltas[i] *= error[i];
			}
		}
		return thisDeltas;
	}

	

	/**
	 * Learn from the last error calculation.
	 * 
	 * @param learnRate
	 *            The learning rate.
	 * @param momentum
	 *            The momentum.
	 */
	public void learn(final double learnRate, final double momentum) {
		// process the matrix
		if (this.layer.getNext()!=null ) {

			final Matrix m1 = MatrixMath.multiply(this.accMatrixDelta,
					learnRate);
			final Matrix m2 = MatrixMath.multiply(this.matrixDelta, momentum);
			this.matrixDelta = MatrixMath.add(m1, m2);
			this.layer.getNext().getMatrix().add(this.matrixDelta);			
			this.accMatrixDelta.clear();
		}
	}

	public NeuralData getLastOutput() {
		return lastOutput;
	}

	public void setLastOutput(NeuralData lastOutput) {
		this.lastOutput = lastOutput;
	}
}
