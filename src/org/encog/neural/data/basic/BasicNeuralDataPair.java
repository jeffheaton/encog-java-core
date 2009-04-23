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
package org.encog.neural.data.basic;

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
public class BasicNeuralDataPair implements NeuralDataPair {

	/**
	 * The the expected output from the neural network, or null for unsupervised
	 * training.
	 */
	private NeuralData ideal;

	/**
	 * The training input to the neural network.
	 */
	private final NeuralData input;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the object with only input. If this constructor is used, then
	 * unsupervised training is being used.
	 * 
	 * @param input
	 *            The input to the neural network.
	 */
	public BasicNeuralDataPair(final NeuralData input) {
		this.input = input;
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
	public String toString() {
		final StringBuilder builder = new StringBuilder("[NeuralDataPair:");
		builder.append("Input:");
		builder.append(getInput());
		builder.append("Ideal:");
		builder.append(getIdeal());
		builder.append("]");
		return builder.toString();
	}

}
