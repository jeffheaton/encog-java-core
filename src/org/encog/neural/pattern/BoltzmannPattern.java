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
	
	private int annealCycles = 100;
	private int runCycles = 1000;
	private double temperature = 0.0;
	
	/**
	 * Not supported, will throw an exception, Boltzmann networks have
	 * no hidden layers.
	 * @param count Not used.
	 */
	public void addHiddenLayer(int count) {
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
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		final Layer layer = new BasicLayer(new ActivationBiPolar(), true,
				this.neuronCount);

		final BasicNetwork result = new BasicNetwork(new BoltzmannLogic());
		result.setProperty(BoltzmannLogic.PROPERTY_ANNEAL_CYCLES, this.annealCycles);
		result.setProperty(BoltzmannLogic.PROPERTY_RUN_CYCLES, this.runCycles);
		result.setProperty(BoltzmannLogic.PROPERTY_TEMPERATURE, this.temperature);
		result.addLayer(layer);
		layer.addNext(layer);
		layer.setX(PatternConst.START_X);
		layer.setY(PatternConst.START_Y);
		result.getStructure().finalizeStructure();
		result.reset();
		return result;
	}

	public void setActivationFunction(ActivationFunction activation) {
		final String str = 
			"A Boltzmann network will use the BiPolar activation " 
			+ "function, no activation function needs to be specified.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
		
	}

	/**
	 * Set the number of input neurons.  This is the same as the
	 * number of output neurons.
	 * @param count The number of input neurons.
	 */
	public void setInputNeurons(int count) {
		this.neuronCount = count;
		
	}

	/**
	 * Set the number of output neurons.  This is the same as the
	 * number of input neurons.
	 * @param count The number of output neurons.
	 */
	public void setOutputNeurons(int count) {
		this.neuronCount = count;
	}

	public int getAnnealCycles() {
		return annealCycles;
	}

	public void setAnnealCycles(int annealCycles) {
		this.annealCycles = annealCycles;
	}

	public int getRunCycles() {
		return runCycles;
	}

	public void setRunCycles(int runCycles) {
		this.runCycles = runCycles;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	
	

}
