package org.encog.neural.networks;

import org.encog.ml.MLMethod;
import org.encog.neural.flat.FlatNetwork;

public interface ContainsFlat extends MLMethod {
	FlatNetwork getFlat();

}
