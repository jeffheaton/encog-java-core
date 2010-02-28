package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.networks.synapse.neat.NEATNeuronType;

public class NEATInnovationDB {

	private List<NEATInnovation> innovations = new ArrayList<NEATInnovation>();
	private int nextNeuronID = 0;
	private int nextInnovationID = 0;

	public NEATInnovationDB(List<NEATLinkGene> links,
			List<NEATNeuronGene> neurons) {

		for (NEATNeuronGene neuronGene : neurons) {
			NEATInnovation innovation = new NEATInnovation(neuronGene,
					assignInnovationID(), assignNeuronID());
			this.innovations.add(innovation);
		}

		for (NEATLinkGene linkGene : links) {
			NEATInnovation innovation = new NEATInnovation(linkGene
					.getFromNeuronID(), linkGene.getToNeuronID(),
					NEATInnovationType.NewLink, assignInnovationID());
			this.innovations.add(innovation);

		}
	}

	private int assignNeuronID() {
		return this.nextNeuronID++;
	}

	private int assignInnovationID() {
		return this.nextInnovationID++;
	}

	public NEATInnovation checkInnovation(int in, int out, NEATInnovationType type) {
		for (NEATInnovation innovation : this.innovations) {

			if ((innovation.getFromNeuronID() == in)
					&& (innovation.getToNeuronID() == out)
					&& (innovation.getInnovationType() == type)) {
				return innovation;
			}
		}

		return null;
	}

	public void createNewInnovation(int in, int out, NEATInnovationType type) {
		NEATInnovation newInnovation = new NEATInnovation(in, out, type,
				assignInnovationID());

		if (type == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(this.assignNeuronID());
		}

		this.innovations.add(newInnovation);
	}

	public int createNewInnovation(int from, int to,
			NEATInnovationType innovationType, NEATNeuronType neuronType,
			double x, double y) {
		NEATInnovation newInnovation = new NEATInnovation(from, to,
				innovationType, assignInnovationID(), neuronType, x, y);

		if (innovationType == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(this.assignNeuronID());
		}

		this.innovations.add(newInnovation);

		return (this.nextNeuronID - 1); // ??????? should it be innov?
	}
	
	public NEATNeuronGene createNeuronFromID(int neuronID)
	{
		NEATNeuronGene result = new  NEATNeuronGene(NEATNeuronType.Hidden,0,0,0);

		for(NEATInnovation innovation: this.innovations)
		{
	    if (innovation.getNeuronID() == neuronID)
	    {
	    	result.setNeuronType(innovation.getNeuronType());
	    	result.setId(innovation.getNeuronID());
	    	result.setSplitY(innovation.getSplitY());
	    	result.setSplitX(innovation.getSplitX());

	      return result;
	    }
	  }

	  return result;
	}

	public int assignInnovationNumber() {
		return this.nextInnovationID++;
	}

	public int getNeuronID(int id) {
		return this.innovations.get(id).getNeuronID();
	}

}
