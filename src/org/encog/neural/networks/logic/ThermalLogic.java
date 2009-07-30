package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

public class ThermalLogic extends SimpleRecurrentLogic {
	
	/**
	 * The thermal layer that is to be used.
	 */
	private Layer thermalLayer;
	
	/**
	 * The thermal layer's single self-connected synapse.
	 */
	private Synapse thermalSynapse;
	
	/**
	 * The current state of the thermal network.
	 */
	private BiPolarNeuralData currentState;
		
	/**
	 * @return Get the neuron count for the network.
	 */
	public int getNeuronCount()
	{
		return this.thermalLayer.getNeuronCount();
	}
	
	/**
	 * @return Calculate the current energy for the network.  The 
	 * network will seek to lower this value.
	 */
	public double calculateEnergy()
    {
        double tempE = 0;
        int neuronCount = getNeuronCount();
        
        for (int i = 0; i < neuronCount; i++)
            for (int j = 0; j < neuronCount; j++)
                if (i != j)
                    tempE += this.thermalSynapse.getMatrix().get(i, j) 
                    * this.currentState.getData(i) 
                    * this.currentState.getData(j);
        return -1 * tempE / 2;
        
    }
	
	/**
	 * Clear any connection weights.
	 */
	public void clear()
	{
		this.thermalSynapse.getMatrix().clear();
	}

	/**
	 * @param state The current state for the network.
	 */
	public void setCurrentState(BiPolarNeuralData state) {
		for(int i=0;i<state.size();i++) {
			this.currentState.setData(i, state.getData(i));
		}
	}

	/**
	 * @return The main thermal layer.
	 */
	public Layer getThermalLayer() {
		return thermalLayer;
	}

	/**
	 * @return The thermal synapse.
	 */
	public Synapse getThermalSynapse() {
		return thermalSynapse;
	}

	/**
	 * @return The current state of the network.
	 */
	public BiPolarNeuralData getCurrentState() {
		return currentState;
	}

	@Override
	public void init(BasicNetwork network) {
		super.init(network);
		// hold references to parts of the network we will need later
		this.thermalLayer = this.getNetwork().getInputLayer();
		this.thermalSynapse = this.getNetwork().getStructure().findSynapse(this.thermalLayer, this.thermalLayer, true);
		this.currentState = new BiPolarNeuralData(this.getNetwork().getInputLayer().getNeuronCount());		
	}
}
