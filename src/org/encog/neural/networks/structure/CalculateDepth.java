package org.encog.neural.networks.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

public class CalculateDepth {

	private Map<Layer,Integer> depths = new HashMap<Layer,Integer>();
	private BasicNetwork network;
	private Layer inputLayer;
	private Layer outputLayer;
	
	public CalculateDepth(BasicNetwork network)
	{
		this.network = network;
		this.inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		this.outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		this.calculate(0, this.outputLayer);
	}
	

	private void calculate(int currentDepth, Layer layer) {
		
		// record this layer
		if( this.depths.containsKey(layer) )
		{
			int oldDepth = this.depths.get(layer);
			if( currentDepth>oldDepth )
			{
				this.depths.put(layer, currentDepth);
			}
		}
		else
		{
			this.depths.put(layer, currentDepth);
		}
		
		// traverse all of the ways to get to that layer
		Collection<Layer> prev = this.network.getStructure()
				.getPreviousLayers(this.outputLayer);
		
		for (Layer nextLayer : prev) {
			if( !this.depths.containsKey(nextLayer))
				calculate(currentDepth+1, nextLayer);
		}
	}
	
	public int getDepth(Layer layer) {
		if( !this.depths.containsKey(layer) ) {
			return -1;
		}
		else
			return this.depths.get(layer);
	}

}
