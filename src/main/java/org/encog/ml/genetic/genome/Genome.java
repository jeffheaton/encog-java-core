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

import java.util.List;

import org.encog.ml.genetic.GeneticAlgorithm;
import org.encog.ml.genetic.population.Population;

/**
 * A genome is the basic blueprint for creating an organism in Encog. A genome
 * is made up of one or more chromosomes, which are in turn made up of genes.
 * 
 */
public interface Genome extends Comparable<Genome> {

	/**
	 * @return The number of genes in this genome.
	 */
	int calculateGeneCount();

	/**
	 * Use the genes to update the organism.
	 */
	void decode();

	/**
	 * Use the organism to update the genes.
	 */
	void encode();

	/**
	 * Get the adjusted score, this considers old-age penalties and youth
	 * bonuses. If there are no such bonuses or penalties, this is the same as
	 * the score.
	 * 
	 * @return The adjusted score.
	 */
	double getAdjustedScore();

	/**
	 * @return The amount of offspring this genome will have.
	 */
	double getAmountToSpawn();

	/**
	 * @return The chromosomes that make up this genome.
	 */
	List<Chromosome> getChromosomes();

	/**
	 * @return Get the GA used by this genome. This is normally a transient
	 *         field and only used during training.
	 */
	GeneticAlgorithm getGeneticAlgorithm();

	/**
	 * @return The genome ID.
	 */
	long getGenomeID();

	/**
	 * @return The organism produced by this genome.
	 */
	Object getOrganism();

	/**
	 * @return The population that this genome belongs to.
	 */
	Population getPopulation();

	/**
	 * @return The score for this genome.
	 */
	double getScore();

	/**
	 * Mate with another genome and produce two children.
	 * 
	 * @param father
	 *            The father genome.
	 * @param child1
	 *            The first child.
	 * @param child2
	 *            The second child.
	 */
	void mate(Genome father, Genome child1, Genome child2);

	/**
	 * Set the adjusted score.
	 * 
	 * @param adjustedScore
	 *            The adjusted score.
	 */
	void setAdjustedScore(double adjustedScore);

	/**
	 * Set the amount to spawn.
	 * 
	 * @param amountToSpawn
	 *            The amount to spawn.
	 */
	void setAmountToSpawn(double amountToSpawn);

	/**
	 * Set the GA used by this genome. This is normally a transient field and
	 * only used during training.
	 * 
	 * @param ga
	 *            The GA.
	 */
	void setGeneticAlgorithm(GeneticAlgorithm ga);

	/**
	 * Set the genome ID.
	 * 
	 * @param genomeID
	 *            The genome id.
	 */
	void setGenomeID(long genomeID);

	/**
	 * Set the population that this genome belongs to.
	 * 
	 * @param population
	 *            The population that this genome belongs to.
	 */
	void setPopulation(Population population);

	/**
	 * Set the score.
	 * 
	 * @param score
	 *            The new score.
	 */
	void setScore(double score);
}
