/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.neat;

import java.io.Serializable;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.genetic.population.BasicPopulation;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;

public class NEATPopulation extends BasicPopulation implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PROPERTY_NEAT_ACTIVATION = "neatAct";
	public static final String PROPERTY_OUTPUT_ACTIVATION = "outAct";

	/**
	 * The number of input units. All members of the population must agree with
	 * this number.
	 */
	int inputCount;

	/**
	 * The number of output units. All members of the population must agree with
	 * this number.
	 */
	int outputCount;

	/**
	 * The activation function for neat to use.
	 */
	private ActivationFunction neatActivationFunction = new ActivationSigmoid();
	
	private boolean snapshot;

	/**
	 * The activation function to use on the output layer of Encog.
	 */
	private ActivationFunction outputActivationFunction = new ActivationLinear();

	/**
	 * Construct a starting NEAT population.
	 * 
	 * @param inputCount
	 *            The input neuron count.
	 * @param outputCount
	 *            The output neuron count.
	 * @param populationSize
	 *            The population size.
	 */
	public NEATPopulation(final int inputCount, final int outputCount,
			final int populationSize) {
		super(populationSize);
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		if (populationSize == 0) {
			throw new NeuralNetworkError(
					"Population must have more than zero genomes.");
		}

		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			NEATGenome genome = new NEATGenome(assignGenomeID(), inputCount,
					outputCount);
			add(genome);
		}

		// create initial innovations
		NEATGenome genome = (NEATGenome) this.getGenomes().get(0);
		this.setInnovations(new NEATInnovationList(this, genome.getLinks(),
				genome.getNeurons()));

	}

	public NEATPopulation() {

	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}

	/**
	 * @return the neatActivationFunction
	 */
	public ActivationFunction getNeatActivationFunction() {
		return neatActivationFunction;
	}

	/**
	 * @param neatActivationFunction the neatActivationFunction to set
	 */
	public void setNeatActivationFunction(
			ActivationFunction neatActivationFunction) {
		this.neatActivationFunction = neatActivationFunction;
	}

	/**
	 * @return the outputActivationFunction
	 */
	public ActivationFunction getOutputActivationFunction() {
		return outputActivationFunction;
	}

	/**
	 * @param outputActivationFunction the outputActivationFunction to set
	 */
	public void setOutputActivationFunction(
			ActivationFunction outputActivationFunction) {
		this.outputActivationFunction = outputActivationFunction;
	}

	/**
	 * @return the snapshot
	 */
	public boolean isSnapshot() {
		return snapshot;
	}

	/**
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}
	
	

}
