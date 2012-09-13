package org.encog.neural.freeform.basic;

import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.InputSummation;
import org.encog.neural.freeform.factory.FreeformNeuronFactory;

public class BasicFreeformNeuronFactory implements FreeformNeuronFactory {

	@Override
	public FreeformNeuron factor(InputSummation object) {
		return new BasicFreeformNeuron(object);
	}

}
