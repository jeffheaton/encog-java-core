/*
  * Encog Neural Network and Bot Library for Java v0.5
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.feedforward;

import java.io.Serializable;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;


/**
 * FeedforwardLayer: This class represents one layer in a 
 * feed forward neural network.  This layer could be input,
 * output, or hidden, depending on its placement inside of
 * the FeedforwardNetwork class.  
 * 
 * An activation function can also be specified.  Usually
 * all layers in a neural network will use the same activation
 * function.  By default this class uses the sigmoid activation
 * function.
 */
public class FeedforwardLayer implements Serializable {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -3698708039331150031L;

	/**
	 * Results from the last time that the outputs were calculated for this
	 * layer.
	 */
	private double fire[];

	/**
	 * The weight and threshold matrix.
	 */
	private Matrix matrix;

	/**
	 * The next layer in the neural network.
	 */
	private FeedforwardLayer next;

	/**
	 * The previous layer in the neural network.
	 */
	private FeedforwardLayer previous;

	/**
	 * Which activation function to use for this layer.
	 */
	private final ActivationFunction activationFunction;

	/**
	 * Construct this layer with a non-default threshold function.
	 * @param thresholdFunction The threshold function to use.
	 * @param neuronCount How many neurons in this layer.
	 */
	public FeedforwardLayer(final ActivationFunction thresholdFunction,
			final int neuronCount) {
		this.fire = new double[neuronCount];
		this.activationFunction = thresholdFunction;
	}

	/**
	 * Construct this layer with a sigmoid threshold function.
	 * @param neuronCount How many neurons in this layer.
	 */
	public FeedforwardLayer(final int neuronCount) {
		this(new ActivationSigmoid(), neuronCount);
	}

	/**
	 * Clone the structure of this layer, but do not copy any matrix data.
	 * 
	 * @return The cloned layer.
	 */
	public FeedforwardLayer cloneStructure() {
		return new FeedforwardLayer(this.activationFunction,this.getNeuronCount());
	}

	/**
	 * Compute the outputs for this layer given the input pattern.
	 * The output is also stored in the fire instance variable.
	 * @param pattern The input pattern.
	 * @return The output from this layer.
	 */
	public double[] computeOutputs(final double pattern[]) {
		int i;
		if (pattern != null) {
			for (i = 0; i < getNeuronCount(); i++) {
				setFire(i, pattern[i]);
			}
		}

		final Matrix inputMatrix = createInputMatrix(this.fire);

		for (i = 0; i < this.next.getNeuronCount(); i++) {
			final Matrix col = this.matrix.getCol(i);
			final double sum = MatrixMath.dotProduct(col, inputMatrix);

			this.next.setFire(i, this.activationFunction.activationFunction(sum));
		}

		return this.fire;
	}

	/**
	 * Take a simple double array and turn it into a matrix that can be used to
	 * calculate the results of the input array. Also takes into account the
	 * threshold.
	 * 
	 * @param pattern
	 * @return A matrix that represents the input pattern.
	 */
	private Matrix createInputMatrix(final double pattern[]) {
		final Matrix result = new Matrix(1, pattern.length + 1);
		for (int i = 0; i < pattern.length; i++) {
			result.set(0, i, pattern[i]);
		}

		// add a "fake" first column to the input so that the threshold is
		// always multiplied by one, resulting in it just being added.
		result.set(0, pattern.length, 1);

		return result;
	}

	/**
	 * Get the output array from the last time that the output of this layer was
	 * calculated.
	 * 
	 * @return The output array.
	 */
	public double[] getFire() {
		return this.fire;
	}

	/**
	 * Get the output from an individual neuron.
	 * @param index The neuron specified.
	 * @return The output from the specified neuron.
	 */
	public double getFire(final int index) {
		return this.fire[index];
	}

	/**
	 * Get the weight and threshold matrix.
	 * @return The weight and threshold matrix.
	 */
	public Matrix getMatrix() {
		return this.matrix;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * @return The size of the matrix.
	 */
	public int getMatrixSize() {
		if (this.matrix == null) {
			return 0;
		} else {
			return this.matrix.size();
		}
	}

	/**
	 * Get the neuron count for this layer.
	 * @return the neuronCount
	 */
	public int getNeuronCount() {
		return this.fire.length;
	}

	/**
	 * @return the next layer.
	 */
	public FeedforwardLayer getNext() {
		return this.next;
	}

	/**
	 * @return the previous layer.
	 */
	public FeedforwardLayer getPrevious() {
		return this.previous;
	}

	/**
	 * Determine if this layer has a matrix.
	 * @return True if this layer has a matrix.
	 */
	public boolean hasMatrix() {
		return this.matrix != null;
	}

	/**
	 * Determine if this is a hidden layer.
	 * @return True if this is a hidden layer.
	 */
	public boolean isHidden() {
		return ((this.next != null) && (this.previous != null));
	}

	/**
	 * Determine if this is an input layer.
	 * @return True if this is an input layer.
	 */
	public boolean isInput() {
		return (this.previous == null);
	}

	/**
	 * Determine if this is an output layer.
	 * @return True if this is an output layer.
	 */
	public boolean isOutput() {
		return (this.next == null);
	}

	/**
	 * Prune one of the neurons from this layer. Remove all entries in this
	 * weight matrix and other layers.
	 * 
	 * @param neuron
	 *            The neuron to prune. Zero specifies the first neuron.
	 */
	public void prune(final int neuron) {
		// delete a row on this matrix
		if (this.matrix != null) {
			setMatrix(MatrixMath.deleteRow(this.matrix, neuron));
		}

		// delete a column on the previous
		final FeedforwardLayer previous = this.getPrevious();
		if (previous != null) {
			if (previous.getMatrix() != null) {
				previous.setMatrix(MatrixMath.deleteCol(previous.getMatrix(),
						neuron));
			}
		}

	}

	/**
	 * Reset the weight matrix and threshold values to random numbers between -1
	 * and 1.
	 */
	public void reset() {

		if (this.matrix != null) {
			this.matrix.ramdomize(-1, 1);

		}

	}

	/**
	 * Set the last output value for the specified neuron.
	 * @param index The specified neuron.
	 * @param f The fire value for the specified neuron.
	 */
	public void setFire(final int index, final double f) {
		this.fire[index] = f;
	}

	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * @param matrix The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		if (matrix.getRows() < 2) {
			throw new NeuralNetworkError(
					"Weight matrix includes threshold values, and must have at least 2 rows.");
		}
		if (matrix != null) {
			this.fire = new double[matrix.getRows() - 1];
		}
		this.matrix = matrix;

	}

	/**
	 * Set the next layer.
	 * @param next the next layer.
	 */
	public void setNext(final FeedforwardLayer next) {
		this.next = next;
		// add one to the neuron count to provide a threshold value in row 0
		this.matrix = new Matrix(this.getNeuronCount() + 1, next
				.getNeuronCount());
	}

	/**
	 * Set the previous layer.
	 * @param previous the previous layer.
	 */
	public void setPrevious(final FeedforwardLayer previous) {
		this.previous = previous;
	}

	/**
	 * Produce a string form of the layer.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[FeedforwardLayer: Neuron Count=");
		result.append(getNeuronCount());
		result.append("]");
		return result.toString();
	}

	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

}
