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

import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.innovation.InnovationList;
import org.encog.ml.genetic.species.Species;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGReferenceable;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.encog.persist.persistors.generic.GenericPersistor;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;

/**
 * Defines the basic functionality for a population of genomes.
 */
@EGReferenceable
public class BasicPopulation extends BasicPersistedObject implements Population, EncogPersistedObject {
	
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
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;


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
	
	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_BASIC_POPULATION);
		obj.setStandardProperties(this);
		
		obj.setProperty( Population.PROPERTY_NEXT_GENE_ID, (int)this.geneIDGenerate.getCurrentID(), false );
		obj.setProperty( Population.PROPERTY_NEXT_GENOME_ID, (int)this.genomeIDGenerate.getCurrentID(), false );
		obj.setProperty( Population.PROPERTY_NEXT_INNOVATION_ID, (int)this.innovationIDGenerate.getCurrentID(), false );
		obj.setProperty( Population.PROPERTY_NEXT_SPECIES_ID, (int)this.speciesIDGenerate.getCurrentID(), false );

		obj.setProperty( Population.PROPERTY_OLD_AGE_PENALTY ,this.oldAgePenalty, false);
		obj.setProperty( Population.PROPERTY_OLD_AGE_THRESHOLD ,this.oldAgeThreshold, false);
		obj.setProperty( Population.PROPERTY_POPULATION_SIZE ,this.populationSize, false);
		obj.setProperty( Population.PROPERTY_SURVIVAL_RATE ,this.survivalRate, false);
		obj.setProperty( Population.PROPERTY_YOUNG_AGE_BONUS ,this.youngScoreBonus, false);
		obj.setProperty( Population.PROPERTY_YOUNG_AGE_THRESHOLD ,this.youngBonusAgeThreshold, false);
		
		obj.setPropertyGenericList( Population.PROPERTY_INNOVATIONS, this.innovations.getInnovations());
		obj.setPropertyGenericList( Population.PROPERTY_SPECIES, this.species);
		obj.setPropertyGenericList( Population.PROPERTY_GENOMES, this.genomes);
		
	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_BASIC_POPULATION);

		this.genomeIDGenerate.setCurrentID( obj.getPropertyInt( Population.PROPERTY_NEXT_GENOME_ID, true));
		this.geneIDGenerate.setCurrentID( obj.getPropertyInt( Population.PROPERTY_NEXT_GENE_ID, true ) );
		this.innovationIDGenerate.setCurrentID( obj.getPropertyInt( Population.PROPERTY_NEXT_INNOVATION_ID, true ) );
		this.speciesIDGenerate.setCurrentID( obj.getPropertyInt( Population.PROPERTY_NEXT_SPECIES_ID, true ) );

		this.oldAgePenalty = obj.getPropertyDouble(Population.PROPERTY_OLD_AGE_PENALTY, true); 
		this.oldAgeThreshold = obj.getPropertyInt(Population.PROPERTY_OLD_AGE_THRESHOLD, true);
		this.populationSize = obj.getPropertyInt( Population.PROPERTY_POPULATION_SIZE, true);
		this.survivalRate = obj.getPropertyDouble( Population.PROPERTY_SURVIVAL_RATE, true);
		this.youngScoreBonus = obj.getPropertyDouble(Population.PROPERTY_YOUNG_AGE_BONUS, true); 
		this.youngBonusAgeThreshold = obj.getPropertyInt(Population.PROPERTY_YOUNG_AGE_THRESHOLD, true);
		
		obj.getPropertyGenericList(Population.PROPERTY_GENOMES, this.genomes); 
	}

}
