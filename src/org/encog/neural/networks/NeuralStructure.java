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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds "cached" information about the structure of the neural network. This is
 * a very good performance boost since the neural network does not need to
 * traverse itself each time a complete collection of layers or synapses is
 * needed.
 * 
 * @author jheaton
 * 
 */
public class NeuralStructure {
	
	/**
	 * The layers in this neural network.
	 */
	private final List<Layer> layers = new ArrayList<Layer>();
	
	/**
	 * The synapses in this neural network.
	 */
	private final List<Synapse> synapses = new ArrayList<Synapse>();
	
	/**
	 * The neural network this class belongs to.
	 */
	private final BasicNetwork network;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a structure object for the specified network.
	 * @param network The network to construct a structure for.
	 */
	public NeuralStructure(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Build the layer structure.
	 */
	private void finalizeLayers() {
		final List<Layer> result = new ArrayList<Layer>();
		
		this.layers.clear();
		if (this.network.getInputLayer() != null) {
			getLayers(result, this.network.getInputLayer());
		}
		
		this.layers.addAll(result);
	}

	/**
	 * Build the synapse and layer structure.  This method should be called 
	 * after you are done adding layers to a network.
	 */
	public void finalizeStructure() {
		finalizeLayers();
		finalizeSynapses();
	}

	/**
	 * Build the synapse structure.
	 */
	private void finalizeSynapses() {
		final Set<Synapse> result = new HashSet<Synapse>();
		for (final Layer layer : getLayers()) {
			for (final Synapse synapse : layer.getNext()) {
				result.add(synapse);
			}
		}
		this.synapses.clear();
		this.synapses.addAll(result);
	}

	/**
	 * @return The layers in this neural network.
	 */
	public List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * Called to help build the layer structure.
	 * @param result The layer list. 
	 * @param layer The current layer being processed.
	 */
	private void getLayers(final List<Layer> result, 
			final Layer layer) {
		
		if (!result.contains(layer)) {
			result.add(layer);
		}

		for (final Synapse synapse : layer.getNext()) {
			final Layer nextLayer = synapse.getToLayer();

			if (!result.contains(nextLayer)) {
				getLayers(result, nextLayer);
			}
		}
	}

	/**
	 * @return The network this structure belongs to.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Get the previous layers from the specified layer.
	 * @param targetLayer The target layer.
	 * @return The previous layers.
	 */
	public Collection<Layer> getPreviousLayers(final Layer targetLayer) {
		final Collection<Layer> result = new HashSet<Layer>();
		for (final Layer layer : this.getLayers()) {
			for (final Synapse synapse : layer.getNext()) {
				if (synapse.getToLayer() == targetLayer) {
					result.add(synapse.getFromLayer());
				}
			}
		}
		return result;
	}

	/**
	 * Get the previous synapses.
	 * @param targetLayer The layer to get the previous layers from.
	 * @return A collection of synapses.
	 */
	public Collection<Synapse> getPreviousSynapses(final Layer targetLayer) {

		final Collection<Synapse> result = new HashSet<Synapse>();

		for (final Synapse synapse : this.synapses) {
			if (synapse.getToLayer() == targetLayer) {
				result.add(synapse);
			}
		}

		return result;

	}

	/**
	 * @return All synapses in the neural network.
	 */
	public List<Synapse> getSynapses() {
		return this.synapses;
	}
}
