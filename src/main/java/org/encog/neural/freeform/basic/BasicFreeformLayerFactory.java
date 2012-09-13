package org.encog.neural.freeform.basic;

import org.encog.neural.freeform.FreeformLayer;
import org.encog.neural.freeform.factory.FreeformLayerFactory;

public class BasicFreeformLayerFactory implements FreeformLayerFactory {

	@Override
	public FreeformLayer factor() {
		return new BasicFreeformLayer();
	}

}
