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
package org.encog.ml.genetic;

import org.encog.ml.MLContext;
import org.encog.ml.genetic.evolutionary.EvolutionaryOperator;
import org.encog.ml.genetic.genome.CalculateGenomeScore;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.population.Population;
import org.encog.ml.genetic.sort.GenomeComparator;
import org.encog.ml.prg.train.GeneticTrainingParams;

/**
 * Provides a basic implementation of a genetic algorithm.
 */
public abstract class BasicGeneticAlgorithm implements GeneticAlgorithm {
	
	private GeneticTrainingParams params = new GeneticTrainingParams();

	/**
	 * The score calculation object.
	 */
	private CalculateGenomeScore calculateScore;
	
	/**
	 * The genome comparator.
	 */
	private GenomeComparator bestComparator;
	
	/**
	 * The genome comparator.
	 */
	private GenomeComparator selectionComparator;
	
	/**
	 * The crossover object.
	 */
	private EvolutionaryOperator crossover;
	
	/**
	 * Is this the first iteration.
	 */
	private boolean first = true;

	/**
	 * Percent of the population that the mating population chooses partners.
	 * from.
	 */
	private double matingPopulation;

	/**
	 * The mutation object to use.
	 */
	private EvolutionaryOperator mutate;

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
	 * Calculate the score for this genome. The genome's score will be set.
	 * 
	 * @param g
	 *            The genome to calculate for.
	 */
	@Override
	public void calculateScore(final Genome g) {
		if (g.getOrganism() instanceof MLContext) {
			((MLContext) g.getOrganism()).clearContext();
		}
		final double score = this.calculateScore.calculateScore(g);
		g.setScore(score);
	}



	/**
	 * @return The score calculation object.
	 */
	@Override
	public CalculateGenomeScore getCalculateScore() {
		return this.calculateScore;
	}

	/**
	 * @return The comparator.
	 */
	@Override
	public GenomeComparator getSelectionComparator() {
		return this.selectionComparator;
	}
	
	/**
	 * @return The comparator.
	 */
	@Override
	public GenomeComparator getBestComparator() {
		return this.bestComparator;
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
	 * Get the percent to mate.
	 * 
	 * @return The percent to mate.
	 */
	public double getPercentToMate() {
		return this.percentToMate;
	}

	/**
	 * @return The population.
	 */
	@Override
	public Population getPopulation() {
		return this.population;
	}

	/**
	 * Set the score calculation object.
	 * 
	 * @param theCalculateScore
	 *            The score calculation object.
	 */
	@Override
	public void setCalculateScore(
			final CalculateGenomeScore theCalculateScore) {
		this.calculateScore = theCalculateScore;
	}

	/**
	 * Set the comparator.
	 * 
	 * @param theComparator
	 *            The comparator.
	 */
	@Override
	public void setBestComparator(final GenomeComparator theComparator) {
		this.bestComparator = theComparator;
	}
	
	/**
	 * Set the comparator.
	 * 
	 * @param theComparator
	 *            The comparator.
	 */
	@Override
	public void setSelectionComparator(final GenomeComparator theComparator) {
		this.selectionComparator = theComparator;
	}

	/**
	 * Set the population.
	 * 
	 * @param thePopulation
	 *            The population.
	 */
	@Override
	public void setPopulation(final Population thePopulation) {
		this.population = thePopulation;
	}



	/**
	 * @return the params
	 */
	public GeneticTrainingParams getParams() {
		return params;
	}



	/**
	 * @param params the params to set
	 */
	public void setParams(GeneticTrainingParams params) {
		this.params = params;
	}
	
	

}
