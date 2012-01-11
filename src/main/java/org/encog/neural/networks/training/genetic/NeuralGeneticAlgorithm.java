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
package org.encog.neural.networks.training.genetic;

import org.encog.mathutil.randomize.Randomizer;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.genetic.BasicGeneticAlgorithm;
import org.encog.ml.genetic.crossover.Splice;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.mutate.MutatePerturb;
import org.encog.ml.genetic.population.BasicPopulation;
import org.encog.ml.genetic.population.Population;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.logging.EncogLogging;

/**
 * Implements a genetic algorithm that allows a feedforward or simple recurrent
 * neural network to be trained using a genetic algorithm.
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
public class NeuralGeneticAlgorithm extends BasicTraining implements MultiThreadable {

	/**
	 * Very simple class that implements a genetic algorithm.
	 * 
	 * @author jheaton
	 */
	public class NeuralGeneticAlgorithmHelper extends BasicGeneticAlgorithm {
		/**
		 * @return The error from the last iteration.
		 */
		public final double getError() {
			final Genome genome = getPopulation().getBest();
			return genome.getScore();
		}

		/**
		 * Get the current best neural network.
		 * 
		 * @return The current best neural network.
		 */
		public final MLMethod getMethod() {
			final Genome genome = getPopulation().getBest();
			return (BasicNetwork) genome.getOrganism();
		}

	}

	/**
	 * Simple helper class that implements the required methods to implement a
	 * genetic algorithm.
	 */
	private NeuralGeneticAlgorithmHelper genetic;

	/**
	 * Construct a neural genetic algorithm.
	 * 
	 * @param network
	 *            The network to base this on.
	 * @param randomizer
	 *            The randomizer used to create this initial population.
	 * @param calculateScore
	 *            The score calculation object.
	 * @param populationSize
	 *            The population size.
	 * @param mutationPercent
	 *            The percent of offspring to mutate.
	 * @param percentToMate
	 *            The percent of the population allowed to mate.
	 */
	public NeuralGeneticAlgorithm(final BasicNetwork network,
			final Randomizer randomizer, final CalculateScore calculateScore,
			final int populationSize, final double mutationPercent,
			final double percentToMate) {
		super(TrainingImplementationType.Iterative);
		this.genetic = new NeuralGeneticAlgorithmHelper();
		this.genetic.setCalculateScore(new GeneticScoreAdapter(calculateScore));
		final Population population = new BasicPopulation(populationSize);
		getGenetic().setMutationPercent(mutationPercent);
		getGenetic().setMatingPopulation(percentToMate * 2);
		getGenetic().setPercentToMate(percentToMate);
		getGenetic().setCrossover(
				new Splice(network.getStructure().calculateSize() / 3));
		getGenetic().setMutate(new MutatePerturb(4.0));
		getGenetic().setPopulation(population);
		for (int i = 0; i < population.getPopulationSize(); i++) {
			final BasicNetwork chromosomeNetwork = (BasicNetwork) network
					.clone();
			randomizer.randomize(chromosomeNetwork);

			final NeuralGenome genome = new NeuralGenome(chromosomeNetwork);
			genome.setGeneticAlgorithm(getGenetic());
			getGenetic().calculateScore(genome);
			getGenetic().getPopulation().add(genome);
		}
		population.sort();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * @return The genetic algorithm implementation.
	 */
	public final NeuralGeneticAlgorithmHelper getGenetic() {
		return this.genetic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MLMethod getMethod() {
		return getGenetic().getMethod();
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public final void iteration() {

		EncogLogging.log(EncogLogging.LEVEL_INFO,
				"Performing Genetic iteration.");
		preIteration();
		getGenetic().iteration();
		setError(getGenetic().getError());
		postIteration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void resume(final TrainingContinuation state) {

	}

	/**
	 * Set the genetic helper class.
	 * 
	 * @param genetic
	 *            The genetic helper class.
	 */
	public final void setGenetic(final NeuralGeneticAlgorithmHelper genetic) {
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
