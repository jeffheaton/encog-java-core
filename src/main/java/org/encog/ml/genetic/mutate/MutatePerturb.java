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
package org.encog.ml.genetic.mutate;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.genetic.genome.DoubleArrayGenome;

/**
 * A simple mutation based on random numbers.
 */
public class MutatePerturb implements EvolutionaryOperator {

	/**
	 * The amount to perturb by.
	 */
	private final double perturbAmount;

	/**
	 * Construct a perturb mutation.
	 * @param thePerturbAmount The amount to mutate by(percent).
	 */
	public MutatePerturb(final double thePerturbAmount) {
		this.perturbAmount = thePerturbAmount;
	}

	/**
	 * Perform a perturb mutation on the specified chromosome.
	 * @param chromosome The chromosome to mutate.
	 */
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		DoubleArrayGenome parent = (DoubleArrayGenome)parents[parentIndex];
		DoubleArrayGenome child = (DoubleArrayGenome)offspring[offspringIndex];
		
		for(int i=0;i<parent.size();i++) {
			double value = parent.getData()[i];
			value += (perturbAmount - (Math.random() * perturbAmount * 2));
			child.getData()[i] = value;
		}
	}
	
	/**
	 * @return The number of offspring produced, which is 1 for this mutation.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	@Override
	public int parentsNeeded() {
		return 1;
	}

	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub
		
	}
}
