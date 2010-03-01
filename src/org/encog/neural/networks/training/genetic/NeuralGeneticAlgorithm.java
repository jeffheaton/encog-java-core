/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a genetic algorithm that allows a feedforward or simple
 * recurrent neural network to be trained using a genetic algorithm. 
 * 
 * There are essentially two ways you can make use of this
 * class.
 * 
 * Either way, you will need a score object.  The score object tells the
 * genetic algorithm how well suited a neural network is.
 * 
 * If you would like to use genetic algorithms with a training set you 
 * should make use TrainingSetScore class.  This score object uses a training
 * set to score your neural network.
 * 
 * If you would like to be more abstract, and not use a training set, you
 * can create your own implementation of the CalculateScore method.  This
 * class can then score the networks any way that you like.
 */
public class NeuralGeneticAlgorithm extends BasicTraining {

	/**
	 * Very simple class that implements a genetic algorithm.  
	 * 
	 * @author jheaton
	 */
	public class NeuralGeneticAlgorithmHelper extends GeneticAlgorithm<Double> {
		/**
		 * @return The error from the last iteration.
		 */
		public double getError() {
			return getChromosome(0).getScore();
		}

		/**
		 * Get the current best neural network.
		 * 
		 * @return The current best neural network.
		 */
		public BasicNetwork getNetwork() {
			final NeuralChromosome c = (NeuralChromosome) getChromosome(0);
			c.updateNetwork();
			return c.getNetwork();
		}
	}

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Simple helper class that implements the required methods to 
	 * implement a genetic algorithm.
	 */
	private NeuralGeneticAlgorithmHelper genetic;
	
	/**
	 * The score calculation object.
	 */
	private CalculateScore calculateScore;

	/**
	 * Construct a neural genetic algorithm.  
	 * @param network The network to base this on.
	 * @param randomizer The randomizer used to create this initial population.
	 * @param calculateScore The score calculation object.
	 * @param populationSize The population size.
	 * @param mutationPercent The percent of offspring to mutate.
	 * @param percentToMate The percent of the population allowed to mate.
	 */
	public NeuralGeneticAlgorithm(final BasicNetwork network,
			final Randomizer randomizer,
			final CalculateScore calculateScore,
			final int populationSize, final double mutationPercent,
			final double percentToMate) {

		this.genetic = new NeuralGeneticAlgorithmHelper();
		this.genetic.setShouldMinimize(calculateScore.shouldMinimize());
		this.calculateScore = calculateScore;
		getGenetic().setMutationPercent(mutationPercent);
		getGenetic().setMatingPopulation(percentToMate * 2);
		getGenetic().setPopulationSize(populationSize);
		getGenetic().setPercentToMate(percentToMate);
		
		getGenetic().setChromosomes(
				new NeuralChromosome[getGenetic()
						.getPopulationSize()]);
		for (int i = 0; i < getGenetic().getChromosomes().length; i++) {
			final BasicNetwork chromosomeNetwork = (BasicNetwork) network
					.clone();
			randomizer.randomize(chromosomeNetwork);

			final NeuralChromosome c = 
				new NeuralChromosome(
					this, chromosomeNetwork);
			getGenetic().setChromosome(i, c);
		}
		getGenetic().sortChromosomes();
		getGenetic().defineCutLength();
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
	 * @param genetic The genetic helper class.
	 */
	public void setGenetic(final NeuralGeneticAlgorithmHelper genetic) {
		this.genetic = genetic;
	}

	/**
	 * @return The score calculation object.
	 */
	public CalculateScore getCalculateScore() {
		return calculateScore;
	}
}
