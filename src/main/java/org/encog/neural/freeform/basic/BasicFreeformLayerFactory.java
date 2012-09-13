package org.encog.neural.freeform.basic;

import java.io.Serializable;

import org.encog.neural.freeform.FreeformLayer;
import org.encog.neural.freeform.factory.FreeformLayerFactory;

public class BasicFreeformLayerFactory implements FreeformLayerFactory, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public FreeformLayer factor() {
		return new BasicFreeformLayer();
	}

}
