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

import org.encog.solve.genetic.genes.BasicGene;
import org.encog.solve.genetic.genes.Gene;

/**
 * Implements a NEAT link gene. This describes a way in which two neurons
 * are linked. 
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATLinkGene extends BasicGene {

	private int fromNeuronID;
	private boolean recurrent;
	private int toNeuronID;
	private double weight;

	public NEATLinkGene(final int fromNeuronID, final int toNeuronID,
			final boolean enabled, final long innovationID,
			final double weight, final boolean recurrent) {
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		setEnabled(enabled);
		setInnovationId(innovationID);
		this.weight = weight;
		this.recurrent = recurrent;
	}

	public void copy(final Gene gene) {
		final NEATLinkGene other = (NEATLinkGene) gene;
		setEnabled(other.isEnabled());
		fromNeuronID = other.fromNeuronID;
		toNeuronID = other.toNeuronID;
		setInnovationId(other.getInnovationId());
		recurrent = other.recurrent;
		weight = other.weight;
	}

	public int getFromNeuronID() {
		return fromNeuronID;
	}

	public int getToNeuronID() {
		return toNeuronID;
	}

	public double getWeight() {
		return weight;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public void setWeight(final double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NEATLinkGene:innov=");
		result.append(getInnovationId());
		result.append(",enabled=");
		result.append(isEnabled());
		result.append(",from=");
		result.append(fromNeuronID);
		result.append(",to=");
		result.append(toNeuronID);
		result.append("]");
		return result.toString();
	}

}
