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
package org.encog.util.network;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.BoltzmannPattern;
import org.encog.util.math.BoundMath;
import org.encog.util.randomize.RangeRandomizer;

/**
 * Utility to hold a Boltzmann machine.
 *
 */
public class BoltzmannHolder extends ThermalNetworkHolder {

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
	 * Construct the holder from an already existing network.
	 * @param network The network to use.
	 */
	public BoltzmannHolder(BasicNetwork network,int runCycles, int annealCycles)
	{
		this.setNetwork(network);
		init(network.getInputLayer().getNeuronCount(),runCycles,annealCycles);
	}
	
	/**
	 * Construct a Boltzmann network with the following parameters.
	 * @param neuronCount The neuron count.
	 * @param runCycles The number of cycles to run before annealing.
	 * @param annealCycles The number of cycles to anneal for.
	 */
	public BoltzmannHolder(int neuronCount,int runCycles, int annealCycles) {
		BoltzmannPattern pattern = new BoltzmannPattern();
		pattern.setInputNeurons(neuronCount);
		setNetwork(pattern.generate());
		init(neuronCount,runCycles,annealCycles);
	}
	
	protected void init(int neuronCount,int runCycles, int annealCycles)
	{
		super.init();
		
		this.temperature = 0;
		this.on = new int[this.getNetwork().getInputLayer().getNeuronCount()];
		this.off = new int[this.getNetwork().getInputLayer().getNeuronCount()];
		
		this.runCycles = runCycles;
		this.annealCycles = annealCycles;
	}
	
	/**
	 * Create a Boltzmann holder, and network, with the specified number 
	 * of neurons.  Use 1000 for cycles and 100 for annealing cycles.
	 * @param neuronCount
	 */
	public BoltzmannHolder(int neuronCount) {
		this(neuronCount,1000,100);
	}

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

}
