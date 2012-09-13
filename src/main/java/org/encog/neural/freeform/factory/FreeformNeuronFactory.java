package org.encog.neural.freeform.factory;

import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.InputSummation;

public interface FreeformNeuronFactory {
	FreeformNeuron factor(InputSummation object);
}
