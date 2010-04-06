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
package org.encog.solve.genetic.species;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReference;
import org.encog.solve.genetic.GeneticAlgorithm;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.population.Population;

/**
 * Provides basic functionality for a species.
 */
public class BasicSpecies implements Species {
	
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
	private List<Genome> members = new ArrayList<Genome>();
	
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
	 * Construct a species.
	 * @param training
	 * @param first
	 * @param speciesID
	 */
	public BasicSpecies(final Population population,
			final Genome first, final long speciesID) {
		this.population = population;
		this.speciesID = speciesID;
		bestScore = first.getScore();
		gensNoImprovement = 0;
		age = 0;
		leader = first;
		spawnsRequired = 0;
		members.add(first);
	}
	
	public BasicSpecies()
	{
		
	}

	/**
	 * Calculate the amount to spawn.
	 */
	public void calculateSpawnAmount() {
		this.spawnsRequired = 0;
		for (final Genome genome : members) {
			spawnsRequired += genome.getAmountToSpawn();
		}

	}

	/**
	 * Choose a parent to mate. Choose from the population,
	 * determined by the survival rate.  From this pool, a random
	 * parent is chosen.
	 * @return The parent.
	 */
	public Genome chooseParent() {
		Genome baby;

		// If there is a single member, then choose that one.
		if (members.size() == 1) {
			baby = members.get(0);
		}

		else {
			// If there are many, then choose the population based on survival rate
			// and select a random genome.
			final int maxIndexSize = (int) (population
					.getSurvivalRate() * members.size()) + 1;
			final int theOne = (int) RangeRandomizer.randomize(0, maxIndexSize);
			baby = members.get(theOne);
		}

		return baby;
	}

	/**
	 * @return The age of this species.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @return The best score for this species.
	 */
	public double getBestScore() {
		return bestScore;
	}

	/**
	 * @return The number of generations with no improvement.
	 */
	public int getGensNoImprovement() {
		return gensNoImprovement;
	}

	/**
	 * @return THe leader of this species.
	 */
	public Genome getLeader() {
		return leader;
	}

	/**
	 * @return The members of this species.
	 */
	public List<Genome> getMembers() {
		return members;
	}

	/**
	 * @return The number to spawn.
	 */
	public double getNumToSpawn() {
		return spawnsRequired;
	}

	/**
	 * @return The spawns required.
	 */
	public double getSpawnsRequired() {
		return spawnsRequired;
	}

	/**
	 * The species ID.
	 */
	public long getSpeciesID() {
		return speciesID;
	}

	/**
	 * Purge all members, increase age by one and count the number of generations
	 * with no improvement.
	 */
	public void purge() {
		members.clear();
		age++;
		gensNoImprovement++;
		spawnsRequired = 0;

	}

	/**
	 * Set the age of this species.
	 * @param age The age of this species.
	 */
	public void setAge(final int age) {
		this.age = age;
	}

	/**
	 * Set the best score.
	 * @param bestScore The best score.
	 */
	public void setBestScore(final double bestScore) {
		this.bestScore = bestScore;
	}

	/**
	 * Set the number of generations with no improvement.
	 * @param gensNoImprovement The number of generations.
	 */
	public void setGensNoImprovement(final int gensNoImprovement) {
		this.gensNoImprovement = gensNoImprovement;
	}

	/**
	 * Set the leader.
	 * @param leader The new leader.
	 */
	public void setLeader(final Genome leader) {
		this.leader = leader;
	}

	/**
	 * Set the number of spawns required.
	 * @param spawnsRequired The number of spawns required.
	 */
	public void setSpawnsRequired(final double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}

	public Population getPopulation() {
		return population;
	}
	
	
}
