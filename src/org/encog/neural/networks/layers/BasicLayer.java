/*
  * Encog Neural Network and Bot Library for Java v1.x
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
package org.encog.neural.networks.layers;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.Layer;
import org.encog.neural.persist.EncogPersistedObject;

public class BasicLayer implements Layer, EncogPersistedObject {
	/**
	 * Results from the last time that the outputs were calculated for this
	 * layer.
	 */
	private NeuralData fire;

	/**
	 * The weight and threshold matrix.
	 */
	private Matrix matrix;

	/**
	 * The next layer in the neural network.
	 */
	private Layer next;

	/**
	 * The previous layer in the neural network.
	 */
	private Layer previous;
	

	public BasicLayer(final int neuronCount) {
		this.setFire( new BasicNeuralData(neuronCount) );
	}
	
	/**
	 * Get the output array from the last time that the output of this layer was
	 * calculated.
	 * 
	 * @return The output array.
	 */
	public NeuralData getFire() {
		return this.fire;
	}

	/**
	 * Get the output from an individual neuron.
	 * 
	 * @param index
	 *            The neuron specified.
	 * @return The output from the specified neuron.
	 */
	public double getFire(final int index) {
		return this.fire.getData(index);
	}

	/**
	 * Get the weight and threshold matrix.
	 * 
	 * @return The weight and threshold matrix.
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
		} else {
			return this.matrix.size();
		}
	}

	/**
	 * Get the neuron count for this layer.
	 * 
	 * @return the neuronCount
	 */
	public int getNeuronCount() {
		return this.fire.size();
	}

	/**
	 * @return the next layer.
	 */
	public Layer getNext() {
		return this.next;
	}

	/**
	 * @return the previous layer.
	 */
	public Layer getPrevious() {
		return this.previous;
	}

	/**
	 * Determine if this layer has a matrix.
	 * 
	 * @return True if this layer has a matrix.
	 */
	public boolean hasMatrix() {
		return this.matrix != null;
	}

	/**
	 * Determine if this is a hidden layer.
	 * 
	 * @return True if this is a hidden layer.
	 */
	public boolean isHidden() {
		return ((this.next != null) && (this.previous != null));
	}

	/**
	 * Determine if this is an input layer.
	 * 
	 * @return True if this is an input layer.
	 */
	public boolean isInput() {
		return (this.previous == null);
	}

	/**
	 * Determine if this is an output layer.
	 * 
	 * @return True if this is an output layer.
	 */
	public boolean isOutput() {
		return (this.next == null);
	}
	
	/**
	 * Set the next layer.
	 * 
	 * @param next
	 *            the next layer.
	 */
	public void setNext(final Layer next) {
		this.next = next;
	}

	/**
	 * Set the previous layer.
	 * 
	 * @param previous
	 *            the previous layer.
	 */
	public void setPrevious(final Layer previous) {
		this.previous = previous;
	}
	
	/**
	 * Set the last output value for the specified neuron.
	 * 
	 * @param index
	 *            The specified neuron.
	 * @param f
	 *            The fire value for the specified neuron.
	 */
	public void setFire(final int index, final double f) {
		this.getFire().setData(index, f);
	}

	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		this.matrix = matrix;

	}
	
	/**
	 * Reset the weight matrix and threshold values to random numbers between -1
	 * and 1.
	 */
	public void reset() {

		if (this.getMatrix() != null) {
			this.getMatrix().ramdomize(-1, 1);

		}

	}

	public void setFire(NeuralData fire) {
		this.fire = fire;		
	}

	public NeuralData compute(NeuralData pattern) {
		return pattern;
	}

	public String getName() {
		return "BasicLayer";
	}

}
