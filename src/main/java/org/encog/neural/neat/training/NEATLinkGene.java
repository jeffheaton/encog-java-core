/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.neat.training;

import java.io.Serializable;

import org.encog.ml.genetic.genes.BasicGene;
import org.encog.ml.genetic.genes.Gene;

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
public class NEATLinkGene extends BasicGene implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The from neuron id.
	 */
	private long fromNeuronID;

	/**
	 * Is this a recurrent connection.
	 */
	private boolean recurrent;

	/**
	 * The to neuron id.
	 */	
	private long toNeuronID;

	/**
	 * The weight of this link.
	 */
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
	 * {@inheritDoc}
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

	public void setFromNeuronID(int i) {
		this.fromNeuronID = i;
	}
	
	public void setToNeuronID(int i) {
		this.toNeuronID = i;
	}

	public void setRecurrent(boolean b) {
		this.recurrent = b;
	}

}
