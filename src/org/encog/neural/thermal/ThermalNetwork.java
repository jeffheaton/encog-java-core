package org.encog.neural.thermal;

import org.encog.engine.util.EngineArray;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.persist.BasicPersistedObject;

public abstract class ThermalNetwork extends BasicPersistedObject {

	/**
	 * The current state of the thermal network.
	 */
	private BiPolarNeuralData currentState;
	
	private double[] weights;
	
	private int neuronCount;
	
	public ThermalNetwork()
	{
		
	}
	
	public ThermalNetwork(int neuronCount) {
		this.neuronCount = neuronCount;
		this.weights = new double[neuronCount*neuronCount];
		this.currentState = new BiPolarNeuralData(neuronCount);		
	}

	/**
	 * @return Calculate the current energy for the network. The network will
	 *         seek to lower this value.
	 */
	public double calculateEnergy() {
		double tempE = 0;
		final int neuronCount = getNeuronCount();

		for (int i = 0; i < neuronCount; i++) {
			for (int j = 0; j < neuronCount; j++) {
				if (i != j) {
					tempE += this.getWeight(i, j)
							* this.currentState.getData(i)
							* this.currentState.getData(j);
				}
			}
		}
		return -1 * tempE / 2;

	}

	/**
	 * Clear any connection weights.
	 */
	public void clear() {
		EngineArray.fill(this.weights, 0);
	}

	/**
	 * @return The current state of the network.
	 */
	public BiPolarNeuralData getCurrentState() {
		return this.currentState;
	}

	/**
	 * @return Get the neuron count for the network.
	 */
	public int getNeuronCount() {
		return this.neuronCount;
	}

	/**
	 * @param state
	 *            The current state for the network.
	 */
	public void setCurrentState(final BiPolarNeuralData state) {
		for (int i = 0; i < state.size(); i++) {
			this.currentState.setData(i, state.getData(i));
		}
	}
	
	public double[] getWeights()
	{
		return this.weights;
	}
	
	public double getWeight(int fromNeuron, int toNeuron)
	{
		int index = (toNeuron*neuronCount) + fromNeuron;
		return weights[index];
	}
	
	public void setWeight(int fromNeuron, int toNeuron, double value)
	{
		int index = (toNeuron*neuronCount) + fromNeuron;
		weights[index] = value;
	}
	
	public void addWeight(int fromNeuron, int toNeuron, double value)
	{
		int index = (toNeuron*neuronCount) + fromNeuron;
		weights[index] += value;
	}
	
	public void init(int neuronCount, double[] weights, double[] output)
	{
		if( neuronCount!=output.length )
		{
			throw new NeuralNetworkError("Neuron count(" + neuronCount + ") must match output count("+output.length+").");
		}
		
		if( (neuronCount*neuronCount)!=weights.length )
		{
			throw new NeuralNetworkError("Weight count(" + weights.length + ") must be the square of the neuron count("+neuronCount+").");
		}
		
		this.neuronCount = neuronCount;
		this.weights = weights;
		this.currentState = new BiPolarNeuralData(neuronCount);
		this.currentState.setData(output);
	}
}
