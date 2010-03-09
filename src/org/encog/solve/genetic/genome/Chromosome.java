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

package org.encog.solve.genetic.genome;

import java.util.ArrayList;
import java.util.List;

import org.encog.solve.genetic.genes.Gene;

/**
 * Implements a chromosome to genetic algorithm. This is an abstract class.
 * Other classes are provided in this book that use this base class to train
 * neural networks or provide an answer to the traveling salesman problem. 
 * Chromosomes are made up of genes. 
 * 
 * Genomes in this genetic algorithm consist of one or more chromosomes. 
 * 
 */

public class Chromosome {

	/**
	 * The individual elements of this chromosome.
	 */
	private final List<Gene> genes = new ArrayList<Gene>();

	public void add(final Gene neuronGene) {
		genes.add(neuronGene);

	}

	public Gene get(final int i) {
		return genes.get(i);
	}

	/**
	 * Get the specified gene.
	 * 
	 * @param gene
	 *            The specified gene.
	 * @return The gene specified.
	 */
	public Gene getGene(final int gene) {
		return genes.get(gene);
	}

	/**
	 * Used the get the entire gene list.
	 * 
	 * @return the genes
	 */
	public List<Gene> getGenes() {
		return genes;
	}

	public int size() {
		return genes.size();
	}

}
