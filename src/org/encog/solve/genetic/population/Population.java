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
package org.encog.solve.genetic.population;

import java.util.List;

import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;

/**
 * Defines a population of genomes.
 */
public interface Population {
	
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
	 * @param oldAgeThreshold
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

}
