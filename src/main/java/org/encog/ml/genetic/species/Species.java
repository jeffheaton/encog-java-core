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
package org.encog.ml.genetic.species;

import java.util.List;

import org.encog.ml.genetic.genome.Genome;

/**
 * Defines the features used in a species. A species is a group of genomes.
 */
public interface Species {


	/**
	 * Calculate the amount that a species will spawn.
	 */
	void calculateSpawnAmount();

	/**
	 * Choose a worthy parent for mating.
	 * 
	 * @return The parent genome.
	 */
	Genome chooseParent();

	/**
	 * @return The age of this species.
	 */
	int getAge();

	/**
	 * @return The best score for this species.
	 */
	double getBestScore();

	/**
	 * @return How many generations with no improvement.
	 */
	int getGensNoImprovement();

	/**
	 * @return Get the leader for this species. The leader is the genome with
	 *         the best score.
	 */
	Genome getLeader();

	/**
	 * @return The numbers of this species.
	 */
	List<Genome> getMembers();

	/**
	 * @return The number of genomes this species will try to spawn into the
	 *         next generation.
	 */
	double getNumToSpawn();

	/**
	 * @return The number of spawns this species requires.
	 */
	double getSpawnsRequired();

	/**
	 * @return The species ID.
	 */
	long getSpeciesID();

	/**
	 * Purge old unsuccessful genomes.
	 */
	void purge();

	/**
	 * Set the age of this species.
	 * @param age The age.
	 */
	void setAge(int age);

	/**
	 * Set the best score.
	 * @param bestScore The best score.
	 */
	void setBestScore(double bestScore);

	/**
	 * Set the number of generations with no improvement.
	 * @param gensNoImprovement The number of generations with
	 * no improvement.
	 */
	void setGensNoImprovement(int gensNoImprovement);

	/**
	 * Set the leader of this species.
	 * @param leader The leader of this species.
	 */
	void setLeader(Genome leader);

	/**
	 * Set the number of spawns required.
	 * @param spawnsRequired The number of spawns required.
	 */
	void setSpawnsRequired(double spawnsRequired);
}
