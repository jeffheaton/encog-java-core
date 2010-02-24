package org.encog.neural.networks.training.neat;

import java.util.List;

import org.encog.neural.networks.synapse.neat.NEATNeuron;
import org.encog.neural.networks.synapse.neat.NEATNeuronType;

public class NEATInnovation {
	
	  private final NEATInnovationType innovationType;
	  private final NEATNeuronType neuronType;
	  private final int innovationID;		
	  private final int fromNeuronID;
	  private final int toNeuronID;
	  private int neuronID;
	  private final double splitX;
	  private final double splitY;
	  
	  public NEATInnovation(
			int fromNeuronID, 
			int toNeuronID,
			NEATInnovationType innovationType, 
			int innovationID) {
		  
		  this.fromNeuronID = fromNeuronID;
		  this.toNeuronID = toNeuronID;
		  this.innovationType = innovationType;
		  this.innovationID = innovationID;
		  
		  this.neuronID = -1;
		  this.splitX = 0;
		  this.splitY = 0;
		  this.neuronType = NEATNeuronType.None;		  
	  }
	

	public NEATInnovation(
			NEATNeuronGene neuronGene, 
			int innovationID, 
			int neuronID) {
		
		this.neuronID = neuronID;
		this.innovationID = innovationID;
		this.splitX = neuronGene.getSplitX();
		this.splitY = neuronGene.getSplitY();
		
		this.neuronType = neuronGene.getNeuronType();
		this.innovationType = NEATInnovationType.NewNeuron;
		this.fromNeuronID = -1;
		this.toNeuronID = -1;		
	}

	public NEATInnovation(
			int fromNeuronID, 
			int toNeuronID,
			NEATInnovationType innovationType, 
			int innovationID,
			NEATNeuronType neuronType, 
			double x, 
			double y) 
	{
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.innovationType = innovationType;
		this.innovationID = innovationID;
		this.neuronType = neuronType;
		this.splitX = x;
		this.splitY = y;
		
		this.neuronID = 0;
	}


	public NEATInnovationType getInnovationType() {
		return innovationType;
	}


	public NEATNeuronType getNeuronType() {
		return neuronType;
	}


	public int getInnovationID() {
		return innovationID;
	}


	public int getFromNeuronID() {
		return fromNeuronID;
	}


	public int getToNeuronID() {
		return toNeuronID;
	}


	public int getNeuronID() {
		return neuronID;
	}


	public double getSplitX() {
		return splitX;
	}


	public double getSplitY() {
		return splitY;
	}


	public void setNeuronID(int neuronID) {
		this.neuronID = neuronID;
	}


	


}
