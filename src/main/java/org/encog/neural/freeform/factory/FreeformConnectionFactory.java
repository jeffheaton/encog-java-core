package org.encog.neural.freeform.factory;

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;

public interface FreeformConnectionFactory {
	FreeformConnection factor(FreeformNeuron sourceNeuron, FreeformNeuron targetNeuron);
}
