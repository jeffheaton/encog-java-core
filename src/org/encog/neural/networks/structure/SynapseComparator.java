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
