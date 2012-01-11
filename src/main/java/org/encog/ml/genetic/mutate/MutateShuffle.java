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
package org.encog.ml.genetic.mutate;

import org.encog.ml.genetic.genes.Gene;
import org.encog.ml.genetic.genome.Chromosome;

/**
 * A simple mutation where genes are shuffled.
 * This mutation will not produce repeated genes.
 */
public class MutateShuffle implements Mutate {

	/**
	 * Perform a shuffle mutation.
	 * @param chromosome The chromosome to mutate.
	 */
	public final void performMutation(final Chromosome chromosome) {
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

		// insert them back in, reverse order
		chromosome.getGenes().add(iswap1, gene2);
		chromosome.getGenes().add(iswap2, gene1);
	}

}
