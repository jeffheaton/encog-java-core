package org.encog.neural.networks.logic;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.BoltzmannPattern;
import org.encog.util.math.BoundMath;
import org.encog.util.randomize.RangeRandomizer;

public class BoltzmannLogic extends ThermalLogic {
	
	public static final String PROPERTY_RUN_CYCLES = "RCYCLE";
	public static final String PROPERTY_ANNEAL_CYCLES = "ACYCLE";
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
	
	public void init(BasicNetwork network)
	{
		super.init(network);
				
		this.on = new int[this.getNetwork().getInputLayer().getNeuronCount()];
		this.off = new int[this.getNetwork().getInputLayer().getNeuronCount()];
		
		this.temperature = this.getNetwork().getPropertyDouble(BoltzmannLogic.PROPERTY_TEMPERATURE);
		this.runCycles = (int)this.getNetwork().getPropertyLong(BoltzmannLogic.PROPERTY_RUN_CYCLES);
		this.annealCycles = (int)this.getNetwork().getPropertyLong(BoltzmannLogic.PROPERTY_ANNEAL_CYCLES);
	}

}
