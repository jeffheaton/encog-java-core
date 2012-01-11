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
package org.encog.ml.genetic.population;

import java.io.Serializable;
import java.util.List;

import org.encog.ml.genetic.GeneticAlgorithm;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.innovation.InnovationList;
import org.encog.ml.genetic.species.Species;

/**
 * Defines a population of genomes.
 */
public interface Population extends Serializable {
	
	/**
	 * Property tag for the next gene id.
	 */
	String PROPERTY_NEXT_GENE_ID = "nextGeneID";
	
	/**
	 * Property tag for the next genome id.
	 */
	String PROPERTY_NEXT_GENOME_ID = "nextGenomeID";
	
	/**
	 * Property tag for the next innovation id.
	 */
	String PROPERTY_NEXT_INNOVATION_ID = "nextInnovationID";
	
	/**
	 * Property tag for the next species id.
	 */
	String PROPERTY_NEXT_SPECIES_ID = "nextSpeciesID";
	
	/**
	 * Property tag for the old age penalty.
	 */
	String PROPERTY_OLD_AGE_PENALTY = "oldAgePenalty";
	
	/**
	 * Property tag for the old age threshold.
	 */
	String PROPERTY_OLD_AGE_THRESHOLD = "oldAgeThreshold";
	
	/**
	 * Property tag for the population size.
	 */
	String PROPERTY_POPULATION_SIZE = "populationSize";
	
	/**
	 * Property tag for the survival rate.
	 */
	String PROPERTY_SURVIVAL_RATE = "survivalRate";
	
	/**
	 * Property tag for the young age bonus.
	 */
	String PROPERTY_YOUNG_AGE_BONUS = "youngAgeBonus";
	
	/**
	 * Property tag for the young age threshold.
	 */
	String PROPERTY_YOUNG_AGE_THRESHOLD = "youngAgeThreshold";

	/**
	 * Property tag for the genomes collection.
	 */
	String PROPERTY_GENOMES = "genomes";
	
	/**
	 * Property tag for the innovations collection.
	 */
	String PROPERTY_INNOVATIONS = "innovations";
	
	/**
	 * Property tag for the species collection.
	 */
	String PROPERTY_SPECIES = "species";

	
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
	 * @return Assign a gene id.
	 */
	long assignGeneID();

	/**
	 * @return Assign a genome id.
	 */
	long assignGenomeID();

	/**
	 * @return Assign an innovation id.
	 */
	long assignInnovationID();

	/**
	 * @return Assign a species id.
	 */
	long assignSpeciesID();

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
	 * @return A list of innovations in this population.
	 */
	InnovationList getInnovations();

	/**
	 * @return The percent to decrease "old" genom's score by.
	 */
	double getOldAgePenalty();

	/**
	 * @return The age at which to consider a genome "old".
	 */
	int getOldAgeThreshold();

	/**
	 * @return The max population size.
	 */
	int getPopulationSize();

	/**
	 * @return A list of species.
	 */
	List<Species> getSpecies();

	/**
	 * @return The survival rate.
	 */
	double getSurvivalRate();

	/**
	 * @return The age, below which, a genome is considered "young".
	 */
	int getYoungBonusAgeThreshold();

	/**
	 * @return The bonus given to "young" genomes.
	 */
	double getYoungScoreBonus();

	/**
	 * Set the innovations collection.
	 * @param innovations The innovations collection.
	 */
	void setInnovations(InnovationList innovations);

	/**
	 * Set the old age penalty.
	 * @param oldAgePenalty The old age penalty.
	 */
	void setOldAgePenalty(double oldAgePenalty);

	/**
	 * Set the age at which a genome is considered "old".
	 * @param oldAgeThreshold The old age threshold.
	 */
	void setOldAgeThreshold(int oldAgeThreshold);

	/**
	 * Set the max population size.
	 * @param populationSize The max population size.
	 */
	void setPopulationSize(final int populationSize);

	/**
	 * Set the survival rate.
	 * @param survivalRate The survival rate.
	 */
	void setSurvivalRate(double survivalRate);

	/**
	 * Set the age at which genoms are considered young.
	 * @param youngBonusAgeThreshhold The age.
	 */
	void setYoungBonusAgeThreshhold(int youngBonusAgeThreshhold);

	/**
	 * Set the youth score bonus.
	 * @param youngScoreBonus The bonus.
	 */
	void setYoungScoreBonus(double youngScoreBonus);

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

}
