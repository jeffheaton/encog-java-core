/*
 * Encog(tm) Core v2.5 
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

package org.encog.neural.networks.training.genetic;

import org.encog.mathutil.randomize.Randomizer;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.solve.genetic.GeneticAlgorithm;
import org.encog.solve.genetic.crossover.Splice;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.mutate.MutatePerturb;
import org.encog.solve.genetic.population.BasicPopulation;
import org.encog.solve.genetic.population.Population;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class NeuralGeneticAlgorithm extends BasicTraining {

	/**
	 * Very simple class that implements a genetic algorithm.
	 * 
	 * @author jheaton
	 */
	public class NeuralGeneticAlgorithmHelper extends GeneticAlgorithm {
		/**
		 * @return The error from the last iteration.
		 */
		public double getError() {
			final Genome genome = getPopulation().getBest();
			return genome.getScore();
		}

		/**
		 * Get the current best neural network.
		 * 
		 * @return The current best neural network.
		 */
		public BasicNetwork getNetwork() {
			final Genome genome = getPopulation().getBest();
			return (BasicNetwork) genome.getOrganism();
		}

	}

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

			final NeuralGenome genome = new NeuralGenome(this,
					chromosomeNetwork);
			getGenetic().calculateScore(genome);
			getGenetic().getPopulation().add(genome);
		}
		population.sort();
	}

	/**
	 * @return The genetic algorithm implementation.
	 */
	public NeuralGeneticAlgorithmHelper getGenetic() {
		return this.genetic;
	}

	/**
	 * @return The network that is being trained.
	 */
	public BasicNetwork getNetwork() {
		return getGenetic().getNetwork();
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Performing Genetic iteration.");
		}
		preIteration();
		getGenetic().iteration();
		setError(getGenetic().getError());
		postIteration();
	}

	/**
	 * Set the genetic helper class.
	 * 
	 * @param genetic
	 *            The genetic helper class.
	 */
	public void setGenetic(final NeuralGeneticAlgorithmHelper genetic) {
		this.genetic = genetic;
	}
}
