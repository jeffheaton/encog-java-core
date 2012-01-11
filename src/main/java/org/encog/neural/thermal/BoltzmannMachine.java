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
package org.encog.neural.thermal;

import org.encog.mathutil.BoundMath;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.util.EngineArray;

/**
 * Implements a Boltzmann machine.
 *
 */
public class BoltzmannMachine extends ThermalNetwork {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The property for run cycles.
	 */
	public static final String RUN_CYCLES = "runCycles";
	
	/**
	 * The property for anneal cycles.
	 */
	public static final String ANNEAL_CYCLES = "annealCycles";

	/**
	 * The current temperature of the neural network. The higher the
	 * temperature, the more random the network will behave.
	 */
	private double temperature;

	/**
	 * The thresholds.
	 */
	private double[] threshold;

	/**
	 * Count used to internally determine if a neuron is "on".
	 */
	private transient int[] on;

	/**
	 * Count used to internally determine if a neuron is "off".
	 */
	private transient int[] off;

	/**
	 * The number of cycles to anneal for.
	 */
	private int annealCycles = 100;

	/**
	 * The number of cycles to run the network through before annealing.
	 */
	private int runCycles = 1000;

	/**
	 * Default constructors.
	 */
	public BoltzmannMachine() {
		super();
	}

	/**
	 * Construct a Boltzmann machine with the specified number of neurons.
	 * @param neuronCount The number of neurons.
	 */
	public BoltzmannMachine(final int neuronCount) {
		super(neuronCount);

		this.threshold = new double[neuronCount];
	}

	/**
	 * Note: for Boltzmann networks, you will usually want to call the "run"
	 * method to compute the output.
	 * 
	 * This method can be used to copy the input data to the current state. A
	 * single iteration is then run, and the new current state is returned.
	 * 
	 * @param input
	 *            The input pattern.
	 * @return The new current state.
	 */
	@Override
	public final MLData compute(final MLData input) {
		final BiPolarNeuralData result = new BiPolarNeuralData(input.size());
		EngineArray.arrayCopy(input.getData(), getCurrentState().getData());
		run();
		EngineArray.arrayCopy(getCurrentState().getData(), result.getData());
		return result;
	}

	/**
	 * Decrease the temperature by the specified amount.
	 * 
	 * @param d
	 *            The amount to decrease by.
	 */
	public final void decreaseTemperature(final double d) {
		this.temperature *= d;
	}

	/**
	 * Run the network until thermal equilibrium is established.
	 */
	public final void establishEquilibrium() {
		final int count = getNeuronCount();

		if (this.on == null) {
			this.on = new int[count];
			this.off = new int[count];
		}

		for (int i = 0; i < count; i++) {
			this.on[i] = 0;
			this.off[i] = 0;
		}

		for (int n = 0; n < this.runCycles * count; n++) {
			run((int) RangeRandomizer.randomize(0, count - 1));
		}
		for (int n = 0; n < this.annealCycles * count; n++) {
			final int i = (int) RangeRandomizer.randomize(0, count - 1);
			run(i);
			if (getCurrentState().getBoolean(i)) {
				this.on[i]++;
			} else {
				this.off[i]++;
			}
		}

		for (int i = 0; i < count; i++) {
			getCurrentState().setData(i, this.on[i] > this.off[i]);
		}
	}

	/**
	 * @return the annealCycles
	 */
	public final int getAnnealCycles() {
		return this.annealCycles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return getNeuronCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getOutputCount() {
		return getNeuronCount();
	}

	/**
	 * @return the runCycles
	 */
	public final int getRunCycles() {
		return this.runCycles;
	}

	/**
	 * @return The temperature the network is currently operating at.
	 */
	public final double getTemperature() {
		return this.temperature;
	}

	/**
	 * @return the threshold
	 */
	public final double[] getThreshold() {
		return this.threshold;
	}

	/**
	 * Run the network for all neurons present.
	 */
	public final void run() {
		final int count = getNeuronCount();
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
	public final void run(final int i) {
		int j;
		double sum, probability;

		final int count = getNeuronCount();

		sum = 0;
		for (j = 0; j < count; j++) {
			sum += getWeight(i, j) * (getCurrentState().getBoolean(j) ? 1 : 0);
		}
		sum -= this.threshold[i];
		probability = 1 / (1 + BoundMath.exp(-sum / this.temperature));
		if (RangeRandomizer.randomize(0, 1) <= probability) {
			getCurrentState().setData(i, true);
		} else {
			getCurrentState().setData(i, false);
		}
	}

	/**
	 * @param annealCycles
	 *            the annealCycles to set
	 */
	public final void setAnnealCycles(final int annealCycles) {
		this.annealCycles = annealCycles;
	}

	/**
	 * @param runCycles
	 *            the runCycles to set
	 */
	public final void setRunCycles(final int runCycles) {
		this.runCycles = runCycles;
	}

	/**
	 * Set the network temperature.
	 * 
	 * @param temperature
	 *            The temperature to operate the network at.
	 */
	public final void setTemperature(final double temperature) {
		this.temperature = temperature;
	}

	/**
	 * Set the thresholds.
	 * @param t The thresholds.
	 */
	public final void setThreshold(final double[] t) {
		this.threshold = t;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// nothing needed here
	}

}
