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

package org.encog.neural.networks.structure;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.util.EngineArray;
import org.encog.engine.util.Format;
import org.encog.mathutil.NumericRange;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

/**
 * Allows the weights and bias values of the neural network to be analyzed.
 */
public class AnalyzeNetwork {

	/**
	 * The ranges of the weights.
	 */
	private final NumericRange weights;

	/**
	 * The ranges of the bias values.
	 */
	private final NumericRange bias;

	/**
	 * The ranges of both the weights and biases.
	 */
	private final NumericRange weightsAndBias;

	/**
	 * The number of disabled connections.
	 */
	private final int disabledConnections;

	/**
	 * The total number of connections.
	 */
	private final int totalConnections;

	/**
	 * All of the values in the neural network.
	 */
	private final double[] allValues;

	/**
	 * The weight values in the neural network.
	 */
	private final double[] weightValues;

	/**
	 * The bias values in the neural network.
	 */
	private final double[] biasValues;

	/**
	 * Construct a network analyze class. Analyze the specified network.
	 *
	 * @param network
	 *            The network to analyze.
	 */
	public AnalyzeNetwork(final BasicNetwork network) {
		int assignDisabled = 0;
		int assignedTotal = 0;
		final List<Double> biasList = new ArrayList<Double>();
		final List<Double> weightList = new ArrayList<Double>();
		final List<Double> allList = new ArrayList<Double>();

		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.hasBias()) {
				for (int i = 0; i < layer.getNeuronCount(); i++) {
					biasList.add(layer.getBiasWeight(i));
					allList.add(layer.getBiasWeight(i));
				}
			}
		}

		for (final Synapse synapse : network.getStructure().getSynapses()) {
			if (synapse.getMatrixSize() > 0) {
				for (int from = 0; from
				< synapse.getFromNeuronCount(); from++) {
					for (int to = 0; to < synapse.getToNeuronCount(); to++) {
						if (network.isConnected(synapse, from, to)) {
							final double d = synapse.getMatrix().get(from, to);
							weightList.add(d);
							allList.add(d);
						} else {
							assignDisabled++;
						}
						assignedTotal++;
					}
				}
			}
		}

		this.disabledConnections = assignDisabled;
		this.totalConnections = assignedTotal;
		this.weights = new NumericRange(weightList);
		this.bias = new NumericRange(biasList);
		this.weightsAndBias = new NumericRange(allList);
		this.weightValues = EngineArray.listToDouble(weightList);
		this.allValues = EngineArray.listToDouble(allList);
		this.biasValues = EngineArray.listToDouble(biasList);
	}

	/**
	 * @return All of the values in the neural network.
	 */
	public double[] getAllValues() {
		return this.allValues;
	}

	/**
	 * @return The numeric range of the bias values.
	 */
	public NumericRange getBias() {
		return this.bias;
	}

	/**
	 * @return The bias values in the neural network.
	 */
	public double[] getBiasValues() {
		return this.biasValues;
	}

	/**
	 * @return The number of disabled connections in the network.
	 */
	public int getDisabledConnections() {
		return this.disabledConnections;
	}

	/**
	 * @return The total number of connections in the network.
	 */
	public int getTotalConnections() {
		return this.totalConnections;
	}

	/**
	 * @return The numeric range of the weights values.
	 */
	public NumericRange getWeights() {
		return this.weights;
	}

	/**
	 * @return The numeric range of the weights and bias values.
	 */
	public NumericRange getWeightsAndBias() {
		return this.weightsAndBias;
	}

	/**
	 * @return The weight values in the neural network.
	 */
	public double[] getWeightValues() {
		return this.weightValues;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("All Values : ");
		result.append(this.weightsAndBias.toString());
		result.append("\n");
		result.append("Bias : ");
		result.append(this.bias.toString());
		result.append("\n");
		result.append("Weights    : ");
		result.append(this.weights.toString());
		result.append("\n");
		result.append("Disabled   : ");
		result.append(Format.formatInteger(this.disabledConnections));
		result.append("\n");
		return result.toString();
	}

}
