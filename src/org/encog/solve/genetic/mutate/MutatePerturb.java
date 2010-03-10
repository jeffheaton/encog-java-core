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

import org.encog.solve.genetic.genes.DoubleGene;
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;

/**
 * A simple mutation based on random numbers.
 */
public class MutatePerturb implements Mutate {

	private final double perturbAmount;

	/**
	 * Construct a perturb mutation.
	 * @param perturbAmount The amount to mutate by(percent).
	 */
	public MutatePerturb(final double perturbAmount) {
		this.perturbAmount = perturbAmount;
	}

	/**
	 * Perform a perturb mutation on the specified chromosome.
	 * @param The chromosome to mutate.
	 */
	public void performMutation(final Chromosome chromosome) {
		for (final Gene gene : chromosome.getGenes()) {
			if (gene instanceof DoubleGene) {
				final DoubleGene doubleGene = (DoubleGene) gene;
				double value = doubleGene.getValue();
				value += (perturbAmount - (Math.random() * perturbAmount * 2));
				doubleGene.setValue(value);
			}
		}
	}
}
