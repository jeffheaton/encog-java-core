package org.encog.neural.freeform.basic;

import java.io.Serializable;

import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNeuron;
import org.encog.neural.freeform.factory.FreeformConnectionFactory;

public class BasicFreeformConnectionFactory implements FreeformConnectionFactory, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public FreeformConnection factor(FreeformNeuron theSourceNeuron,
			FreeformNeuron theTargetNeuron) {
		return new BasicFreeformConnection(theSourceNeuron, theTargetNeuron);
	}

}
