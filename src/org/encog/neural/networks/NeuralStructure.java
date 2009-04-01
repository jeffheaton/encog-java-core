package org.encog.neural.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public class NeuralStructure {
	private List<Layer> layers = new ArrayList<Layer>();
	private List<Synapse> synapses = new ArrayList<Synapse>();
	private BasicNetwork network;
	
	public NeuralStructure(BasicNetwork network)
	{
		this.network = network;
	}
	
	public void finalizeStructure()
	{
		finalizeLayers();
		finalizeSynapses();		
	}
	
	private void finalizeLayers()
	{
		Set<Layer> result = new HashSet<Layer>();
		getLayers(result, this.network.getInputLayer());
		this.layers.clear();
		this.layers.addAll(result);
	}
	
	private void finalizeSynapses()
	{
		Set<Synapse> result = new HashSet<Synapse>();
		for (Layer layer : getLayers()) {
			for (Synapse synapse : layer.getNext()) {
				result.add(synapse);
			}
		}
		this.synapses.clear();
		this.synapses.addAll(result);
	}
	
	private void getLayers(Set<Layer> result, Layer layer) {
		result.add(layer);

		for (Synapse synapse : layer.getNext()) {
			Layer nextLayer = synapse.getToLayer();

			if (!result.contains(nextLayer)) {
				getLayers(result, nextLayer);
			}
		}
	}

	public Collection<Layer> getPreviousLayers(Layer targetLayer) {
		Collection<Layer> result = new HashSet<Layer>();
		for (Layer layer : this.getLayers()) {
			for (Synapse synapse : layer.getNext()) {
				if (synapse.getToLayer() == targetLayer) {
					result.add(synapse.getFromLayer());
				}
			}
		}
		return result;
	}

	public Collection<Synapse> getPreviousSynapses(Layer targetLayer) {
		
		Collection<Synapse> result = new HashSet<Synapse>();
		
		for(Synapse synapse: this.synapses)
		{
			if( synapse.getToLayer()==targetLayer)
				result.add(synapse);
		}
		
		return result;
		
		/*Collection<Synapse> result = new HashSet<Synapse>();
		for (Layer layer : this.getLayers()) {
			for (Synapse synapse : layer.getNext()) {
				if (synapse.getToLayer() == targetLayer) {
					result.add(synapse);
				}
			}
		}
		return result;*/
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public List<Synapse> getSynapses() {
		return synapses;
	}

	public BasicNetwork getNetwork() {
		return network;
	}
}
