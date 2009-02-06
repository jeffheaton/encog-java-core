/*
 * Encog Artificial Intelligence Framework v1.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.training.genetic;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;



/**
 * TrainingSetNeuralGeneticAlgorithm: Implements a genetic algorithm 
 * that allows a feedforward neural network to be trained using a 
 * genetic algorithm.  This algorithm is for a feed forward neural 
 * network.  The neural network is trained using training sets.
 */
public class TrainingSetNeuralGeneticAlgorithm extends
		NeuralGeneticAlgorithm {

	/**
	 * The training set to use.
	 */
	private NeuralDataSet training;

	/**
	 * Construct a training object.
	 * @param network The network to train.
	 * @param reset Should each chromosome be reset.
	 * @param training The training set.
	 * @param populationSize The population size.
	 * @param mutationPercent The mutation percent.
	 * @param percentToMate The percent to mate.
	 */
	public TrainingSetNeuralGeneticAlgorithm(
			final BasicNetwork network,
			final boolean reset, 
			final NeuralDataSet training, 
			final int populationSize,
			final double mutationPercent, 
			final double percentToMate) {

		this.setMutationPercent(mutationPercent);
		this.setMatingPopulation(percentToMate * 2);
		this.setPopulationSize(populationSize);
		this.setPercentToMate(percentToMate);

		this.training = training;

		setChromosomes(new TrainingSetNeuralChromosome[getPopulationSize()]);
		for (int i = 0; i < getChromosomes().length; i++) {
			final BasicNetwork chromosomeNetwork = (BasicNetwork) network
					.clone();
			if (reset) {
				chromosomeNetwork.reset();
			}

			final TrainingSetNeuralChromosome c = 
				new TrainingSetNeuralChromosome(
					this, chromosomeNetwork);
			c.updateGenes();
			setChromosome(i, c);
		}
		sortChromosomes();
	}

	/**
	 * Returns the root mean square error for a complet training set.
	 * @return The current error for the neural network.
	 * @throws NeuralNetworkException
	 */
	public double getError() {
		final BasicNetwork network = this.getNetwork();
		return network.calculateError(this.training);
	}

	/**
	 * @return the training data
	 */
	public NeuralDataSet getTraining() {
		return this.training;
	}

}
