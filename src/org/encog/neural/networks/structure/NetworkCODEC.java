/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.neural.networks.structure;

import java.util.Arrays;
import java.util.List;

import org.encog.engine.util.EngineArray;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			index = NetworkCODEC.processLayer(network, layer, array, index);
		}

		network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
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

		for(int i=0;i<array1.length;i++) {
			final long l1 = (long) (array1[i] * test);
			final long l2 = (long) (array2[i] * test);
			if (l1 != l2) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Determine if the two neural networks are equal.
	 * Uses exact precision required by Arrays.equals.
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


	public static int networkSize(final BasicNetwork network) {

		// see if there is already an up to date flat network
		if ((network.getStructure().getFlat() != null)
				&& ((network.getStructure().getFlatUpdate() == FlatUpdateNeeded.None) || (network
						.getStructure().getFlatUpdate() == FlatUpdateNeeded.Unflatten))) {
			return network.getStructure().getFlat().getWeights().length;
		}

		int index = 0;

		// loop over all of the layers, take the output layer first
		for (final Layer layer : network.getStructure().getLayers()) {

			// see if the previous layer, which is the next layer that the loop
			// will hit,
			// is either a connection to a BasicLayer or a ContextLayer.
			Synapse synapse = network.getStructure()
					.findPreviousSynapseByLayerType(layer, BasicLayer.class);
			final Synapse contextSynapse = network.getStructure()
					.findPreviousSynapseByLayerType(layer, ContextLayer.class);

			// get a list of of the previous synapses to this layer
			final List<Synapse> list = network.getStructure()
					.getPreviousSynapses(layer);

			// If there is not a BasicLayer or contextLayer as the next layer,
			// then
			// just take the first synapse of any type.
			if ((synapse == null) && (contextSynapse == null)
					&& (list.size() > 0)) {
				synapse = list.get(0);
			}

			// is there any data to record for this synapse?
			if ((synapse != null) && (synapse.getMatrix() != null)) {
				// process each weight matrix
				for (int x = 0; x < synapse.getToNeuronCount(); x++) {

					index += synapse.getFromNeuronCount();

					if (synapse.getToLayer().hasBias()) {
						index++;
					}

					if (contextSynapse != null) {
						index += contextSynapse.getFromNeuronCount();
					}
				}
			}
		}

		return index;
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
	public static double[] networkToArray(final BasicNetwork network) {
		final int size = NetworkCODEC.networkSize(network);

		// see if there is already an up to date flat network
		if ((network.getStructure().getFlat() != null)
				&& ((network.getStructure().getFlatUpdate() == FlatUpdateNeeded.None) || (network
						.getStructure().getFlatUpdate() == FlatUpdateNeeded.Unflatten))) {
			return EngineArray.arrayCopy(network.getStructure().getFlat()
					.getWeights());
		}

		// allocate an array to hold
		final double[] result = new double[size];

		int index = 0;

		// loop over all of the layers, take the output layer first
		for (final Layer layer : network.getStructure().getLayers()) {

			// see if the previous layer, which is the next layer that the loop
			// will hit,
			// is either a connection to a BasicLayer or a ContextLayer.
			Synapse synapse = network.getStructure()
					.findPreviousSynapseByLayerType(layer, BasicLayer.class);
			final Synapse contextSynapse = network.getStructure()
					.findPreviousSynapseByLayerType(layer, ContextLayer.class);

			// get a list of of the previous synapses to this layer
			final List<Synapse> list = network.getStructure()
					.getPreviousSynapses(layer);

			// If there is not a BasicLayer or contextLayer as the next layer,
			// then
			// just take the first synapse of any type.
			if ((synapse == null) && (contextSynapse == null)
					&& (list.size() > 0)) {
				synapse = list.get(0);
			}

			// is there any data to record for this synapse?
			if ((synapse != null) && (synapse.getMatrix() != null)) {
				// process each weight matrix
				for (int x = 0; x < synapse.getToNeuronCount(); x++) {
					for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
						result[index++] = synapse.getMatrix().get(y, x);
					}

					if (synapse.getToLayer().hasBias()) {
						result[index++] = synapse.getToLayer().getBiasWeights()[x];
					}

					if (contextSynapse != null) {
						for (int z = 0; z < contextSynapse.getFromNeuronCount(); z++) {
							result[index++] = contextSynapse.getMatrix().get(z,
									x);
						}
					}
				}
			}

		}

		return result;
	}

	/**
	 * Process a synapse.
	 * 
	 * @param network
	 *            The network to process.
	 * @param layer
	 *            The layer to process.
	 * @param array
	 *            The array to process.
	 * @param index
	 *            The current index.
	 * @return The index after this synapse has been read.
	 */
	private static int processLayer(final BasicNetwork network,
			final Layer layer, final double[] array, final int index) {
		int result = index;

		// see if the previous layer, which is the next layer that the loop will
		// hit,
		// is either a connection to a BasicLayer or a ContextLayer.
		Synapse synapse = network.getStructure()
				.findPreviousSynapseByLayerType(layer, BasicLayer.class);
		final Synapse contextSynapse = network.getStructure()
				.findPreviousSynapseByLayerType(layer, ContextLayer.class);

		// get a list of of the previous synapses to this layer
		final List<Synapse> list = network.getStructure().getPreviousSynapses(
				layer);

		// If there is not a BasicLayer or contextLayer as the next layer, then
		// just take the first synapse of any type.
		if ((synapse == null) && (contextSynapse == null) && (list.size() > 0)) {
			synapse = list.get(0);
		}

		// is there any data to record for this synapse?
		if ((synapse != null) && (synapse.getMatrix() != null)) {
			// process each weight matrix
			for (int x = 0; x < synapse.getToNeuronCount(); x++) {
				for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
					synapse.getMatrix().set(y, x, array[result++]);
				}
				if (synapse.getToLayer().hasBias()) {
					synapse.getToLayer().getBiasWeights()[x] = array[result++];
				}

				if (contextSynapse != null) {
					for (int z = 0; z < contextSynapse.getFromNeuronCount(); z++) {

						double value = array[result++];

						final double oldValue = synapse.getMatrix().get(z, x);

						// if this connection is limited, do not update it to
						// anything but zero
						if (Math.abs(oldValue) < network.getStructure()
								.getConnectionLimit()) {
							value = 0;
						}

						// update the actual matrix
						contextSynapse.getMatrix().set(z, x, value);
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
