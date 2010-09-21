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
				.getPreviousLayers(layer);

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
