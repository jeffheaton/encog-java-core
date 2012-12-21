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

import org.encog.ml.genetic.genome.DoubleArrayGenome;
import org.encog.ml.genetic.genome.Genome;

/**
 * A simple mutation based on random numbers.
 */
public class MutatePerturb implements Mutate {

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
	public void performMutation(final Genome theParent, final Genome theChild) {
		DoubleArrayGenome parent = (DoubleArrayGenome)theParent;
		DoubleArrayGenome child = (DoubleArrayGenome)theChild;
		
		for(int i=0;i<parent.size();i++) {
			double value = parent.getData()[i];
			value += (perturbAmount - (Math.random() * perturbAmount * 2));
			child.getData()[i] = value;
		}
	}
}
