package org.encog.util.network;

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.synapse.Synapse;

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
public class ThermalNetworkHolder {

	/**
	 * The thermal neural network.
	 */
	private final BasicNetwork network;
	
	/**
	 * The thermal layer that is to be used.
	 */
	private final BasicLayer thermalLayer;
	
	/**
	 * The thermal layer's single self-connected synapse.
	 */
	private final Synapse thermalSynapse;
	
	/**
	 * The current state of the thermal network.
	 */
	private final BiPolarNeuralData currentState;
	
	public ThermalNetworkHolder(int neuronCount, boolean threshold)
	{
		// construct the network
		this.network = new BasicNetwork();
		this.network.addLayer(thermalLayer = new BasicLayer(new ActivationBiPolar(), threshold, neuronCount ));
		this.thermalLayer.addNext(this.thermalLayer);
		this.network.getStructure().finalizeStructure();
				
		// hold references to parts of the network we will need later
		this.thermalSynapse = this.thermalLayer.getNext().get(0);
		this.currentState = new BiPolarNeuralData(neuronCount);		
	}
	
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


	public BasicNetwork getNetwork() {
		return network;
	}

	public BasicLayer getThermalLayer() {
		return thermalLayer;
	}

	public Synapse getThermalSynapse() {
		return thermalSynapse;
	}

	public BiPolarNeuralData getCurrentState() {
		return currentState;
	}

	
	
}
