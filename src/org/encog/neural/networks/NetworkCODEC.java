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

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkCODEC {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Private constructor.
	 */
	private NetworkCODEC() {

	}

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
		Collection<Synapse> synapses = network.getStructure().getSynapses();
		for (final Synapse synapse : synapses) {
			if (synapse.getMatrix() != null) {

					currentIndex = synapse.getMatrix().fromPackedArray(array,
							currentIndex);
				
			}
		}
		
		// copy all threshold data
		for( final Layer layer: network.getStructure().getLayers() ) {
			for(int i=0;i<layer.getNeuronCount();i++) {
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
		for( final Layer layer: network.getStructure().getLayers() ) {
			size+=layer.getNeuronCount();
		}

		// allocate an array to hold
		final Double[] result = new Double[size];

		// copy all weight data
		int currentIndex = 0;
		Collection<Synapse> synapses = network.getStructure().getSynapses();
		for (final Synapse synapse : synapses ) {
			if (synapse.getMatrix() != null) {
				Double[] temp = synapse.getMatrix().toPackedArray();
				for (int i = 0; i < temp.length; i++) {
					result[currentIndex++] = temp[i];
				}
			}
		}
		
		// copy all threshold data
		for( final Layer layer: network.getStructure().getLayers() ) {
			for(int i=0;i<layer.getNeuronCount();i++) {
				result[currentIndex++] = layer.getThreshold(i); 
			}
		}

		return result;
	}


}
