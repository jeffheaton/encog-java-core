/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.neural.networks.structure;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will extract the "long term memory" of a neural network, that is
 * the weights and threshold values into an array. This array can be used to
 * view the neural network as a linear array of doubles. These values can then
 * be modified and copied back into the neural network. This is very useful for
 * simulated annealing, as well as genetic algorithms.
 * 
 * @author jheaton
 * 
 */
public final class NetworkCODEC {

	/**
	 * The logging object.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NetworkCODEC.class);

	/**
	 * Use an array to populate the memory of the neural network.
	 * 
	 * @param array
	 *            An array of doubles.
	 * @param network
	 *            The network to encode.
	 */
	public static void arrayToNetwork(final double[] array,
			final BasicNetwork network) {

		int index = 0;

		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.hasThreshold()) {
				// process layer thresholds
				for (int i = 0; i < layer.getNeuronCount(); i++) {
					layer.setThreshold(i, array[index++]);
				}
			}

			// process synapses
			for (final Synapse synapse : network.getStructure()
					.getPreviousSynapses(layer)) {
				if (synapse.getMatrix() != null) {
					// process each weight matrix
					for (int x = 0; x < synapse.getToNeuronCount(); x++) {
						for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
							synapse.getMatrix().set(y, x, array[index++]);
						}
					}
				}
			}
		}
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
			final String str = "Precision of " + precision
					+ " decimal places is not supported.";
			if (NetworkCODEC.LOGGER.isErrorEnabled()) {
				NetworkCODEC.LOGGER.error(str);
			}
			throw new NeuralNetworkError(str);
		}

		for (final Double element : array1) {
			final long l1 = (long) (element.doubleValue() * test);
			final long l2 = (long) (element.doubleValue() * test);
			if (l1 != l2) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Convert to an array. This is used with some training algorithms that
	 * require that the "memory" of the neuron(the weight and threshold values)
	 * be expressed as a linear array.
	 * 
	 * @param network
	 *            The network to encode.
	 * @return The memory of the neuron.
	 */
	public static double[] networkToArray(final BasicNetwork network) {
		final int size = network.getStructure().calculateSize();

		// allocate an array to hold
		final double[] result = new double[size];

		int index = 0;

		for (final Layer layer : network.getStructure().getLayers()) {
			// process layer thresholds
			if (layer.hasThreshold()) {
				for (int i = 0; i < layer.getNeuronCount(); i++) {
					result[index++] = layer.getThreshold(i);
				}
			}

			// process synapses
			for (final Synapse synapse : network.getStructure()
					.getPreviousSynapses(layer)) {
				if (synapse.getMatrix() != null) {
					// process each weight matrix
					for (int x = 0; x < synapse.getToNeuronCount(); x++) {
						for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
							result[index++] = synapse.getMatrix().get(y, x);
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * Private constructor.
	 */
	private NetworkCODEC() {

	}

}
