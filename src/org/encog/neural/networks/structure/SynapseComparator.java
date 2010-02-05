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

import java.util.Comparator;

import org.encog.neural.networks.synapse.Synapse;

/**
 * Used to compare the order of synapses. Allows synapses to be sorted the same
 * way each time. They are sorted from output back to input.
 */
public class SynapseComparator implements Comparator<Synapse> {

	/**
	 * Used to compare layers.
	 */
	private final LayerComparator layerCompare;

	/**
	 * Construct a layer comparator.
	 * 
	 * @param structure
	 *            The structure of the network to use.
	 */
	public SynapseComparator(final NeuralStructure structure) {
		this.layerCompare = new LayerComparator(structure);
	}

	/**
	 * Compare two layers.
	 * 
	 * @param synapse1
	 *            The first layer to compare.
	 * @param synapse2
	 *            The second layer to compare.
	 * @return The value 0 if the argument layer is equal to this synapse; a
	 *         value less than 0 if this synapse is less than the argument; and
	 *         a value greater than 0 if this synapse is greater than the
	 *         synapse argument.
	 */
	public int compare(final Synapse synapse1, final Synapse synapse2) {

		if (synapse1 == synapse2) {
			return 0;
		}

		final int cmp = this.layerCompare.compare(synapse1.getToLayer(),
				synapse2.getToLayer());

		if (cmp != 0) {
			return cmp;
		}

		return this.layerCompare.compare(synapse1.getFromLayer(), synapse2
				.getFromLayer());
	}

}
