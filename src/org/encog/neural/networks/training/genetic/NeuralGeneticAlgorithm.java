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

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.solve.genetic.GeneticAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * NeuralGeneticAlgorithm: Implements a genetic algorithm that 
 * allows a feedforward neural network to be trained using a 
 * genetic algorithm.  This algorithm is for a feed forward neural 
 * network.  
 * 
 * This class is abstract.  If you wish to train the neural
 * network using training sets, you should use the 
 * TrainingSetNeuralGeneticAlgorithm class.  If you wish to use 
 * a cost function to train the neural network, then
 * implement a subclass of this one that properly calculates
 * the cost.
 */
public class NeuralGeneticAlgorithm extends BasicTraining {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	class NeuralGeneticAlgorithmHelper extends GeneticAlgorithm<Double>
	{
		/**
		 * Get the current best neural network.
		 * @return The current best neural network.
		 */
		public BasicNetwork getNetwork() {
			final NeuralChromosome c = (NeuralChromosome) getChromosome(0);
			c.updateNetwork();
			return c.getNetwork();
		}

		/**
		 * @return The error from the last iteration.
		 */
		public double getError() {
			return this.getChromosome(0).getCost();
		}
	}
	
	private NeuralGeneticAlgorithmHelper genetic;
	
	
	public NeuralGeneticAlgorithm()
	{
		this.genetic = new NeuralGeneticAlgorithmHelper();
	}
	

	public NeuralGeneticAlgorithmHelper getGenetic() {
		return genetic;
	}

	public void setGenetic(NeuralGeneticAlgorithmHelper genetic) {
		this.genetic = genetic;
	}

	public BasicNetwork getNetwork() {
		return getGenetic().getNetwork();
	}

	public void iteration() {
		
		if( logger.isInfoEnabled())
		{
			logger.info("Performing Genetic iteration.");
		}
		preIteration();
		getGenetic().iteration();
		setError(getGenetic().getError());
		postIteration();
	}
	
	
	


}
