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

package org.encog.neural.networks.training.genetic;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.solve.genetic.genes.DoubleGene;
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.BasicGenome;
import org.encog.solve.genetic.genome.Chromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a genome that allows a feedforward neural
 * network to be trained using a genetic algorithm. The chromosome for a feed
 * forward neural network is the weight and bias matrix.
 */
public class NeuralGenome extends BasicGenome {

	private Chromosome networkChromosome;

	public NeuralGenome(NeuralGeneticAlgorithm nga, final BasicNetwork network) {
		super(nga.getGenetic());

		setOrganism(network);

		this.networkChromosome = new Chromosome();

		// create an array of "double genes"
		int size = network.getStructure().calculateSize();
		for(int i=0;i<size;i++)
		{
			Gene gene = new DoubleGene();
			this.networkChromosome.getGenes().add(gene);
		}

		this.getChromosomes().add(this.networkChromosome);

		encode();
	}


	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void decode() {
		double[] net = new double[networkChromosome.getGenes().size()];
		for(int i=0;i<net.length;i++)
		{
			DoubleGene gene = (DoubleGene)networkChromosome.getGenes().get(i);
			net[i] = gene.getValue();

		}
		NetworkCODEC.arrayToNetwork(net, (BasicNetwork)getOrganism());

	}

	public void encode() {
		double[] net = NetworkCODEC.networkToArray((BasicNetwork)getOrganism());

		for(int i=0;i<net.length;i++)
		{
			((DoubleGene)networkChromosome.getGene(i)).setValue(net[i]);
		}
	}
}
