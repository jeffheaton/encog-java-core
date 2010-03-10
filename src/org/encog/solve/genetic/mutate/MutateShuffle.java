/*
 * Encog(tm) Core v2.4
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
package org.encog.solve.genetic.mutate;

import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;

/**
 * A simple mutation where genes are shuffled.
 * This mutation will not produce repeated genes.
 */
public class MutateShuffle implements Mutate {

	/**
	 * Perform a shuffle mutation.
	 * @param chromosome The chromosome to mutate.
	 */
	public void performMutation(final Chromosome chromosome) {
		final int length = chromosome.getGenes().size();
		int iswap1 = (int) (Math.random() * length);
		int iswap2 = (int) (Math.random() * length);

		// can't be equal
		if (iswap1 == iswap2) {
			// move to the next, but
			// don't go out of bounds
			if (iswap1 > 0) {
				iswap1--;
			} else {
				iswap1++;
			}

		}

		// make sure they are in the right order
		if (iswap1 > iswap2) {
			final int temp = iswap1;
			iswap1 = iswap2;
			iswap2 = temp;
		}

		final Gene gene1 = chromosome.getGenes().get(iswap1);
		final Gene gene2 = chromosome.getGenes().get(iswap2);

		// remove the two genes
		chromosome.getGenes().remove(gene1);
		chromosome.getGenes().remove(gene2);

		// insert them back in, opposit order
		chromosome.getGenes().add(iswap1, gene2);
		chromosome.getGenes().add(iswap2, gene1);
	}

}
