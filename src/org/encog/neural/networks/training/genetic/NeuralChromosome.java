/*
 * Encog Artificial Intelligence Framework v2.x
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

import java.util.Arrays;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.solve.genetic.Chromosome;
import org.encog.util.randomize.Distort;
import org.encog.util.randomize.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *Implements a chromosome that allows a feedforward neural
 * network to be trained using a genetic algorithm. The chromosome for a feed
 * forward neural network is the weight and threshold matrix.
 * 
 * This class is abstract. If you wish to train the neural network using
 * training sets, you should use the TrainingSetNeuralChromosome class. If you
 * wish to use a score function to train the neural network, then implement a
 * subclass of this one that properly calculates the score.
 * 
 * The generic type GA_TYPE specifies the GeneticAlgorithm derived class that
 * implements the genetic algorithm that this class is to be used with.
 */
public class NeuralChromosome
		extends Chromosome<Double> {

	/**
	 * The amount of distortion to perform a mutation.
	 */
	public static final double DISTORT_FACTOR = 4.0;
	
	/**
	 * Zero.
	 */
	private static final Double ZERO = Double.valueOf(0);

	/**
	 * Mutation range.
	 */
	private Randomizer mutate = new Distort(DISTORT_FACTOR);

	/**
	 * The network to train.
	 */
	private BasicNetwork network;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private NeuralGeneticAlgorithm genetic;
	
	public NeuralChromosome(
			final NeuralGeneticAlgorithm genetic,
			final BasicNetwork network) {
		setGeneticAlgorithm(genetic.getGenetic());
		this.genetic = genetic;
		setNetwork(network);

		initGenes(network.getWeightMatrixSize());
		updateGenes();
	}
	
	
	/**
	 * @return the network
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Init the genes array.
	 * @param length The length to create.
	 */
	public void initGenes(final int length) {
		final Double[] result = new Double[length];
		Arrays.fill(result, ZERO);
		setGenesDirect(result);
	}

	/**
	 * Mutate this chromosome randomly.
	 */
	@Override
	public void mutate() {
		this.mutate.randomize(this.getGenes());
	}

	/**
	 * Set all genes.
	 * 
	 * @param list
	 *            A list of genes.
	 */
	@Override
	public void setGenes(final Double[] list) {

		// copy the new genes
		super.setGenes(list);

		calculateScore();
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Copy the network to the genes.
	 */
	public void updateGenes()  {
		this.setGenes(NetworkCODEC.networkToArray(this.network));
	}

	/**
	 * Copy the genes to the network.
	 */
	public void updateNetwork() {
		NetworkCODEC.arrayToNetwork(getGenes(), this.network);
	}

	@Override
	public void calculateScore() {
		this.updateNetwork();
		double score = this.genetic.getCalculateScore().calculateScore(this.getNetwork());
		setScore(score);		
	}
}
