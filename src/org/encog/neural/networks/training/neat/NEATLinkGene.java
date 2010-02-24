package org.encog.neural.networks.training.neat;

public class NEATLinkGene {
		
	private final int fromNeuronID;
	private final int toNeuronID;
	private double weight;
	private boolean enabled;
	private final boolean recurrent;
	private final int innovationID;
	

	public NEATLinkGene(
			int fromNeuronID, 
			int toNeuronID, 
			boolean enabled, 
			int innovationID, 
			double weight,
			boolean recurrent) {
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.enabled = enabled;
		this.innovationID = innovationID;
		this.weight = weight;
		this.recurrent = recurrent;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getFromNeuronID() {
		return fromNeuronID;
	}

	public int getToNeuronID() {
		return toNeuronID;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public int getInnovationID() {
		return innovationID;
	}
	
	
	
	
}
