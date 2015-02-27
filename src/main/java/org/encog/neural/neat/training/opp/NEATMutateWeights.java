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
package org.encog.neural.neat.training.opp;

import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.opp.links.MutateLinkWeight;
import org.encog.neural.neat.training.opp.links.SelectLinks;

/**
 * Mutate the weights of a genome. A method is select the links for mutation.
 * Another method should also be provided for the actual mutation.
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
public class NEATMutateWeights extends NEATMutation {
	/**
	 * The method used to select the links to mutate.
	 */
	private final SelectLinks linkSelection;

	/**
	 * The method used to mutate the selected links.
	 */
	private final MutateLinkWeight weightMutation;

	/**
	 * Construct a weight mutation operator.
	 * @param theLinkSelection The method used to choose the links for mutation.
	 * @param theWeightMutation The method used to actually mutate the weights.
	 */
	public NEATMutateWeights(final SelectLinks theLinkSelection,
			final MutateLinkWeight theWeightMutation) {
		this.linkSelection = theLinkSelection;
		this.weightMutation = theWeightMutation;
	}

	/**
	 * @return The method used to select links for mutation.
	 */
	public SelectLinks getLinkSelection() {
		return this.linkSelection;
	}

	/**
	 * @return The method used to mutate the weights.
	 */
	public MutateLinkWeight getWeightMutation() {
		return this.weightMutation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		final NEATGenome target = obtainGenome(parents, parentIndex, offspring,
				offspringIndex);
		final double weightRange = ((NEATPopulation)getOwner().getPopulation()).getWeightRange();
		final List<NEATLinkGene> list = this.linkSelection.selectLinks(rnd,
				target);
		for (final NEATLinkGene gene : list) {
			this.weightMutation.mutateWeight(rnd, gene, weightRange);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
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
