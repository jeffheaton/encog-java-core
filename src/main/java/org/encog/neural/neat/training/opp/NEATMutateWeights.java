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

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;

/**
 * Mutate the weights of a genome. This works very similar to Simulated
 * Annealing, and is used to finetune the weights of a genome.
 * 
 * The activation function will be randomly chosen from the activation function 
 * factory stored in the population.
 */
public class NEATMutateWeights extends NEATMutation {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		NEATGenome target = this.obtainGenome(parents, parentIndex, offspring,
				offspringIndex);

		double weightRange = ((NEATPopulation) target.getPopulation())
				.getWeightRange();

		for (final NEATLinkGene linkGene : target.getLinksChromosome()) {
			if (Math.random() < getOwner().getMutateRate()) {
				if (Math.random() < getOwner().getProbNewMutate()) {
					linkGene.setWeight(RangeRandomizer.randomize(-weightRange,
							weightRange));
				} else {					
					double w = linkGene.getWeight()
							+ RangeRandomizer.randomize(-weightRange, weightRange)
							* getOwner().getMaxPertubation();
					w = NEATPopulation.clampWeight(w, weightRange);
					linkGene.setWeight(w);
				}
			}
		}
	}
}
