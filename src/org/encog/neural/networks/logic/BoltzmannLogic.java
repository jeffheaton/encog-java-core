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

package org.encog.neural.networks.logic;

import org.encog.engine.util.BoundMath;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an Boltzmann type network. See BoltzmannPattern
 * for more information on this type of network.
 */
public class BoltzmannLogic extends ThermalLogic {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 8067779325187120187L;

	/**
	 * Neural network property, the number of cycles to run.
	 */
	public static final String PROPERTY_RUN_CYCLES = "RCYCLE";

	/**
	 * Neural network property, the number of annealing cycles to run.
	 */
	public static final String PROPERTY_ANNEAL_CYCLES = "ACYCLE";

	/**
	 * Neural network property, the temperature.
	 */
	public static final String PROPERTY_TEMPERATURE = "TEMPERATURE";

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(BoltzmannLogic.class);

	/**
	 * The current temperature of the neural network. The higher the
	 * temperature, the more random the network will behave.
	 */
	private double temperature;

	/**
	 * Count used to internally determine if a neuron is "on".
	 */
	private int[] on;

	/**
	 * Count used to internally determine if a neuron is "off".
	 */
	private int[] off;

	/**
	 * The number of cycles to anneal for.
	 */
	private int annealCycles;

	/**
	 * The number of cycles to run the network through before annealing.
	 */
	private int runCycles;

	/**
	 * Setup the network logic, read parameters from the network. NOT USED, call
	 * the run method.
	 * 
	 * @param input
	 *            NOT USED
	 * @param useHolder
	 *            NOT USED
	 * @return NOT USED
	 */
	@Override
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		final String str = "Compute on BasicNetwork cannot be used, rather call"
				+ " the run method on the logic class.";
		if (BoltzmannLogic.LOGGER.isErrorEnabled()) {
			BoltzmannLogic.LOGGER.error(str);
		}
		throw new NeuralNetworkError(str);
	}

	/**
	 * Decrease the temperature by the specified amount.
	 * 
	 * @param d
	 *            The amount to decrease by.
	 */
	public void decreaseTemperature(final double d) {
		this.temperature *= d;
	}

	/**
	 * Run the network until thermal equilibrium is established.
	 */
	public void establishEquilibrium() {
		int n, i;

		final int count = getThermalSynapse().getFromNeuronCount();

		for (i = 0; i < count; i++) {
			this.on[i] = 0;
			this.off[i] = 0;
		}

		for (n = 0; n < this.runCycles * count; n++) {
			run((int) RangeRandomizer.randomize(0, count - 1));
		}
		for (n = 0; n < this.annealCycles * count; n++) {
			i = (int) RangeRandomizer.randomize(0, count - 1);
			run(i);
			if (getCurrentState().getBoolean(i)) {
				this.on[i]++;
			} else {
				this.off[i]++;
			}
		}

		for (i = 0; i < count; i++) {
			getCurrentState().setData(i, this.on[i] > this.off[i]);
		}
	}

	/**
	 * @return The temperature the network is currently operating at.
	 */
	public double getTemperature() {
		return this.temperature;
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

		final Layer layer = getNetwork().getLayer(BasicNetwork.TAG_INPUT);

		this.on = new int[layer.getNeuronCount()];
		this.off = new int[layer.getNeuronCount()];

		this.temperature = getNetwork().getPropertyDouble(
				BoltzmannLogic.PROPERTY_TEMPERATURE);
		this.runCycles = (int) getNetwork().getPropertyLong(
				BoltzmannLogic.PROPERTY_RUN_CYCLES);
		this.annealCycles = (int) getNetwork().getPropertyLong(
				BoltzmannLogic.PROPERTY_ANNEAL_CYCLES);
	}

	/**
	 * Run the network for all neurons present.
	 */
	public void run() {
		final int count = getThermalSynapse().getFromNeuronCount();
		for (int i = 0; i < count; i++) {
			run(i);
		}
	}

	/**
	 * Run the network for the specified neuron.
	 * 
	 * @param i
	 *            The neuron to run for.
	 */
	void run(final int i) {
		int j;
		double sum, probability;

		final int count = getThermalSynapse().getFromNeuronCount();

		sum = 0;
		for (j = 0; j < count; j++) {
			sum += getThermalSynapse().getMatrix().get(i, j)
					* (getCurrentState().getBoolean(j) ? 1 : 0);
		}
		sum -= getThermalLayer().getBiasWeight(i);
		probability = 1 / (1 + BoundMath.exp(-sum / this.temperature));
		if (RangeRandomizer.randomize(0, 1) <= probability) {
			getCurrentState().setData(i, true);
		} else {
			getCurrentState().setData(i, false);
		}
	}

	/**
	 * Set the network temperature.
	 * 
	 * @param temperature
	 *            The temperature to operate the network at.
	 */
	public void setTemperature(final double temperature) {
		this.temperature = temperature;
	}

}
