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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.solve.genetic.crossover.Crossover;
import org.encog.solve.genetic.genome.CalculateGenomeScore;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.genome.GenomeComparator;
import org.encog.solve.genetic.mutate.Mutate;
import org.encog.solve.genetic.population.Population;
import org.encog.util.concurrency.EncogConcurrency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a genetic algorithm. This is an abstract class. Other classes are
 * provided in this book that use this base class to train neural networks or
 * provide an answer to the traveling salesman problem.
 * 
 * The genetic algorithm is also capable of using a thread pool to speed
 * execution.
 * 
 * @param <GENE_TYPE>
 *            The datatype of the gene.
 */
public class GeneticAlgorithm {

	/**
	 * Threadpool timeout.
	 */
	public static final int TIMEOUT = 120;



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
	 * Percent of the population that the mating population chooses partners.
	 * from.
	 */
	private double matingPopulation;
	
	private Crossover crossover;
	
	private Mutate mutate;
	
	private Population population;
	
	private CalculateGenomeScore calculateScore;
	
	private GenomeComparator comparator;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	/**
	 * Get the mating population.
	 * 
	 * @return The mating population percent.
	 */
	public double getMatingPopulation() {
		return this.matingPopulation;
	}

	/**
	 * Get the mutation percent.
	 * 
	 * @return The mutation percent.
	 */
	public double getMutationPercent() {
		return this.mutationPercent;
	}

	/**
	 * Get the percent to mate.
	 * 
	 * @return The percent to mate.
	 */
	public double getPercentToMate() {
		return this.percentToMate;
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 * 
	 * @throws NeuralNetworkException
	 */
	public void iteration() {

		final int countToMate = (int) (population.getPopulationSize() 
				* getPercentToMate());
		final int offspringCount = countToMate * 2;
		int offspringIndex = population.getPopulationSize() - offspringCount;
		final int matingPopulationSize = (int) (population.getPopulationSize() 
				* getMatingPopulation());

		// mate and form the next generation
		for (int i = 0; i < countToMate; i++) {
			final Genome mother = this.population.getGenomes().get(i);
			final int fatherInt = (int) (Math.random() * matingPopulationSize);
			final Genome father = this.population.getGenomes().get(fatherInt);
			final Genome child1 
			= this.population.getGenomes().get(offspringIndex);
			final Genome child2 
			= this.population.getGenomes().get(offspringIndex + 1);

			final MateWorker worker = new MateWorker(
					mother, father, child1, child2);

			EncogConcurrency.getInstance().processTask(worker);

			offspringIndex += 2;
		}

		EncogConcurrency.getInstance().shutdown(5);

		// sort the next generation
		population.sort();
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

	public Crossover getCrossover() {
		return crossover;
	}

	public void setCrossover(Crossover crossover) {
		this.crossover = crossover;
	}

	public Mutate getMutate() {
		return mutate;
	}

	public void setMutate(Mutate mutate) {
		this.mutate = mutate;
	}

	public Population getPopulation() {
		return population;
	}

	public void setPopulation(Population population) {
		this.population = population;
	}

	public CalculateGenomeScore getCalculateScore() {
		return calculateScore;
	}

	public void setCalculateScore(CalculateGenomeScore calculateScore) {
		this.calculateScore = calculateScore;
	}

	public GenomeComparator getComparator() {
		return comparator;
	}

	public void setComparator(GenomeComparator comparator) {
		this.comparator = comparator;
	}

	public void calculateScore(Genome g) {
		double score = this.calculateScore.calculateScore(g);
		g.setScore(score);		
	}	
}
