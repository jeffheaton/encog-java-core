package org.encog.neural.networks.synapse.neat;

public class NEATLink {

	private final double weight; 
	private final NEATNeuron fromNeuron; 
	private final NEATNeuron toNeuron;
	private final boolean recurrent;
	
	public NEATLink(double weight, NEATNeuron fromNeuron, NEATNeuron toNeuron,
			boolean recurrent) {
		this.weight = weight;
		this.fromNeuron = fromNeuron;
		this.toNeuron = toNeuron;
		this.recurrent = recurrent;
	}

	public double getWeight() {
		return weight;
	}

	public NEATNeuron getFromNeuron() {
		return fromNeuron;
	}

	public NEATNeuron getToNeuron() {
		return toNeuron;
	}

	public boolean isRecurrent() {
		return recurrent;
	}
}
