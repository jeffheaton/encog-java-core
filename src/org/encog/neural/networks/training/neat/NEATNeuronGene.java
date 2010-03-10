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
import org.encog.solve.genetic.genes.BasicGene;
import org.encog.solve.genetic.genes.Gene;

/**
 * Implements a NEAT neuron gene.  
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATNeuronGene extends BasicGene {

	/**
	 * The activation response, the slope of the activation function.
	 */
	private double activationResponse;
	
	/**
	 * The neuron id.
	 */
	private long id;
	
	/**
	 * The neuron type.
	 */
	private NEATNeuronType neuronType;
	
	/**
	 * True if this is recurrent.
	 */
	private boolean recurrent;
	
	/**
	 * The x-split.
	 */
	private double splitX;
	
	/**
	 * The y-split.
	 */
	private double splitY;

	/**
	 * Construct a gene.
	 * @param type The type of neuron.
	 * @param id The id of this gene.
	 * @param splitY The split y. 
	 * @param splitX The split x.
	 */
	public NEATNeuronGene(final NEATNeuronType type, final long id,
			final double splitY, final double splitX) {
		this(type, id, splitY, splitX, false, 1.0);
	}

	/**
	 * Construct a neuron gene.
	 * @param type The type of neuron.
	 * @param id The id of this gene.
	 * @param splitY The split y.
	 * @param splitX The split x.
	 * @param recurrent True if this is a recurrent link.
	 * @param act The activation response.
	 */
	public NEATNeuronGene(final NEATNeuronType type, final long id,
			final double splitY, final double splitX, final boolean recurrent,
			final double act) {
		neuronType = type;
		this.id = id;
		this.splitX = splitX;
		this.splitY = splitY;
		this.recurrent = recurrent;
		activationResponse = act;
	}

	/**
	 * Copy another gene to this one.
	 * @param gene The other gene.
	 */
	public void copy(final Gene gene) {
		final NEATNeuronGene other = (NEATNeuronGene) gene;
		activationResponse = other.activationResponse;
		id = other.id;
		neuronType = other.neuronType;
		recurrent = other.recurrent;
		splitX = other.splitX;
		splitY = other.splitY;

	}

	/**
	 * @return The activation response.
	 */
	public double getActivationResponse() {
		return activationResponse;
	}

	/**
	 * @return The id for this neuron gene.
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * @return The type for this neuron.
	 */
	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	/**
	 * @return The split x value.
	 */
	public double getSplitX() {
		return splitX;
	}

	/**
	 * @return The split y value.
	 */
	public double getSplitY() {
		return splitY;
	}

	/**
	 * @return True if this is recurrent.
	 */
	public boolean isRecurrent() {
		return recurrent;
	}

	/**
	 * Set the activation response.
	 * @param activationResponse The activation response.
	 */
	public void setActivationResponse(final double activationResponse) {
		this.activationResponse = activationResponse;
	}

	/**
	 * Set the gene id.
	 * @param id the id.
	 */
	@Override
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * Set the neuron type.
	 * @param neuronType The neuron type.
	 */
	public void setNeuronType(final NEATNeuronType neuronType) {
		this.neuronType = neuronType;
	}

	/**
	 * Set if this is a recurrent neuron.
	 * @param recurrent True if this is a recurrent neuron.
	 */
	public void setRecurrent(final boolean recurrent) {
		this.recurrent = recurrent;
	}

	/**
	 * Set the split x.
	 * @param splitX The split x.
	 */
	public void setSplitX(final double splitX) {
		this.splitX = splitX;
	}

	/**
	 * Set the split y.
	 * @param splitY The split y.
	 */
	public void setSplitY(final double splitY) {
		this.splitY = splitY;
	}

}
