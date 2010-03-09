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

package org.encog.neural.networks.training.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.solve.genetic.genome.Genome;

public class NEATSpecies {

	private Genome leader;
	private final List<Genome> members = new ArrayList<Genome>();
	private long speciesID;
	private double bestFitness;
	private int gensNoImprovement;
	private int age;
	private double spawnsRequired;
	private final NEATTraining training;

	public NEATSpecies(NEATTraining training, NEATGenome first, long speciesID) {
		this.training = training;
		this.speciesID = speciesID;
		this.bestFitness = first.getScore();
		this.gensNoImprovement = 0;
		this.age = 0;
		this.leader = first;
		this.spawnsRequired = 0;
		this.members.add(first);
	}

	public void adjustFitness() {

		for (Genome member : this.members) {
			double fitness = member.getScore();

			if (this.age < training.getParamYoungBonusAgeThreshhold()) {
				fitness = training.getComparator().applyBonus(fitness,training.getParamYoungFitnessBonus());
			}

			if (this.age > this.training.getParamOldAgeThreshold()) {
				fitness = this.training.getComparator().applyPenalty(fitness, this.training.getParamOldAgePenalty());
			}

			double adjustedScore = fitness / this.members.size();

			member.setAdjustedScore(adjustedScore);

		}
	}

	public Genome getLeader() {
		return leader;
	}

	public void setLeader(NEATGenome leader) {
		this.leader = leader;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}

	public int getGensNoImprovement() {
		return gensNoImprovement;
	}

	public void setGensNoImprovement(int gensNoImprovement) {
		this.gensNoImprovement = gensNoImprovement;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getSpawnsRequired() {
		return spawnsRequired;
	}

	public void setSpawnsRequired(double spawnsRequired) {
		this.spawnsRequired = spawnsRequired;
	}

	public List<Genome> getMembers() {
		return members;
	}

	public long getSpeciesID() {
		return speciesID;
	}

	public void addMember(NEATGenome genome) {

		if( this.training.getComparator().isBetterThan(genome.getScore(), this.bestFitness)) {
			this.bestFitness = genome.getScore();
			this.gensNoImprovement = 0;
			this.leader = genome;
		}

		this.members.add(genome);

	}

	public void calculateSpawnAmount() {
		for (Genome genome : this.members) {
			this.spawnsRequired += genome.getAmountToSpawn();
		}

	}

	public void purge() {
		this.members.clear();

		this.age++;

		this.gensNoImprovement++;

		this.spawnsRequired = 0;

	}

	public double getNumToSpawn() {
		return this.spawnsRequired;
	}

	public Genome spawn() {
		Genome baby;

		if (this.members.size() == 1) {
			baby = members.get(0);
		}

		else {
			int maxIndexSize = (int) (this.training.getParamSurvivalRate() * this.members
					.size()) + 1;
			int theOne = (int) RangeRandomizer.randomize(0, maxIndexSize);
			baby = this.members.get(theOne);
		}

		return baby;
	}

}
