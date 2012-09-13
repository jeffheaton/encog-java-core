package org.encog.neural.freeform.basic;

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.factory.FreeformConnectionFactory;

public class BasicFreeformConnectionFactory implements FreeformConnectionFactory {

	@Override
	public FreeformConnection factor(FreeformNeuron theSourceNeuron,
			FreeformNeuron theTargetNeuron) {
		return new BasicFreeformConnection(theSourceNeuron, theTargetNeuron);
	}

}
