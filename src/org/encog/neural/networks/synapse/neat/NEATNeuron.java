package org.encog.neural.networks.synapse.neat;

import java.util.ArrayList;
import java.util.List;

public class NEATNeuron {

	  private final List<NEATLink> inboundLinks = new ArrayList<NEATLink>();
	  private final List<NEATLink> outputboundLinks = new ArrayList<NEATLink>();
	  private final double sumActivation;
	  private double output;
	  private final NEATNeuronType neuronType;
	  private final int neuronID;
	  private final double activationResponse;
	  private final int posX;
	  private final int posY;
	  private final double splitY; 
	  private final double splitX;
	  
	public NEATNeuron(
			NEATNeuronType neuronType, 
			int neuronID, 
			double splitY,
			double splitX, 
			double activationResponse) {
		this.neuronType = neuronType;
		this.neuronID = neuronID;
		this.splitY = splitY;
		this.splitX = splitX;
		this.activationResponse = activationResponse;
		this.posX = 0;
		this.posY = 0;
		this.output = 0;
		this.sumActivation = 0;
	}
	public List<NEATLink> getInboundLinks() {
		return inboundLinks;
	}
	public List<NEATLink> getOutputboundLinks() {
		return outputboundLinks;
	}
	public double getSumActivation() {
		return sumActivation;
	}
	public double getOutput() {
		return output;
	}
	public NEATNeuronType getNeuronType() {
		return neuronType;
	}
	public int getNeuronID() {
		return neuronID;
	}
	public double getActivationResponse() {
		return activationResponse;
	}
	public int getPosX() {
		return posX;
	}
	public int getPosY() {
		return posY;
	}
	public double getSplitY() {
		return splitY;
	}
	public double getSplitX() {
		return splitX;
	}
	public void setOutput(double output) {
		this.output = output;
		
	}
	
	  
}
