package org.encog.neural.networks.training.propagation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropagationLayer {

	private final int neuronCount;
	private final int previousNeuronCount;
	private final Layer layer;
	private final Collection<Synapse> previousSynapses;
	private final double[] errorDeltas;
	
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public PropagationLayer(Layer layer, int previousNeuronCount,Collection<Synapse> previousSynapses)
	{
		this.layer = layer;
		this.neuronCount = layer.getNeuronCount();
		this.previousNeuronCount = previousNeuronCount;
		this.errorDeltas = new double[layer.getNeuronCount()];
		this.previousSynapses = previousSynapses;
	}

	public Collection<Synapse> getPreviousSynapses() {
		return previousSynapses;
	}
	
	public void setErrorDelta(int index,double d)
	{
		this.errorDeltas[index] = d;
	}
	
	public double getErrorDelta(int index)
	{
		return this.errorDeltas[index];
	}

	public Layer getLayer() {
		return layer;
	}

	public double[] getErrorDeltas() {
		return errorDeltas;
	}

	public int getNeuronCount() {
		return neuronCount;
	}

	public int getPreviousNeuronCount() {
		return previousNeuronCount;
	}
	
	public double getWeight(int sourceNeuron,int targetNeuron)
	{
		return 0;
	}
	
	public void setWeight(int sourceNeuron,int targetNeuron,double d)
	{
		
	}
	
	public double getThreshold(int index)
	{
		return 0;
	}
	
	public void setThreshold(int index, double d)
	{
	}
	
	
}
