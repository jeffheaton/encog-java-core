/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
 * The following Journal articles were used to implement NEAT/HyperNEAT in
 * Encog. Provided in BibTeX form.
 * 
 * Article{stanley:ec02,title={Evolving Neural Networks Through Augmenting
 * Topologies}, author={Kenneth O. Stanley and Risto Miikkulainen}, volume={10},
 * journal={Evolutionary Computation}, number={2}, pages={99-127}, url=
 * "http://nn.cs.utexas.edu/?stanley:ec02" , year={2002}}
 * 
 * MISC{Gauci_abstractgenerating, author = {Jason Gauci and Kenneth Stanley},
 * title = {ABSTRACT Generating Large-Scale Neural Networks Through Discovering
 * Geometric Regularities}, year = {}}
 * 
 * INPROCEEDINGS{Whiteson05automaticfeature, author = {Shimon Whiteson and
 * Kenneth O. Stanley and Risto Miikkulainen}, title = {Automatic feature
 * selection in neuroevolution}, booktitle = {In Genetic and Evolutionary
 * Computation Conference}, year = {2005}, pages = {1225--1232}, publisher =
 * {ACM Press} }
 */
public class NEATNetwork implements MLRegression, MLError, Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3660295468309926508L;

	/**
	 * The neuron links.
	 */
	private final NEATLink[] links;

	/**
	 * The activation functions.
	 */
	private final ActivationFunction[] activationFunctions;

	/**
	 * The pre-activation values, used to feed the neurons.
	 */
	private final double[] preActivation;

	/**
	 * The post-activation values, used as the output from the neurons.
	 */
	private final double[] postActivation;

	/**
	 * The index to the starting location of the output neurons.
	 */
	private final int outputIndex;

	/**
	 * The input count.
	 */
	private int inputCount;

	/**
	 * The output count.
	 */
	private int outputCount;

	/**
	 * The number of activation cycles to use.
	 */
	private int activationCycles = NEATPopulation.DEFAULT_CYCLES;

	/**
	 * True, if the network has relaxed and values no longer changing. Used when
	 * activationCycles is set to zero for auto.
	 */
	private boolean hasRelaxed = false;

	/**
	 * The amount of change allowed before the network is considered to have
	 * relaxed.
	 */
	private double relaxationThreshold;

	/**
	 * Construct a NEAT network. The links that are passed in also define the
	 * neurons.
	 * 
	 * @param inputNeuronCount
	 *            The input neuron count.
	 * @param outputNeuronCount
	 *            The output neuron count.
	 * @param connectionArray
	 *            The links.
	 * @param theActivationFunctions
	 *            The activation functions.
	 */
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

	/**
	 * @return The number of activation cycles to use.
	 */
	public int getActivationCycles() {
		return this.activationCycles;
	}

	/**
	 * @return The activation functions.
	 */
	public ActivationFunction[] getActivationFunctions() {
		return this.activationFunctions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The links in the neural network.
	 */
	public NEATLink[] getLinks() {
		return this.links;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return The starting location of the output neurons.
	 */
	public int getOutputIndex() {
		return this.outputIndex;
	}

	/**
	 * @return The post-activation values, used as the output from the neurons.
	 */
	public double[] getPostActivation() {
		return this.postActivation;
	}

	/**
	 * @return The pre-activation values, used to feed the neurons.
	 */
	public double[] getPreActivation() {
		return this.preActivation;
	}

	/**
	 * @return The amount of change allowed before the network is considered to
	 *         have relaxed.
	 */
	public double getRelaxationThreshold() {
		return this.relaxationThreshold;
	}

	/**
	 * Perform one activation cycle.
	 */
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

	/**
	 * @return True, if the network has relaxed and values no longer changing.
	 *         Used when activationCycles is set to zero for auto.
	 */
	public boolean isHasRelaxed() {
		return this.hasRelaxed;
	}

	/**
	 * Set the number of activation cycles to use.
	 * 
	 * @param activationCycles
	 *            The number of activation cycles.
	 */
	public void setActivationCycles(final int activationCycles) {
		this.activationCycles = activationCycles;
	}

	/**
	 * Set true, if the network has relaxed and values no longer changing. Used
	 * when activationCycles is set to zero for auto.
	 * 
	 * @param hasRelaxed
	 *            True if the network has relaxed.
	 */
	public void setHasRelaxed(final boolean hasRelaxed) {
		this.hasRelaxed = hasRelaxed;
	}

	/**
	 * The amount of change allowed before the network is considered to have
	 * relaxed.
	 * 
	 * @param relaxationThreshold
	 *            The relaxation threshold.
	 */
	public void setRelaxationThreshold(final double relaxationThreshold) {
		this.relaxationThreshold = relaxationThreshold;
	}

}
