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
package org.encog.neural.neat.training;

import java.io.Serializable;

/**
 * Implements a NEAT innovation. This lets NEAT track what changes it has
 * previously tried with a neural network.
 *
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/ Encog's NEAT implementation was drawn from
 * the following three Journal Articles. For more complete BibTeX sources, see
 * NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 *
 */
public class NEATInnovation implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The neuron id.
	 */
	private long neuronID;

	/**
	 * Default constructor, used mainly for persistence.
	 */
	public NEATInnovation() {

	}

	/**
	 * @return The neuron ID.
	 */
	public long getNeuronID() {
		return this.neuronID;
	}

	/**
	 * Set the neuron id.
	 *
	 * @param neuronID
	 *            The neuron id.
	 */
	public void setNeuronID(final long neuronID) {
		this.neuronID = neuronID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NeatInnovation:");
		result.append("id=");
		result.append(this.getInnovationID());
		result.append(",neuron=");
		result.append(this.neuronID);
		result.append("]");
		return result.toString();
	}
	
	/**
	 * The innovation id.
	 */
	private long innovationID;

	/**
	 * @return The innovation ID.
	 */
	public long getInnovationID() {
		return innovationID;
	}

	/**
	 * Set the innovation id.
	 * @param theInnovationID The innovation id.
	 */
	public void setInnovationID(final long theInnovationID) {
		this.innovationID = theInnovationID;
	}
}
