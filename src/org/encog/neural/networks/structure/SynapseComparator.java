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
