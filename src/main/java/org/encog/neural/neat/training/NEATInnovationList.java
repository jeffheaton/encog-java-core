/*
 * Encog(tm) Core v3.2 - Java Version
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
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.genetic.innovation.BasicInnovationList;
import org.encog.ml.genetic.innovation.Innovation;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.networks.training.TrainingError;

/**
 * Implements a NEAT innovation list.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATInnovationList extends BasicInnovationList implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The next neuron id.
	 */
	private long nextNeuronID = 0;

	/**
	 * The population.
	 */
	private NEATPopulation population;

	/**
	 * The default constructor, used mainly for persistance.
	 */
	public NEATInnovationList() {

	}

	/**
	 * Construct an innovation list, that includes the initial innovations.
	 * 
	 * @param population
	 *            The population.
	 * @param links
	 *            The links.
	 * @param neurons
	 *            THe neurons.
	 */
	public NEATInnovationList(final NEATPopulation population,
			final List<NEATLinkGene> links, final List<NEATNeuronGene> neurons) {

		this.population = population;
		for (final NEATNeuronGene neuronGene : neurons) {
			final NEATInnovation innovation = new NEATInnovation(neuronGene,
					population.assignInnovationID(), assignNeuronID());
			add(innovation);
		}

		for (final NEATLinkGene linkGene : links) {
			final NEATInnovation innovation = new NEATInnovation(linkGene
					.getFromNeuronID(), linkGene.getToNeuronID(),
					NEATInnovationType.NewLink, this.population
							.assignInnovationID());
			if( linkGene.getInnovationId()!=innovation.getInnovationID() ) {
				throw new EncogError("Invalid innovation number creating initial innovations. Gene: " + linkGene.getInnovationId() + ", Innov: " + innovation.getInnovationID());
			}
			add(innovation);

		}
	}

	/**
	 * Assign a neuron ID.
	 * 
	 * @return The neuron id.
	 */
	private long assignNeuronID() {
		return this.nextNeuronID++;
	}

	/**
	 * Check to see if we already have an innovation.
	 * 
	 * @param in
	 *            The input neuron.
	 * @param out
	 *            THe output neuron.
	 * @param type
	 *            The type.
	 * @return The innovation, either new or existing if found.
	 */
	public NEATInnovation checkInnovation(final long in, final long out,
			final NEATInnovationType type) {
		for (final Innovation i : getInnovations()) {
			final NEATInnovation innovation = (NEATInnovation) i;
			if ((innovation.getFromNeuronID() == in)
					&& (innovation.getToNeuronID() == out)
					&& (innovation.getInnovationType() == type)) {
				return innovation;
			}
		}

		return null;
	}

	/**
	 * Create a new neuron gene from an id.
	 * 
	 * @param neuronID
	 *            The neuron id.
	 * @return The neuron gene.
	 */
	public NEATNeuronGene createNeuronFromID(final long neuronID) {
		final NEATNeuronGene result = new NEATNeuronGene(NEATNeuronType.Hidden,
				0, 0, 0);

		for (final Innovation i : getInnovations()) {
			final NEATInnovation innovation = (NEATInnovation) i;
			if (innovation.getNeuronID() == neuronID) {
				result.setNeuronType(innovation.getNeuronType());
				result.setId(innovation.getNeuronID());
				result.setSplitY(innovation.getSplitY());
				result.setSplitX(innovation.getSplitX());
				return result;
			}
		}

		throw new TrainingError("Failed to find innovation for neuron: " + neuronID );
	}

	/**
	 * Create a new NEAT innovation, without the need of x & y.
	 * @param from The starting point of a link. 
	 * @param to The ending point of a link.
	 * @param type The type of innovation.
	 * @return The innovation id of the new innovation.
	 */
	public NEATInnovation createNewInnovation(final long from, final long to,
			final NEATInnovationType type) {
		final long innovationID = this.population.assignInnovationID();
		
		final NEATInnovation newInnovation = new NEATInnovation(from, to, type,
				innovationID);

		if (type == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(assignNeuronID());
		}

		add(newInnovation);
		return newInnovation;
	}

	/**
	 * Create a new innovation.
	 * 
	 * @param from
	 *            The from neuron.
	 * @param to
	 *            The to neuron.
	 * @param innovationType
	 *            THe innovation type.
	 * @param neuronType
	 *            The neuron type.
	 * @param x
	 *            The x-coordinate.
	 * @param y
	 *            The y-coordinate.
	 * @return The new neuron, if one was created.
	 */
	public NEATInnovation createNewInnovation(final long from, final long to,
			final NEATInnovationType innovationType,
			final NEATNeuronType neuronType, final double x, final double y) {
		
		final long innovationID = this.population.assignInnovationID();
		
		final NEATInnovation newInnovation = new NEATInnovation(from, to,
				innovationType, innovationID, neuronType, x, y);

		if (innovationType == NEATInnovationType.NewNeuron) {
			long neuronID = assignNeuronID();
			newInnovation.setNeuronID(neuronID);
		}

		add(newInnovation);

		return newInnovation; 
	}

	public void setPopulation(NEATPopulation population) {
		this.population = population;		
	}

	public void init() {
		long maxNeuron = 0;
		
		for(Innovation innovation: this.getInnovations() ) {
			NEATInnovation ni = (NEATInnovation)innovation;
			maxNeuron = Math.max(ni.getFromNeuronID(), maxNeuron);
			maxNeuron = Math.max(ni.getToNeuronID(), maxNeuron);
		}
		this.nextNeuronID = maxNeuron+1;
		
	}
	
	public void setNextNeuronID(int l) {
		this.nextNeuronID = l;		
	}
}
