/*
 * Encog(tm) Core v2.5 
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

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGReferenceable;
import org.encog.persist.persistors.generic.GenericPersistor;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.innovation.InnovationList;
import org.encog.solve.genetic.species.Species;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

/**
 * Defines the basic functionality for a population of genomes.
 */
@EGReferenceable
public class BasicPopulation implements Population, EncogPersistedObject {

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
	 * The object description.
	 */
	private String description;

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

	}

	/**
	 * Add all of the specified members to this population.
	 * 
	 * @param newPop
	 *            A list of new genomes to add.
	 */
	public void addAll(final List<? extends Genome> newPop) {
		this.genomes.addAll(newPop);
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
	 * @return A persistor for this object.
	 */
	public Persistor createPersistor() {
		return new GenericPersistor(BasicPopulation.class);
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
	 * @return This object's description.
	 */
	public String getDescription() {
		return this.description;
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
	 * Set the description.
	 * @param theDescription The description.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;

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

}
