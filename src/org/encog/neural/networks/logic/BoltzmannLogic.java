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
package org.encog.neural.networks.logic;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.util.math.BoundMath;
import org.encog.util.randomize.RangeRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an Boltzmann type network.  See BoltzmannPattern
 * for more information on this type of network.
 */
public class BoltzmannLogic extends ThermalLogic {
	
	/**
	 * The serial ID
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
	 * The current temperature of the neural network.  The higher the 
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
	 * The logging object.
	 */
	private static transient final Logger logger = LoggerFactory.getLogger(BoltzmannLogic.class);

	/**
	 * Run the network for the specified neuron.
	 * @param i The neuron to run for.
	 */
	void run(int i) {
		int j;
		double sum, probability;

		int count = getThermalSynapse().getFromNeuronCount();

		sum = 0;
		for (j = 0; j < count; j++) {
			sum += getThermalSynapse().getMatrix().get(i, j)
					* (this.getCurrentState().getBoolean(j) ? 1 : 0);
		}
		sum -= this.getThermalLayer().getThreshold(i);
		probability = 1 / (1 + BoundMath.exp(-sum / temperature));
		if (RangeRandomizer.randomize(0, 1) <= probability)
			getCurrentState().setData(i, true);
		else
			getCurrentState().setData(i, false);
	}

	/**
	 * Run the network for all neurons present.
	 */
	public void run() {
		int count = getThermalSynapse().getFromNeuronCount();
		for (int i = 0; i < count; i++) {
			run(i);
		}
	}

	/**
	 * Run the network until thermal equalibrium is established.
	 */
	public void establishEquilibrium() {
		int n, i;

		int count = getThermalSynapse().getFromNeuronCount();

		for (i = 0; i < count; i++) {
			on[i] = 0;
			off[i] = 0;
		}

		for (n = 0; n < runCycles * count; n++) {
			run(i = (int) RangeRandomizer.randomize(0, count - 1));
		}
		for (n = 0; n < this.annealCycles * count; n++) {
			run(i = (int) RangeRandomizer.randomize(0, count - 1));
			if (getCurrentState().getBoolean(i))
				on[i]++;
			else
				off[i]++;
		}

		for (i = 0; i < count; i++) {
			getCurrentState().setData(i, on[i] > off[i]);
		}
	}

	/**
	 * @return The temperature the network is currently operating at.
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * Set the network temperature.
	 * @param temperature The temperature to operate the network at.
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	/**
	 * Decrease the temperature by the specified amount.
	 * @param d The amount to decrease by.
	 */
	public void decreaseTemperature(double d) {
		this.temperature *= d;
	}
	
	/**
	 * Setup the network logic, read parameters from the network.
	 * @param network The network that this logic class belongs to.
	 */
	public void init(BasicNetwork network)
	{
		super.init(network);
		
		Layer layer = this.getNetwork().getLayer(BasicNetwork.TAG_INPUT);
				
		this.on = new int[layer.getNeuronCount()];
		this.off = new int[layer.getNeuronCount()];
		
		this.temperature = this.getNetwork().getPropertyDouble(BoltzmannLogic.PROPERTY_TEMPERATURE);
		this.runCycles = (int)this.getNetwork().getPropertyLong(BoltzmannLogic.PROPERTY_RUN_CYCLES);
		this.annealCycles = (int)this.getNetwork().getPropertyLong(BoltzmannLogic.PROPERTY_ANNEAL_CYCLES);
	}
	
	/**
	 * Setup the network logic, read parameters from the network.
	 * NOT USED, call the run method.
	 * @param network The network that this logic class belongs to.
	 */
	@Override
	public NeuralData compute(NeuralData input, NeuralOutputHolder useHolder) {
		String str = "Compute on BasicNetwork cannot be used, rather call" +
				" the run method on the logic class.";
		if( logger.isErrorEnabled() )
		{
			logger.error(str);
		}
		throw new NeuralNetworkError(str);
	}

}
