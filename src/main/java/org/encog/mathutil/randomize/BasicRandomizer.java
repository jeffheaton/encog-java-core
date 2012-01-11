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
package org.encog.mathutil.randomize;

import java.util.Random;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;

/**
 * Provides basic functionality that most randomizers will need.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicRandomizer implements Randomizer {

	/**
	 * The random number generator.
	 */
	private Random random;

	/**
	 * Construct a random number generator with a random(current time) seed. If
	 * you want to set your own seed, just call "getRandom().setSeed".
	 */
	public BasicRandomizer() {
		this.random = new Random(System.nanoTime());
	}

	/**
	 * @return The random number generator in use. Use this to set the seed, if
	 *         desired.
	 */
	public final Random getRandom() {
		return this.random;
	}

	/**
	 * @return The next double.
	 */
	public final double nextDouble() {
		return this.random.nextDouble();
	}

	/**
	 * Generate a random number in the specified range.
	 * 
	 * @param min
	 *            The minimum value.
	 * @param max
	 *            The maximum value.
	 * @return A random number.
	 */
	public final double nextDouble(final double min, final double max) {
		final double range = max - min;
		return (range * this.random.nextDouble()) + min;
	}

	/**
	 * Randomize one level of a neural network.
	 * 
	 * @param network
	 *            The network to randomize
	 * @param fromLayer
	 *            The from level to randomize.
	 */
	public void randomize(final BasicNetwork network, 
			final int fromLayer) {
		final int fromCount = network.getLayerTotalNeuronCount(fromLayer);
		final int toCount = network.getLayerNeuronCount(fromLayer + 1);

		for (int fromNeuron = 0; fromNeuron < fromCount; fromNeuron++) {
			for (int toNeuron = 0; toNeuron < toCount; toNeuron++) {
				double v = network.getWeight(fromLayer, fromNeuron, toNeuron);
				v = randomize(v);
				network.setWeight(fromLayer, fromNeuron, toNeuron, v);
			}
		}
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final double[] d) {
		randomize(d, 0, d.length);
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 * @param begin
	 *            The beginning element of the array.
	 * @param size
	 *            The size of the array to copy.
	 */
	@Override
	public void randomize(final double[] d, final int begin, 
				final int size) {
		for (int i = 0; i < size; i++) {
			d[begin + i] = randomize(d[begin + i]);
		}
	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
	}

	/**
	 * Randomize the matrix based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param m
	 *            A matrix to randomize.
	 */
	@Override
	public void randomize(final Matrix m) {
		final double[][] d = m.getData();
		for (int r = 0; r < m.getRows(); r++) {
			for (int c = 0; c < m.getCols(); c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
	}

	/**
	 * Randomize the synapses and biases in the basic network based on an array,
	 * modify the array. Previous values may be used, or they may be discarded,
	 * depending on the randomizer.
	 * 
	 * @param method
	 *            A network to randomize.
	 */
	@Override
	public void randomize(final MLMethod method) {

		if (method instanceof BasicNetwork) {
			final BasicNetwork network = (BasicNetwork) method;
			for (int i = 0; i < network.getLayerCount() - 1; i++) {
				randomize(network, i);
			}
		} else if (method instanceof MLEncodable) {
			final MLEncodable encode = (MLEncodable) method;
			final double[] encoded = new double[encode.encodedArrayLength()];
			encode.encodeToArray(encoded);
			randomize(encoded);
			encode.decodeFromArray(encoded);
		}
	}

	/**
	 * @param theRandom
	 *            the random to set
	 */
	public final void setRandom(final Random theRandom) {
		this.random = theRandom;
	}
}
