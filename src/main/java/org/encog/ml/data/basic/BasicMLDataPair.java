/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.data.basic;

import java.io.Serializable;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;

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
public class BasicMLDataPair implements MLDataPair, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -9068229682273861359L;

	/**
	 * The the expected output from the neural network, or null for unsupervised
	 * training.
	 */
	private final MLData ideal;

	/**
	 * The training input to the neural network.
	 */
	private final MLData input;

	/**
	 * Construct the object with only input. If this constructor is used, then
	 * unsupervised training is being used.
	 *
	 * @param input
	 *            The input to the neural network.
	 */
	public BasicMLDataPair(final MLData input) {
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
	public BasicMLDataPair(final MLData input, final MLData ideal) {
		this.input = input;
		this.ideal = ideal;
	}

	/**
	 * Get the expected results. Returns null if this is unsupervised training.
	 *
	 * @return Returns the expected results, or null if unsupervised training.
	 */
	public MLData getIdeal() {
		return this.ideal;
	}

	/**
	 * Get the input data.
	 *
	 * @return The input data.
	 */
	public MLData getInput() {
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
	 * {@inheritDoc}
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
	public static MLDataPair createPair(final int inputSize, final int idealSize) {
		MLDataPair result;

		if (idealSize > 0) {
			result = new BasicMLDataPair(new BasicMLDataArray(inputSize),
					new BasicMLDataArray(idealSize));
		} else {
			result = new BasicMLDataPair(new BasicMLDataArray(inputSize));
		}

		return result;
	}

	@Override
	public double[] getIdealArray() {
		if( this.ideal==null )
			return null;
		return this.ideal.getData();
	}

	@Override
	public double[] getInputArray() {
		return this.input.getData();
	}

	@Override
	public void setIdealArray(double[] data) {
		this.ideal.setData(data);

	}

	@Override
	public void setInputArray(double[] data) {
		this.input.setData(data);
	}

}
