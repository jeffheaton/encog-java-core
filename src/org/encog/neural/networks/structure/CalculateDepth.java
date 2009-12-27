/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

/**
 * Utility class to calculate the depth that a layer is from the output layer.
 * If there are multiple ways to get to the specified layer, then the longest
 * depth is returned.  This class is used by propagation training to ensure
 * that the layers are always returned on a consistent order.
 */
public class CalculateDepth {

	/**
	 * The depth so far at each layer.
	 */
	private final Map<Layer, Integer> depths = new HashMap<Layer, Integer>();
	
	/**
	 * The network.
	 */
	private final BasicNetwork network;
	
	/**
	 * The output layer.
	 */
	private final Layer outputLayer;

	/**
	 * Construct the depth calculation object.
	 * @param network The network that we are calculating for.
	 */
	public CalculateDepth(final BasicNetwork network) {
		this.network = network;
		this.outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		calculate(0, this.outputLayer);
	}

	/**
	 * Called internally to calculate a depth.
	 * @param currentDepth The current depth.
	 * @param layer The layer we are on.
	 */
	private void calculate(final int currentDepth, final Layer layer) {

		// record this layer
		if (this.depths.containsKey(layer)) {
			final int oldDepth = this.depths.get(layer);
			if (currentDepth > oldDepth) {
				this.depths.put(layer, currentDepth);
			}
		} else {
			this.depths.put(layer, currentDepth);
		}

		// traverse all of the ways to get to that layer
		final Collection<Layer> prev = this.network.getStructure()
				.getPreviousLayers(this.outputLayer);

		for (final Layer nextLayer : prev) {
			if (!this.depths.containsKey(nextLayer)) {
				calculate(currentDepth + 1, nextLayer);
			}
		}
	}

	/**
	 * Get the depth for a specific layer.
	 * @param layer The layer to get the depth for.
	 * @return The depth of the specified layer.
	 */
	public int getDepth(final Layer layer) {
		if (!this.depths.containsKey(layer)) {
			return -1;
		} else {
			return this.depths.get(layer);
		}
	}

}
