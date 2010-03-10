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
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;
import org.encog.solve.genetic.innovation.BasicInnovationList;
import org.encog.solve.genetic.innovation.Innovation;
import org.encog.solve.genetic.population.Population;

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
public class NEATInnovationList extends BasicInnovationList {

	private long nextNeuronID = 0;

	private Population population;
	
	public NEATInnovationList(Population population, final Chromosome links, final Chromosome neurons) {

		this.population = population;
		for (final Gene gene : neurons.getGenes()) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;

			final NEATInnovation innovation = new NEATInnovation(neuronGene,
					population.assignInnovationID(), assignNeuronID());
			add(innovation);
		}

		for (final Gene gene : links.getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			final NEATInnovation innovation = new NEATInnovation(linkGene
					.getFromNeuronID(), linkGene.getToNeuronID(),
					NEATInnovationType.NewLink, this.population.assignInnovationID());
			add(innovation);

		}
	}

	private long assignNeuronID() {
		return nextNeuronID++;
	}

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

		return result;
	}

	public void createNewInnovation(final long in, final long out,
			final NEATInnovationType type) {
		final NEATInnovation newInnovation = new NEATInnovation(in, out, type,
				this.population.assignInnovationID());

		if (type == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(assignNeuronID());
		}

		add(newInnovation);
	}

	public long createNewInnovation(final long from, final long to,
			final NEATInnovationType innovationType,
			final NEATNeuronType neuronType, final double x, final double y) {
		final NEATInnovation newInnovation = new NEATInnovation(from, to,
				innovationType, population.assignInnovationID(), neuronType, x, y);

		if (innovationType == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(assignNeuronID());
		}

		add(newInnovation);

		return (this.nextNeuronID - 1); // ??????? should it be innov?
	}
}
