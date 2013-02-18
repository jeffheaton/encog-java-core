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
package org.encog.neural.neat;

import java.io.Serializable;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

/**
 * NEAT networks relieve the programmer of the need to define the hidden layer
 * structure of the neural network.
 * 
 * The output from the neural network can be calculated normally or using a
 * snapshot. The snapshot mode is slower, but it can be more accurate. The
 * snapshot handles recurrent layers better, as it takes the time to loop
 * through the network multiple times to "flush out" the recurrent links.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATNetwork implements MLRegression, MLError, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3660295468309926508L;

	public static final String PROPERTY_NETWORK_DEPTH = "depth";
	public static final String PROPERTY_LINKS = "links";
	public static final String PROPERTY_SNAPSHOT = "snapshot";

	private final NEATLink[] links;
	private final ActivationFunction[] activationFunctions;
	private final double[] preActivation;
	private final double[] postActivation;
	private final int outputIndex;
	private int inputCount;
	private int outputCount;
	private int activationCycles = NEATPopulation.DEFAULT_CYCLES;
	private boolean hasRelaxed = false;
	private double relaxationThreshold;

	public NEATNetwork(final int inputNeuronCount, final int outputNeuronCount,
			final List<NEATLink> connectionArray,
			final ActivationFunction[] theActivationFunctions) {
		this.links = new NEATLink[connectionArray.size()];
		for (int i = 0; i < connectionArray.size(); i++) {
			this.links[i] = connectionArray.get(i);
		}

		this.activationFunctions = theActivationFunctions;
		final int neuronCount = this.activationFunctions.length;

		this.preActivation = new double[neuronCount];
		this.postActivation = new double[neuronCount];

		this.inputCount = inputNeuronCount;
		this.outputIndex = inputNeuronCount + 1;
		this.outputCount = outputNeuronCount;

		// bias
		this.postActivation[0] = 1.0;
	}

	/**
	 * Calculate the error for this neural network.
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	@Override
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output from this synapse.
	 */
	@Override
	public MLData compute(final MLData input) {
		final MLData result = new BasicMLData(this.outputCount);

		// clear from previous
		EngineArray.fill(this.preActivation, 0.0);
		EngineArray.fill(this.postActivation, 0.0);
		this.postActivation[0] = 1.0;

		// copy input
		EngineArray.arrayCopy(input.getData(), 0, this.postActivation, 1,
				this.inputCount);

		// iterate through the network activationCycles times
		for (int i = 0; i < this.activationCycles; ++i) {
			internalCompute();
		}

		// copy output
		EngineArray.arrayCopy(this.postActivation, this.outputIndex,
				result.getData(), 0, this.outputCount);

		return result;
	}

	public int getActivationCycles() {
		return this.activationCycles;
	}

	public ActivationFunction[] getActivationFunctions() {
		return this.activationFunctions;
	}

	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	public NEATLink[] getLinks() {
		return this.links;
	}

	@Override
	public int getOutputCount() {
		return this.outputCount;
	}

	public int getOutputIndex() {
		return this.outputIndex;
	}

	public double[] getPostActivation() {
		return this.postActivation;
	}

	public double[] getPreActivation() {
		return this.preActivation;
	}

	public double getRelaxationThreshold() {
		return this.relaxationThreshold;
	}

	private void internalCompute() {
		for (int j = 0; j < this.links.length; j++) {
			this.preActivation[this.links[j].getToNeuron()] += this.postActivation[this.links[j]
					.getFromNeuron()] * this.links[j].getWeight();
		}

		for (int j = this.outputIndex; j < this.preActivation.length; j++) {
			this.postActivation[j] = this.preActivation[j];
			this.activationFunctions[j].activationFunction(this.postActivation,
					j, 1);
			this.preActivation[j] = 0.0F;
		}
	}

	public boolean isHasRelaxed() {
		return this.hasRelaxed;
	}

	public void setActivationCycles(final int activationCycles) {
		this.activationCycles = activationCycles;
	}

	public void setHasRelaxed(final boolean hasRelaxed) {
		this.hasRelaxed = hasRelaxed;
	}

	public void setInputCount(final int i) {
		this.inputCount = i;
	}

	public void setOutputCount(final int i) {
		this.outputCount = i;
	}

	public void setRelaxationThreshold(final double relaxationThreshold) {
		this.relaxationThreshold = relaxationThreshold;
	}

}
