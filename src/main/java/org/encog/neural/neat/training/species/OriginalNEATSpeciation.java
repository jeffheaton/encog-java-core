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
package org.encog.neural.neat.training.species;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.ThresholdSpeciation;
import org.encog.neural.neat.training.NEATGenome;

/**
 * The original NEAT Speciation Strategy. This is currently the only speciation
 * strategy implemented by Encog. There are other speciation strategies that
 * have been proposed (and implemented) for NEAT. One example is k-means.
 * 
 * NEAT starts up by creating an initial population of genomes with randomly
 * generated connections between input and output nodes. Not every input neuron
 * is necessarily connected, this allows NEAT to determine which input neurons
 * to use. Once the population has been generated it is speciated by iterating
 * over this population of genomes. The first genome is placed in its own
 * species.
 * 
 * The second genome is then compared to the first genome. If the compatibility
 * is below the threshold then the genome is placed into the same species as the
 * first. If not, the second genome founds a new species of its own. The
 * remaining genomes follow this same process.
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
 */
public class OriginalNEATSpeciation extends ThresholdSpeciation {

	/**
	 * The serial id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The adjustment factor for disjoint genes.
	 */
	private double constDisjoint = 1;

	/**
	 * The adjustment factor for excess genes.
	 */
	private double constExcess = 1;

	/**
	 * The adjustment factor for matched genes.
	 */
	private double constMatched = 0.4;



	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getCompatibilityScore(final Genome gen1,
			final Genome gen2) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		final NEATGenome genome1 = (NEATGenome)gen1;
		final NEATGenome genome2 = (NEATGenome)gen2;
		
		final int genome1Size = genome1.getLinksChromosome().size();
		final int genome2Size = genome2.getLinksChromosome().size();
		final int n = 1;// Math.max(genome1Size, genome2Size);

		int g1 = 0;
		int g2 = 0;

		while ((g1 < genome1Size) || (g2 < genome2Size)) {

			if (g1 == genome1Size) {
				g2++;
				numExcess++;
				continue;
			}

			if (g2 == genome2Size) {
				g1++;
				numExcess++;
				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = genome1.getLinksChromosome().get(g1)
					.getInnovationId();
			final long id2 = genome2.getLinksChromosome().get(g2)
					.getInnovationId();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {

				// get the weight difference between these two genes
				weightDifference += Math.abs(genome1.getLinksChromosome()
						.get(g1).getWeight()
						- genome2.getLinksChromosome().get(g2).getWeight());
				g1++;
				g2++;
				numMatched++;
			}

			// innovation numbers are different so increment the disjoint score
			if (id1 < id2) {
				numDisjoint++;
				g1++;
			}

			if (id1 > id2) {
				++numDisjoint;
				++g2;
			}

		}

		final double score = ((this.constExcess * numExcess) / n)
				+ ((this.constDisjoint * numDisjoint) / n)
				+ (this.constMatched * (weightDifference / numMatched));

		return score;
	}

	/**
	 * @return the constDisjoint
	 */
	public double getConstDisjoint() {
		return this.constDisjoint;
	}

	/**
	 * @return the constExcess
	 */
	public double getConstExcess() {
		return this.constExcess;
	}

	/**
	 * @return the constMatched
	 */
	public double getConstMatched() {
		return this.constMatched;
	}



	/**
	 * @param constDisjoint
	 *            the constDisjoint to set
	 */
	public void setConstDisjoint(final double constDisjoint) {
		this.constDisjoint = constDisjoint;
	}

	/**
	 * @param constExcess
	 *            the constExcess to set
	 */
	public void setConstExcess(final double constExcess) {
		this.constExcess = constExcess;
	}

	/**
	 * @param constMatched
	 *            the constMatched to set
	 */
	public void setConstMatched(final double constMatched) {
		this.constMatched = constMatched;
	}
}
