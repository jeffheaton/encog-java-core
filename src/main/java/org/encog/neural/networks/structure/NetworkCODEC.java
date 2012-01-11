/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.structure;

import java.util.Arrays;

import org.encog.ml.MLEncodable;
import org.encog.ml.MLMethod;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;

/**
 * This class will extract the "long term memory" of a neural network, that is
 * the weights and bias values into an array. This array can be used to view the
 * neural network as a linear array of doubles. These values can then be
 * modified and copied back into the neural network. This is very useful for
 * simulated annealing, as well as genetic algorithms.
 * 
 * @author jheaton
 * 
 */
public final class NetworkCODEC {

	/**
	 * Error message.
	 */
	private final static String ERROR = "This machine learning method cannot be encoded:";

	/**
	 * Use an array to populate the memory of the neural network.
	 * 
	 * @param array
	 *            An array of doubles.
	 * @param network
	 *            The network to encode.
	 */
	public static void arrayToNetwork(final double[] array,
			final MLMethod network) {
		if (network instanceof MLEncodable) {
			((MLEncodable) network).decodeFromArray(array);
			return;
		}
		throw new NeuralNetworkError(NetworkCODEC.ERROR
				+ network.getClass().getName());
	}

	/**
	 * Determine if the two neural networks are equal. Uses exact precision
	 * required by Arrays.equals.
	 * 
	 * @param network1
	 *            The first network.
	 * @param network2
	 *            The second network.
	 * @return True if the two networks are equal.
	 */
	public static boolean equals(final BasicNetwork network1,
			final BasicNetwork network2) {
		final double[] array1 = NetworkCODEC.networkToArray(network1);
		final double[] array2 = NetworkCODEC.networkToArray(network2);

		if (array1.length != array2.length) {
			return false;
		}

		return Arrays.equals(array1, array2);
	}

	/**
	 * Determine if the two neural networks are equal.
	 * 
	 * @param network1
	 *            The first network.
	 * @param network2
	 *            The second network.
	 * @param precision
	 *            How many decimal places to check.
	 * @return True if the two networks are equal.
	 */
	public static boolean equals(final BasicNetwork network1,
			final BasicNetwork network2, final int precision) {
		final double[] array1 = NetworkCODEC.networkToArray(network1);
		final double[] array2 = NetworkCODEC.networkToArray(network2);

		if (array1.length != array2.length) {
			return false;
		}

		final double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			throw new NeuralNetworkError("Precision of " + precision
					+ " decimal places is not supported.");
		}

		for (int i = 0; i < array1.length; i++) {
			final long l1 = (long) (array1[i] * test);
			final long l2 = (long) (array2[i] * test);
			if (l1 != l2) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determine the network size.
	 * @param network The network.
	 * @return The size.
	 */
	public static int networkSize(final MLMethod network) {
		if (network instanceof MLEncodable) {
			return ((MLEncodable) network).encodedArrayLength();
		}
		throw new NeuralNetworkError(NetworkCODEC.ERROR
				+ network.getClass().getName());
	}

	/**
	 * Convert to an array. This is used with some training algorithms that
	 * require that the "memory" of the neuron(the weight and bias values) be
	 * expressed as a linear array.
	 * 
	 * @param network
	 *            The network to encode.
	 * @return The memory of the neuron.
	 */
	public static double[] networkToArray(final MLMethod network) {
		final int size = NetworkCODEC.networkSize(network);

		if (network instanceof MLEncodable) {
			final double[] encoded = new double[size];
			((MLEncodable) network).encodeToArray(encoded);
			return encoded;
		}
		throw new NeuralNetworkError(NetworkCODEC.ERROR
				+ network.getClass().getName());

	}

	/**
	 * Private constructor.
	 */
	private NetworkCODEC() {

	}

}
