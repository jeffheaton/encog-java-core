/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.neural.som;

import java.io.Serializable;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.som.NormalizeInput.NormalizationType;


/**
 * SelfOrganizingMap: The Self Organizing Map, or Kohonen Neural Network, is a
 * special type of neural network that is used to classify input into groups.
 * The SOM makes use of unsupervised training.
 * 
 * @author Jeff Heaton
 * @version 2.1
 */
public class SelfOrganizingMap implements Serializable {

	/**
	 * The serial id for this class.
	 */
	private static final long serialVersionUID = -3514494417789856185L;

	/**
	 * Do not allow patterns to go below this very small number.
	 */
	public static final double VERYSMALL = 1.E-30;

	/**
	 * The weights of the output neurons base on the input from the input
	 * neurons.
	 */
	private Matrix outputWeights;

	/**
	 * Output neuron activations
	 */
	protected double output[];

	/**
	 * Number of input neurons
	 */
	protected int inputNeuronCount;

	/**
	 * Number of output neurons
	 */
	protected int outputNeuronCount;

	/**
	 * The normalization type.
	 */
	protected NormalizationType normalizationType;

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of input neurons
	 * @param outputCount
	 *            Number of output neurons
	 * @param normalizationType
	 *            The normalization type to use.
	 */
	public SelfOrganizingMap(final int inputCount, final int outputCount,
			final NormalizationType normalizationType) {

		this.inputNeuronCount = inputCount;
		this.outputNeuronCount = outputCount;
		this.outputWeights = new Matrix(this.outputNeuronCount,
				this.inputNeuronCount + 1);
		this.output = new double[this.outputNeuronCount];
		this.normalizationType = normalizationType;
	}

	/**
	 * Get the input neuron count.
	 * @return The input neuron count.
	 */
	public int getInputNeuronCount() {
		return this.inputNeuronCount;
	}

	/**
	 * Get the normalization type.
	 * @return The normalization type.
	 */
	public NormalizationType getNormalizationType() {
		return this.normalizationType;
	}

	/**
	 * Get the output neurons.
	 * @return The output neurons.
	 */
	public double[] getOutput() {
		return this.output;
	}

	/**
	 * Get the output neuron count.
	 * @return The output neuron count.
	 */
	public int getOutputNeuronCount() {
		return this.outputNeuronCount;
	}

	/**
	 * Get the output neuron weights.
	 * @return The output neuron weights.
	 */
	public Matrix getOutputWeights() {
		return this.outputWeights;
	}

	/**
	 * Set the output neuron weights.
	 * @param outputWeights The new output neuron weights.
	 */
	public void setOutputWeights(final Matrix outputWeights) {
		this.outputWeights = outputWeights;
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(final double input[]) {
		final NormalizeInput normalizedInput = new NormalizeInput(input,
				this.normalizationType);
		return winner(normalizedInput);
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * @param input The input pattern.
	 * @return The winning neuron.
	 */
	public int winner(final NormalizeInput input) {
		int win = 0;

		double biggest = Double.MIN_VALUE;
		for (int i = 0; i < this.outputNeuronCount; i++) {
			final Matrix optr = this.outputWeights.getRow(i);
			this.output[i] = MatrixMath
					.dotProduct(input.getInputMatrix(), optr)
					* input.getNormfac();
			
			this.output[i] = (this.output[i]+1.0)/2.0;

			if (this.output[i] > biggest) {
				biggest = this.output[i];
				win = i;
			}
			
			if( this.output[i] <0 ) {
				this.output[i]=0;
			}
			
			if( this.output[i]>1 ) {
				this.output[i]=1;
			}
		}

		return win;
	}

}