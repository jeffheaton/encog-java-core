/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.ml.genetic.species;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.population.Population;
import org.encog.persist.BasicPersistedSubObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGReference;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;

/**
 * Provides basic functionality for a species.
 */
public class BasicSpecies extends BasicPersistedSubObject implements Species, Serializable {

	public static final String PROPERTY_AGE = "age";
	public static final String PROPERTY_BEST_SCORE = "bestScore";
	public static final String PROPERTY_GENS_NO_IMPROVE = "gensNoImprove";
	public static final String PROPERTY_LEADER = "leader";
	public static final String PROPERTY_SPAWNS_REQUIRED = "spawnsReq";
	
	/**
	 * The age of this species.
	 */
	private int age;

	/**
	 * The best score.
	 */
	private double bestScore;

	/**
	 * The number of generations with no improvement.
	 */
	private int gensNoImprovement;

	/**
	 * The leader.
	 */
	private Genome leader;

	/**
	 * The list of genomes.
	 */
	private final List<Genome> members = new ArrayList<Genome>();

	/**
	 * The number of spawns required.
	 */
	private double spawnsRequired;

	/**
	 * The species id.
	 */
	private long speciesID;

	/**
	 * The owner class.
	 */
	@EGReference
	private Population population;

	/**
	 * Default constructor, used mainly for persistence.
	 */
	public BasicSpecies() {

	}

	/**
	 * Construct a species.
	 * @param population The population the species belongs to.
	 * @param first The first genome in the species.
	 * @param speciesID The species id.
	 */
	public BasicSpecies(final Population population, final Genome first,
			final long speciesID) {
		this.population = population;
		this.speciesID = speciesID;
		this.bestScore = first.getScore();
		this.gensNoImprovement = 0;
		this.age = 0;
		this.leader = first;
		this.spawnsRequired = 0;
		this.members.add(first);
	}

	/**
	 * Calculate the amount to spawn.
	 */
	public void calculateSpawnAmount() {
		this.spawnsRequired = 0;
		for (final Genome genome : this.members) {
			this.spawnsRequired += genome.getAmountToSpawn();
		}

	}

	/**
	 * Choose a parent to mate. Choose from the population, determined by the
	 * survival rate. From this pool, a random parent is chosen.
	 * 
	 * @return The parent.
	 */
	public Genome chooseParent() {
		Genome baby;

		// If there is a single member, then choose that one.
		if (this.members.size() == 1) {
			baby = this.members.get(0);
		} else {
			// If there are many, then choose the population based on survival
			// rate
			// and select a random genome.
			final int maxIndexSize = (int) (this.population.getSurvivalRate() * this.members
					.size()) + 1;
			final int theOne = (int) RangeRandomizer.randomize(0, maxIndexSize);
			baby = this.members.get(theOne);
		}

		return baby;
	}

	/**
	 * @return The age of this species.
	 */
	public int getAge() {
		return this.age;
	}

	/**
	 * @return The best score for this species.
	 */
	public double getBestScore() {
		return this.bestScore;
	}

	/**
	 * @return The number of generations with no improvement.
	 */
	public int getGensNoImprovement() {
		return this.gensNoImprovement;
	}

	/**
	 * @return THe leader of this species.
	 */
	public Genome getLeader() {
		return this.leader;
	}

	/**
	 * @return The members of this species.
	 */
	public List<Genome> getMembers() {
		return this.members;
	}

	/**
	 * @return The number to spawn.
	 */
	public double getNumToSpawn() {
		return this.spawnsRequired;
	}

	/**
	 * @return The population that this species belongs to.
	 */
	public Population getPopulation() {
		return this.population;
	}

	/**
	 * @return The spawns required.
	 */
	public double getSpawnsRequired() {
		return this.spawnsRequired;
	}

	/**
	 * @return The species ID.
	 */
	public long getSpeciesID() {
		return this.speciesID;
	}

	/**
	 * Purge all members, increase age by one and count the number of
	 * generations with no improvement.
	 */
	public void purge() {
		this.members.clear();
		this.age++;
		this.gensNoImprovement++;
		this.spawnsRequired = 0;

	}

	/**
	 * Set the age of this species.
	 * 
	 * @param age
	 *            The age of this species.
	 */
	public void setAge(final int age) {
		this.age = age;
	}

	/**
	 * Set the best score.
	 * 
	 * @param bestScore
	 *            The best score.
	 */
	public void setBestScore(final double bestScore) {
		this.bestScore = bestScore;
	}

	/**
	 * Set the number of generations with no improvement.
	 * 
	 * @param gensNoImprovement
	 *            The number of generations.
	 */
	public void setGensNoImprovement(final int gensNoImprovement) {
		this.gensNoImprovement = gensNoImprovement;
	}

	/**
	 * Set the leader.
	 * 
	 * @param leader
	 *            The new leader.
	 */
	public void setLeader(final Genome leader) {
		this.leader = leader;
	}

	/**
	 * Set the number of spawns required.
	 * 
	 * @param spawnsRequired
	 *            The number of spawns required.
	 */
	public void setSpawnsRequired(final double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}

	@Override
	public Persistor createPersistor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_BASIC_SPECIES);
		
		obj.setProperty(PersistConst.PROPERTY_ID,(int)this.getSpeciesID(),true);
		obj.setProperty(BasicSpecies.PROPERTY_AGE,this.age,true);
		obj.setProperty(BasicSpecies.PROPERTY_BEST_SCORE,this.bestScore,true);
		obj.setProperty(BasicSpecies.PROPERTY_GENS_NO_IMPROVE,this.gensNoImprovement,true);
		obj.setProperty(BasicSpecies.PROPERTY_LEADER,this.leader.getGenomeID(),true);
		obj.setProperty(BasicSpecies.PROPERTY_SPAWNS_REQUIRED,this.spawnsRequired,true);
	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_BASIC_SPECIES);
		this.speciesID = obj.getPropertyInt(PersistConst.PROPERTY_ID, true);
		this.age = obj.getPropertyInt(BasicSpecies.PROPERTY_AGE, true);
		this.bestScore = obj.getPropertyDouble(BasicSpecies.PROPERTY_BEST_SCORE, true);
		this.gensNoImprovement = obj.getPropertyInt(BasicSpecies.PROPERTY_GENS_NO_IMPROVE, true);
		this.spawnsRequired = obj.getPropertyDouble(BasicSpecies.PROPERTY_SPAWNS_REQUIRED, true);
	}

}
