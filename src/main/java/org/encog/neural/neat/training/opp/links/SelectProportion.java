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
package org.encog.neural.neat.training.opp.links;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;

/**
 * Select a random proportion of links to mutate.
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
public class SelectProportion implements SelectLinks {
	
	/**
	 * The portion of the links to mutate. 0.0 for none, 1.0 for all.
	 */
	private double proportion;
	
	/**
	 * The trainer.
	 */
	private EvolutionaryAlgorithm trainer;
	
	/**
	 * Select based on proportion.
	 * @param theProportion The proportion to select from.
	 */
	public SelectProportion(double theProportion) {
		this.proportion = theProportion;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(EvolutionaryAlgorithm theTrainer) {
		this.trainer = theTrainer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<NEATLinkGene> selectLinks(Random rnd, NEATGenome genome) {
		List<NEATLinkGene> result = new ArrayList<NEATLinkGene>();
		
		boolean mutated = false;
		
		for (final NEATLinkGene linkGene : genome.getLinksChromosome()) {
			if (rnd.nextDouble() < this.proportion) {
				mutated = true;
				result.add(linkGene);	
			}
		}
		
		if( !mutated ) {
			int idx = rnd.nextInt(genome.getLinksChromosome().size());
			NEATLinkGene linkGene  = genome.getLinksChromosome().get(idx);
			result.add(linkGene);	
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EvolutionaryAlgorithm getTrainer() {
		return trainer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":proportion=");
		result.append(this.proportion);
		result.append("]");
		return result.toString();
	}
	
	
}
