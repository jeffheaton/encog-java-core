package org.encog.neural.networks;

import org.encog.neural.data.NeuralData;

public interface Network {
	public NeuralData compute(NeuralData pattern);
}
