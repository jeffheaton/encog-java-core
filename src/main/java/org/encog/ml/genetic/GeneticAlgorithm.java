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
package org.encog.ml.genetic;

import org.encog.ml.MLContext;
import org.encog.ml.genetic.crossover.Crossover;
import org.encog.ml.genetic.genome.CalculateGenomeScore;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.genome.GenomeComparator;
import org.encog.ml.genetic.mutate.Mutate;
import org.encog.ml.genetic.population.Population;
import org.encog.ml.genetic.species.Species;
import org.encog.util.concurrency.MultiThreadable;

/**
 * Implements a genetic algorithm. This is an abstract class. Other classes are
 * provided by Encog use this base class to train neural networks or provide an
 * answer to the traveling salesman problem.
 * 
 * The genetic algorithm is also capable of using a thread pool to speed
 * execution.
 */
public abstract class GeneticAlgorithm  implements MultiThreadable {
	
	/**
	 * The thread count;
	 */
	private int threadCount;
	
	/**
	 * The score calculation object.
	 */
	private CalculateGenomeScore calculateScore;

	/**
	 * The genome comparator.
	 */
	private GenomeComparator comparator;

	/**
	 * The crossover object.
	 */
	private Crossover crossover;

	/**
	 * Percent of the population that the mating population chooses partners.
	 * from.
	 */
	private double matingPopulation;

	/**
	 * The mutation object to use.
	 */
	private Mutate mutate;

	/**
	 * The percent that should mutate.
	 */
	private double mutationPercent;

	/**
	 * What percent should be chosen to mate. They will choose partners from the
	 * entire mating population.
	 */
	private double percentToMate;

	/**
	 * The population.
	 */
	private Population population;
	
	/**
	 * Should this run multi-threaded.
	 */
	private boolean multiThreaded = true;

	/**
	 * Add a genome.
	 * 
	 * @param species
	 *            The species to add.
	 * @param genome
	 *            The genome to add.
	 */
	public final void addSpeciesMember(final Species species, 
			final Genome genome) {

		if (getComparator().isBetterThan(genome.getScore(),
				species.getBestScore())) {
			species.setBestScore(genome.getScore());
			species.setGensNoImprovement(0);
			species.setLeader(genome);
		}

		species.getMembers().add(genome);

	}

	/**
	 * Calculate the score for this genome. The genome's score will be set.
	 * 
	 * @param g
	 *            The genome to calculate for.
	 */
	public final void calculateScore(final Genome g) {
		if (g.getOrganism() instanceof MLContext) {
			((MLContext) g.getOrganism()).clearContext();
		}
		final double score = this.calculateScore.calculateScore(g);
		g.setScore(score);
	}

	/**
	 * @return The score calculation object.
	 */
	public final CalculateGenomeScore getCalculateScore() {
		return this.calculateScore;
	}

	/**
	 * @return The comparator.
	 */
	public final GenomeComparator getComparator() {
		return this.comparator;
	}

	/**
	 * @return The crossover object.
	 */
	public final Crossover getCrossover() {
		return this.crossover;
	}

	/**
	 * Get the mating population.
	 * 
	 * @return The mating population percent.
	 */
	public final double getMatingPopulation() {
		return this.matingPopulation;
	}

	/**
	 * @return The mutate object.
	 */
	public final Mutate getMutate() {
		return this.mutate;
	}

	/**
	 * Get the mutation percent.
	 * 
	 * @return The mutation percent.
	 */
	public final double getMutationPercent() {
		return this.mutationPercent;
	}

	/**
	 * Get the percent to mate.
	 * 
	 * @return The percent to mate.
	 */
	public final double getPercentToMate() {
		return this.percentToMate;
	}

	/**
	 * @return The population.
	 */
	public final Population getPopulation() {
		return this.population;
	}


	/**
	 * Set the score calculation object.
	 * 
	 * @param theCalculateScore
	 *            The score calculation object.
	 */
	public final void setCalculateScore(
			final CalculateGenomeScore theCalculateScore) {
		this.calculateScore = theCalculateScore;
	}

	/**
	 * Set the comparator.
	 * 
	 * @param theComparator
	 *            The comparator.
	 */
	public final void setComparator(final GenomeComparator theComparator) {
		this.comparator = theComparator;
	}

	/**
	 * Set the crossover object.
	 * 
	 * @param theCrossover
	 *            The crossover object.
	 */
	public final void setCrossover(final Crossover theCrossover) {
		this.crossover = theCrossover;
	}

	/**
	 * Set the mating population percent.
	 * 
	 * @param theMatingPopulation
	 *            The mating population percent.
	 */
	public final void setMatingPopulation(final double theMatingPopulation) {
		this.matingPopulation = theMatingPopulation;
	}

	/**
	 * Set the mutate object.
	 * 
	 * @param theMutate
	 *            The mutate object.
	 */
	public final void setMutate(final Mutate theMutate) {
		this.mutate = theMutate;
	}

	/**
	 * Set the mutation percent.
	 * 
	 * @param theMutationPercent
	 *            The percent to mutate.
	 */
	public final void setMutationPercent(final double theMutationPercent) {
		this.mutationPercent = theMutationPercent;
	}

	/**
	 * Set the percent to mate.
	 * 
	 * @param thePercentToMate
	 *            The percent to mate.
	 */
	public final void setPercentToMate(final double thePercentToMate) {
		this.percentToMate = thePercentToMate;
	}

	/**
	 * Set the population.
	 * 
	 * @param thePopulation
	 *            The population.
	 */
	public final void setPopulation(final Population thePopulation) {
		this.population = thePopulation;
	}
	
	/**
	 * Perform one training iteration.
	 */
	public abstract void iteration();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getThreadCount() {
		return this.threadCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setThreadCount(int numThreads) {
		this.threadCount = numThreads;
		
	}
	
}
