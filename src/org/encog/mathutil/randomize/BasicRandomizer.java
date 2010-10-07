/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides basic functionality that most randomizers will need.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicRandomizer implements Randomizer {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * The random number generator.
	 */
	private Random random;
	
	/**
	 * Construct a random number generator with a random(current time) seed.
	 * If you want to set your own seed, just call "getRandom().setSeed".
	 */
	public BasicRandomizer() {
		this.random = new Random(System.currentTimeMillis());
	}
	
	/**
	 * Randomize the synapses and biases in the basic network based on an array,
	 * modify the array. Previous values may be used, or they may be discarded,
	 * depending on the randomizer.
	 * 
	 * @param network
	 *            A network to randomize.
	 */
	public void randomize(final BasicNetwork network) {

		network.getStructure().updateFlatNetwork();
		// randomize the weight matrix
		for (final Synapse synapse : network.getStructure().getSynapses()) {
			if (synapse.getMatrix() != null) {
				randomize(network, synapse);
			}
		}

		// randomize the bias
		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.hasBias()) {
				randomize(layer.getBiasWeights());
			}
		}
		network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
		network.getStructure().flattenWeights();
	}

	/**
	 * Randomize a synapse, only randomize those connections that are actually
	 * connected.
	 * 
	 * @param network
	 *            The network the synapse belongs to.
	 * @param synapse
	 *            The synapse to randomize.
	 */
	public void randomize(final BasicNetwork network, final Synapse synapse) {
		if (synapse.getMatrix() != null) {
			boolean limited = network.getStructure().isConnectionLimited();
			final double[][] d = synapse.getMatrix().getData();
			for (int fromNeuron = 0; fromNeuron 
				< synapse.getMatrix().getRows(); fromNeuron++) {
				for (int toNeuron = 0; toNeuron 
					< synapse.getMatrix().getCols(); toNeuron++) {
					if (!limited
							|| network.isConnected(synapse, fromNeuron,
									toNeuron)) {
						d[fromNeuron][toNeuron] = 
							randomize(d[fromNeuron][toNeuron]);
					}
				}
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
	public void randomize(final double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = randomize(d[i]);
		}

	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	public void randomize(final Double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = randomize(d[i]);
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
	public void randomize(final double[][] d) {
		for (int r = 0; r < d.length; r++) {
			for (int c = 0; c < d[0].length; c++) {
				d[r][c] = randomize(d[r][c]);
			}
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
	public void randomize(final Double[][] d) {
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
	public void randomize(final Matrix m) {
		final double[][] d = m.getData();
		for (int r = 0; r < m.getRows(); r++) {
			for (int c = 0; c < m.getCols(); c++) {
				d[r][c] = randomize(d[r][c]);
			}
		}
	}

	/**
	 * @return The random number generator in use. Use this to set the seed, if
	 *         desired.
	 */
	public Random getRandom() {
		return random;
	}
	
	/**
	 * @return The next double.
	 */
	public double nextDouble() {
		return this.random.nextDouble();
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(final Random random) {
		this.random = random;
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
	public double nextDouble(final double min, final double max) {
		final double range = max - min;
		return (range * this.random.nextDouble()) + min;
	}
}
