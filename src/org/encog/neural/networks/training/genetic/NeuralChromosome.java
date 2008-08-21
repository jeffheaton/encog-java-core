/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

import org.encog.matrix.MatrixCODEC;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.solve.genetic.Chromosome;
import org.encog.solve.genetic.GeneticAlgorithm;



/**
 * NeuralChromosome: Implements a chromosome that allows a 
 * feedforward neural network to be trained using a genetic
 * algorithm.  The chromosome for a feed forward neural network
 * is the weight and threshold matrix.  
 * 
 * This class is abstract.  If you wish to train the neural
 * network using training sets, you should use the 
 * TrainingSetNeuralChromosome class.  If you wish to use 
 * a cost function to train the neural network, then
 * implement a subclass of this one that properly calculates
 * the cost.
 * 
 * The generic type GA_TYPE specifies the GeneticAlgorithm derived
 * class that implements the genetic algorithm that this class is 
 * to be used with.
 */
abstract public class NeuralChromosome<GA_TYPE extends GeneticAlgorithm<?>>
		extends Chromosome<Double, GA_TYPE> {

	private static final Double ZERO = Double.valueOf(0);
	private static final double RANGE = 20.0;

	private BasicNetwork network;

	/**
	 * @return the network
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	public void initGenes(final int length) {
		final Double result[] = new Double[length];
		Arrays.fill(result, ZERO);
		this.setGenesDirect(result);
	}

	/**
	 * Mutate this chromosome randomly
	 */
	@Override
	public void mutate() {
		final int length = getGenes().length;
		for (int i = 0; i < length; i++) {
			double d = getGene(i);
			final double ratio = (int) ((RANGE * Math.random()) - RANGE);
			d*=ratio;
			setGene(i,d);
		}
	}



	/**
	 * Set all genes.
	 * 
	 * @param list
	 *            A list of genes.
	 * @throws NeuralNetworkException
	 */
	@Override
	public void setGenes(final Double[] list) throws NeuralNetworkError {

		// copy the new genes
		super.setGenes(list);

		calculateCost();
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(final BasicNetwork network) {
		this.network = network;
	}

	public void updateGenes() throws NeuralNetworkError {
		this.setGenes(MatrixCODEC.networkToArray(this.network));
	}

	public void updateNetwork() {
		MatrixCODEC.arrayToNetwork(getGenes(), this.network);
	}

}
