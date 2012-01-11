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
package org.encog.ml.genetic.genome;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.genetic.genes.Gene;

/**
 * Implements a chromosome to genetic algorithm. This is an abstract class.
 * Other classes are provided in this book that use this base class to train
 * neural networks or provide an answer to the traveling salesman problem. 
 * Chromosomes are made up of genes. 
 * 
 * Genomes in this genetic algorithm consist of one or more chromosomes. 
 * 
 */
public class Chromosome implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The individual elements of this chromosome.
	 */
	private final List<Gene> genes = new ArrayList<Gene>();

	/**
	 * Add a gene.
	 * @param gene The gene to add.
	 */
	public final void add(final Gene gene) {
		genes.add(gene);

	}

	/**
	 * Get an individual gene.
	 * @param i The index of the gene.
	 * @return The gene.
	 */
	public final Gene get(final int i) {
		return genes.get(i);
	}

	/**
	 * Get the specified gene.
	 * 
	 * @param gene
	 *            The specified gene.
	 * @return The gene specified.
	 */
	public final Gene getGene(final int gene) {
		return genes.get(gene);
	}

	/**
	 * Used the get the entire gene list.
	 * 
	 * @return the genes
	 */
	public final List<Gene> getGenes() {
		return genes;
	}

	/**
	 * @return The number of genes in this chromosome.
	 */
	public final int size() {
		return genes.size();
	}

}
