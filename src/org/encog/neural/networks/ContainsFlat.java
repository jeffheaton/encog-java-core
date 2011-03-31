package org.encog.neural.networks;

import org.encog.engine.network.flat.FlatNetwork;
import org.encog.ml.MLMethod;

public interface ContainsFlat extends MLMethod {
	FlatNetwork getFlat();

}
