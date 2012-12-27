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

import java.util.Random;

import org.encog.ml.MLContext;
import org.encog.ml.genetic.genome.CalculateGenomeScore;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.population.Population;
import org.encog.ml.genetic.sort.GenomeComparator;
import org.encog.ml.genetic.sort.MaximizeAdjustedScoreComp;
import org.encog.ml.genetic.sort.MaximizeScoreComp;
import org.encog.ml.genetic.sort.MinimizeAdjustedScoreComp;
import org.encog.ml.genetic.sort.MinimizeScoreComp;
import org.encog.ml.prg.train.GeneticTrainingParams;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.ml.prg.train.selection.TournamentSelection;

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
	 * The population.
	 */
	private Population population;
	private final CalculateGenomeScore scoreFunction;
	private PrgSelection selection;
	
	
	public BasicGeneticAlgorithm(Population thePopulation, CalculateGenomeScore theScoreFunction) {
		
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
		this.selection = new TournamentSelection(this, 4);
		
		if (theScoreFunction.shouldMinimize()) {
			this.selectionComparator = new MinimizeAdjustedScoreComp();
			this.bestComparator = new MinimizeScoreComp();
		} else {
			this.selectionComparator = new MaximizeAdjustedScoreComp();
			this.bestComparator = new MaximizeScoreComp();
		}
	}
	
	
	
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

	public CalculateGenomeScore getScoreFunction() {
		return scoreFunction;
	}

	public PrgSelection getSelection() {
		return selection;
	}

	public void setSelection(PrgSelection selection) {
		this.selection = selection;
	}
	
	@Override
	public int getMaxIndividualSize() {
		return this.population.getMaxIndividualSize();
	}
	
	
}
