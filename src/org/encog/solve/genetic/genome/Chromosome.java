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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.mathutil.matrices.MatrixError;
import org.encog.neural.networks.training.neat.NEATNeuronGene;
import org.encog.solve.genetic.genes.Gene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a chromosome to genetic algorithm. This is an abstract class.
 * Other classes are provided in this book that use this base class to train
 * neural networks or provide an answer to the traveling salesman problem.
 * 
 * Lifeforms in this genetic algorithm consist of one single chromosome each.
 * Therefore, this class represents a virtual lifeform. The chromosome is a
 * string of objects that represent one solution. For a neural network, this
 * string of objects usually represents the weight and threshold matrix.
 * 
 * Chromosomes are made up of genes. These are of the generic type GENE_TYPE.
 * For a neural network this type would most likely be double values.
 * 
 * @param <GENE_TYPE>
 *            The datatype for a gene.
 */

public class Chromosome  {


	/**
	 * The individual elements of this chromosome.
	 */
	private final List<Gene> genes = new ArrayList<Gene>();

	/**
	 * Get the specified gene.
	 * 
	 * @param gene
	 *            The specified gene.
	 * @return The gene specified.
	 */
	public Gene getGene(final int gene) {
		return this.genes.get(gene);
	}

	/**
	 * Used the get the entire gene list.
	 * 
	 * @return the genes
	 */
	public List<Gene> getGenes() {
		return this.genes;
	}

	public void add(Gene neuronGene) {
		this.genes.add(neuronGene);
		
	}

	public Gene get(int i) {
		return this.genes.get(i);
	}

	public int size() {
		return this.genes.size();
	}


}
