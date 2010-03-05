/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;

public class NEATInnovationDB {

	private List<NEATInnovation> innovations = new ArrayList<NEATInnovation>();
	private int nextNeuronID = 0;
	private int nextInnovationID = 0;

	public NEATInnovationDB(Chromosome links,
			Chromosome neurons) {

		for (Gene gene : neurons.getGenes()) {
			NEATNeuronGene neuronGene = (NEATNeuronGene)gene;
			
			NEATInnovation innovation = new NEATInnovation(neuronGene,
					assignInnovationID(), assignNeuronID());
			this.innovations.add(innovation);
		}

		for (Gene gene : links.getGenes()) {
			NEATLinkGene linkGene = (NEATLinkGene)gene;
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
