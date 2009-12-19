package org.encog.neural.networks.structure;

import java.util.Comparator;

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public class SynapseComparator implements Comparator<Synapse> {

	private LayerComparator layerCompare;

	public SynapseComparator(NeuralStructure structure) {
		this.layerCompare = new LayerComparator(structure);
	}

	public int compare(Synapse synapse1, Synapse synapse2) {
		
		if (synapse1 == synapse2)
			return 0;
		
		int cmp = this.layerCompare.compare(synapse1.getToLayer(), synapse2
				.getToLayer()); 

		if( cmp!=0 )
			return cmp;
		
		return this.layerCompare.compare(synapse1.getFromLayer(), synapse2
				.getFromLayer());
	}

}
