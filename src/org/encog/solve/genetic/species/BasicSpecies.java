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
	private int age;
	private double bestFitness;
	private int gensNoImprovement;
	private Genome leader;
	private final List<Genome> members = new ArrayList<Genome>();
	private double spawnsRequired;
	private final long speciesID;
	private final GeneticAlgorithm training;

	public BasicSpecies(final GeneticAlgorithm training,
			final NEATGenome first, final long speciesID) {
		this.training = training;
		this.speciesID = speciesID;
		bestFitness = first.getScore();
		gensNoImprovement = 0;
		age = 0;
		leader = first;
		spawnsRequired = 0;
		members.add(first);
	}

	public void addMember(final NEATGenome genome) {

		if (training.getComparator().isBetterThan(genome.getScore(),
				bestFitness)) {
			bestFitness = genome.getScore();
			gensNoImprovement = 0;
			leader = genome;
		}

		members.add(genome);

	}

	public void adjustFitness() {

		for (final Genome member : members) {
			double fitness = member.getScore();

			if (age < training.getPopulation().getYoungBonusAgeThreshhold()) {
				fitness = training.getComparator().applyBonus(fitness,
						training.getPopulation().getYoungFitnessBonus());
			}

			if (age > training.getPopulation().getOldAgeThreshold()) {
				fitness = training.getComparator().applyPenalty(fitness,
						training.getPopulation().getOldAgePenalty());
			}

			final double adjustedScore = fitness / members.size();

			member.setAdjustedScore(adjustedScore);

		}
	}

	public void calculateSpawnAmount() {
		for (final Genome genome : members) {
			spawnsRequired += genome.getAmountToSpawn();
		}

	}

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

	public int getAge() {
		return age;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public int getGensNoImprovement() {
		return gensNoImprovement;
	}

	public Genome getLeader() {
		return leader;
	}

	public List<Genome> getMembers() {
		return members;
	}

	public double getNumToSpawn() {
		return spawnsRequired;
	}

	public double getSpawnsRequired() {
		return spawnsRequired;
	}

	public long getSpeciesID() {
		return speciesID;
	}

	public void purge() {
		members.clear();

		age++;

		gensNoImprovement++;

		spawnsRequired = 0;

	}

	public void setAge(final int age) {
		this.age = age;
	}

	public void setBestFitness(final double bestFitness) {
		this.bestFitness = bestFitness;
	}

	public void setGensNoImprovement(final int gensNoImprovement) {
		this.gensNoImprovement = gensNoImprovement;
	}

	public void setLeader(final NEATGenome leader) {
		this.leader = leader;
	}

	public void setSpawnsRequired(final double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}

}
