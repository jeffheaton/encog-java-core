/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.solve.genetic;

import java.util.Arrays;

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
public abstract class GeneticAlgorithm<GENE_TYPE> {

	/**
	 * Threadpool timeout.
	 */
	public static final int TIMEOUT = 120;

	/**
	 * How many chromosomes should be created.
	 */
	private int populationSize;

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

	/**
	 * Should the same gene be prevented from repeating.
	 */
	private boolean preventRepeat;

	/**
	 * How much genetic material should be cut when mating.
	 */
	private int cutLength;

	/**
	 * The population.
	 */
	private Chromosome<GENE_TYPE>[] chromosomes;
	
	/**
	 * Should the "score" be minimized?
	 */
	private boolean shouldMinimize;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Define the cut length to be 1/3 the length of a chromosome. This is a
	 * good default value for it. If there are no chromosomes yet this call will
	 * set the cut length to 0.
	 */
	public void defineCutLength() {
		if (this.chromosomes.length > 0) {
			final int size = this.chromosomes[0].getGenes().length;
			setCutLength(size / 3);
		}
	}

	/**
	 * Get a specific chromosome.
	 * 
	 * @param i
	 *            The chromosome to return, 0 for the first one.
	 * @return A chromosome.
	 */
	public Chromosome<GENE_TYPE> getChromosome(final int i) {
		return this.chromosomes[i];
	}

	/**
	 * Return the entire population.
	 * 
	 * @return the chromosomes
	 */
	public Chromosome<GENE_TYPE>[] getChromosomes() {
		return this.chromosomes;
	}

	/**
	 * Get the cut length.
	 * 
	 * @return The cut length.
	 */
	public int getCutLength() {
		return this.cutLength;
	}

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
	 * Get the population size.
	 * 
	 * @return The population size.
	 */
	public int getPopulationSize() {
		return this.populationSize;
	}

	/**
	 * Should repeating genes be prevented.
	 * 
	 * @return True if repeating genes should be prevented.
	 */
	public boolean isPreventRepeat() {
		return this.preventRepeat;
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 * 
	 * @throws NeuralNetworkException
	 */
	public void iteration() {

		final int countToMate = (int) (getPopulationSize() 
				* getPercentToMate());
		final int offspringCount = countToMate * 2;
		int offspringIndex = getPopulationSize() - offspringCount;
		final int matingPopulationSize = (int) (getPopulationSize() 
				* getMatingPopulation());

		// mate and form the next generation
		for (int i = 0; i < countToMate; i++) {
			final Chromosome<GENE_TYPE> mother = this.chromosomes[i];
			final int fatherInt = (int) (Math.random() * matingPopulationSize);
			final Chromosome<GENE_TYPE> father = this.chromosomes[fatherInt];
			final Chromosome<GENE_TYPE> child1 
			= this.chromosomes[offspringIndex];
			final Chromosome<GENE_TYPE> child2 
			= this.chromosomes[offspringIndex + 1];

			final MateWorker<GENE_TYPE> worker = new MateWorker<GENE_TYPE>(
					mother, father, child1, child2);

			EncogConcurrency.getInstance().processTask(worker);

			offspringIndex += 2;
		}

		EncogConcurrency.getInstance().shutdown(5);

		// sort the next generation
		sortChromosomes();
	}

	/**
	 * Set the specified chromosome.
	 * 
	 * @param i
	 *            The chromosome to set.
	 * @param value
	 *            The value for the specified chromosome.
	 */
	public void setChromosome(final int i, final Chromosome<GENE_TYPE> value) {
		this.chromosomes[i] = value;
	}

	/**
	 * Set the entire population.
	 * 
	 * @param chromosomes
	 *            the chromosomes to set
	 */
	public void setChromosomes(final Chromosome<GENE_TYPE>[] chromosomes) {
		this.chromosomes = chromosomes;
	}

	/**
	 * Set the cut length.
	 * 
	 * @param cutLength
	 *            The cut length.
	 */
	public void setCutLength(final int cutLength) {
		this.cutLength = cutLength;
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

	/**
	 * Set the population size.
	 * 
	 * @param populationSize
	 *            The population size.
	 */
	public void setPopulationSize(final int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * Set the gene.
	 * 
	 * @param preventRepeat
	 *            Should repeats be prevented.
	 */
	public void setPreventRepeat(final boolean preventRepeat) {
		this.preventRepeat = preventRepeat;
	}

	/**
	 * Sort the chromosomes.
	 */
	public void sortChromosomes() {
		Arrays.sort(this.chromosomes);
	}

	/**
	 * @return True if the score should be minimized.
	 */
	public boolean getShouldMinimize() {
		return shouldMinimize;
	}

	/**
	 * Determine if the score should be minimized. 
	 * @param shouldMinimize True if the score should be minimized.
	 */
	public void setShouldMinimize(final boolean shouldMinimize) {
		this.shouldMinimize = shouldMinimize;
	}
	
	
}
