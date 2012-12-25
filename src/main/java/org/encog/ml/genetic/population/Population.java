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
package org.encog.ml.genetic.population;

import java.io.Serializable;
import java.util.List;

import org.encog.ml.genetic.GeneticAlgorithm;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.genome.GenomeFactory;
import org.encog.ml.genetic.genome.IntegerArrayGenomeFactory;
import org.encog.ml.prg.EncogProgram;

/**
 * Defines a population of genomes.
 */
public interface Population extends Serializable {
		
	/**
	 * Add a genome to the population.
	 * @param genome The genome to add.
	 */
	void add(Genome genome);

	/**
	 * Add all of the specified members to this population.
	 * @param newPop A list of new genomes to add.
	 */
	void addAll(List<? extends Genome> newPop);

	/**
	 * Clear all genomes from this population.
	 */
	void clear();

	
	/**
	 * Get a genome by index.  Index 0 is the best genome.
	 * @param i The genome to get.
	 * @return The genome at the specified index.
	 */
	Genome get(int i);
		
	/**
	 * @return The best genome in the population.
	 */
	Genome getBest();

	/**
	 * @return The genomes in the population.
	 */
	List<Genome> getGenomes();

	/**
	 * @return The max population size.
	 */
	int getPopulationSize();


	/**
	 * Set the max population size.
	 * @param populationSize The max population size.
	 */
	void setPopulationSize(final int populationSize);

	/**
	 * @return The size of the population.
	 */
	int size();

	/**
	 * Sort the population by best score.
	 */
	void sort();
	
	/**
	 * Claim the population, before training.
	 * @param ga The GA that is claiming.
	 */
	void claim(GeneticAlgorithm ga);

	void rewrite(Genome prg);
	
	GenomeFactory getGenomeFactory();

	void setGenomeFactory(GenomeFactory factory);

	int getMaxIndividualSize();
}
