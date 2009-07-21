package org.encog.util.network;

import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.math.BoundMath;
import org.encog.util.randomize.RangeRandomizer;

public class BoltzmannHolder extends ThermalNetworkHolder {

	private double temperature;
	private int[] on;
	private int[] off;
	private int annealCycles;
	private int runCycles;

	public BoltzmannHolder(int neuronCount,int runCycles, int annealCycles) {
		super(neuronCount, true);

		this.temperature = 0;
		this.on = new int[neuronCount];
		this.off = new int[neuronCount];
		
		this.runCycles = runCycles;
		this.annealCycles = annealCycles;
	}
	
	public BoltzmannHolder(int neuronCount) {
		this(neuronCount,1000,100);
	}

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

	public void run() {
		int count = getThermalSynapse().getFromNeuronCount();
		for (int i = 0; i < count; i++) {
			run(i);
		}
	}

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

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public void decreaseTemperature(double d) {
		this.temperature *= d;
	}

}
