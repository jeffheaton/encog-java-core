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

import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

/**
 * Provides the neural logic for thermal networks. Functions as a base class for
 * BoltzmannLogic and HopfieldLogic.
 */
public class ThermalLogic extends SimpleRecurrentLogic {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -8993932460566008196L;

	/**
	 * The thermal layer that is to be used.
	 */
	private Layer thermalLayer;

	/**
	 * The thermal layer's single self-connected synapse.
	 */
	private Synapse thermalSynapse;

	/**
	 * The current state of the thermal network.
	 */
	private BiPolarNeuralData currentState;

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
					tempE += this.thermalSynapse.getMatrix().get(i, j)
							* this.currentState.getData(i)
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
		this.thermalSynapse.getMatrix().clear();
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
		return this.thermalLayer.getNeuronCount();
	}

	/**
	 * @return The main thermal layer.
	 */
	public Layer getThermalLayer() {
		return this.thermalLayer;
	}

	/**
	 * @return The thermal synapse.
	 */
	public Synapse getThermalSynapse() {
		return this.thermalSynapse;
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * 
	 * @param network
	 *            The network that this logic class belongs to.
	 */
	@Override
	public void init(final BasicNetwork network) {
		super.init(network);
		// hold references to parts of the network we will need later
		this.thermalLayer = getNetwork().getLayer(BasicNetwork.TAG_INPUT);
		this.thermalSynapse = getNetwork().getStructure().findSynapse(
				this.thermalLayer, this.thermalLayer, true);
		this.currentState = new BiPolarNeuralData(this.thermalLayer
				.getNeuronCount());
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
}
