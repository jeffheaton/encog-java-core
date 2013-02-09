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
package org.encog.ml.ea.train.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.Encog;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLContext;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.opp.OperationList;
import org.encog.ml.ea.opp.selection.PrgSelection;
import org.encog.ml.ea.opp.selection.TournamentSelection;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.sort.GenomeComparator;
import org.encog.ml.ea.sort.MaximizeAdjustedScoreComp;
import org.encog.ml.ea.sort.MaximizeScoreComp;
import org.encog.ml.ea.sort.MinimizeAdjustedScoreComp;
import org.encog.ml.ea.sort.MinimizeScoreComp;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.train.GeneticTrainingParams;

/**
 * Provides a basic implementation of a genetic algorithm.
 */
public abstract class BasicEA implements EvolutionaryAlgorithm, Serializable {
	
	private GeneticTrainingParams params = new GeneticTrainingParams();
	
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
	private final CalculateScore scoreFunction;
	private PrgSelection selection;
	
	private List<AdjustScore> adjusters = new ArrayList<AdjustScore>();
	private final OperationList operators = new OperationList();
	
	private GeneticCODEC codec;
	private RandomFactory randomNumberFactory = Encog.getInstance().getRandomFactory().factorFactory();
	
	public BasicEA(Population thePopulation, CalculateScore theScoreFunction) {
		
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
		MLMethod phenotype = this.getCODEC().decode(g);
		if (phenotype instanceof MLContext) {
			((MLContext)phenotype).clearContext();
		}
		final double score = this.getScoreFunction().calculateScore(phenotype);
		g.setScore(score);
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

	@Override
	public CalculateScore getScoreFunction() {
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
	

	@Override
	public List<AdjustScore> getScoreAdjusters() {
		return this.adjusters;
	}

	@Override
	public void addScoreAdjuster(AdjustScore scoreAdjust) {
		this.adjusters.add(scoreAdjust);
	}
	
	public static void calculateScoreAdjustment(Genome genome, List<AdjustScore> adjusters) {
		double score = genome.getScore();
		double delta = 0;
		
		for(AdjustScore a: adjusters) {
			delta+=a.calculateAdjustment(genome);
		}
		
		genome.setAdjustedScore(score+delta);
	}
	
	public GeneticCODEC getCODEC() {
		return this.codec;
	}
	
	public void setCODEC(GeneticCODEC theCodec) {
		this.codec = theCodec;
	}
	
	/**
	 * @return the operators
	 */
	public OperationList getOperators() {
		return operators;
	}
	
	public void addOperation(double probability, EvolutionaryOperator opp) {
		this.getOperators().add(probability, opp);
		opp.init(this);
	}
	
	/**
	 * @return the randomNumberFactory
	 */
	public RandomFactory getRandomNumberFactory() {
		return randomNumberFactory;
	}

	/**
	 * @param randomNumberFactory
	 *            the randomNumberFactory to set
	 */
	public void setRandomNumberFactory(RandomFactory randomNumberFactory) {
		this.randomNumberFactory = randomNumberFactory;
	}
	
}
