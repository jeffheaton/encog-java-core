package org.encog.neural.networks.synapse;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.layers.Layer;

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
	
	public boolean isSelfConnected()
	{
		return this.fromLayer==this.toLayer;
	}
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(": from=");
		result.append(this.getFromNeuronCount());
		result.append(",to=");
		result.append(this.getToNeuronCount());
		result.append("]");
		return result.toString();
	}
	
	abstract public Object clone();

}
