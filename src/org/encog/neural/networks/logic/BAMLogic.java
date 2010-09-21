/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.logic;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralDataMapping;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.pattern.BAMPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an BAM type network. See BAMPattern for more
 * information on this type of network.
 */
public class BAMLogic implements NeuralLogic {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 246153941060562476L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(BAMLogic.class);

	/**
	 * The neural network.
	 */
	private BasicNetwork network;

	/**
	 * The F1 layer.
	 */
	private Layer f1Layer;

	/**
	 * The F2 layer.
	 */
	private Layer f2Layer;

	/**
	 * The connection between the input and output layer.
	 */
	private Synapse synapseF1ToF2;

	/**
	 * The connection between the output and the input layer.
	 */
	private Synapse synapseF2ToF1;

	/**
	 * Add a pattern to the neural network.
	 * 
	 * @param inputPattern
	 *            The input pattern.
	 * @param outputPattern
	 *            The output pattern(for this input).
	 */
	public void addPattern(final NeuralData inputPattern,
			final NeuralData outputPattern) {

		int weight;

		for (int i = 0; i < getF1Neurons(); i++) {
			for (int j = 0; j < getF2Neurons(); j++) {
				weight = (int) (inputPattern.getData(i) * outputPattern
						.getData(j));
				this.synapseF1ToF2.getMatrix().add(i, j, weight);
				this.synapseF2ToF1.getMatrix().add(j, i, weight);
			}
		}

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
		this.synapseF1ToF2.getMatrix().clear();
		this.synapseF2ToF1.getMatrix().clear();
	}

	/**
	 * Setup the network logic, read parameters from the network. NOT USED, call
	 * compute(NeuralInputData).
	 * 
	 * @param input
	 *            NOT USED
	 * @param useHolder
	 *            NOT USED
	 * @return NOT USED
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		final String str = "Compute on BasicNetwork cannot be used, rather call"
				+ " the compute(NeuralData) method on the BAMLogic.";
		if (BAMLogic.LOGGER.isErrorEnabled()) {
			BAMLogic.LOGGER.error(str);
		}
		throw new NeuralNetworkError(str);
	}

	/**
	 * Compute the network for the specified input.
	 * 
	 * @param input
	 *            The input to the network.
	 * @return The output from the network.
	 */
	public NeuralDataMapping compute(final NeuralDataMapping input) {

		boolean stable1 = true, stable2 = true;

		do {

			stable1 = propagateLayer(this.synapseF1ToF2, input.getFrom(), input
					.getTo());
			stable2 = propagateLayer(this.synapseF2ToF1, input.getTo(), input
					.getFrom());

		} while (!stable1 && !stable2);
		return null;
	}

	/**
	 * @return The count of input neurons.
	 */
	public int getF1Neurons() {
		return this.f1Layer.getNeuronCount();
	}

	/**
	 * @return The count of output neurons.
	 */
	public int getF2Neurons() {
		return this.f2Layer.getNeuronCount();
	}

	/**
	 * @return The network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Get the specified weight.
	 * 
	 * @param synapse
	 *            The synapse to get the weight from.
	 * @param input
	 *            The input, to obtain the size from.
	 * @param x
	 *            The x matrix value. (could be row or column, depending on
	 *            input)
	 * @param y
	 *            The y matrix value. (could be row or column, depending on
	 *            input)
	 * @return The value from the matrix.
	 */
	private double getWeight(final Synapse synapse, final NeuralData input,
			final int x, final int y) {
		if (synapse.getFromNeuronCount() != input.size()) {
			return synapse.getMatrix().get(x, y);
		} else {
			return synapse.getMatrix().get(y, x);
		}
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * 
	 * @param network
	 *            The network that this logic class belongs to.
	 */
	public void init(final BasicNetwork network) {
		this.network = network;
		this.f1Layer = network.getLayer(BAMPattern.TAG_F1);
		this.f2Layer = network.getLayer(BAMPattern.TAG_F2);
		this.synapseF1ToF2 = network.getStructure().findSynapse(this.f1Layer,
				this.f2Layer, true);
		this.synapseF2ToF1 = network.getStructure().findSynapse(this.f2Layer,
				this.f1Layer, true);

	}

	/**
	 * Propagate the layer.
	 * 
	 * @param synapse
	 *            The synapse for this layer.
	 * @param input
	 *            The input pattern.
	 * @param output
	 *            The output pattern.
	 * @return True if the network has become stable.
	 */
	private boolean propagateLayer(final Synapse synapse,
			final NeuralData input, final NeuralData output) {
		int i, j;
		int sum, out = 0;
		boolean stable;

		stable = true;

		for (i = 0; i < output.size(); i++) {
			sum = 0;
			for (j = 0; j < input.size(); j++) {
				sum += getWeight(synapse, input, i, j) * input.getData(j);
			}
			if (sum != 0) {
				if (sum < 0) {
					out = -1;
				} else {
					out = 1;
				}
				if (out != (int) output.getData(i)) {
					stable = false;
					output.setData(i, out);
				}
			}
		}
		return stable;
	}
}
