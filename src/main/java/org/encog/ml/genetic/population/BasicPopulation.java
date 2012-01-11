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
	 * Thed default old age penalty.
	 */
	public static final double DEFAULT_OLD_AGE_PENALTY = 0.3;
	
	/**
	 * The default old age threshold.
	 */
	public static final int DEFAULT_OLD_AGE_THRESHOLD = 50;
	
	/**
	 * The default survival rate.
	 */
	public static final double DEFAULT_SURVIVAL_RATE = 0.2;
	
	/**
	 * The default youth penalty.
	 */
	public static final double DEFAULT_YOUTH_BONUS = 0.3;
	
	/**
	 * The default youth threshold.
	 */
	public static final int DEFAULT_YOUTH_THRESHOLD = 10;
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

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
	private double oldAgePenalty = DEFAULT_OLD_AGE_PENALTY;

	/**
	 * The old age threshold.
	 */
	private int oldAgeThreshold = DEFAULT_OLD_AGE_THRESHOLD;

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
	private double survivalRate = DEFAULT_SURVIVAL_RATE;

	/**
	 * The young threshold.
	 */
	private int youngBonusAgeThreshold = DEFAULT_YOUTH_THRESHOLD;

	/**
	 * The young score bonus.
	 */
	private double youngScoreBonus = DEFAULT_YOUTH_BONUS;

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
	 * @param thePopulationSize
	 *            The population size.
	 */
	public BasicPopulation(final int thePopulationSize) {
		this.populationSize = thePopulationSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void add(final Genome genome) {
		this.genomes.add(genome);
		genome.setPopulation(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addAll(final List<? extends Genome> newPop) {
		this.genomes.addAll(newPop);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long assignGeneID() {
		return this.geneIDGenerate.generate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long assignGenomeID() {
		return this.genomeIDGenerate.generate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long assignInnovationID() {
		return this.innovationIDGenerate.generate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long assignSpeciesID() {
		return this.speciesIDGenerate.generate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void claim(final GeneticAlgorithm ga) {
		for (final Genome genome : this.genomes) {
			genome.setGeneticAlgorithm(ga);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void clear() {
		this.genomes.clear();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Genome get(final int i) {
		return this.genomes.get(i);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Genome getBest() {
		if (this.genomes.size() == 0) {
			return null;
		} else {
			return this.genomes.get(0);
		}
	}

	/**
	 * @return the geneIDGenerate
	 */
	public final GenerateID getGeneIDGenerate() {
		return this.geneIDGenerate;
	}

	/**
	 * @return the genomeIDGenerate
	 */
	public final GenerateID getGenomeIDGenerate() {
		return this.genomeIDGenerate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Genome> getGenomes() {
		return this.genomes;
	}

	/**
	 * @return the innovationIDGenerate
	 */
	public final GenerateID getInnovationIDGenerate() {
		return this.innovationIDGenerate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final InnovationList getInnovations() {
		return this.innovations;
	}

	/**
	 * @return The name.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getOldAgePenalty() {
		return this.oldAgePenalty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getOldAgeThreshold() {
		return this.oldAgeThreshold;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPopulationSize() {
		return this.populationSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Species> getSpecies() {
		return this.species;
	}

	/**
	 * @return the speciesIDGenerate
	 */
	public final GenerateID getSpeciesIDGenerate() {
		return this.speciesIDGenerate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getSurvivalRate() {
		return this.survivalRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getYoungBonusAgeThreshold() {
		return this.youngBonusAgeThreshold;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getYoungScoreBonus() {
		return this.youngScoreBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setInnovations(final InnovationList theInnovations) {
		this.innovations = theInnovations;
	}

	/**
	 * Set the name.
	 * 
	 * @param theName
	 *            The new name.
	 */
	public final void setName(final String theName) {
		this.name = theName;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setOldAgePenalty(final double theOldAgePenalty) {
		this.oldAgePenalty = theOldAgePenalty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setOldAgeThreshold(final int theOldAgeThreshold) {
		this.oldAgeThreshold = theOldAgeThreshold;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPopulationSize(final int thePopulationSize) {
		this.populationSize = thePopulationSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setSurvivalRate(final double theSurvivalRate) {
		this.survivalRate = theSurvivalRate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setYoungBonusAgeThreshhold(
			final int theYoungBonusAgeThreshold) {
		this.youngBonusAgeThreshold = theYoungBonusAgeThreshold;
	}

	/**
	 * @param theYoungBonusAgeThreshold
	 *            the youngBonusAgeThreshold to set
	 */
	public final void setYoungBonusAgeThreshold(
			final int theYoungBonusAgeThreshold) {
		this.youngBonusAgeThreshold = theYoungBonusAgeThreshold;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setYoungScoreBonus(final double theYoungScoreBonus) {
		this.youngScoreBonus = theYoungScoreBonus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size() {
		return this.genomes.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void sort() {
		Collections.sort(this.genomes);
	}
}
