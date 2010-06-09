/*
 * Encog(tm) Core v2.4
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
