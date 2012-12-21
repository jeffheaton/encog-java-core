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
package org.encog.ml.genetic.crossover;

import org.encog.ml.genetic.genome.ArrayGenome;
import org.encog.ml.genetic.genome.Genome;

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
	public void mate(final Genome theMother, final Genome theFather,
			final Genome theOffspring1, final Genome theOffspring2) {
		
		ArrayGenome mother = (ArrayGenome)theMother;
		ArrayGenome father = (ArrayGenome)theFather;
		ArrayGenome offspring1 = (ArrayGenome)theOffspring1;
		ArrayGenome offspring2 = (ArrayGenome)theOffspring2;
		
		final int geneLength = mother.size();

		// the chromosome must be cut at two positions, determine them
		final int cutpoint1 = (int) (Math.random() 
				* (geneLength - this.cutLength));
		final int cutpoint2 = cutpoint1 + this.cutLength;

		// handle cut section
		for (int i = 0; i < geneLength; i++) {
			if (!((i < cutpoint1) || (i > cutpoint2))) {
				offspring1.copy(father,i,i);
				offspring2.copy(mother,i,i);
			}
		}

		// handle outer sections
		for (int i = 0; i < geneLength; i++) {
			if ((i < cutpoint1) || (i > cutpoint2)) {
				offspring1.copy(mother,i,i);
				offspring2.copy(father,i,i);
			}
		}
	}
}
