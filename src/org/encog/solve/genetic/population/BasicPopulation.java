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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

/**
 * Defines the basic functionality for a population of genomes.
 */
public class BasicPopulation implements Population {
	
	/**
	 * Generate gene id's.
	 */
	private final GenerateID geneIDGenerate = new BasicGenerateID();

	/**
	 * Generate genome id's.
	 */
	private final GenerateID genomeIDGenerate = new BasicGenerateID();
	
	/**
	 * The population.
	 */
	private final List<Genome> genomes = new ArrayList<Genome>();
	
	/**
	 * Generate innovation id's.
	 */
	private final GenerateID innovationIDGenerate = new BasicGenerateID();
	
	/**
	 * A list of innovations, or null if this feature is not being used.
	 */
	private InnovationList innovations;

	/**
	 * The old age penalty.
	 */
	private double oldAgePenalty = 0.3;
	
	/**
	 * The old age threshold.
	 */
	private int oldAgeThreshold = 50;
	
	/**
	 * How many genomes should be created.
	 */
	private int populationSize;
	
	/**
	 * The species in this population.
	 */
	private final List<Species> species = new ArrayList<Species>();
	
	/**
	 * Generate species id's.
	 */
	private final GenerateID speciesIDGenerate = new BasicGenerateID();
	
	/**
	 * The survival rate.
	 */
	private double survivalRate = 0.2;
	
	/**
	 * The young threshold.
	 */
	private int youngBonusAgeThreshold = 10;

	/**
	 * The young score bonus.
	 */
	private double youngScoreBonus = 0.3;

	/**
	 * Construct a population.
	 * @param populationSize The population size.
	 */
	public BasicPopulation(final int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Add a genome to the population.
	 * @param genome The genome to add.
	 */
	public void add(final Genome genome) {
		genomes.add(genome);

	}

	/**
	 * Add all of the specified members to this population.
	 * @param newPop A list of new genomes to add.
	 */
	public void addAll(final List<? extends Genome> newPop) {
		genomes.addAll(newPop);
	}

	
	/**
	 * @return Assign a gene id.
	 */
	public long assignGeneID() {
		return geneIDGenerate.generate();
	}

	/**
	 * @return Assign a genome id.
	 */
	public long assignGenomeID() {
		return genomeIDGenerate.generate();
	}

	/**
	 * @return Assign an innovation id.
	 */
	public long assignInnovationID() {
		return innovationIDGenerate.generate();
	}

	/**
	 * @return Assign a species id.
	 */
	public long assignSpeciesID() {
		return speciesIDGenerate.generate();
	}

	/**
	 * Clear all genomes from this population.
	 */
	public void clear() {
		genomes.clear();

	}

	/**
	 * Get a genome by index.  Index 0 is the best genome.
	 * @param i The genome to get.
	 */
	public Genome get(final int i) {
		return genomes.get(i);
	}

	/**
	 * @return The best genome in the population.
	 */
	public Genome getBest() {
		if (genomes.size() == 0) {
			return null;
		} else {
			return genomes.get(0);
		}
	}

	/**
	 * @return The genomes in the population.
	 */
	public List<Genome> getGenomes() {
		return genomes;
	}

	/**
	 * @return A list of innovations in this population.
	 */
	public InnovationList getInnovations() {
		return innovations;
	}

	/**
	 * @return The old age penalty, or zero for none.
	 */
	public double getOldAgePenalty() {
		return oldAgePenalty;
	}

	public int getOldAgeThreshold() {
		return oldAgeThreshold;
	}

	/**
	 * Get the population size.
	 * 
	 * @return The population size.
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @return The species in this population.
	 */
	public List<Species> getSpecies() {
		return species;
	}

	/**
	 * @return The survival rate.
	 */
	public double getSurvivalRate() {
		return survivalRate;
	}

	/**
	 * @return The age at which a genome is considered "young".
	 */
	public int getYoungBonusAgeThreshold() {
		return youngBonusAgeThreshold;
	}

	/**
	 * @return The bonus applied to young genomes.
	 */
	public double getYoungScoreBonus() {
		return youngScoreBonus;
	}

	/**
	 * Set the innovation list.
	 * @param innovations The innovations, or null to disable. 
	 */
	public void setInnovations(final InnovationList innovations) {
		this.innovations = innovations;
	}

	/**
	 * Set the old age penalty.
	 * @param oldAgePenalty The percent the score is affected by.
	 */
	public void setOldAgePenalty(final double oldAgePenalty) {
		this.oldAgePenalty = oldAgePenalty;
	}

	/**
	 * Set the threshold at which a genome is considered "old".
	 * @param oldAgeThreshold The age.
	 */
	public void setOldAgeThreshold(final int oldAgeThreshold) {
		this.oldAgeThreshold = oldAgeThreshold;
	}

	/**
	 * Set the population size.
	 * 
	 * @param populationSize
	 *            The population size.
	 */
	public void setPopulationSize(final int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Set the survival rate.
	 * @param survivalRate The survival rate.
	 */
	public void setSurvivalRate(final double survivalRate) {
		this.survivalRate = survivalRate;
	}

	/**
	 * Set the young bonus age threshold.
	 * @param youngBonusAgeThreshold The age.
	 */
	public void setYoungBonusAgeThreshhold(final int youngBonusAgeThreshold) {
		this.youngBonusAgeThreshold = youngBonusAgeThreshold;
	}

	/**
	 * Set the young genome bonus.
	 * @param youngScoreBonus The score bonus.
	 */
	public void setYoungScoreBonus(final double youngScoreBonus) {
		this.youngScoreBonus = youngScoreBonus;
	}

	/**
	 * @return The max size of the population.
	 */
	public int size() {
		return genomes.size();
	}

	/**
	 * Sort the population.
	 */
	public void sort() {
		Collections.sort(genomes);
	}

}
