/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.ml.genetic.GeneticAlgorithm;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.innovation.InnovationList;
import org.encog.ml.genetic.species.Species;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

/**
 * Defines the basic functionality for a population of genomes.
 */
public class BasicPopulation implements Population {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4097921208348173582L;

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
	 * The object name.
	 */
	private String name;

	/**
	 * Construct an empty population.
	 */
	public BasicPopulation() {
		this.populationSize = 0;
	}

	/**
	 * Construct a population.
	 * 
	 * @param populationSize
	 *            The population size.
	 */
	public BasicPopulation(final int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Add a genome to the population.
	 * 
	 * @param genome
	 *            The genome to add.
	 */
	public void add(final Genome genome) {
		this.genomes.add(genome);
		genome.setPopulation(this);
	}

	/**
	 * @return Assign a gene id.
	 */
	public long assignGeneID() {
		return this.geneIDGenerate.generate();
	}

	/**
	 * @return Assign a genome id.
	 */
	public long assignGenomeID() {
		return this.genomeIDGenerate.generate();
	}

	/**
	 * @return Assign an innovation id.
	 */
	public long assignInnovationID() {
		return this.innovationIDGenerate.generate();
	}

	/**
	 * @return Assign a species id.
	 */
	public long assignSpeciesID() {
		return this.speciesIDGenerate.generate();
	}

	/**
	 * Clear all genomes from this population.
	 */
	public void clear() {
		this.genomes.clear();

	}

	/**
	 * Get a genome by index. Index 0 is the best genome.
	 * 
	 * @param i
	 *            The genome to get.
	 * @return The genome found at the specified index.
	 */
	public Genome get(final int i) {
		return this.genomes.get(i);
	}

	/**
	 * @return The best genome in the population.
	 */
	public Genome getBest() {
		if (this.genomes.size() == 0) {
			return null;
		} else {
			return this.genomes.get(0);
		}
	}

	/**
	 * @return The genomes in the population.
	 */
	public List<Genome> getGenomes() {
		return this.genomes;
	}

	/**
	 * @return A list of innovations in this population.
	 */
	public InnovationList getInnovations() {
		return this.innovations;
	}

	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The old age penalty, or zero for none.
	 */
	public double getOldAgePenalty() {
		return this.oldAgePenalty;
	}

	/**
	 * @return The old age threshold.
	 */
	public int getOldAgeThreshold() {
		return this.oldAgeThreshold;
	}

	/**
	 * Get the population size.
	 * 
	 * @return The population size.
	 */
	public int getPopulationSize() {
		return this.populationSize;
	}

	/**
	 * @return The species in this population.
	 */
	public List<Species> getSpecies() {
		return this.species;
	}

	/**
	 * @return The survival rate.
	 */
	public double getSurvivalRate() {
		return this.survivalRate;
	}

	/**
	 * @return The age at which a genome is considered "young".
	 */
	public int getYoungBonusAgeThreshold() {
		return this.youngBonusAgeThreshold;
	}

	/**
	 * @return The bonus applied to young genomes.
	 */
	public double getYoungScoreBonus() {
		return this.youngScoreBonus;
	}

	/**
	 * Set the innovation list.
	 * 
	 * @param innovations
	 *            The innovations, or null to disable.
	 */
	public void setInnovations(final InnovationList innovations) {
		this.innovations = innovations;
	}

	/**
	 * Set the name.
	 * @param theName The new name.
	 */
	public void setName(final String theName) {
		this.name = theName;

	}

	/**
	 * Set the old age penalty.
	 * 
	 * @param oldAgePenalty
	 *            The percent the score is affected by.
	 */
	public void setOldAgePenalty(final double oldAgePenalty) {
		this.oldAgePenalty = oldAgePenalty;
	}

	/**
	 * Set the threshold at which a genome is considered "old".
	 * 
	 * @param oldAgeThreshold
	 *            The age.
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
	 * 
	 * @param survivalRate
	 *            The survival rate.
	 */
	public void setSurvivalRate(final double survivalRate) {
		this.survivalRate = survivalRate;
	}

	/**
	 * Set the young bonus age threshold.
	 * 
	 * @param youngBonusAgeThreshold
	 *            The age.
	 */
	public void setYoungBonusAgeThreshhold(final int youngBonusAgeThreshold) {
		this.youngBonusAgeThreshold = youngBonusAgeThreshold;
	}

	/**
	 * Set the young genome bonus.
	 * 
	 * @param youngScoreBonus
	 *            The score bonus.
	 */
	public void setYoungScoreBonus(final double youngScoreBonus) {
		this.youngScoreBonus = youngScoreBonus;
	}

	/**
	 * @return The max size of the population.
	 */
	public int size() {
		return this.genomes.size();
	}

	/**
	 * Sort the population.
	 */
	public void sort() {
		Collections.sort(this.genomes);
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}

	private Genome findGenome(long id) {
		for(Genome genome: this.genomes) {
			if( genome.getGenomeID()==id)
				return genome;
		}
		return null;
	}

	@Override
	public void claim(GeneticAlgorithm ga) {
		for(Genome genome: this.genomes) {
			genome.setGeneticAlgorithm(ga);
		}
		
	}

	@Override
	public void addAll(List<? extends Genome> newPop) {
		this.genomes.addAll(newPop);		
	}

	/**
	 * @return the geneIDGenerate
	 */
	public GenerateID getGeneIDGenerate() {
		return geneIDGenerate;
	}

	/**
	 * @return the genomeIDGenerate
	 */
	public GenerateID getGenomeIDGenerate() {
		return genomeIDGenerate;
	}

	/**
	 * @return the innovationIDGenerate
	 */
	public GenerateID getInnovationIDGenerate() {
		return innovationIDGenerate;
	}

	/**
	 * @return the speciesIDGenerate
	 */
	public GenerateID getSpeciesIDGenerate() {
		return speciesIDGenerate;
	}

	/**
	 * @param youngBonusAgeThreshold the youngBonusAgeThreshold to set
	 */
	public void setYoungBonusAgeThreshold(int youngBonusAgeThreshold) {
		this.youngBonusAgeThreshold = youngBonusAgeThreshold;
	}

	
}
