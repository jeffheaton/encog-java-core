package org.encog.neural.networks.training.neat;

import org.encog.neural.networks.synapse.neat.NEATNeuronType;

public class NEATNeuronGene {

	  private int id;
	  private NEATNeuronType neuronType;
	  private boolean recurrent;
	  private double activationResponse;
	  private double splitY; 
	  private double splitX;
	  	
	public NEATNeuronGene(
			NEATNeuronType type,
            int id,
            double splitY,
            double splitX,
            boolean recurrent,
            double act)
	{
		this.neuronType = type;
		this.id = id;
		this.splitX = splitX;
		this.splitY = splitY;
		this.recurrent = recurrent;
		this.activationResponse = act;
	}

	public NEATNeuronGene(NEATNeuronType type,
            int         id,
            double      splitY,
            double      splitX)
	{
		this(type,id,splitY,splitX,false,1.0);
	}
  
	

	public int getId() {
		return id;
	}

	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public double getActivationResponse() {
		return activationResponse;
	}

	public double getSplitY() {
		return splitY;
	}

	public double getSplitX() {
		return splitX;
	}

	public void setNeuronType(NEATNeuronType neuronType) {
		this.neuronType = neuronType;
	}

	public void setActivationResponse(double activationResponse) {
		this.activationResponse = activationResponse;
	}

	public void setSplitY(double splitY) {
		this.splitY = splitY;
	}

	public void setSplitX(double splitX) {
		this.splitX = splitX;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
	}
	
	
	  
	  
	
}
