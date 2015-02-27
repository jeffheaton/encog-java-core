/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.neat;

import java.io.Serializable;

/**
 * Implements a link between two NEAT neurons.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/
 * Encog's NEAT implementation was drawn from the following three Journal
 * Articles. For more complete BibTeX sources, see NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public class NEATLink implements Serializable, Comparable<NEATLink> {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -4117045705080951946L;

	/**
	 * The source neuron.
	 */
	private int fromNeuron;

	/**
	 * The target neuron.
	 */
	private int toNeuron;

	/**
	 * The weight.
	 */
	private double weight;

	/**
	 * Construct a NEAT link.
	 * @param theFromNeuron The from neuron.
	 * @param theToNeuron The to neuron.
	 * @param theWeight The weight.
	 */
	public NEATLink(final int theFromNeuron, final int theToNeuron,
			final double theWeight) {
		this.fromNeuron = theFromNeuron;
		this.toNeuron = theToNeuron;
		this.weight = theWeight;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final NEATLink other) {
		final int result = this.fromNeuron - other.fromNeuron;
		if (result != 0) {
			return result;
		}

		return this.toNeuron - other.toNeuron;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof NEATLink)) {
			return false;
		}
		final NEATLink otherMyClass = (NEATLink) other;
		return compareTo(otherMyClass) == 0;
	}

	/**
	 * @return The from neuron.
	 */
	public int getFromNeuron() {
		return this.fromNeuron;
	}

	/**
	 * @return The to neuron.
	 */
	public int getToNeuron() {
		return this.toNeuron;
	}

	/**
	 * @return The weight of the link.
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * Set the from neuron.
	 * @param fromNeuron The from neuron.
	 */
	public void setFromNeuron(final int fromNeuron) {
		this.fromNeuron = fromNeuron;
	}

	/**
	 * Set the target neuron.
	 * @param toNeuron The target neuron.
	 */
	public void setToNeuron(final int toNeuron) {
		this.toNeuron = toNeuron;
	}

	/**
	 * Set the weight of this link.
	 * @param weight The weight.
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
		result.append("[NEATLink: fromNeuron=");
		result.append(this.fromNeuron);
		result.append(", toNeuron=");
		result.append(this.toNeuron);
		result.append("]");
		return result.toString();
	}
}
