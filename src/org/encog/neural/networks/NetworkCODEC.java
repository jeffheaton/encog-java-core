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

package org.encog.neural.networks;

import java.util.Collection;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixError;
import org.encog.neural.NeuralNetworkError;
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
	 * Use an array to populate the memory of the neural network.
	 * 
	 * @param array
	 *            An array of doubles.
	 * @param network
	 *            The network to encode.
	 */
	public static void arrayToNetwork(final Double[] array,
			final BasicNetwork network) {

		// copy all weight data
		int currentIndex = 0;
		final Collection<Synapse> synapses = network.getStructure()
				.getSynapses();
		for (final Synapse synapse : synapses) {
			if (synapse.getMatrix() != null) {

				currentIndex = synapse.getMatrix().fromPackedArray(array,
						currentIndex);

			}
		}

		// copy all threshold data
		for (final Layer layer : network.getStructure().getLayers()) {
			for (int i = 0; i < layer.getNeuronCount(); i++) {
				layer.setThreshold(i, array[currentIndex++]);
			}
		}

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
	public static Double[] networkToArray(final BasicNetwork network) {
		int size = 0;

		// first determine size from matrixes
		for (final Synapse synapse : network.getStructure().getSynapses()) {
			size += synapse.getMatrixSize();
		}

		// determine size from threshold values
		for (final Layer layer : network.getStructure().getLayers()) {
			size += layer.getNeuronCount();
		}

		// allocate an array to hold
		final Double[] result = new Double[size];

		// copy all weight data
		int currentIndex = 0;
		final Collection<Synapse> synapses = network.getStructure()
				.getSynapses();
		for (final Synapse synapse : synapses) {
			if (synapse.getMatrix() != null) {
				final Double[] temp = synapse.getMatrix().toPackedArray();
				for (final Double element : temp) {
					result[currentIndex++] = element;
				}
			}
		}

		// copy all threshold data
		for (final Layer layer : network.getStructure().getLayers()) {
			for (int i = 0; i < layer.getNeuronCount(); i++) {
				result[currentIndex++] = layer.getThreshold(i);
			}
		}

		return result;
	}
	
	public static boolean equals(BasicNetwork network1, BasicNetwork network2, final int precision)
	{
		Double[] array1 = networkToArray(network1);
		Double[] array2 = networkToArray(network2);
		
		if( array1.length != array2.length )
			return false;
		
		final double test = Math.pow(10.0, precision);
		if (Double.isInfinite(test) || (test > Long.MAX_VALUE)) {
			final String str = "Precision of " + precision
					+ " decimal places is not supported.";
			if (NetworkCODEC.logger.isErrorEnabled()) {
				NetworkCODEC.logger.error(str);
			}
			throw new NeuralNetworkError(str);
		}
		
		for(int i=0;i<array1.length;i++)
		{
			long l1 = (long)(array1[i].doubleValue() * test);
			long l2 = (long)(array1[i].doubleValue() * test);
			if( l1!=l2 )
				return false;
		}
		
		return true;
	}

	/**
	 * The logging object.
	 */
	private final static Logger logger = LoggerFactory.getLogger(NetworkCODEC.class);

	/**
	 * Private constructor.
	 */
	private NetworkCODEC() {

	}

}
