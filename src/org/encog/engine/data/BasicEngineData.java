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

package org.encog.engine.data;

import java.io.Serializable;

/**
 * A basic implementation of the EngineData interface. This implementation
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
public class BasicEngineData implements EngineData, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -9068229682273861359L;

	/**
	 * Create a new neural data pair object of the correct size for the neural
	 * network that is being trained. This object will be passed to the getPair
	 * method to allow the neural data pair objects to be copied to it.
	 * 
	 * @param inputSize
	 *            The size of the input data.
	 * @param idealSize
	 *            The size of the ideal data.
	 * @return A new neural data pair object.
	 */
	public static EngineData createPair(final int inputSize, 
			final int idealSize) {
		EngineData result;

		if (idealSize > 0) {
			result = new BasicEngineData(new double[inputSize],
					new double[idealSize]);
		} else {
			result = new BasicEngineData(new double[inputSize]);
		}

		return result;
	}

	/**
	 * The the expected output from the neural network, or null for unsupervised
	 * training.
	 */
	private double[] ideal;

	/**
	 * The training input to the neural network.
	 */
	private double[] input;

	/**
	 * Construct the object with only input. If this constructor is used, then
	 * unsupervised training is being used.
	 * 
	 * @param input
	 *            The input to the neural network.
	 */
	public BasicEngineData(final double[] input) {
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
	public BasicEngineData(final double[] input, final double[] ideal) {
		this.input = input;
		this.ideal = ideal;
	}

	/**
	 * Get the expected results. Returns null if this is unsupervised training.
	 * 
	 * @return Returns the expected results, or null if unsupervised training.
	 */
	public double[] getIdealArray() {
		return this.ideal;
	}

	/**
	 * Get the input data.
	 * 
	 * @return The input data.
	 */
	public double[] getInputArray() {
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
	 * Set the ideal array.
	 * @param data The ideal array.
	 */
	@Override
	public void setIdealArray(final double[] data) {
		this.ideal = data;

	}

	/**
	 * Set the input array.
	 * @param data The input array.
	 */
	@Override
	public void setInputArray(final double[] data) {
		this.input = data;
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
		builder.append(getInputArray());
		builder.append("Ideal:");
		builder.append(getIdealArray());
		builder.append("]");
		return builder.toString();
	}

}
