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
package org.encog.neural.thermal;

import org.encog.ml.BasicML;
import org.encog.ml.MLAutoAssocation;
import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.NeuralNetworkError;
import org.encog.util.EngineArray;

/**
 * The thermal network forms the base class for Hopfield and Boltzmann machines.
 * @author jheaton
 *
 */
public abstract class ThermalNetwork extends BasicML implements MLMethod,
		MLAutoAssocation, MLResettable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The current state of the thermal network.
	 */
	private BiPolarNeuralData currentState;

	/**
	 * The weights.
	 */
	private double[] weights;

	/**
	 * The neuron count.
	 */
	private int neuronCount;

	/**
	 * Default constructor.
	 */
	public ThermalNetwork() {

	}

	/**
	 * Construct the network with the specicified neuron count.
	 * @param neuronCount The number of neurons.
	 */
	public ThermalNetwork(final int neuronCount) {
		this.neuronCount = neuronCount;
		this.weights = new double[neuronCount * neuronCount];
		this.currentState = new BiPolarNeuralData(neuronCount);
	}

	/**
	 * Add to the specified weight.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @param value The value to add.
	 */
	public void addWeight(final int fromNeuron, final int toNeuron,
			final double value) {
		final int index = (toNeuron * this.neuronCount) + fromNeuron;
		if (index >= this.weights.length) {
			throw new NeuralNetworkError("Out of range: fromNeuron:"
					+ fromNeuron + ", toNeuron: " + toNeuron);
		}
		this.weights[index] += value;
	}

	/**
	 * @return Calculate the current energy for the network. The network will
	 *         seek to lower this value.
	 */
	public double calculateEnergy() {
		double tempE = 0;
		final int neuronCount = getNeuronCount();

		for (int i = 0; i < neuronCount; i++) {
			for (int j = 0; j < neuronCount; j++) {
				if (i != j) {
					tempE += getWeight(i, j) * this.currentState.getData(i)
							* this.currentState.getData(j);
				}
			}
		}
		return -1 * tempE / 2;

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
		EngineArray.fill(this.weights, 0);
	}

	/**
	 * @return The current state of the network.
	 */
	public BiPolarNeuralData getCurrentState() {
		return this.currentState;
	}

	/**
	 * @return Get the neuron count for the network.
	 */
	public int getNeuronCount() {
		return this.neuronCount;
	}

	/**
	 * Get a weight.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @return The weight.
	 */
	public double getWeight(final int fromNeuron, final int toNeuron) {
		final int index = (toNeuron * this.neuronCount) + fromNeuron;
		return this.weights[index];
	}

	/**
	 * @return The weights.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/**
	 * Init the network.
	 * @param neuronCount The neuron count.
	 * @param weights The weights.
	 * @param output The toutpu
	 */
	public void init(final int neuronCount, final double[] weights,
			final double[] output) {
		if (neuronCount != output.length) {
			throw new NeuralNetworkError("Neuron count(" + neuronCount
					+ ") must match output count(" + output.length + ").");
		}

		if ((neuronCount * neuronCount) != weights.length) {
			throw new NeuralNetworkError("Weight count(" + weights.length
					+ ") must be the square of the neuron count(" + neuronCount
					+ ").");
		}

		this.neuronCount = neuronCount;
		this.weights = weights;
		this.currentState = new BiPolarNeuralData(neuronCount);
		this.currentState.setData(output);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		reset(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(final int seed) {
		getCurrentState().clear();
		EngineArray.fill(this.weights, 0.0);
	}

	/**
	 * @param state
	 *            The current state for the network.
	 */
	public void setCurrentState(final BiPolarNeuralData state) {
		for (int i = 0; i < state.size(); i++) {
			this.currentState.setData(i, state.getData(i));
		}
	}

	/**
	 * Set the current state.
	 * @param s The current state array.
	 */
	public void setCurrentState(final double[] s) {
		this.currentState = new BiPolarNeuralData(s.length);
		EngineArray.arrayCopy(s, this.currentState.getData());
	}

	/**
	 * Set the neuron count.
	 * @param c The neuron count.
	 */
	public void setNeuronCount(final int c) {
		this.neuronCount = c;

	}

	/**
	 * Set the weight.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @param value The value.
	 */
	public void setWeight(final int fromNeuron, final int toNeuron,
			final double value) {
		final int index = (toNeuron * this.neuronCount) + fromNeuron;
		this.weights[index] = value;
	}

	/**
	 * Set the weight array.
	 * @param w The weight array.
	 */
	public void setWeights(final double[] w) {
		this.weights = w;
	}

}
