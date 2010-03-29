package org.encog.neural.networks.synapse;

import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

public class PartialSynapse extends WeightedSynapse {

	public PartialSynapse(Layer inputLayer, Layer outputLayer) {
		super(inputLayer,outputLayer);
	}

}
