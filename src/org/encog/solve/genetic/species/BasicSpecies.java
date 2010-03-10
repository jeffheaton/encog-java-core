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
import org.encog.neural.networks.training.neat.NEATGenome;
import org.encog.solve.genetic.GeneticAlgorithm;
import org.encog.solve.genetic.genome.Genome;

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
	private final List<Genome> members = new ArrayList<Genome>();
	
	/**
	 * The number of spawns required.
	 */
	private double spawnsRequired;
	
	/**
	 * The species id.
	 */
	private final long speciesID;
	
	/**
	 * The owner class.
	 */
	private final GeneticAlgorithm training;

	/**
	 * Construct a species.
	 * @param training
	 * @param first
	 * @param speciesID
	 */
	public BasicSpecies(final GeneticAlgorithm training,
			final NEATGenome first, final long speciesID) {
		this.training = training;
		this.speciesID = speciesID;
		bestScore = first.getScore();
		gensNoImprovement = 0;
		age = 0;
		leader = first;
		spawnsRequired = 0;
		members.add(first);
	}

	/**
	 * Add a genome.
	 * @param genome The genome to add.
	 */
	public void addMember(final NEATGenome genome) {

		if (training.getComparator().isBetterThan(genome.getScore(),
				bestScore)) {
			bestScore = genome.getScore();
			gensNoImprovement = 0;
			leader = genome;
		}

		members.add(genome);

	}

	/**
	 * Adjust the score.  This is to give bonus or penalty.
	 */
	public void adjustScore() {

		for (final Genome member : members) {
			double score = member.getScore();

			if (age < training.getPopulation().getYoungBonusAgeThreshhold()) {
				score = training.getComparator().applyBonus(score,
						training.getPopulation().getYoungScoreBonus());
			}

			if (age > training.getPopulation().getOldAgeThreshold()) {
				score = training.getComparator().applyPenalty(score,
						training.getPopulation().getOldAgePenalty());
			}

			final double adjustedScore = score / members.size();

			member.setAdjustedScore(adjustedScore);

		}
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
	 * Choose a parent to mate.
	 * @return The parent.
	 */
	public Genome chooseParent() {
		Genome baby;

		if (members.size() == 1) {
			baby = members.get(0);
		}

		else {
			final int maxIndexSize = (int) (training.getPopulation()
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
	 * Purge all members.
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
	public void setLeader(final NEATGenome leader) {
		this.leader = leader;
	}

	/**
	 * Set the number of spawns required.
	 * @param spawnsRequired The number of spawns required.
	 */
	public void setSpawnsRequired(final double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}
}
