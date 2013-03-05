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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLContext;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.codec.GenomeAsPhenomeCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.opp.OperationList;
import org.encog.ml.ea.opp.selection.PrgSelection;
import org.encog.ml.ea.opp.selection.TournamentSelection;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.parallel.ParallelScore;
import org.encog.ml.ea.sort.GenomeComparator;
import org.encog.ml.ea.sort.MaximizeAdjustedScoreComp;
import org.encog.ml.ea.sort.MaximizeScoreComp;
import org.encog.ml.ea.sort.MinimizeAdjustedScoreComp;
import org.encog.ml.ea.sort.MinimizeScoreComp;
import org.encog.ml.ea.species.SingleSpeciation;
import org.encog.ml.ea.species.Speciation;
import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.prg.train.GeneticTrainingParams;
import org.encog.util.concurrency.MultiThreadable;

/**
 * Provides a basic implementation of a genetic algorithm.
 */
public class BasicEA implements EvolutionaryAlgorithm,
		MultiThreadable, Serializable {

	public static void calculateScoreAdjustment(final Genome genome,
			final List<AdjustScore> adjusters) {
		final double score = genome.getScore();
		double delta = 0;

		for (final AdjustScore a : adjusters) {
			delta += a.calculateAdjustment(genome);
		}

		genome.setAdjustedScore(score + delta);
	}

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
	
	/**
	 * The score calculation function.
	 */
	private final CalculateScore scoreFunction;

	private PrgSelection selection;
	private final List<AdjustScore> adjusters = new ArrayList<AdjustScore>();

	private final OperationList operators = new OperationList();
	private GeneticCODEC codec = new GenomeAsPhenomeCODEC();
	private RandomFactory randomNumberFactory = Encog.getInstance()
			.getRandomFactory().factorFactory();
	private boolean validationMode;

	/**
	 * The iteration number.
	 */
	private int iteration;
	private int threadCount;
	private int actualThreadCount = -1;
	private Speciation speciation = new SingleSpeciation();
	private Throwable reportedError;
	private Genome oldBestGenome;
	private final List<Genome> newPopulation = new ArrayList<Genome>();
	private EvolutionaryOperator champMutation;

	private double eliteRate = 0.3;
	private int maxTries = 5;

	/**
	 * The best ever network.
	 */
	private Genome bestGenome;

	public BasicEA(final Population thePopulation,
			final CalculateScore theScoreFunction) {

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

	public boolean addChild(final Genome genome) {
		synchronized (this.newPopulation) {
			if (this.newPopulation.size() < getPopulation().getPopulationSize()) {
				// don't readd the old best genome, it was already added
				if (genome != this.oldBestGenome) {

					if (isValidationMode()) {
						if (this.newPopulation.contains(genome)) {
							throw new EncogError(
									"Genome already added to population: "
											+ genome.toString());
						}
					}

					this.newPopulation.add(genome);
				}

				if (getBestComparator().isBetterThan(genome, this.bestGenome)) {
					this.bestGenome = genome;
					getPopulation().setBestGenome(this.bestGenome);
				}
				return true;
			} else {
				if (isValidationMode()) {
					// throw new EncogError("Population overflow");
				}
				return false;
			}
		}
	}

	public void addOperation(final double probability,
			final EvolutionaryOperator opp) {
		getOperators().add(probability, opp);
		opp.init(this);
	}

	@Override
	public void addScoreAdjuster(final AdjustScore scoreAdjust) {
		this.adjusters.add(scoreAdjust);
	}

	/**
	 * Calculate the score for this genome. The genome's score will be set.
	 * 
	 * @param g
	 *            The genome to calculate for.
	 */
	@Override
	public void calculateScore(final Genome g) {
		final MLMethod phenotype = getCODEC().decode(g);
		double score;

		// deal with invalid decode
		if (phenotype == null) {
			if (getBestComparator().shouldMinimize()) {
				score = Double.POSITIVE_INFINITY;
			} else {
				score = Double.NEGATIVE_INFINITY;
			}
		} else {
			if (phenotype instanceof MLContext) {
				((MLContext) phenotype).clearContext();
			}
			score = getScoreFunction().calculateScore(phenotype);
		}

		// now set the scores
		g.setScore(score);
		g.setAdjustedScore(score);
	}

	/**
	 * @return The comparator.
	 */
	@Override
	public GenomeComparator getBestComparator() {
		return this.bestComparator;
	}

	/**
	 * @return the bestGenome
	 */
	public Genome getBestGenome() {
		return this.bestGenome;
	}

	/**
	 * @return the champMutation
	 */
	public EvolutionaryOperator getChampMutation() {
		return this.champMutation;
	}

	@Override
	public GeneticCODEC getCODEC() {
		return this.codec;
	}

	/**
	 * @return the eliteRate
	 */
	public double getEliteRate() {
		return this.eliteRate;
	}

	/**
	 * return The error for the best genome.
	 */
	public double getError() {
		if (this.bestGenome != null) {
			return this.bestGenome.getScore();
		} else {
			if (getScoreFunction().shouldMinimize()) {
				return Double.POSITIVE_INFINITY;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		}
	}

	public int getIteration() {
		return this.iteration;
	}

	@Override
	public int getMaxIndividualSize() {
		return this.population.getMaxIndividualSize();
	}

	/**
	 * @return the oldBestGenome
	 */
	public Genome getOldBestGenome() {
		return this.oldBestGenome;
	}

	/**
	 * @return the operators
	 */
	public OperationList getOperators() {
		return this.operators;
	}

	/**
	 * @return the params
	 */
	@Override
	public GeneticTrainingParams getParams() {
		return this.params;
	}

	/**
	 * @return The population.
	 */
	@Override
	public Population getPopulation() {
		return this.population;
	}

	/**
	 * @return the randomNumberFactory
	 */
	public RandomFactory getRandomNumberFactory() {
		return this.randomNumberFactory;
	}

	@Override
	public List<AdjustScore> getScoreAdjusters() {
		return this.adjusters;
	}

	@Override
	public CalculateScore getScoreFunction() {
		return this.scoreFunction;
	}

	@Override
	public PrgSelection getSelection() {
		return this.selection;
	}

	/**
	 * @return The comparator.
	 */
	@Override
	public GenomeComparator getSelectionComparator() {
		return this.selectionComparator;
	}

	/**
	 * @return the speciation
	 */
	public Speciation getSpeciation() {
		return this.speciation;
	}

	@Override
	public int getThreadCount() {
		return this.threadCount;
	}

	/**
	 * @return the validationMode
	 */
	public boolean isValidationMode() {
		return this.validationMode;
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public void iteration() {
		if (this.actualThreadCount == -1) {
			preIteration();
		}

		this.iteration++;

		ExecutorService taskExecutor = null;

		if (this.actualThreadCount == 1) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.actualThreadCount);
		}

		// Clear new population to just best genome.
		this.newPopulation.clear();
		this.newPopulation.add(this.bestGenome);
		this.oldBestGenome = this.bestGenome;

		// execute species in parallel
		for (final Species s : getPopulation().getSpecies()) {
			final EAWorker worker = new EAWorker(this, s);
			taskExecutor.execute(worker);
		}

		// wait for threadpool to shutdown
		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			throw new GeneticError(e);
		}

		if (this.reportedError != null) {
			throw new GeneticError(this.reportedError);
		}

		if (isValidationMode()) {
			final int currentPopSize = this.newPopulation.size();
			final int targetPopSize = getPopulation().getPopulationSize();
			if (currentPopSize != targetPopSize) {
				throw new EncogError("Population size of " + currentPopSize
						+ " is outside of the target size of " + targetPopSize);
			}

			if (this.oldBestGenome != null
					&& !this.newPopulation.contains(this.oldBestGenome)) {
				throw new EncogError(
						"The top genome died, this should never happen!!");
			}

			if (this.bestGenome != null
					&& this.oldBestGenome != null
					&& getBestComparator().isBetterThan(this.oldBestGenome,
							this.bestGenome)) {
				throw new EncogError(
						"The best genome's score got worse, this should never happen!! Went from "
								+ this.oldBestGenome.getScore() + " to "
								+ this.bestGenome.getScore());
			}
		}

		this.speciation.performSpeciation(this.newPopulation);
	}

	private void preIteration() {

		this.speciation.init(this);

		// find out how many threads to use
		if (this.threadCount == 0) {
			this.actualThreadCount = Runtime.getRuntime().availableProcessors();
		} else {
			this.actualThreadCount = this.threadCount;
		}

		// score the initial population
		final ParallelScore pscore = new ParallelScore(getPopulation(),
				getCODEC(), new ArrayList<AdjustScore>(), getScoreFunction(),
				this.actualThreadCount);
		pscore.setThreadCount(this.actualThreadCount);
		pscore.process();
		this.actualThreadCount = pscore.getThreadCount();

		// just pick the first genome as best, it will be updated later.
		// also most populations are sorted this way after training finishes
		// (for reload)
		// if there is an empty population, the constructor would have blow
		this.bestGenome = getPopulation().getSpecies().get(0).getMembers()
				.get(0);
		getPopulation().setBestGenome(this.bestGenome);

		// speciate
		final List<Genome> genomes = getPopulation().flatten();
		this.speciation.performSpeciation(genomes);

	}

	public void reportError(final Throwable t) {
		synchronized (this) {
			if (this.reportedError == null) {
				this.reportedError = t;
			}
		}
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
	 * @param champMutation
	 *            the champMutation to set
	 */
	public void setChampMutation(final EvolutionaryOperator champMutation) {
		this.champMutation = champMutation;
	}

	public void setCODEC(final GeneticCODEC theCodec) {
		this.codec = theCodec;
	}

	/**
	 * @param eliteRate
	 *            the eliteRate to set
	 */
	public void setEliteRate(final double eliteRate) {
		this.eliteRate = eliteRate;
	}

	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(final GeneticTrainingParams params) {
		this.params = params;
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
	 * @param randomNumberFactory
	 *            the randomNumberFactory to set
	 */
	public void setRandomNumberFactory(final RandomFactory randomNumberFactory) {
		this.randomNumberFactory = randomNumberFactory;
	}

	@Override
	public void setSelection(final PrgSelection selection) {
		this.selection = selection;
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
	 * @param speciation
	 *            the speciation to set
	 */
	public void setSpeciation(final Speciation speciation) {
		this.speciation = speciation;
	}

	@Override
	public void setThreadCount(final int numThreads) {
		this.threadCount = numThreads;
	}

	/**
	 * @param validationMode
	 *            the validationMode to set
	 */
	public void setValidationMode(final boolean validationMode) {
		this.validationMode = validationMode;
	}

	@Override
	public int getMaxTries() {
		return this.maxTries;
	}

	/**
	 * @param maxTries the maxTries to set
	 */
	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}

	@Override
	public void finishTraining() {
		// TODO Auto-generated method stub
		
	}
	
	

}
