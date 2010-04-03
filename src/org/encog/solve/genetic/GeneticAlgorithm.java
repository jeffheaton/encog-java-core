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

package org.encog.solve.genetic;

import org.encog.neural.networks.ContextClearable;
import org.encog.solve.genetic.crossover.Crossover;
import org.encog.solve.genetic.genome.CalculateGenomeScore;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.genome.GenomeComparator;
import org.encog.solve.genetic.mutate.Mutate;
import org.encog.solve.genetic.population.Population;
import org.encog.util.concurrency.EncogConcurrency;
import org.encog.util.concurrency.TaskGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a genetic algorithm. This is an abstract class. Other classes are
 * provided in this book that use this base class to train neural networks or
 * provide an answer to the traveling salesman problem.
 * 
 * The genetic algorithm is also capable of using a thread pool to speed
 * execution.
 */
public class GeneticAlgorithm {

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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	 * Calculate the score for this genome.  The genome's score will be set.
	 * @param g The genome to calculate for.
	 */
	public void calculateScore(final Genome g) {
		((ContextClearable)g.getOrganism()).clearContext();
		final double score = calculateScore.calculateScore(g);
		g.setScore(score);
	}

	/**
	 * @return The score calculation object.
	 */
	public CalculateGenomeScore getCalculateScore() {
		return calculateScore;
	}

	/**
	 * @return The comparator.
	 */
	public GenomeComparator getComparator() {
		return comparator;
	}

	/**
	 * @return The crossover object.
	 */
	public Crossover getCrossover() {
		return crossover;
	}

	/**
	 * Get the mating population.
	 * 
	 * @return The mating population percent.
	 */
	public double getMatingPopulation() {
		return matingPopulation;
	}

	/**
	 * @return The mutate object.
	 */
	public Mutate getMutate() {
		return mutate;
	}

	/**
	 * Get the mutation percent.
	 * 
	 * @return The mutation percent.
	 */
	public double getMutationPercent() {
		return mutationPercent;
	}

	/**
	 * Get the percent to mate.
	 * 
	 * @return The percent to mate.
	 */
	public double getPercentToMate() {
		return percentToMate;
	}

	/**
	 * @return The population.
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 * 
	 * @throws NeuralNetworkException
	 */
	public void iteration() {

		final int countToMate = (int) (population.getPopulationSize() * getPercentToMate());
		final int offspringCount = countToMate * 2;
		int offspringIndex = population.getPopulationSize() - offspringCount;
		final int matingPopulationSize = (int) (population.getPopulationSize() * getMatingPopulation());

		final TaskGroup group = EncogConcurrency.getInstance()
				.createTaskGroup();

		// mate and form the next generation
		for (int i = 0; i < countToMate; i++) {
			final Genome mother = population.getGenomes().get(i);
			final int fatherInt = (int) (Math.random() * matingPopulationSize);
			final Genome father = population.getGenomes().get(fatherInt);
			final Genome child1 = population.getGenomes().get(offspringIndex);
			final Genome child2 = population.getGenomes().get(
					offspringIndex + 1);

			final MateWorker worker = new MateWorker(mother, father, child1,
					child2);

			EncogConcurrency.getInstance().processTask(worker);

			offspringIndex += 2;
		}

		group.waitForComplete();

		// sort the next generation
		population.sort();
	}

	/**
	 * Set the score calculation object.
	 * @param calculateScore The score calculation object.
	 */
	public void setCalculateScore(final CalculateGenomeScore calculateScore) {
		this.calculateScore = calculateScore;
	}

	/**
	 * Set the comparator.
	 * @param comparator The comparator.
	 */
	public void setComparator(final GenomeComparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * Set the crossover object.
	 * @param crossover The crossover object.
	 */
	public void setCrossover(final Crossover crossover) {
		this.crossover = crossover;
	}

	/**
	 * Set the mating population percent.
	 * 
	 * @param matingPopulation
	 *            The mating population percent.
	 */
	public void setMatingPopulation(final double matingPopulation) {
		this.matingPopulation = matingPopulation;
	}

	/**
	 * Set the mutate object.
	 * @param mutate The mutate object.
	 */
	public void setMutate(final Mutate mutate) {
		this.mutate = mutate;
	}

	/**
	 * Set the mutation percent.
	 * 
	 * @param mutationPercent
	 *            The percent to mutate.
	 */
	public void setMutationPercent(final double mutationPercent) {
		this.mutationPercent = mutationPercent;
	}

	/**
	 * Set the percent to mate.
	 * 
	 * @param percentToMate
	 *            The percent to mate.
	 */
	public void setPercentToMate(final double percentToMate) {
		this.percentToMate = percentToMate;
	}

	/**
	 * Set the population.
	 * @param population The population.
	 */
	public void setPopulation(final Population population) {
		this.population = population;
	}
}
