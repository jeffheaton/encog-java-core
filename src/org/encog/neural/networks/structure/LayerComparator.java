package org.encog.neural.networks.structure;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

public class LayerComparator implements Comparator<Layer> {

	private NeuralStructure structure;
	private CalculateDepth depth;
	private Layer inputLayer;
	private Layer outputLayer;

	public LayerComparator(NeuralStructure structure) {
		this.structure = structure;
		this.depth = new CalculateDepth(structure.getNetwork());
		this.inputLayer = this.structure.getNetwork().getLayer(
				BasicNetwork.TAG_INPUT);
		this.outputLayer = this.structure.getNetwork().getLayer(
				BasicNetwork.TAG_OUTPUT);
	}

	public int compare(Layer layer1, Layer layer2) {
		
		int depth1 = this.depth.getDepth(layer1);
		int depth2 = this.depth.getDepth(layer2);
		
		// are they the same layers?
		if (layer1 == layer2)
			return 0;
		// output layer always comes first
		else if (layer1 == this.outputLayer || layer2 == this.inputLayer)
			return -1;
		// 
		else if (layer2 == this.outputLayer || layer1 == this.inputLayer)
			return 1;
		else if( depth1!=depth2 )
			return depth2-depth1;
		// failing all else, just sort them by their ids
		else
			return layer1.getID() - layer2.getID();
	}

	public boolean equal(Layer layer1, Layer layer2) {
		return layer1 == layer2;
	}
}
