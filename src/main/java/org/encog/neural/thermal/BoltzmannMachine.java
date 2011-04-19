/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.util.EngineArray;

public class BoltzmannMachine extends ThermalNetwork {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RUN_CYCLES = "runCycles";
	public static final String ANNEAL_CYCLES = "annealCycles";
	
	/**
	 * The current temperature of the neural network. The higher the
	 * temperature, the more random the network will behave.
	 */
	private double temperature;
	
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
	
	public BoltzmannMachine()
	{
		super();
	}
	
	public BoltzmannMachine(int neuronCount)
	{
		super(neuronCount);

		this.threshold = new double[neuronCount];
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
		final int count = getNeuronCount();
		
		if( this.on==null ) {
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
			int i = (int) RangeRandomizer.randomize(0, count - 1);
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
	 * @return The temperature the network is currently operating at.
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * Run the network for all neurons present.
	 */
	public void run() {
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
	void run(final int i) {
		int j;
		double sum, probability;

		final int count = getNeuronCount();

		sum = 0;
		for (j = 0; j < count; j++) {
			sum += getWeight(i, j)
					* (getCurrentState().getBoolean(j) ? 1 : 0);
		}
		sum -= threshold[i];
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

	/**
	 * @return the threshold
	 */
	public double[] getThreshold() {
		return threshold;
	}
	
	public boolean supportsMapPersistence()
	{
		return true;
	}
	
	/**
	 * @return the annealCycles
	 */
	public int getAnnealCycles() {
		return annealCycles;
	}

	/**
	 * @param annealCycles the annealCycles to set
	 */
	public void setAnnealCycles(int annealCycles) {
		this.annealCycles = annealCycles;
	}

	/**
	 * @return the runCycles
	 */
	public int getRunCycles() {
		return runCycles;
	}

	/**
	 * @param runCycles the runCycles to set
	 */
	public void setRunCycles(int runCycles) {
		this.runCycles = runCycles;
	}

	/**
	 * Note: for Boltzmann networks, you will usually want to call the "run" method to 
	 * compute the output.
	 * 
	 * This method can be used to copy the input data to the current state.  A single 
	 * iteration is then run, and the new current state is returned.
	 * @param input The input pattern.
	 * @return The new current state.
	 */
	@Override
	public MLData compute(MLData input) {
		BiPolarNeuralData result = new BiPolarNeuralData(input.size());
		EngineArray
				.arrayCopy(input.getData(), this.getCurrentState().getData());
		run();
		EngineArray.arrayCopy(this.getCurrentState().getData(),
				result.getData());
		return result;
	}

	@Override
	public int getInputCount() {
		return this.getNeuronCount();
	}

	@Override
	public int getOutputCount() {
		return this.getNeuronCount();
	}

	@Override
	public void updateProperties() {
		// nothing needed here		
	}

	public void setThreshold(double[] t) {
		this.threshold = t;
		
	}
	
}
