/*
 * Encog(tm) Core v2.5 
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

import org.encog.persist.annotations.EGAttribute;
import org.encog.solve.genetic.genes.BasicGene;
import org.encog.solve.genetic.genes.Gene;

/**
 * Implements a NEAT link gene. This describes a way in which two neurons are
 * linked.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATLinkGene extends BasicGene {

	/**
	 * The from neuron id.
	 */
	@EGAttribute
	private long fromNeuronID;

	/**
	 * Is this a recurrent connection.
	 */
	@EGAttribute
	private boolean recurrent;

	/**
	 * The to neuron id.
	 */
	@EGAttribute
	private long toNeuronID;

	/**
	 * The weight of this link.
	 */
	@EGAttribute
	private double weight;

	/**
	 * Default constructor, used mainly for persistence.
	 */
	public NEATLinkGene() {

	}

	/**
	 * Construct a NEAT link gene.
	 * @param fromNeuronID The source neuron.
	 * @param toNeuronID The target neuron.
	 * @param enabled Is this link enabled.
	 * @param innovationID The innovation id.
	 * @param weight The weight.
	 * @param recurrent Is this a recurrent link?
	 */
	public NEATLinkGene(final long fromNeuronID, final long toNeuronID,
			final boolean enabled, final long innovationID,
			final double weight, final boolean recurrent) {
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		setEnabled(enabled);
		setInnovationId(innovationID);
		this.weight = weight;
		this.recurrent = recurrent;
	}

	/**
	 * Copy from another gene.
	 * 
	 * @param gene
	 *            The other gene.
	 */
	public void copy(final Gene gene) {
		final NEATLinkGene other = (NEATLinkGene) gene;
		setEnabled(other.isEnabled());
		this.fromNeuronID = other.fromNeuronID;
		this.toNeuronID = other.toNeuronID;
		setInnovationId(other.getInnovationId());
		this.recurrent = other.recurrent;
		this.weight = other.weight;
	}

	/**
	 * @return The from neuron id.
	 */
	public long getFromNeuronID() {
		return this.fromNeuronID;
	}

	/**
	 * @return The to neuron id.
	 */
	public long getToNeuronID() {
		return this.toNeuronID;
	}

	/**
	 * @return The weight of this connection.
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * @return True if this is a recurrent link.
	 */
	public boolean isRecurrent() {
		return this.recurrent;
	}

	/**
	 * Set the weight of this connection.
	 * 
	 * @param weight
	 *            The connection weight.
	 */
	public void setWeight(final double weight) {
		this.weight = weight;
	}

	/**
	 * @return This link as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NEATLinkGene:innov=");
		result.append(getInnovationId());
		result.append(",enabled=");
		result.append(isEnabled());
		result.append(",from=");
		result.append(this.fromNeuronID);
		result.append(",to=");
		result.append(this.toNeuronID);
		result.append("]");
		return result.toString();
	}

}
