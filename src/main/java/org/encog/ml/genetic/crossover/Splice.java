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
package org.encog.ml.genetic.crossover;

import org.encog.ml.genetic.genome.Chromosome;

/**
 * A simple cross over where genes are simply "spliced". Genes are allowed to
 * repeat.
 */
public class Splice implements Crossover {

	/**
	 * The cut length.
	 */
	private final int cutLength;

	/**
	 * Create a slice crossover with the specified cut length.
	 * @param theCutLength The cut length.
	 */
	public Splice(final int theCutLength) {
		this.cutLength = theCutLength;
	}

	/**
	 * Assuming this chromosome is the "mother" mate with the passed in
	 * "father".
	 * @param mother
	 * 			The mother.
	 * @param father
	 *            The father.
	 * @param offspring1
	 *            Returns the first offspring
	 * @param offspring2
	 *            Returns the second offspring.
	 */
	public final void mate(final Chromosome mother, final Chromosome father,
			final Chromosome offspring1, final Chromosome offspring2) {
		final int geneLength = mother.getGenes().size();

		// the chromosome must be cut at two positions, determine them
		final int cutpoint1 = (int) (Math.random() 
				* (geneLength - this.cutLength));
		final int cutpoint2 = cutpoint1 + this.cutLength;

		// handle cut section
		for (int i = 0; i < geneLength; i++) {
			if (!((i < cutpoint1) || (i > cutpoint2))) {
				offspring1.getGene(i).copy(father.getGene(i));
				offspring2.getGene(i).copy(mother.getGene(i));
			}
		}

		// handle outer sections
		for (int i = 0; i < geneLength; i++) {
			if ((i < cutpoint1) || (i > cutpoint2)) {
				offspring1.getGene(i).copy(mother.getGene(i));
				offspring2.getGene(i).copy(father.getGene(i));
			}
		}
	}
}
