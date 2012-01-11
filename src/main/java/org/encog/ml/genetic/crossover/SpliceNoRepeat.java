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

import java.util.HashSet;
import java.util.Set;

import org.encog.ml.genetic.genes.Gene;
import org.encog.ml.genetic.genome.Chromosome;

/**
 * A simple cross over where genes are simply "spliced". Genes are not allowed
 * to repeat.
 */
public class SpliceNoRepeat implements Crossover {

	/**
	 * Get a list of the genes that have not been taken before. This is useful
	 * if you do not wish the same gene to appear more than once in a
	 * chromosome.
	 * 
	 * @param source
	 *            The pool of genes to select from.
	 * @param taken
	 *            An array of the taken genes.
	 * @return Those genes in source that are not taken.
	 */
	private static Gene getNotTaken(final Chromosome source,
			final Set<Gene> taken) {

		for (final Gene trial : source.getGenes()) {
			if (!taken.contains(trial)) {
				taken.add(trial);
				return trial;
			}
		}

		return null;
	}

	/**
	 * The cut length.
	 */
	private final int cutLength;

	/**
	 * Construct a splice crossover.
	 * 
	 * @param theCutLength
	 *            The cut length.
	 */
	public SpliceNoRepeat(final int theCutLength) {
		this.cutLength = theCutLength;
	}

	/**
	 * Assuming this chromosome is the "mother" mate with the passed in
	 * "father".
	 * @param mother
	 * 		The mother.
	 * @param father
	 *            The father.
	 * @param offspring1
	 *            Returns the first offspring
	 * @param offspring2
	 *            Returns the second offspring.
	 */
	public final void mate(final Chromosome mother, final Chromosome father,
			final Chromosome offspring1, final Chromosome offspring2) {
		final int geneLength = father.getGenes().size();

		// the chromosome must be cut at two positions, determine them
		final int cutpoint1 = (int) (Math.random() 
				* (geneLength - this.cutLength));
		final int cutpoint2 = cutpoint1 + this.cutLength;

		// keep track of which genes have been taken in each of the two
		// offspring, defaults to false.
		final Set<Gene> taken1 = new HashSet<Gene>();
		final Set<Gene> taken2 = new HashSet<Gene>();

		// handle cut section
		for (int i = 0; i < geneLength; i++) {
			if (!((i < cutpoint1) || (i > cutpoint2))) {
				offspring1.getGene(i).copy(father.getGene(i));
				offspring2.getGene(i).copy(mother.getGene(i));
				taken1.add(father.getGene(i));
				taken2.add(mother.getGene(i));
			}
		}

		// handle outer sections
		for (int i = 0; i < geneLength; i++) {
			if ((i < cutpoint1) || (i > cutpoint2)) {

				offspring1.getGene(i).copy(
						SpliceNoRepeat.getNotTaken(mother, taken1));
				offspring2.getGene(i).copy(
						SpliceNoRepeat.getNotTaken(father, taken2));

			}
		}
	}
}
