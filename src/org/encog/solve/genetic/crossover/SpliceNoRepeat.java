/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.solve.genetic.crossover;

import java.util.HashSet;
import java.util.Set;

import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;

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
	 * @param cutLength
	 *            The cut length.
	 */
	public SpliceNoRepeat(final int cutLength) {
		this.cutLength = cutLength;
	}

	/**
	 * Assuming this chromosome is the "mother" mate with the passed in
	 * "father".
	 * 
	 * @param father
	 *            The father.
	 * @param offspring1
	 *            Returns the first offspring
	 * @param offspring2
	 *            Returns the second offspring.
	 */
	public void mate(final Chromosome mother, final Chromosome father,
			final Chromosome offspring1, final Chromosome offspring2) {
		final int geneLength = father.getGenes().size();

		// the chromosome must be cut at two positions, determine them
		final int cutpoint1 = (int) (Math.random() * (geneLength - this.cutLength));
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
