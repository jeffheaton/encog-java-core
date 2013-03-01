/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.ml.ea.species;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.util.Format;

/**
 * Provides basic functionality for a species.
 */
public class BasicSpecies implements Serializable, Species {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

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
	 * The owner class.
	 */
	private Population population;


	private transient int offspringCount;
	private transient double offspringShare;

	/**
	 * Default constructor, used mainly for persistence.
	 */
	public BasicSpecies() {

	}

	/**
	 * Construct a species.
	 * 
	 * @param thePopulation
	 *            The population the species belongs to.
	 * @param theFirst
	 *            The first genome in the species.
	 * @param theSpeciesID
	 *            The species id.
	 */
	public BasicSpecies(final Population thePopulation,
			final Genome theFirst) {
		this.population = thePopulation;
		this.bestScore = theFirst.getScore();
		this.gensNoImprovement = 0;
		this.age = 0;
		this.leader = theFirst;
		this.members.add(theFirst);
	}

	@Override
	public double calculateShare(final boolean shouldMinimize,
			final double maxScore) {
		double total = 0;

		int count = 0;
		for (final Genome genome : this.members) {
			if (!Double.isNaN(genome.getScore())
					&& !Double.isInfinite(genome.getScore())) {
				double s;
				if (shouldMinimize) {
					s = maxScore - genome.getScore();
				} else {
					s = genome.getScore();
				}
				total += s;
				count++;
			}
		}

		if (count == 0) {
			this.offspringShare = 0;
		} else {
			this.offspringShare = total / count;
		}

		return this.offspringShare;
	}

	/**
	 * @return The age of this species.
	 */
	@Override
	public int getAge() {
		return this.age;
	}

	/**
	 * @return The best score for this species.
	 */
	@Override
	public double getBestScore() {
		return this.bestScore;
	}

	/**
	 * @return The number of generations with no improvement.
	 */
	@Override
	public int getGensNoImprovement() {
		return this.gensNoImprovement;
	}

	/**
	 * @return THe leader of this species.
	 */
	@Override
	public Genome getLeader() {
		return this.leader;
	}

	/**
	 * @return The members of this species.
	 */
	@Override
	public List<Genome> getMembers() {
		return this.members;
	}

	/**
	 * @return the offspringCount
	 */
	@Override
	public int getOffspringCount() {
		return this.offspringCount;
	}

	/**
	 * @return the offspringShare
	 */
	@Override
	public double getOffspringShare() {
		return this.offspringShare;
	}

	/**
	 * @return The population that this species belongs to.
	 */
	@Override
	public Population getPopulation() {
		return this.population;
	}

	/**
	 * Purge all members, increase age by one and count the number of
	 * generations with no improvement.
	 */
	public void purge() {
		this.members.clear();
		if (this.leader != null) {
			this.members.add(this.leader);
		}
		this.age++;
		this.gensNoImprovement++;
		this.offspringCount = 0;
		this.offspringShare = 0;
	}

	/**
	 * Set the age of this species.
	 * 
	 * @param theAge
	 *            The age of this species.
	 */
	@Override
	public void setAge(final int theAge) {
		this.age = theAge;
	}

	/**
	 * Set the best score.
	 * 
	 * @param theBestScore
	 *            The best score.
	 */
	@Override
	public void setBestScore(final double theBestScore) {
		this.bestScore = theBestScore;
	}

	/**
	 * Set the number of generations with no improvement.
	 * 
	 * @param theGensNoImprovement
	 *            The number of generations.
	 */
	@Override
	public void setGensNoImprovement(final int theGensNoImprovement) {
		this.gensNoImprovement = theGensNoImprovement;
	}

	/**
	 * Set the leader.
	 * 
	 * @param theLeader
	 *            The new leader.
	 */
	@Override
	public void setLeader(final Genome theLeader) {
		this.leader = theLeader;
	}

	/**
	 * @param offspringCount
	 *            the offspringCount to set
	 */
	@Override
	public void setOffspringCount(final int offspringCount) {
		this.offspringCount = offspringCount;
	}

	/**
	 * @param thePopulation
	 *            the population to set
	 */
	@Override
	public void setPopulation(final Population thePopulation) {
		this.population = thePopulation;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NEATSpecies: score=");
		result.append(Format.formatDouble(getBestScore(), 2));
		result.append(", members=");
		result.append(this.members.size());
		result.append(", age=");
		result.append(this.age);
		result.append(", no_improv=");
		result.append(this.gensNoImprovement);
		result.append(", share=");
		result.append(this.offspringShare);
		result.append(", offspring count=");
		result.append(this.offspringShare);
		result.append("]");
		return result.toString();
	}

	@Override
	public void add(Genome genome) {
		genome.setPopulation(this.population);
		this.members.add(genome);
	}

}
