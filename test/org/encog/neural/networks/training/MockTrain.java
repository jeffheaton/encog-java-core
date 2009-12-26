package org.encog.neural.networks.training;

import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;

public class MockTrain extends BasicTraining implements LearningRate, Momentum {

	private BasicNetwork network;
	private boolean wasUsed;
	private double momentum;
	private double learningRate;

	
	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	public void setNetwork(BasicNetwork network)
	{
		this.network = network;
	}

	public void simulate(double newError, double firstValue) {
		preIteration();
		MockTrain.setFirstElement(firstValue, this.network);
		setError(newError);
		postIteration();
		this.wasUsed = true;
	}
	
	public void iteration() {
		preIteration();
		postIteration();
		this.wasUsed = true;
	}
	
	public static void setFirstElement(double value, BasicNetwork network)
	{
		double[] d = NetworkCODEC.networkToArray(network);
		d[0] = value;
		NetworkCODEC.arrayToNetwork(d, network);
	}
	
	public static double getFirstElement(BasicNetwork network)
	{
		double[] d = NetworkCODEC.networkToArray(network);
		return d[0];
	}

	public boolean wasUsed() {
		return wasUsed;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}

	public double getMomentum() {
		return this.momentum;
	}

	public void setMomentum(double m) {
		this.momentum = m;
		
	}



}
