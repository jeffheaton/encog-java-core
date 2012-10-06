package org.encog.neural.freeform.factory;

import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.InputSummation;

public interface FreeformNeuronFactory {
	FreeformNeuron factorRegular(InputSummation object);
	FreeformNeuron factorContext(FreeformNeuron neuron);
}
