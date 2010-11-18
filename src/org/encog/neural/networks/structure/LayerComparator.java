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

import java.util.Comparator;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

/**
 * Used to compare layers and ensure a consistent sort order.
 */
public class LayerComparator implements Comparator<Layer> {

	/**
	 * The structure of the neural network.
	 */
	private final NeuralStructure structure;

	/**
	 * The current depth.
	 */
	private final CalculateDepth depth;

	/**
	 * The input layer.
	 */
	private final Layer inputLayer;

	/**
	 * The output layer.
	 */
	private final Layer outputLayer;

	/**
	 * Construct a level comparator for the specified structure.
	 * 
	 * @param structure
	 *            The structure of the neural network to compare.
	 */
	public LayerComparator(final NeuralStructure structure) {
		this.structure = structure;
		this.depth = new CalculateDepth(structure.getNetwork());
		this.inputLayer = this.structure.getNetwork().getLayer(
				BasicNetwork.TAG_INPUT);
		this.outputLayer = this.structure.getNetwork().getLayer(
				BasicNetwork.TAG_OUTPUT);
	}

	/**
	 * Compare two layers.
	 * 
	 * @param layer1
	 *            The first layer to compare.
	 * @param layer2
	 *            The second layer to compare.
	 * @return The value 0 if the argument layer is equal to this layer; a value
	 *         less than 0 if this layer is less than the argument; and a value
	 *         greater than 0 if this layer is greater than the layer argument.
	 */
	public int compare(final Layer layer1, final Layer layer2) {

		final int depth1 = this.depth.getDepth(layer1);
		final int depth2 = this.depth.getDepth(layer2);

		// are they the same layers?
		if (layer1 == layer2) {
			return 0;
		} else if ((layer1 == this.outputLayer) 
				|| (layer2 == this.inputLayer)) {
			return -1;
		} else if ((layer2 == this.outputLayer) 
				|| (layer1 == this.inputLayer)) {
			return 1;
		} else if (depth1 != depth2) {
			return depth1 - depth2;
			// failing all else, just sort them by their ids
		} else {
			return layer1.getID() - layer2.getID();
		}
	}

	/**
	 * Determine if two layers are equal. They are only equal if they have the
	 * same weight matrix size.
	 * 
	 * @param layer1
	 *            The first layer to compare.
	 * @param layer2
	 *            The second layer to compare.
	 * @return True if they are equal, false if they are not.
	 */
	public boolean equal(final Layer layer1, final Layer layer2) {
		return layer1 == layer2;
	}
}
