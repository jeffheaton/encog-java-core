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

package org.encog.neural.data.basic;

import java.io.Serializable;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic implementation of the NeuralDataPair interface. This implementation
 * simply holds and input and ideal NeuralData object.
 * 
 * For supervised training both input and ideal should be specified.
 * 
 * For unsupervised training the input property should be valid, however the
 * ideal property should contain null.
 * 
 * @author jheaton
 * 
 */
public class BasicNeuralDataPair implements NeuralDataPair, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -9068229682273861359L;

	/**
	 * The the expected output from the neural network, or null for unsupervised
	 * training.
	 */
	private final NeuralData ideal;

	/**
	 * The training input to the neural network.
	 */
	private final NeuralData input;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final transient Logger logger = LoggerFactory.getLogger(this
			.getClass());

	/**
	 * Construct the object with only input. If this constructor is used, then
	 * unsupervised training is being used.
	 * 
	 * @param input
	 *            The input to the neural network.
	 */
	public BasicNeuralDataPair(final NeuralData input) {
		this.input = input;
		this.ideal = null;
	}

	/**
	 * Construct a BasicNeuralDataPair class with the specified input and ideal
	 * values.
	 * 
	 * @param input
	 *            The input to the neural network.
	 * @param ideal
	 *            The expected results from the neural network.
	 */
	public BasicNeuralDataPair(final NeuralData input, final NeuralData ideal) {
		this.input = input;
		this.ideal = ideal;
	}

	/**
	 * Get the expected results. Returns null if this is unsupervised training.
	 * 
	 * @return Returns the expected results, or null if unsupervised training.
	 */
	public NeuralData getIdeal() {
		return this.ideal;
	}

	/**
	 * Get the input data.
	 * 
	 * @return The input data.
	 */
	public NeuralData getInput() {
		return this.input;
	}

	/**
	 * Determine if this data pair is supervised.
	 * 
	 * @return True if this data pair is supervised.
	 */
	public boolean isSupervised() {
		return this.ideal != null;
	}

	/**
	 * Convert the object to a string.
	 * 
	 * @return The object as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("[NeuralDataPair:");
		builder.append("Input:");
		builder.append(getInput());
		builder.append("Ideal:");
		builder.append(getIdeal());
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Create a new neural data pair object of the correct size for the neural
	 * network that is being trained. This object will be passed to the getPair
	 * method to allow the neural data pair objects to be copied to it.
	 * @param inputSize The size of the input data.
	 * @param idealSize The size of the ideal data.
	 * @return A new neural data pair object.
	 */
	public static NeuralDataPair createPair(final int inputSize, final int idealSize) {
		NeuralDataPair result;

		if (idealSize > 0) {
			result = new BasicNeuralDataPair(new BasicNeuralData(inputSize),
					new BasicNeuralData(idealSize));
		} else {
			result = new BasicNeuralDataPair(new BasicNeuralData(inputSize));
		}

		return result;
	}

	@Override
	public boolean[] defined() {
		return null;
	}

	@Override
	public double[] getIdealArray() {
		return this.ideal.getData();
	}

	@Override
	public double[] getInputArray() {
		return this.input.getData();
	}

	@Override
	public void setIdeal(double[] data) {
		this.ideal.setData(data);
		
	}

	@Override
	public void setInput(double[] data) {
		this.input.setData(data);		
	}

}
