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
package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.neural.thermal.BoltzmannMachine;

/**
 * Pattern to create a Boltzmann machine.
 * 
 */
public class BoltzmannPattern implements NeuralNetworkPattern {

	/**
	 * The number of neurons in the Boltzmann network.
	 */
	private int neuronCount;

	/**
	 * The number of annealing cycles per run.
	 */
	private int annealCycles = 100;

	/**
	 * The number of cycles per run.
	 */
	private int runCycles = 1000;

	/**
	 * The current temperature.
	 */
	private double temperature = 0.0;

	/**
	 * Not supported, will throw an exception, Boltzmann networks have no hidden
	 * layers.
	 * 
	 * @param count
	 *            Not used.
	 */
	public final void addHiddenLayer(final int count) {
		throw new PatternError("A Boltzmann network has no hidden layers.");
	}

	/**
	 * Clear any properties set on this network.
	 */
	public final void clear() {
		this.neuronCount = 0;

	}

	/**
	 * Generate the network.
	 * 
	 * @return The generated network.
	 */
	public final MLMethod generate() {
		BoltzmannMachine boltz = new BoltzmannMachine(this.neuronCount);
		boltz.setTemperature(this.temperature);
		boltz.setRunCycles(this.runCycles);
		boltz.setAnnealCycles(this.annealCycles);
		return boltz;		
	}

	/**
	 * @return The number of annealing cycles per run.
	 */
	public final int getAnnealCycles() {
		return this.annealCycles;
	}

	/**
	 * @return The number of cycles per run.
	 */
	public final int getRunCycles() {
		return this.runCycles;
	}

	/**
	 * @return The temperature.
	 */
	public final double getTemperature() {
		return this.temperature;
	}

	/**
	 * Not used, will throw an exception.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError( "A Boltzmann network will use the BiPolar activation "
				+ "function, no activation function needs to be specified.");
	}

	/**
	 * Set the number of annealing cycles per run.
	 * 
	 * @param annealCycles
	 *            The new value.
	 */
	public final void setAnnealCycles(final int annealCycles) {
		this.annealCycles = annealCycles;
	}

	/**
	 * Set the number of input neurons. This is the same as the number of output
	 * neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public final void setInputNeurons(final int count) {
		this.neuronCount = count;

	}

	/**
	 * Set the number of output neurons. This is the same as the number of input
	 * neurons.
	 * 
	 * @param count
	 *            The number of output neurons.
	 */
	public final void setOutputNeurons(final int count) {
		this.neuronCount = count;
	}

	/**
	 * Set the number of cycles per run.
	 * 
	 * @param runCycles
	 *            The new value.
	 */
	public final void setRunCycles(final int runCycles) {
		this.runCycles = runCycles;
	}

	/**
	 * Set the temperature.
	 * 
	 * @param temperature
	 *            The new value.
	 */
	public final void setTemperature(final double temperature) {
		this.temperature = temperature;
	}
}
