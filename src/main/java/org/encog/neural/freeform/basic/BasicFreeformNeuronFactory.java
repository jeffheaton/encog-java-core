package org.encog.neural.freeform.basic;

import java.io.Serializable;

import org.encog.neural.freeform.FreeformContextNeuron;
import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.InputSummation;
import org.encog.neural.freeform.factory.FreeformNeuronFactory;

public class BasicFreeformNeuronFactory implements FreeformNeuronFactory, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public FreeformNeuron factorRegular(InputSummation object) {
		return new BasicFreeformNeuron(object);
	}

	@Override
	public FreeformNeuron factorContext(FreeformNeuron neuron) {
		FreeformNeuron result = new FreeformContextNeuron(neuron);
		return result;
	}
}
