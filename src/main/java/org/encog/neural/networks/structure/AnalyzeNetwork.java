/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.neural.networks.structure;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.NumericRange;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.util.EngineArray;
import org.encog.util.Format;

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

		for (int layerNumber = 0; layerNumber < network.getLayerCount() - 1; layerNumber++) {
			final int fromCount = network.getLayerNeuronCount(layerNumber);
			final int fromBiasCount = network
					.getLayerTotalNeuronCount(layerNumber);
			final int toCount = network.getLayerNeuronCount(layerNumber + 1);

			// weights
			for (int fromNeuron = 0; fromNeuron < fromCount; fromNeuron++) {
				for (int toNeuron = 0; toNeuron < toCount; toNeuron++) {
					final double v = network.getWeight(layerNumber, fromNeuron,
							toNeuron);
					
					if( network.getStructure().isConnectionLimited() ) {
						if( Math.abs(v)<network.getStructure().getConnectionLimit() ) {
							assignDisabled++;
						}
					}

					assignedTotal++;
					weightList.add(v);
					allList.add(v);
				}
			}

			// bias
			if (fromCount != fromBiasCount) {
				final int biasNeuron = fromCount;
				for (int toNeuron = 0; toNeuron < toCount; toNeuron++) {
					final double v = network.getWeight(layerNumber, biasNeuron,
							toNeuron);
					
					if( network.getStructure().isConnectionLimited() ) {
						if( Math.abs(v)<network.getStructure().getConnectionLimit() ) {
							assignDisabled++;
						}
					}
					
					assignedTotal++;
					biasList.add(v);
					allList.add(v);
				}
			}
		}

		for (final Layer layer : network.getStructure().getLayers()) {
			if (layer.hasBias()) {
				for (int i = 0; i < layer.getNeuronCount(); i++) {

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
	public final double[] getAllValues() {
		return this.allValues;
	}

	/**
	 * @return The numeric range of the bias values.
	 */
	public final NumericRange getBias() {
		return this.bias;
	}

	/**
	 * @return The bias values in the neural network.
	 */
	public final double[] getBiasValues() {
		return this.biasValues;
	}

	/**
	 * @return The number of disabled connections in the network.
	 */
	public final int getDisabledConnections() {
		return this.disabledConnections;
	}

	/**
	 * @return The total number of connections in the network.
	 */
	public final int getTotalConnections() {
		return this.totalConnections;
	}

	/**
	 * @return The numeric range of the weights values.
	 */
	public final NumericRange getWeights() {
		return this.weights;
	}

	/**
	 * @return The numeric range of the weights and bias values.
	 */
	public final NumericRange getWeightsAndBias() {
		return this.weightsAndBias;
	}

	/**
	 * @return The weight values in the neural network.
	 */
	public final double[] getWeightValues() {
		return this.weightValues;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
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
