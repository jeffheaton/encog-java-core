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

	private double activationResponse;
	private long id;
	private NEATNeuronType neuronType;
	private boolean recurrent;
	private double splitX;
	private double splitY;

	public NEATNeuronGene(final NEATNeuronType type, final long id,
			final double splitY, final double splitX) {
		this(type, id, splitY, splitX, false, 1.0);
	}

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

	@Override
	public int compareTo(final Gene o) {
		return 0;
	}

	public void copy(final Gene gene) {
		final NEATNeuronGene other = (NEATNeuronGene) gene;
		activationResponse = other.activationResponse;
		id = other.id;
		neuronType = other.neuronType;
		recurrent = other.recurrent;
		splitX = other.splitX;
		splitY = other.splitY;

	}

	public double getActivationResponse() {
		return activationResponse;
	}

	@Override
	public long getId() {
		return id;
	}

	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	public double getSplitX() {
		return splitX;
	}

	public double getSplitY() {
		return splitY;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public void setActivationResponse(final double activationResponse) {
		this.activationResponse = activationResponse;
	}

	@Override
	public void setId(final long id) {
		this.id = id;
	}

	public void setNeuronType(final NEATNeuronType neuronType) {
		this.neuronType = neuronType;
	}

	public void setRecurrent(final boolean recurrent) {
		this.recurrent = recurrent;
	}

	public void setSplitX(final double splitX) {
		this.splitX = splitX;
	}

	public void setSplitY(final double splitY) {
		this.splitY = splitY;
	}

}
