/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.pattern;

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.BoltzmannLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pattern to create a Boltzmann machine.
 * 
 */
public class BoltzmannPattern implements NeuralNetworkPattern {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public void addHiddenLayer(final int count) {
		final String str = "A Boltzmann network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
	}

	/**
	 * Clear any properties set on this network.
	 */
	public void clear() {
		this.neuronCount = 0;

	}

	/**
	 * Generate the network.
	 * 
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		final Layer layer = new BasicLayer(new ActivationBiPolar(), true,
				this.neuronCount);

		final BasicNetwork result = new BasicNetwork(new BoltzmannLogic());
		result.setProperty(BoltzmannLogic.PROPERTY_ANNEAL_CYCLES,
				this.annealCycles);
		result.setProperty(BoltzmannLogic.PROPERTY_RUN_CYCLES, this.runCycles);
		result.setProperty(BoltzmannLogic.PROPERTY_TEMPERATURE,
				this.temperature);
		result.addLayer(layer);
		layer.addNext(layer);
		layer.setX(PatternConst.START_X);
		layer.setY(PatternConst.START_Y);
		result.getStructure().finalizeStructure();
		result.reset();
		return result;
	}

	/**
	 * @return The number of annealing cycles per run.
	 */
	public int getAnnealCycles() {
		return this.annealCycles;
	}

	/**
	 * @return The number of cycles per run.
	 */
	public int getRunCycles() {
		return this.runCycles;
	}

	/**
	 * @return The temperature.
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * Not used, will throw an exception.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = 
			"A Boltzmann network will use the BiPolar activation "
				+ "function, no activation function needs to be specified.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Set the number of annealing cycles per run.
	 * 
	 * @param annealCycles
	 *            The new value.
	 */
	public void setAnnealCycles(final int annealCycles) {
		this.annealCycles = annealCycles;
	}

	/**
	 * Set the number of input neurons. This is the same as the number of output
	 * neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public void setInputNeurons(final int count) {
		this.neuronCount = count;

	}

	/**
	 * Set the number of output neurons. This is the same as the number of input
	 * neurons.
	 * 
	 * @param count
	 *            The number of output neurons.
	 */
	public void setOutputNeurons(final int count) {
		this.neuronCount = count;
	}

	/**
	 * Set the number of cycles per run.
	 * 
	 * @param runCycles
	 *            The new value.
	 */
	public void setRunCycles(final int runCycles) {
		this.runCycles = runCycles;
	}

	/**
	 * Set the temperature.
	 * 
	 * @param temperature
	 *            The new value.
	 */
	public void setTemperature(final double temperature) {
		this.temperature = temperature;
	}
}
