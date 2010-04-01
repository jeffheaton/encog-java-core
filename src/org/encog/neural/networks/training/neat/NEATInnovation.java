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

import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.solve.genetic.innovation.BasicInnovation;

/**
 * Implements a NEAT innovation.  This lets NEAT track what changes it has previously
 * tried with a neural network.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATInnovation extends BasicInnovation {

	/**
	 * The from neuron id.
	 */
	private long fromNeuronID;
	
	/**
	 * The type of innovation.
	 */
	private NEATInnovationType innovationType;
	
	/**
	 * The neuron id.
	 */
	private long neuronID;
	
	/**
	 * The type of neuron, or none, if this is a link innovation.
	 */
	private NEATNeuronType neuronType;
	
	/**
	 * The split x property.
	 */
	private double splitX;
	
	/**
	 * The split y property.
	 */
	private double splitY;
	
	/**
	 * The to neuron's id.
	 */
	private long toNeuronID;

	/**
	 * Construct an innovation.
	 * @param fromNeuronID The from neuron.
	 * @param toNeuronID The two neuron.
	 * @param innovationType The innovation type.
	 * @param innovationID The innovation id.
	 */
	public NEATInnovation(final long fromNeuronID, final long toNeuronID,
			final NEATInnovationType innovationType, final long innovationID) {

		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.innovationType = innovationType;
		setInnovationID(innovationID);

		neuronID = -1;
		splitX = 0;
		splitY = 0;
		neuronType = NEATNeuronType.None;
	}
	
	public NEATInnovation()
	{
		
	}

	/**
	 * Construct an innovation.
	 * @param fromNeuronID The from neuron.
	 * @param toNeuronID The to neuron.
	 * @param innovationType The innovation type.
	 * @param innovationID The innovation id.
	 * @param neuronType The neuron type.
	 * @param x The x coordinate.
	 * @param y THe y coordinate.
	 */
	public NEATInnovation(final long fromNeuronID, final long toNeuronID,
			final NEATInnovationType innovationType, final long innovationID,
			final NEATNeuronType neuronType, final double x, final double y) {
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.innovationType = innovationType;
		setInnovationID(innovationID);
		this.neuronType = neuronType;
		splitX = x;
		splitY = y;

		neuronID = 0;
	}

	/**
	 * Construct an innovation.
	 * @param neuronGene The neuron gene.
	 * @param innovationID The innovation id.
	 * @param neuronID The neuron id.
	 */
	public NEATInnovation(final NEATNeuronGene neuronGene,
			final long innovationID, final long neuronID) {

		this.neuronID = neuronID;
		setInnovationID(innovationID);
		splitX = neuronGene.getSplitX();
		splitY = neuronGene.getSplitY();

		neuronType = neuronGene.getNeuronType();
		innovationType = NEATInnovationType.NewNeuron;
		fromNeuronID = -1;
		toNeuronID = -1;
	}

	/**
	 * @return The from neuron id.
	 */
	public long getFromNeuronID() {
		return fromNeuronID;
	}

	/**
	 * @return The innovation type.
	 */
	public NEATInnovationType getInnovationType() {
		return innovationType;
	}

	/**
	 * @return The neuron ID.
	 */
	public long getNeuronID() {
		return neuronID;
	}

	/**
	 * @return The neuron type.
	 */
	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	/**
	 * @return The split x.
	 */
	public double getSplitX() {
		return splitX;
	}

	/**
	 * @return The split y.
	 */
	public double getSplitY() {
		return splitY;
	}

	/**
	 * @return The to neuron id.
	 */
	public long getToNeuronID() {
		return toNeuronID;
	}

	/**
	 * Set the neuron id.
	 * @param neuronID The neuron id.
	 */
	public void setNeuronID(final long neuronID) {
		this.neuronID = neuronID;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[NeatInnovation:type=");
		switch(this.innovationType)
		{
			case NewLink: 
				result.append("link");
				break;
			case NewNeuron: 
				result.append("link");
				break;
		}
		result.append(",from=");
		result.append(this.fromNeuronID);
		result.append(",to=");
		result.append(this.toNeuronID);
		result.append(",splitX=");
		result.append(this.splitX);
		result.append(",splitY=");
		result.append(this.splitY);
		result.append("]");
		return result.toString();
	}

}
