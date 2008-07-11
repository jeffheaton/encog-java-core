/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.neural.feedforward.train.genetic;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.feedforward.FeedforwardNetwork;



/**
 * TrainingSetNeuralGeneticAlgorithm: Implements a genetic algorithm 
 * that allows a feedforward neural network to be trained using a 
 * genetic algorithm.  This algorithm is for a feed forward neural 
 * network.  The neural network is trained using training sets.
 * 
 * @author Jeff Heaton
 * @version 2.1
 */
public class TrainingSetNeuralGeneticAlgorithm extends
		NeuralGeneticAlgorithm<TrainingSetNeuralGeneticAlgorithm> {

	protected double input[][];
	protected double ideal[][];

	public TrainingSetNeuralGeneticAlgorithm(final FeedforwardNetwork network,
			final boolean reset, final double input[][],
			final double ideal[][], final int populationSize,
			final double mutationPercent, final double percentToMate)
			throws NeuralNetworkError {

		this.setMutationPercent(mutationPercent);
		this.setMatingPopulation(percentToMate * 2);
		this.setPopulationSize(populationSize);
		this.setPercentToMate(percentToMate);

		this.input = input;
		this.ideal = ideal;

		setChromosomes(new TrainingSetNeuralChromosome[getPopulationSize()]);
		for (int i = 0; i < getChromosomes().length; i++) {
			final FeedforwardNetwork chromosomeNetwork = (FeedforwardNetwork) network
					.clone();
			if (reset) {
				chromosomeNetwork.reset();
			}

			final TrainingSetNeuralChromosome c = new TrainingSetNeuralChromosome(
					this, chromosomeNetwork);
			c.updateGenes();
			setChromosome(i, c);
		}
		sortChromosomes();
	}

	/**
	 * Returns the root mean square error for a complet training set.
	 * 
	 * @param len
	 *            The length of a complete training set.
	 * @return The current error for the neural network.
	 * @throws NeuralNetworkException
	 */
	public double getError() throws NeuralNetworkError {
		final FeedforwardNetwork network = this.getNetwork();
		return network.calculateError(this.input, this.ideal);
	}

	/**
	 * @return the ideal
	 */
	public double[][] getIdeal() {
		return this.ideal;
	}

	/**
	 * @return the input
	 */
	public double[][] getInput() {
		return this.input;
	}

}
