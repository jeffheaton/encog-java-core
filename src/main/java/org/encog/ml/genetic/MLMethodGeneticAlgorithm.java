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

import org.encog.ml.MLEncodable;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.GeneticScoreAdapter;
import org.encog.ml.ea.sort.GenomeComparator;
import org.encog.ml.ea.sort.MaximizeScoreComp;
import org.encog.ml.ea.sort.MinimizeScoreComp;
import org.encog.ml.ea.train.threaded.MultiThreadedEA;
import org.encog.ml.genetic.crossover.Splice;
import org.encog.ml.genetic.mutate.MutatePerturb;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.logging.EncogLogging;

/**
 * Implements a genetic algorithm that allows an MLMethod that is encodable (MLEncodable)
 * to be trained.  It works well with both BasicNetwork and FreeformNetwork class, as well
 * as any MLEncodable class.
 * 
 * There are essentially two ways you can make use of this class.
 * 
 * Either way, you will need a score object. The score object tells the genetic
 * algorithm how well suited a neural network is.
 * 
 * If you would like to use genetic algorithms with a training set you should
 * make use TrainingSetScore class. This score object uses a training set to
 * score your neural network.
 * 
 * If you would like to be more abstract, and not use a training set, you can
 * create your own implementation of the CalculateScore method. This class can
 * then score the networks any way that you like.
 */
public class MLMethodGeneticAlgorithm extends BasicTraining implements MultiThreadable {

	/**
	 * Very simple class that implements a genetic algorithm.
	 * 
	 * @author jheaton
	 */
	public class MLMethodGeneticAlgorithmHelper extends MultiThreadedEA {
		public MLMethodGeneticAlgorithmHelper(Population thePopulation,
				CalculateScore theScoreFunction) {
			super(thePopulation, new GeneticScoreAdapter(theScoreFunction));
		}
	}

	/**
	 * Simple helper class that implements the required methods to implement a
	 * genetic algorithm.
	 */
	private MLMethodGeneticAlgorithmHelper genetic;

	/**
	 * Construct a method genetic algorithm.
	 * 
	 * @param network
	 *            The network to base this on.
	 * @param factory
	 * 				This is used to create the initial population.
	 * @param populationSize
	 *            The population size.
	 * @param mutationPercent
	 *            The percent of offspring to mutate.
	 * @param percentToMate
	 *            The percent of the population allowed to mate.
	 */
	public MLMethodGeneticAlgorithm(final MethodFactory factory,
			final CalculateScore calculateScore,
			final int populationSize) {
		super(TrainingImplementationType.Iterative);
		
		final Population population = new BasicPopulation(populationSize, null);
		population.setGenomeFactory(new MLMethodGenomeFactory(factory));
		this.genetic = new MLMethodGeneticAlgorithmHelper(population, calculateScore);
		
		GenomeComparator comp = null;
		if( calculateScore.shouldMinimize() ) {
			comp = new MinimizeScoreComp();
		} else {
			comp = new MaximizeScoreComp();
		}
		this.genetic.setBestComparator(comp);
		this.genetic.setSelectionComparator(comp);

		for (int i = 0; i < population.getPopulationSize(); i++) {
			final MLEncodable chromosomeNetwork = (MLEncodable)factory.factor();
			final MLMethodGenome genome = new MLMethodGenome(chromosomeNetwork);
			getGenetic().calculateScore(genome);
			population.add(genome);
			getGenetic().evaluateBestGenome(genome);
		}
		
		int s = Math.max(population.get(0).size()/5,1);
		getGenetic().setPopulation(population);
		
		this.genetic.addOperation(0.9,new Splice(s));
		this.genetic.addOperation(0.1,new MutatePerturb(1.0));
	
		population.sort(this.genetic.getBestComparator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return The genetic algorithm implementation.
	 */
	public MLMethodGeneticAlgorithmHelper getGenetic() {
		return this.genetic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod getMethod() {
		Genome best = (Genome)genetic.getPopulation().getGenomeFactory().factor();
		this.genetic.copyBestGenome(best);
		best.decode();
		return (MLMethod)best.getOrganism();
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public void iteration() {

		EncogLogging.log(EncogLogging.LEVEL_INFO,
				"Performing Genetic iteration.");
		preIteration();
		setError(getGenetic().getError());
		getGenetic().iteration();
		postIteration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	/**
	 * Set the genetic helper class.
	 * 
	 * @param genetic
	 *            The genetic helper class.
	 */
	public void setGenetic(final MLMethodGeneticAlgorithmHelper genetic) {
		this.genetic = genetic;
	}

	@Override
	public int getThreadCount() {
		return this.genetic.getThreadCount();
	}

	@Override
	public void setThreadCount(int numThreads) {
		this.genetic.setThreadCount(numThreads);
		
	}
}
