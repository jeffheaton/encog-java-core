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
import java.util.HashMap;
import java.util.Map;

import org.encog.neural.neat.NEATPopulation;

/**
 * Implements a NEAT innovation list.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
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
public class NEATInnovationList implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The population.
	 */
	private NEATPopulation population;

	/**
	 * The list of innovations.
	 */
	private Map<String, NEATInnovation> list = new HashMap<String, NEATInnovation>();

	/**
	 * The default constructor, used mainly for persistance.
	 */
	public NEATInnovationList() {

	}

	/**
	 * Produce an innovation key for a neuron.
	 * @param id The neuron id.
	 * @return The newly created key.
	 */
	public static String produceKeyNeuron(long id) {
		StringBuilder result = new StringBuilder();
		result.append("n:");
		result.append(id);
		return result.toString();
	}

	/**
	 * Produce a key for a split neuron.
	 * @param fromID Thf from id.
	 * @param toID The to id.
	 * @return The key.
	 */
	public static String produceKeyNeuronSplit(long fromID, long toID) {
		StringBuilder result = new StringBuilder();
		result.append("ns:");
		result.append(fromID);
		result.append(":");
		result.append(toID);
		return result.toString();
	}

	/**
	 * Produce a key for a link.
	 * @param fromID The from id.
	 * @param toID The to id.
	 * @return The key for the link.
	 */
	public static String produceKeyLink(long fromID, long toID) {
		StringBuilder result = new StringBuilder();
		result.append("l:");
		result.append(fromID);
		result.append(":");
		result.append(toID);
		return result.toString();
	}

	/**
	 * Construct an innovation list, that includes the initial innovations.
	 * @param population The population to base this innovation list on.
	 */
	public NEATInnovationList(final NEATPopulation population) {

		this.population = population;

		this.findInnovation(this.population.assignGeneID()); // bias

		// input neurons
		for (int i = 0; i < population.getInputCount(); i++) {
			this.findInnovation(this.population.assignGeneID());
		}

		// output neurons
		for (int i = 0; i < population.getOutputCount(); i++) {
			this.findInnovation(this.population.assignGeneID());
		}
		
		// connections
		for (long fromID = 0; fromID < this.population.getInputCount() + 1; fromID++) {
			for (long toID = 0; toID < this.population.getOutputCount(); toID++) {
				findInnovation(fromID, toID);
			}
		}
		
		
		
	}

	/**
	 * Find an innovation for a hidden neuron that split a existing link. This
	 * is the means by which hidden neurons are introduced in NEAT.
	 * 
	 * @param fromID
	 *            The source neuron ID in the link.
	 * @param toID
	 *            The target neuron ID in the link.
	 * @return The newly created innovation, or the one that matched the search.
	 */
	public NEATInnovation findInnovationSplit(long fromID, long toID) {
		String key = NEATInnovationList.produceKeyNeuronSplit(fromID, toID);

		synchronized (this.list) {
			if (this.list.containsKey(key)) {
				return this.list.get(key);
			} else {
				long neuronID = this.population.assignGeneID();
				NEATInnovation innovation = new NEATInnovation();
				innovation
						.setInnovationID(this.population.assignInnovationID());
				innovation.setNeuronID(neuronID);
				list.put(key, innovation);
				
				// create other sides of split, if needed
				findInnovation(fromID,neuronID);
				findInnovation(neuronID,toID);
				return innovation;
			}
		}
	}

	/**
	 * Find an innovation for a single neuron. Single neurons were created
	 * without producing a split. This means, the only single neurons are the
	 * input, bias and output neurons.
	 * 
	 * @param neuronID
	 *            The neuron ID to find.
	 * @return The newly created innovation, or the one that matched the search.
	 */
	public NEATInnovation findInnovation(long neuronID) {
		String key = NEATInnovationList.produceKeyNeuron(neuronID);

		synchronized (this.list) {
			if (this.list.containsKey(key)) {
				return this.list.get(key);
			} else {
				NEATInnovation innovation = new NEATInnovation();
				innovation.setInnovationID(this.population.assignInnovationID());
				innovation.setNeuronID(neuronID);
				list.put(key, innovation);
				return innovation;
			}
		}
	}

	/**
	 * Find an innovation for a new link added between two existing neurons.
	 * 
	 * @param fromID
	 *            The source neuron ID in the link.
	 * @param toID
	 *            The target neuron ID in the link.
	 * @return The newly created innovation, or the one that matched the search.
	 */
	public NEATInnovation findInnovation(long fromID, long toID) {
		String key = NEATInnovationList.produceKeyLink(fromID, toID);

		synchronized (this.list) {
			if (this.list.containsKey(key)) {
				return this.list.get(key);
			} else {
				NEATInnovation innovation = new NEATInnovation();
				innovation
						.setInnovationID(this.population.assignInnovationID());
				innovation.setNeuronID(-1);
				list.put(key, innovation);
				return innovation;
			}
		}
	}

	/**
	 * Set the population that this genome belongs to.
	 * @param population The population.
	 */
	public void setPopulation(NEATPopulation population) {
		this.population = population;
	}
	
	/**
	 * @return A list of innovations.
	 */
	public Map<String, NEATInnovation> getInnovations() {
		return list;
	}
}
