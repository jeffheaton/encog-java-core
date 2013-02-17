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
package org.encog.neural.neat.training.opp;

import java.util.List;
import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.opp.links.MutateLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectLinks;

/**
 * Mutate the weights of a genome. This works very similar to Simulated
 * Annealing, and is used to fine tune the weights of a genome.
 * 
 * The activation function will be randomly chosen from the activation function 
 * factory stored in the population.
 */
public class NEATMutateWeights extends NEATMutation {
	
	private SelectLinks linkSelection; 
	private MutateLinkWeight weightMutation;
	
	public NEATMutateWeights(SelectLinks theLinkSelection, MutateLinkWeight theWeightMutation) {
		this.linkSelection = theLinkSelection;
		this.weightMutation = theWeightMutation;
	}
	
	
	
	/**
	 * @return the linkSelection
	 */
	public SelectLinks getLinkSelection() {
		return linkSelection;
	}



	/**
	 * @return the weightMutation
	 */
	public MutateLinkWeight getWeightMutation() {
		return weightMutation;
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		NEATGenome target = this.obtainGenome(parents, parentIndex, offspring,
				offspringIndex);
		double weightRange = this.getOwner().getNEATPopulation().getWeightRange();
		List<NEATLinkGene> list = this.linkSelection.selectLinks(rnd, target);
		for(NEATLinkGene gene: list) {
			this.weightMutation.mutateWeight(rnd, gene, weightRange);
		}
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":sel=");
		result.append(this.linkSelection.toString());
		result.append(",mutate=");
		result.append(this.weightMutation.toString());
		result.append("]");
		return result.toString();
	}

}
