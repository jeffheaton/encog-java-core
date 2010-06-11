/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.structure;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.NumericRange;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.EncogArray;
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
		this.weightValues = EncogArray.listToDouble(weightList);
		this.allValues = EncogArray.listToDouble(allList);
		this.biasValues = EncogArray.listToDouble(biasList);
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
	 * @return The network analysis as a string.
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
