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
package org.encog.neural.networks.training.genetic;

import org.encog.ml.genetic.genes.DoubleGene;
import org.encog.ml.genetic.genes.Gene;
import org.encog.ml.genetic.genome.BasicGenome;
import org.encog.ml.genetic.genome.Chromosome;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;

/**
 * Implements a genome that allows a feedforward neural network to be trained
 * using a genetic algorithm. The chromosome for a feed forward neural network
 * is the weight and bias matrix.
 */
public class NeuralGenome extends BasicGenome {
	
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The chromosome.
	 */
	private final Chromosome networkChromosome;

	/**
	 * Construct a neural genome.
	 * @param network The network to use.
	 */
	public NeuralGenome(
			final BasicNetwork network) {
		setOrganism(network);

		this.networkChromosome = new Chromosome();

		// create an array of "double genes"
		final int size = network.getStructure().calculateSize();
		for (int i = 0; i < size; i++) {
			final Gene gene = new DoubleGene();
			this.networkChromosome.getGenes().add(gene);
		}

		getChromosomes().add(this.networkChromosome);

		encode();
	}

	/**
	 * Decode the genomes into a neural network.
	 */
	public final void decode() {
		final double[] net = new double[this.networkChromosome.getGenes()
				.size()];
		for (int i = 0; i < net.length; i++) {
			final DoubleGene gene = (DoubleGene) this.networkChromosome
					.getGenes().get(i);
			net[i] = gene.getValue();

		}
		NetworkCODEC.arrayToNetwork(net, (BasicNetwork) getOrganism());

	}

	/**
	 * Encode the neural network into genes.
	 */
	public final void encode() {
		final double[] net = NetworkCODEC
				.networkToArray((BasicNetwork) getOrganism());

		for (int i = 0; i < net.length; i++) {
			((DoubleGene) this.networkChromosome.getGene(i)).setValue(net[i]);
		}
	}
}
