package org.encog.neural.freeform.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.neural.freeform.FreeformLayer;
import org.encog.neural.freeform.FreeformNeuron;

public class BasicFreeformLayer implements FreeformLayer, Serializable {
	
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FreeformNeuron> neurons = new ArrayList<FreeformNeuron>();

	@Override
	public List<FreeformNeuron> getNeurons() {
		return this.neurons;
	}

	@Override
	public int size() {
		return this.neurons.size();
	}

	@Override
	public void add(FreeformNeuron basicFreeformNeuron) {
		this.neurons.add(basicFreeformNeuron);		
	}

	@Override
	public void setActivation(int i, double activation) {
		this.neurons.get(i).setActivation(activation);
	}

	@Override
	public int sizeNonBias() {
		int result = 0;
		for(FreeformNeuron neuron: this.neurons) {
			if( !neuron.isBias() ) {
				result++;
			}
		}
		return result;
	}

	@Override
	public boolean hasBias() {
		for(FreeformNeuron neuron: this.neurons) {
			if( neuron.isBias() ) {
				return true;
			}
		}
		return false;
	}
	
}
