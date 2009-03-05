package org.encog.neural.networks.synapse;

import org.encog.neural.networks.Layer;

public abstract class BasicSynapse implements Synapse {
	
	private Layer fromLayer;
	private Layer toLayer;
	
	public int getFromNeuronCount() {
		return this.fromLayer.getNeuronCount();
	}
	
	public int getToNeuronCount() {
		return this.toLayer.getNeuronCount();
	}	


	public Layer getFromLayer() {
		return fromLayer;
	}

	public void setFromLayer(Layer fromLayer) {
		this.fromLayer = fromLayer;
	}

	public Layer getToLayer() {
		return toLayer;
	}

	public void setToLayer(Layer toLayer) {
		this.toLayer = toLayer;
	}

}
